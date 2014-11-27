/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.cores;

/**
 * The <code>CidsServerCore</code> interface is the base for every core implementation. That way, it is ensured that
 * every core can be identified by a single key.
 *
 * @author   thorsten
 * @author   martin.scholl@cismet.de
 * @version  0.1
 */
public interface CidsServerCore {

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the identifier of this particular core implementation. Implementors should construct a package-like string in
     * order to avoid name clashes and to make it possible for humans to find out the type of the core, e.g. <code>
     * 'core.fs.entity'</code>.
     *
     * @return  the key of the core implementation
     */
    String getCoreKey();
}
