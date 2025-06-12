 /*
 * Copyright (c) 2023-2025 Wink.
 */

 package com.example.spring_doc;

 import io.swagger.v3.core.converter.AnnotatedType;
 import io.swagger.v3.core.converter.ModelConverters;
 import io.swagger.v3.oas.models.Components;
 import io.swagger.v3.oas.models.ExternalDocumentation;
 import io.swagger.v3.oas.models.OpenAPI;
 import io.swagger.v3.oas.models.PathItem;
 import io.swagger.v3.oas.models.Paths;
 import io.swagger.v3.oas.models.info.Info;
 import io.swagger.v3.oas.models.media.Schema;
 import io.swagger.v3.oas.models.security.OAuthFlow;
 import io.swagger.v3.oas.models.security.OAuthFlows;
 import io.swagger.v3.oas.models.security.Scopes;
 import io.swagger.v3.oas.models.security.SecurityRequirement;
 import io.swagger.v3.oas.models.security.SecurityScheme;
 import io.swagger.v3.oas.models.servers.Server;
 import lombok.RequiredArgsConstructor;
 import lombok.extern.slf4j.Slf4j;
 import org.springdoc.core.customizers.OpenApiCustomizer;
 import org.springdoc.core.customizers.OperationCustomizer;
 import org.springdoc.core.models.GroupedOpenApi;
 import org.springdoc.core.utils.SpringDocUtils;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;

 import javax.money.MonetaryAmount;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 @Configuration
 @RequiredArgsConstructor
 @Slf4j
 public class OpenApiConfig {

     static {
         SpringDocUtils.getConfig().replaceWithClass(MonetaryAmount.class, CustomMonetaryAmount.class);
     }

     /**
      * Open controller open controller.
      *
      * @return the open controller
      */
     @Bean
     public OpenAPI openAPI() {
         io.swagger.v3.oas.models.info.Contact contact = new io.swagger.v3.oas.models.info.Contact();
         contact.setName("Bjorn Harvold");
         contact.setEmail("bjorn@wink.travel");

         Info info = new Info()
                 .title("Wink API")
                 .contact(contact)
                 .description("""
                          # Introduction
                         """)
                 .version("0.0.1");

         Map<String, String> logo = new HashMap<>();
         logo.put("url", "https://res.cloudinary.com/traveliko/image/upload/c_scale,h_129/v1653285543/wink/logo_text_white.svg");
         logo.put("backgroundColor", "#FFFFFF");
         logo.put("altText", "wink");
         info.addExtension("x-logo", logo);

         final Server server = new Server();
         server.description("Endpoint");
         server.url("https://api.wink.travel");

         final List<Server> servers = List.of(
                 server
         );

         final Scopes scopes = new Scopes();
         scopes.addString("inventory.read", "Read Wink data");
         scopes.addString("inventory.write", "Create Wink data");
         scopes.addString("inventory.remove", "Remove Wink data");

         Components components = new Components()
                 .addSecuritySchemes("oauth2ClientCredentials",
                         new SecurityScheme()
                                 .name("Client Credentials")
                                 .description("""
                                         ## Retrieve access token
                                         Make a POST (`Content-Type: application/x-www-form-urlencoded`) request to the Token URL below. Include one POST key/value entry: `grant_type=client_credentials` and include your clientID / secretKey credentials in a Basic Auth header. E.g. `Authorization: Basic base64_encode($clientId + ':' + $secretKey)`.
                                         ## Add Bearer access token to all API requests
                                         Once you have the access token, you can pass that along on all API requests in the authentication header: `'Authorization: Bearer $accessToken`.
                                         Note: Tokens expire after 3 hours. Please make sure you refresh your token before that time.""")
                                 .type(SecurityScheme.Type.OAUTH2)
                                 .flows(
                                         new OAuthFlows()
                                                 .clientCredentials(new OAuthFlow()
                                                         .authorizationUrl("https://iam.wink.travel/oauth2/authorize")
                                                         .tokenUrl("https://iam.wink.travel/oauth2/token")
                                                         .refreshUrl("https://iam.wink.travel/oauth2/refresh")
                                                         .scopes(scopes)
                                                 )
                                 )
                 )
                 // TODO comment this line out and the app will start successfully
                 .addSchemas("prop1", getSchemaWithDifferentDescription(PingResponse.class, "Response 1", "Custom response 1.", io.swagger.v3.oas.annotations.media.Schema.AccessMode.AUTO, true, null))
                 .addSchemas("prop2", getSchemaWithDifferentDescription(PingResponse.class, "Response 2", "Custom response 2.", io.swagger.v3.oas.annotations.media.Schema.AccessMode.AUTO, true, null))
//                 .addSchemas("amount", getSchemaWithDifferentDescription(MonetaryAmount.class, "Source total", "The total amount for this booking in property defined currency.", io.swagger.v3.oas.annotations.media.Schema.AccessMode.AUTO, true, null))
                 ;

         return new OpenAPI()
                 .info(info)
                 .servers(servers)
                 .components(components)
                 ;
     }

     @Bean
     public GroupedOpenApi testApi() {
         final String[] paths = {
                 "/api/hello"
         };

         return GroupedOpenApi.builder()
                 .group("ping")
                 .pathsToMatch(paths)
                 .packagesToScan(
                         "travel.wink.inventory.web.controller.ping"
                 )
                 .addOperationCustomizer(securityOperationCustomizer("oauth2ClientCredentials"))
                 .addOpenApiCustomizer(setDescription("""
                         
                         # Ping API
                         Easy way to test your credentials.
                         
                         """
                 ))
                 .build();
     }

     private OpenApiCustomizer setDescription(String description) {
         return openApi -> {
             Info info = openApi.getInfo();
             info.description(info.getDescription() + description);
             openApi.setInfo(info);
         };
     }

     private OperationCustomizer securityOperationCustomizer(String securityScheme) {
         return (operation, handlerMethod) -> {
             SecurityRequirement securityRequirement = new SecurityRequirement()
                     .addList(securityScheme);
             operation.addSecurityItem(securityRequirement);
             return operation;
         };
     }

     private Schema getSchemaWithDifferentDescription(
             Class clazz,
             String title,
             String description,
             io.swagger.v3.oas.annotations.media.Schema.AccessMode accessMode,
             boolean required,
             String example
     ) {
         final Schema schema = ModelConverters.getInstance()
                 .resolveAsResolvedSchema(
                         new AnnotatedType(clazz).resolveAsRef(false)
                 ).schema
                 .title(title)
                 .description(description)
                 .example(example);

         switch (accessMode) {
             case AUTO, READ_WRITE -> {
                 schema.readOnly(false);
                 schema.writeOnly(false);
             }
             case READ_ONLY -> schema.readOnly(true);
             case WRITE_ONLY -> schema.writeOnly(true);
         }

         return schema;
     }
 }
