/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.domain;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import de.cismet.cids.server.rest.domain.types.User;
import de.cismet.cids.server.rest.registry.CidsServerInfo;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class Tools {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Tools object.
     */
    private Tools() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   domain  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static WebResource getDomainWebResource(final String domain) {
        if (RuntimeContainer.getServer().getRegistry().equals(ServerConstants.STANDALONE)) {
            final WebResource registryLookup = RuntimeContainer.getClient()
                        .resource(RuntimeContainer.getServer().getRegistry());
            final ClientResponse response = registryLookup.path(domain.toLowerCase())
                        .type(MediaType.APPLICATION_JSON)
                        .get(ClientResponse.class);
            final int status = response.getStatus();
            // TODO: status check
            final CidsServerInfo csiRegistryLookup = response.getEntity(CidsServerInfo.class);

            return RuntimeContainer.getClient().resource(csiRegistryLookup.getUri());
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static User validationHelper(final String authString) {
        if (authString == null) {
            return User.NONE;
        }
        User user = new User(authString);
        if (user.getDomain().equalsIgnoreCase(RuntimeContainer.getServer().getDomainName())) {
            RuntimeContainer.getServer().getUserCore().validate(user);
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(user.getDomain());
            final ClientResponse crValidationCall = delegateCall.path("/users")
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            user = crValidationCall.getEntity(User.class);
        }
        return user;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   user  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    static boolean canHazUserProblems(final User user) {
        return (user == null) || !user.isValidated()
                    || ((user == User.NONE) && !RuntimeContainer.getServer().getUserCore().isNoneUserAllowed());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    static Response getUserProblemResponse() {
        return Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"Please Authenticate with cids Credentials\"")
                    .build();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   r  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Response clientResponseToResponse(final ClientResponse r) {
        // copy the status code
        final ResponseBuilder rb = Response.status(r.getStatus());
        // copy all the headers
        for (final Entry<String, List<String>> entry : r.getHeaders().entrySet()) {
            for (final String value : entry.getValue()) {
                rb.header(entry.getKey(), value);
            }
        }
        // copy the entity
        rb.entity(r.getEntityInputStream());
        // return the response
        return rb.build();
    }
}
