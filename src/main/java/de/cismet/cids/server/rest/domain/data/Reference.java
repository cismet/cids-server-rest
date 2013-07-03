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
package de.cismet.cids.server.rest.domain.data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public class Reference {

    //~ Instance fields --------------------------------------------------------

    String $ref;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Reference object.
     *
     * @param  $ref  DOCUMENT ME!
     */
    public Reference(final String $ref) {
        this.$ref = $ref;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String get$ref() {
        return $ref;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  $ref  DOCUMENT ME!
     */
    public void set$ref(final String $ref) {
        this.$ref = $ref;
    }
}
