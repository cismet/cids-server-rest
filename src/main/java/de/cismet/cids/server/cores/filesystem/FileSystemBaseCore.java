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
package de.cismet.cids.server.cores.filesystem;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import lombok.extern.slf4j.Slf4j;

import org.openide.util.lookup.ServiceProvider;

import java.io.File;

import de.cismet.cids.server.cores.CidsServerCore;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@lombok.Getter
@Slf4j
@ServiceProvider(service = CidsServerCore.class)
@Parameters(separators = "=")
public class FileSystemBaseCore implements CidsServerCore {

    //~ Static fields/initializers ---------------------------------------------

    @Parameter(
        names = { "-core.fs.basedir", "--core.fs.basedir" },
        required = true,
        description = "base directory of the cids file system cores"
//            ,
//        converter = FileSystemBaseCore.BaseDirConverter.class
    )
    static String baseDir;

    @Parameter(
        names = { "-core.fs.allow-case-insensitive", "--core.fs.allow-case-insensitive" },
        description = "allow FS cores on a case-insensitive FS"
    )
    static boolean allowCaseInsensitiveFilesystem = false;

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getCoreKey() {
        return "core.fs"; // NOI18N
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class BaseDirConverter implements IStringConverter<String> {

        //~ Methods ------------------------------------------------------------

        @Override
        public String convert(final String baseDir) {
            if (File.separatorChar == baseDir.charAt(baseDir.length() - 1)) {
                return baseDir.substring(0, baseDir.length() - 1);
            } else {
                return baseDir;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class BaseDirValidator implements IParameterValidator {

        //~ Methods ------------------------------------------------------------

        @Override
        public void validate(final String name, final String value) throws ParameterException {
            if (!isCaseSensitiveFS()) {
                if (allowCaseInsensitiveFilesystem) {
                    throw new IllegalStateException("FS Core implementation cannot be used on a case-insensitive FS");                  // NOI18N
                } else {
                    if (log.isWarnEnabled()) {
                        log.warn(
                            "FS is not case-sensitive, FS Core implementation will not be fully compliant to interface specification"); // NOI18N
                    }
                }
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private boolean isCaseSensitiveFS() {
            File tmpFile = null;
            try {
                tmpFile = File.createTempFile("caseSensitiveFS", null); // NOI18N
                final File file2 = new File(tmpFile.getParentFile(), tmpFile.getName().toLowerCase());

                return !file2.exists();
            } catch (final Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("cannot determine case sensitivity of FS", e); // NOI18N
                }
            } finally {
                if (tmpFile != null) {
                    try {
                        if (!tmpFile.delete()) {
                            tmpFile.deleteOnExit();
                        }
                    } catch (final Exception e) {
                        tmpFile.deleteOnExit();
                    }
                }
            }

            return false;
        }
    }
}
