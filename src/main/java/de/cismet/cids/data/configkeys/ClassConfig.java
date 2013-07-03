/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.data.configkeys;

import de.cismet.cids.Tools;

import de.cismet.cids.data.configkeys.interfaces.CidsClassConfigurationFlagKey;
import de.cismet.cids.data.configkeys.interfaces.CidsClassConfigurationKey;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class ClassConfig {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Keys implements CidsClassConfigurationKey {

        //~ Enum constants -----------------------------------------------------

        Name, ClassIcon, ObjectIcon, PK_Field, Policy, AttributePolicy, FeatureBG, FeatureFG, FeaturePointSymbol,
        FeaturePointSymbolSweetspotX, FeaturePointSymbolSweetspotY;

        //~ Methods ------------------------------------------------------------

        @Override
        public String toString() {
            return Tools.decapitalize(super.toString());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum FlagKeys implements CidsClassConfigurationFlagKey {

        //~ Enum constants -----------------------------------------------------

        Indexed, ArrayLink, HideFeature, ReasonableFew;

        //~ Methods ------------------------------------------------------------

        @Override
        public String toString() {
            return Tools.decapitalize(super.toString());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum XPKeys implements CidsClassConfigurationKey {

        //~ Enum constants -----------------------------------------------------

        EditorXP, ToStringXP, FromStringXP, RendererXP, AggregationRendererXP, IconFactoryXP, FeatureRendererXP;

        //~ Methods ------------------------------------------------------------

        @Override
        public String toString() {
            return Tools.decapitalize(super.toString());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum FeatureSupportingrasterServiceKey implements CidsClassConfigurationKey {

        //~ Enum constants -----------------------------------------------------

        FeatureSupportingRasterServiceSupportXP, FeatureSupportingRasterServiceIdAttribute,
        FeatureSupportingRasterServiceName, FeatureSupportingRasterServiceLayer,
        FeatureSupportingRasterServiceSimpleURL;

        //~ Methods ------------------------------------------------------------

        @Override
        public String toString() {
            return Tools.decapitalize(super.toString());
        }
    }
}
