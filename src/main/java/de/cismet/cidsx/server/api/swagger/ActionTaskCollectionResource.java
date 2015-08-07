/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api.swagger;

import de.cismet.cidsx.server.api.types.ActionTask;
import de.cismet.cidsx.server.api.types.GenericCollectionResource;

/**
 * Helper Class for Swagger to generate proper response JSON models despite of type erasure in
 * GenericCollectionResource. <strong>Do not use this class directly.</strong>
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
public final class ActionTaskCollectionResource extends GenericCollectionResource<ActionTask> {
}
