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
package de.cismet.cidsx.server.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.HttpMethod;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@HttpMethod(value = "PATCH")
public @interface PATCH {
}
