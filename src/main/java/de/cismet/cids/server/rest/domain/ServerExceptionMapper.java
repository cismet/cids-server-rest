/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.cismet.cids.server.rest.cores.InvalidLevelException;
import de.cismet.cids.server.rest.domain.types.APIException;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Slf4j
public final class ServerExceptionMapper {

    //~ Static fields/initializers ---------------------------------------------

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ServerExceptionMapper object.
     */
    private ServerExceptionMapper() {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Provider
    public static final class InvalidLevelExceptionMapper implements ExceptionMapper<InvalidLevelException> {

        //~ Methods ------------------------------------------------------------

        @Override
        public Response toResponse(final InvalidLevelException e) {
            final Response.ResponseBuilder builder = Response.status(403);

            final APIException ex = new APIException(
                    e.getMessage()
                            + ": level="
                            + e.getLevel(), // NOI18N
                    "The level / deduplicate parameter combination is not valid. "
                            + "Remember to explicitely set the level to a value not exceeting 10 "
                            + "if deduplication shall not be done",
                    4030001,
                    "https://github.com/cismet/cids-server-rest/wiki/4030001"); // NOI18N

            builder.entity(ex);
            builder.type("application/json"); // NOI18N

            return builder.build();
        }
    }
}
