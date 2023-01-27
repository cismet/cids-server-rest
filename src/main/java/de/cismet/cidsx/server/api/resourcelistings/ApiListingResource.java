/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api.resourcelistings;

import io.swagger.annotations.Api;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

import javax.ws.rs.ApplicationPath;
//import com.wordnik.swagger.jaxrs.JavaApiListing;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */

@Path("/resources")
@Api("/resources")
@SwaggerDefinition(
    info =
        @Info(
            title = "Rest Service",
            version = "1.0"
        ),
    tags = {
            @Tag(
                name = "actions",
                description = "Show, run and maintain custom actions within the cids system."
            ),
            @Tag(
                name = "classes",
                description = "Get information about entities. Retrieve, create update and delete objects."
            ),
            @Tag(
                name = "configattributes",
                description = "Show, run and maintain custom actions within the cids system."
            ),
            @Tag(
                name = "entities",
                description = "Get information about entities. Retrieve, create update and delete objects."
            ),
            @Tag(
                name = "graphQl",
                description = "Show, run and maintain graphQl actions within the cids system."
            ),
            @Tag(
                name = "service",
                description = "General Service API that provides common infrastructure service methods."
            ),
            @Tag(
                name = "nodes",
                description = "Show, run and maintain custom actions within the cids system."
            ),
            @Tag(
                name = "permissions",
                description = "Show, run and maintain custom actions within the cids system."
            ),
            @Tag(
                name = "searches",
                description = "Show, run and maintain custom actions within the cids system."
            ),
            @Tag(
                name = "secres",
                description = "Sends secured server resources"
            ),
            @Tag(
                name = "subscriptions",
                description = "... subscriptions within the cids system."
            ),
            @Tag(
                name = "users",
                description = "Show, run and maintain custom actions within the cids system."
            )
        }
)
@Produces({ "application/json", "application/xml" })
public class ApiListingResource extends io.swagger.jaxrs.listing.ApiListingResource {
}
