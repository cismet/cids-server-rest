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
package de.cismet.cids.server.cores.database;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import lombok.extern.slf4j.Slf4j;

import org.openide.util.lookup.ServiceProvider;

import de.cismet.cids.server.cores.CidsServerCore;

/**
 * DOCUMENT ME!
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
@lombok.Getter
@Slf4j
@ServiceProvider(service = CidsServerCore.class)
//@Parameters(separators = "=")
public class DatabaseBaseCore implements CidsServerCore {

    //~ Methods ----------------------------------------------------------------

// @Parameter(
// names = { "-core.db.connection.driver_class", "--core.db.connection.driver_class" },
// required = true,
// description = "Database Connection JDBC Driver Class"
// )
// static String connectionDriver;
//
// @Parameter(
// names = { "-core.db.connection.url", "--core.db.connection.url" },
// required = true,
// description = "JDBC Database Connection URL"
// )
// static String connectionUrl;
//
// @Parameter(
// names = { "-core.db.connection.schema", "--core.db.connection.schema" },
// required = true,
// description = "JDBC Database Connection Schema"
// )
// static String connectionSchema;
//
// @Parameter(
// names = { "-core.db.connection.username", "--core.db.connection.username" },
// required = true,
// description = "Database Connection Username"
// )
// static String connectionUsername;
//
// @Parameter(
// names = { "-core.db.connection.password", "--core.db.connection.password" },
// required = true,
// description = "Database Connection Password"
// )
// static String connectionPassword;

    @Override
    public String getCoreKey() {
        return "core.db";
    }
}
