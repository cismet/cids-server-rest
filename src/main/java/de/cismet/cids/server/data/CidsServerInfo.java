/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
@XmlRootElement
public class CidsServerInfo {

    //~ Instance fields --------------------------------------------------------

    private String name;
    private String uri;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsServerInfo object.
     */
    @SuppressWarnings("all")
    public CidsServerInfo() {
    }

    /**
     * Creates a new CidsServerInfo object.
     *
     * @param  name  DOCUMENT ME!
     * @param  uri   DOCUMENT ME!
     */
    @java.beans.ConstructorProperties({ "name", "uri" })
    @SuppressWarnings("all")
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
    @SuppressWarnings("all")
    public String getName() {
        return this.name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public String getUri() {
        return this.uri;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  uri  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public void setUri(final String uri) {
        this.uri = uri;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CidsServerInfo)) {
            return false;
        }
        final CidsServerInfo other = (CidsServerInfo)o;
        if (!other.canEqual((java.lang.Object)this)) {
            return false;
        }
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if ((this$name == null) ? (other$name != null) : (!this$name.equals(other$name))) {
            return false;
        }
        final java.lang.Object this$uri = this.getUri();
        final java.lang.Object other$uri = other.getUri();
        if ((this$uri == null) ? (other$uri != null) : (!this$uri.equals(other$uri))) {
            return false;
        }
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   other  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @SuppressWarnings("all")
    public boolean canEqual(final java.lang.Object other) {
        return other instanceof CidsServerInfo;
    }

    @Override
    @SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        final java.lang.Object $name = this.getName();
        result = (result * PRIME) + (($name == null) ? 0 : $name.hashCode());
        final java.lang.Object $uri = this.getUri();
        result = (result * PRIME) + (($uri == null) ? 0 : $uri.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public java.lang.String toString() {
        return "CidsServerInfo(name=" + this.getName() + ", uri=" + this.getUri() + ")";
    }
}
