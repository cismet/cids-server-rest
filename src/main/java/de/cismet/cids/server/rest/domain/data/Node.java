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
public class Node {

    //~ Instance fields --------------------------------------------------------

    String key;
    String name;
    String classKey;
    String objectKey;
    String type;
    String org;
    String dynamicChildren;
    boolean clientSort;
    boolean derivePermissionsFromClass = false;
    String icon;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getKey() {
        return key;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    public void setKey(final String key) {
        this.key = key;
    }

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
    public String getClassKey() {
        return classKey;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  classKey  DOCUMENT ME!
     */
    public void setClassKey(final String classKey) {
        this.classKey = classKey;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  objectKey  DOCUMENT ME!
     */
    public void setObjectKey(final String objectKey) {
        this.objectKey = objectKey;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getType() {
        return type;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  type  DOCUMENT ME!
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getOrg() {
        return org;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  org  DOCUMENT ME!
     */
    public void setOrg(final String org) {
        this.org = org;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDynamicChildren() {
        return dynamicChildren;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dynamicChildren  DOCUMENT ME!
     */
    public void setDynamicChildren(final String dynamicChildren) {
        this.dynamicChildren = dynamicChildren;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isClientSort() {
        return clientSort;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  clientSort  DOCUMENT ME!
     */
    public void setClientSort(final boolean clientSort) {
        this.clientSort = clientSort;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isDerivePermissionsFromClass() {
        return derivePermissionsFromClass;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  derivePermissionsFromClass  DOCUMENT ME!
     */
    public void setDerivePermissionsFromClass(final boolean derivePermissionsFromClass) {
        this.derivePermissionsFromClass = derivePermissionsFromClass;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getIcon() {
        return icon;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  icon  DOCUMENT ME!
     */
    public void setIcon(final String icon) {
        this.icon = icon;
    }
}
