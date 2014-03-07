/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.rest.domain.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

import de.cismet.cids.server.rest.Key;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
@XmlRootElement
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Action implements Key {

    //~ Instance fields --------------------------------------------------------

    private String key;
    private String description;
}
