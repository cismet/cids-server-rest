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
package de.cismet.cids.server.rest.registry;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public class CidsServerInfo {

    //~ Instance fields --------------------------------------------------------

    String name;
    String uri;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsServerInfo object.
     */
    public CidsServerInfo() {
    }

    /**
     * Creates a new CidsServerInfo object.
     *
     * @param  name  DOCUMENT ME!
     * @param  uri   DOCUMENT ME!
     */
    public CidsServerInfo(final String name, final String uri) {
        this.name = name;
        this.uri = uri;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  DOCUMENT ME!
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getUri() {
        return uri;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  uri  DOCUMENT ME!
     */
    public void setUri(final String uri) {
        this.uri = uri;
    }
}
