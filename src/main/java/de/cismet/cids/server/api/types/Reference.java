/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.api.types;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  1.0
 */
// NOTE: by default lombok ignores all $ vars and @Data does not support "of"
@XmlRootElement
@NoArgsConstructor
@ToString(of = { "$ref" })
@EqualsAndHashCode(of = { "$ref" })
public class Reference {

    //~ Instance fields --------------------------------------------------------

    @Getter
    @Setter
    private String $ref;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Reference object.
     *
     * @param  $ref  DOCUMENT ME!
     */
    public Reference(final String $ref) {
        this.$ref = $ref;
    }
}
