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
public class NodeQuery {

    //~ Instance fields --------------------------------------------------------

    String query;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getQuery() {
        return query;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  query  DOCUMENT ME!
     */
    public void setQuery(final String query) {
        this.query = query;
    }
}
