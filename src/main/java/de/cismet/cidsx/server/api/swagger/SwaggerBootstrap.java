/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api.swagger;

import io.swagger.models.*;
import io.swagger.models.auth.*;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@Slf4j
public class SwaggerBootstrap extends HttpServlet {

    //~ Methods ----------------------------------------------------------------

    @Override
    public void init(final ServletConfig config) throws ServletException {
        final Info info = new Info().title("cids REST API")
                    .description("This is the cids REST API. You can find out more about the API "
                            + "at [GitHub](https://github.com/cismet/cids-server-rest) and the cids integration Toolkit at [cismet.de](http://www.cismet.de/en/index.html).")
                    .contact(new Contact().email("info@cismet.de"))
                    .license(new License().name("LGPL v3").url("http://www.gnu.de/documents/lgpl-3.0.de.html"));

        final ServletContext context = config.getServletContext();
        final Swagger swagger = new Swagger().info(info);

        swagger.externalDocs(new ExternalDocs(
                "Find out more about the CIDS REST API",
                "https://github.com/cismet/cids-server-rest/wiki"));

        swagger.securityDefinition("basic", new BasicAuthDefinition());
        // swagger.securityDefinition("api_key", new ApiKeyAuthDefinition("api_key", In.HEADER));

        swagger.tag(new Tag().name("actions").description("get them, run them, kill them, get results").externalDocs(
                new ExternalDocs("Find out more", "https://github.com/cismet/cids-server-rest")));

        swagger.tag(new Tag().name("classes").description("list them, get them, get class attributes"));

        swagger.tag(new Tag().name("entities").description("list them, get them").externalDocs(
                new ExternalDocs("Find out more", "https://github.com/cismet/cids-server-rest")));

        swagger.tag(new Tag().name("configattributes").description("CRUD them").externalDocs(
                new ExternalDocs("Find out more", "https://github.com/cismet/cids-server-rest")));

        swagger.tag(new Tag().name("nodes").description("list them, get children of them").externalDocs(
                new ExternalDocs("Find out more", "https://github.com/cismet/cids-server-rest")));

        swagger.tag(new Tag().name("service").description("get service information").externalDocs(
                new ExternalDocs("Find out more", "https://github.com/cismet/cids-server-rest")));

        context.setAttribute("swagger", swagger);

        log.info("Swagger Bootstrap Servlet initialized");
    }
}
