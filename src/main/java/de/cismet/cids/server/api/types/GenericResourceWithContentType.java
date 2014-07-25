/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.api.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResourceWithContentType {

    //~ Instance fields --------------------------------------------------------

    private String contentType;
    private Object res;
}
