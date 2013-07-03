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
package de.cismet.cids.server.cores.filesystem;

import org.apache.commons.io.FileUtils;

import java.io.File;

import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class TEst {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        final ArrayList<String> al = new ArrayList<String>();
        al.add("/Users/thorsten/Desktop/my-cids-on-a-filesystem/actions/beep/beep.sh");
        al.add("3");
        al.add("1");
        final ProcessBuilder pb = new ProcessBuilder(al);
        final File f = new File("/Users/thorsten/Desktop/my-cids-on-a-filesystem/actions/beep/ficken");
        FileUtils.forceMkdir(f);
        pb.directory(f);
        final Process p = pb.start();
        p.waitFor();
    }
}
