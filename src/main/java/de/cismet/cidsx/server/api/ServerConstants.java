/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.api;

import de.cismet.cidsx.base.types.MediaTypes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Variant;
import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ServerConstants {

    //~ Static fields/initializers ---------------------------------------------

    public static final String STANDALONE = "NO-REGISTRY-BECAUSE-OF-STANDALONE-SERVER";

    public static final String LOCAL_DOMAIN = "local";

    public static final String ALL_DOMAINS = "all";

    public static final List<Variant> ICON_VARIANTS = Variant
        .mediaTypes(
            new MediaType[] {
                MediaTypes.IMAGE_PNG_TYPE,
                MediaTypes.APPLICATION_X_CIDS_CLASS_ICON_TYPE,
                MediaTypes.APPLICATION_X_CIDS_OBJECT_ICON_TYPE,
            }
        )
        .add()
        .build();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ServerConstants object.
     */
    private ServerConstants() {}
}
