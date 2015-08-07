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
package de.cismet.cidsx.server.api.swagger;

import de.cismet.cidsx.server.api.types.GenericCollectionResource;
import de.cismet.cidsx.server.api.types.ServerStatus;

/**
 * Helper Class for Swagger to generate proper response JSON models despite of type erasure in
 * GenericCollectionResource. <strong>Do not use this class directly.</strong>
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
public final class ServerStatusCollectionResource extends GenericCollectionResource<ServerStatus> {
}
