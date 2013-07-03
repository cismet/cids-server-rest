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

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class GenericResourceWithContentType {

    //~ Instance fields --------------------------------------------------------

    String contentType;
    Object res;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GenericResourceWithContentType object.
     */
    public GenericResourceWithContentType() {
    }

    /**
     * Creates a new GenericResourceWithContentType object.
     *
     * @param  contentType  DOCUMENT ME!
     * @param  res          DOCUMENT ME!
     */
    public GenericResourceWithContentType(final String contentType, final Object res) {
        this.contentType = contentType;
        this.res = res;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  contentType  DOCUMENT ME!
     */
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object getRes() {
        return res;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  res  DOCUMENT ME!
     */
    public void setRes(final Object res) {
        this.res = res;
    }
}
