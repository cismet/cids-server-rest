/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.cismet.cids.server.rest.cores.InvalidLevelException;

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

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   e        t DOCUMENT ME!
     * @param   builder  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Response toResponse(final Exception e, final Response.ResponseBuilder builder) {
        final Response.ResponseBuilder response;
        if (builder == null) {
            response = Response.serverError();
        } else {
            response = builder;
        }

        if (e != null) {
            if (log.isTraceEnabled()) {
                log.trace("converting exception to response", e);
            }

            try {
                final String exJson = MAPPER.writer().writeValueAsString(e);
                response.entity(exJson);
                response.type("application/json"); // NOI18N
            } catch (JsonProcessingException ex) {
                log.warn("cannot convert exception to json", ex);
            }
        }

        return response.build();
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
            final Response.ResponseBuilder builder = Response.status(413);

            return ServerExceptionMapper.toResponse(e, builder);
        }
    }
}
