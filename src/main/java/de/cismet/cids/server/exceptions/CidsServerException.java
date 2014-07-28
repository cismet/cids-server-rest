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
package de.cismet.cids.server.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@XmlRootElement
@AllArgsConstructor
@Getter
public class CidsServerException extends RuntimeException {

    //~ Instance fields --------------------------------------------------------

    private final String developerMessage;
    private final String userMessage;
    private final int httpErrorCode;
}
