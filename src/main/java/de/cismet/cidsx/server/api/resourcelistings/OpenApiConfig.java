package de.cismet.cidsx.server.api.resourcelistings;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
    info = @Info(
        title = "Rest Service",
        version = "1.0"
    ),
    tags = {
        @Tag(name = "actions", description = "Show, run and maintain custom actions within the cids system."),
        @Tag(name = "classes", description = "Get information about entities. Retrieve, create update and delete objects."),
        @Tag(name = "configattributes", description = "Show, run and maintain custom actions within the cids system."),
        @Tag(name = "entities", description = "Get information about entities. Retrieve, create update and delete objects."),
        @Tag(name = "graphQl", description = "Run graphQl queries within the cids system."),
        @Tag(name = "service", description = "General Service API that provides common infrastructure service methods."),
        @Tag(name = "nodes", description = "Show, run and maintain nodes within the cids system."),
        @Tag(name = "permissions", description = "Show, run and maintain permissions within the cids system."),
        @Tag(name = "searches", description = "Show, run custom searches within the cids system."),
        @Tag(name = "secres", description = "Sends secured server resources"),
        @Tag(name = "subscriptions", description = "... subscriptions within the cids system."),
        @Tag(name = "users", description = "Show, run and maintain users within the cids system.")
    }
)
public class OpenApiConfig {
}
