/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api.tools;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.cismet.cidsx.server.api.types.APIException;
import de.cismet.cidsx.server.exceptions.CidsServerException;
import de.cismet.cidsx.server.exceptions.InvalidFilterFormatException;
import de.cismet.cidsx.server.exceptions.InvalidLevelException;

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
                            + ": level=" // NOI18N
                            + e.getLevel(),
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

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Provider
    public static final class InvalidFilterFormatExceptionMapper
            implements ExceptionMapper<InvalidFilterFormatException> {

        //~ Methods ------------------------------------------------------------

        @Override
        public Response toResponse(final InvalidFilterFormatException e) {
            final Response.ResponseBuilder builder = Response.status(400);

            final APIException ex = new APIException(
                    e.getMessage()
                            + ": filter=" // NOI18N
                            + e.getFilter(),
                    "The format of the provided filter parameter is invalid",
                    4000001,
                    "https://github.com/cismet/cids-server-rest/wiki/4000001"); // NOI18N

            builder.entity(ex);
            builder.type("application/json"); // NOI18N

            return builder.build();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Provider
    public static final class CidsServerExceptionMapper implements ExceptionMapper<CidsServerException> {

        //~ Methods ------------------------------------------------------------

        @Override
        public Response toResponse(final CidsServerException e) {
            final Response.ResponseBuilder builder = Response.status(e.getHttpErrorCode());

            final APIException ex = new APIException(
                    e.getDeveloperMessage(),
                    e.getUserMessage(),
                    e.getHttpErrorCode(),
                    (e.getCause() != null) ? e.getCause().toString() : "no further information provided");        // NOI18N
            if (e.getCause() != null) {
                log.info("A CidsServerException has been thrown. The cause was a:" + e.getCause(), e.getCause()); // NOI18N
            }
            builder.entity(ex);
            builder.type("application/json");                                                                     // NOI18N

            return builder.build();
        }
    }
}
