/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cidsx.server.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class ServerOptions {

    //~ Instance fields --------------------------------------------------------

    @Getter @Setter private String corsAccessControlAllowOrigin = "*";
    @Getter @Setter private String corsAccessControlAllowMethods = "GET, POST, DELETE, PUT, OPTIONS";
    @Getter @Setter private String corsAccessControlAllowHeaders = "Content-Type, Authorization";

    @Getter @Setter private String anonymousUser = null;
    @Getter @Setter private String anonymousPassword = null;

    @Getter @Setter private List<String> allowedUsers = null;
    @Getter @Setter private List<String> allowedSearches = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ServerOptions object.
     */
    public ServerOptions() {
    }
}
