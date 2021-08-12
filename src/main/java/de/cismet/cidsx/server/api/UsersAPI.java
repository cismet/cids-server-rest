/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.Use;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;

import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.security.MessageDigest;
import java.security.interfaces.RSAPublicKey;

import java.util.Base64;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.cismet.cidsx.server.api.tools.Jwks;
import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.data.RuntimeContainer;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Api(
    value = "/users",
    description = "Show, run and maintain custom actions within the cids system.",
    listingPath = "/resources/users"
)
@Path("/users")
@Produces("application/json")
@Slf4j
public class UsersAPI {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @GET
    @Produces("application/json")
    @ApiOperation(
        value = "Validate the user whose credentials are submitted via the header.",
        notes = "-"
    )
    @ApiErrors(
        value = {
                @ApiError(
                    code = 400,
                    reason = "Invalid user supplied"
                ),
                @ApiError(
                    code = 404,
                    reason = "User not found"
                )
            }
    )
    public Response validate(
            @ApiParam(
                value = "Basic Auth Realm",
                required = false
            )
            @HeaderParam("Authorization")
            final String authString) {
        final User user = Tools.validationHelper(authString);
        if (!user.isValidated()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                        .header("WWW-Authenticate", "Basic realm=\"Please Authenticate with cids Credentials\"")
                        .build();
        } else {
            return Response.status(Response.Status.OK).entity(user).build();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain  authString DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/jwk/{domain}")
    @GET
    @Produces("application/json")
    @ApiOperation(
        value = "return the current jwk.",
        notes = "-"
    )
    public Response jwk(
            @ApiParam(
                value = "domain",
                required = false,
                defaultValue = "default"
            )
            @PathParam("domain")
            final String domain) {
        final Key pkey = RuntimeContainer.getServer().getUserCore().getPublicJwtKey(domain);
        final Jwks jwk = new Jwks();
        final Jwks.Key key = new Jwks.Key();
        String keyId = "RandomString";

        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            keyId = Base64.getEncoder().encodeToString(md.digest(pkey.getEncoded()));
        } catch (Exception ex) {
            log.error("Error while creating key id", ex);
        }

        final RSAKey jwkRsaPublicKey = new RSAKey((RSAPublicKey)pkey,
                Use.SIGNATURE,
                new Algorithm("RS256"),
                keyId);

        key.setAlg("RS256");
        key.setKty("RSA");
        key.setUse("sig");
        key.setKid(jwkRsaPublicKey.getKeyID());
        key.setE(jwkRsaPublicKey.getPublicExponent().toString());
        key.setN(jwkRsaPublicKey.getModulus().toString());

        jwk.setKeys(new Jwks.Key[] { key });

        try {
            return Response.status(Response.Status.OK)
                        .header("cache-control", "max-age=300")
                        .entity(new ObjectMapper().writeValueAsString(jwk))
                        .build();
        } catch (Exception ex) {
            log.error("Error while creating jwks", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/roles")
    @GET
    @Produces("application/json")
    @ApiOperation(
        value = "Get all roles.",
        notes = "-"
    )
    public Response getRoles() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   role  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Path("/roles/{role}")
    @GET
    @Produces("application/json")
    @ApiOperation(
        value = "Get a certain role.",
        notes = "-"
    )
    public Response getRole(
            @ApiParam(
                value = "role of the user",
                required = false,
                defaultValue = "default"
            )
            @PathParam("role")
            final String role) {
        return null;
    }
}
