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

import de.cismet.cids.data.configkeys.interfaces.CidsAttributeConfigurationFlagKey;
import de.cismet.cids.data.configkeys.interfaces.CidsAttributeConfigurationKey;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class AttributeConfig {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Keys implements CidsAttributeConfigurationKey {

        //~ Enum constants -----------------------------------------------------

        Name, ArrayKeyFieldName, DefaultValue, Position, Javaclassname, ReferenceType;

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
    public enum FlagKeys implements CidsAttributeConfigurationFlagKey {

        //~ Enum constants -----------------------------------------------------

        ForeignKey, Visible, Indexed, Array, Optional, ExtensionAttribute, Virtual;

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
    public enum XPKeys implements CidsAttributeConfigurationKey {

        //~ Enum constants -----------------------------------------------------

        EditorXP, ToStringXP, FromStringXP, ComplexEditorXP, RendererXP;

        //~ Methods ------------------------------------------------------------

        @Override
        public String toString() {
            return Tools.decapitalize(super.toString());
        }
    }
}
