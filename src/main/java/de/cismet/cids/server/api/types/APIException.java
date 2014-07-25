/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.api.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@XmlRootElement
@AllArgsConstructor
@Getter
public final class APIException {

    //~ Instance fields --------------------------------------------------------

    private final String developerMessage;
    private final String userMessage;
    private final int errorCode;
    private final String moreInfo;
}
