/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import de.cismet.cidsx.server.api.tools.Jwks;
import de.cismet.cidsx.server.api.tools.Tools;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.data.RuntimeContainer;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.security.Key;
import java.security.MessageDigest;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@OpenAPIDefinition(
    info = @Info(
        title = "Subscriptions API",
        description = "Show, run and maintain users within the cids system."
    )
)
@Tag(name = "users", description = "Users within the cids system.")
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
    @Operation(summary = "Validate the user whose credentials are submitted via the header.", description = "-")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "400", description = "Invalid user supplied"),
            @ApiResponse(responseCode = "404", description = "User not found"),
        }
    )
    public Response validate(
        @Parameter(description = "Basic Auth Realm", required = false) @HeaderParam("Authorization") final String authString
    ) {
        final User user = Tools.validationHelper(authString);
        if (!user.isValidated()) {
            return Response
                .status(Response.Status.UNAUTHORIZED)
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
    @Operation(summary = "return the current jwk.", description = "-")
    public Response jwk(
        @Parameter(description = "domain", required = false, schema = @Schema(defaultValue = "default")) @PathParam("domain") final String domain
    ) {
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

        final RSAKey jwkRsaPublicKey = new RSAKey.Builder((RSAPublicKey) pkey)
            .keyUse(KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.RS256)
            .keyID(keyId)
            .build();

        key.setAlg("RS256");
        key.setKty("RSA");
        key.setUse("sig");
        key.setKid(jwkRsaPublicKey.getKeyID());
        key.setE(jwkRsaPublicKey.getPublicExponent().toString());
        key.setN(jwkRsaPublicKey.getModulus().toString());

        jwk.setKeys(new Jwks.Key[] { key });

        try {
            return Response
                .status(Response.Status.OK)
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
    @Operation(summary = "Get all roles.", description = "-")
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
    @Operation(summary = "Get a certain role.", description = "-")
    public Response getRole(
        @Parameter(description = "role of the user", required = false, schema = @Schema(defaultValue = "default")) @PathParam(
            "role"
        ) final String role
    ) {
        return null;
    }
}
