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

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import de.cismet.cids.server.data.configkeys.Key;

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
public class SearchInfo implements Key {

    //~ Instance fields --------------------------------------------------------

    private String key;
    private String name;
    private String description;
    private Map<String, String> parameterDescription;
}
