/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;

import de.cismet.cidsx.base.types.MediaTypes;

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

    public static final List<Variant> ICON_VARIANTS = Variant.mediaTypes(
                new MediaType[] {
                    MediaTypes.IMAGE_PNG_TYPE,
                    MediaTypes.APPLICATION_X_CIDS_CLASS_ICON_TYPE,
                    MediaTypes.APPLICATION_X_CIDS_OBJECT_ICON_TYPE
                })
                .add()
                .build();

    public static final List<Variant> ACTION_RESPONSE_VARIANTS = Variant.mediaTypes(
            new MediaType[] { MediaType.WILDCARD_TYPE })
                .add()
                .build();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ServerConstants object.
     */
    private ServerConstants() {
    }
}
