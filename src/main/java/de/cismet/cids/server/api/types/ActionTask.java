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
public class ActionTask implements Key {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  1.0
     */
    public enum Status {

        //~ Enum constants -----------------------------------------------------

        STARTING, RUNNING, CANCELING, FINISHED, ERROR
    }

    //~ Instance fields --------------------------------------------------------

    private String key;
    private String actionKey;
    private String description;
    private Map<String, Object> parameters;
    private Status status;
}
