/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cidsx.server.data;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class CidsServerInfo {

    //~ Instance fields --------------------------------------------------------

    private String name;
    private String uri;
}
