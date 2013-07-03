/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain.data;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public class CollectionResource {

    //~ Instance fields --------------------------------------------------------

    String $self;
    int $offset;
    int $limit;
    String $first;
    String $previous;
    String $next;
    String $last;
    List $collection;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CollectionResource object.
     *
     * @param  $self        DOCUMENT ME!
     * @param  $offset      DOCUMENT ME!
     * @param  $limit       DOCUMENT ME!
     * @param  $first       DOCUMENT ME!
     * @param  $previous    DOCUMENT ME!
     * @param  $next        DOCUMENT ME!
     * @param  $last        DOCUMENT ME!
     * @param  $collection  DOCUMENT ME!
     */
    public CollectionResource(final String $self,
            final int $offset,
            final int $limit,
            final String $first,
            final String $previous,
            final String $next,
            final String $last,
            final List $collection) {
        this.$self = $self;
        this.$offset = $offset;
        this.$limit = $limit;
        this.$first = $first;
        this.$previous = $previous;
        this.$next = $next;
        this.$last = $last;
        this.$collection = $collection;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String get$self() {
        return $self;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  $self  DOCUMENT ME!
     */
    public void set$self(final String $self) {
        this.$self = $self;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int get$offset() {
        return $offset;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  $offset  DOCUMENT ME!
     */
    public void set$offset(final int $offset) {
        this.$offset = $offset;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int get$limit() {
        return $limit;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  $limit  DOCUMENT ME!
     */
    public void set$limit(final int $limit) {
        this.$limit = $limit;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String get$first() {
        return $first;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  $first  DOCUMENT ME!
     */
    public void set$first(final String $first) {
        this.$first = $first;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String get$previous() {
        return $previous;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  $previous  DOCUMENT ME!
     */
    public void set$previous(final String $previous) {
        this.$previous = $previous;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String get$next() {
        return $next;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  $next  DOCUMENT ME!
     */
    public void set$next(final String $next) {
        this.$next = $next;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String get$last() {
        return $last;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  $last  DOCUMENT ME!
     */
    public void set$last(final String $last) {
        this.$last = $last;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List get$collection() {
        return $collection;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  $collection  DOCUMENT ME!
     */
    public void set$collection(final List $collection) {
        this.$collection = $collection;
    }
}
