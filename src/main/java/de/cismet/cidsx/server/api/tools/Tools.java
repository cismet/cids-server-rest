/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api.tools;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import de.cismet.cidsx.server.api.ServerConstants;
import de.cismet.cidsx.server.api.types.User;
import de.cismet.cidsx.server.data.CidsServerInfo;
import de.cismet.cidsx.server.data.RuntimeContainer;
import de.cismet.cidsx.server.exceptions.CidsServerException;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Slf4j
public class Tools {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Tools object.
     */
    private Tools() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   listParam  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Collection<String> splitListParameter(final String listParam) {
        final Collection<String> ret;
        if (listParam == null) {
            ret = Collections.emptySet();
        } else {
            final String[] split = listParam.split(","); // NOI18N
            // hashset for fast random access (contains)
            final Set<String> set = new HashSet<String>(split.length, 1);
            for (final String s : split) {
                set.add(s.trim());
            }

            ret = Collections.unmodifiableSet(set);
        }

        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  CidsServerException  DOCUMENT ME!
     */
    public static WebResource getDomainWebResource(final String domain) {
        if (!RuntimeContainer.getServer().getRegistry().equals(ServerConstants.STANDALONE)) {
            final WebResource registryLookup = RuntimeContainer.getClient()
                        .resource(RuntimeContainer.getServer().getRegistry());
            final ClientResponse response = registryLookup.path(domain.toLowerCase())
                        .type(MediaType.APPLICATION_JSON)
                        .get(ClientResponse.class);
            final ClientResponse.Status status = response.getClientResponseStatus();
            if (status.equals(ClientResponse.Status.OK)
                        || status.equals(ClientResponse.Status.ACCEPTED)) {
                final CidsServerInfo csiRegistryLookup = response.getEntity(CidsServerInfo.class);
                return RuntimeContainer.getClient().resource(csiRegistryLookup.getUri());
            } else {
                final String message = "domain '" + domain + "' not found in service registry";
                log.error(message);

                throw new CidsServerException(message, message,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            }
        } else {
            final String message = "server is standalone, cannot ask registry for domain '" + domain + "'";
            log.error(message);
            throw new CidsServerException(message, message, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   authString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static User validationHelper(final String authString) {
        if (authString == null) {
            return User.NONE;
        }
        User user = new User(authString);
        if ((user.getDomain() == null) || user.getDomain().equalsIgnoreCase("local")
                    || user.getDomain().equalsIgnoreCase(RuntimeContainer.getServer().getDomainName())) {
            RuntimeContainer.getServer().getUserCore().validate(user);
        } else {
            final WebResource delegateCall = Tools.getDomainWebResource(user.getDomain());
            final ClientResponse crValidationCall = delegateCall.path("/users")
                        .header("Authorization", authString)
                        .get(ClientResponse.class);
            user = crValidationCall.getEntity(User.class);
        }
        return user;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   user  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean canHazUserProblems(final User user) {
        return (user == null) || !user.isValidated()
                    || ((user == User.NONE)
                        && ((RuntimeContainer.getServer().getUserCore() != null)
                            && !RuntimeContainer.getServer().getUserCore().isNoneUserAllowed()));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Response getUserProblemResponse() {
        return Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"Please Authenticate with cids Credentials\"")
                    .build();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   r  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Response clientResponseToResponse(final ClientResponse r) {
        // copy the status code
        final ResponseBuilder rb = Response.status(r.getStatus());
        // copy all the headers
        for (final Entry<String, List<String>> entry : r.getHeaders().entrySet()) {
            for (final String value : entry.getValue()) {
                rb.header(entry.getKey(), value);
            }
        }
        // copy the entity
        rb.entity(r.getEntityInputStream());
        // return the response
        return rb.build();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   process  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Integer processId(final Process process) {
        Integer pid = unixLikeProcessId(process);
        if (pid == null) {
            pid = windowsProcessId(process);
        }
        return pid;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   process  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    static Integer unixLikeProcessId(final Process process) {
        final Class<?> clazz = process.getClass();
        try {
            if (clazz.getName().equals("java.lang.UNIXProcess")) {
                final Field pidField = clazz.getDeclaredField("pid");
                pidField.setAccessible(true);
                final Object value = pidField.get(process);
                if (value instanceof Integer) {
                    if (log.isDebugEnabled()) {
                        log.debug("Detected pid: " + value);
                    }
                    return (Integer)value;
                }
            }
        } catch (SecurityException sx) {
            log.warn(sx.getMessage(), sx);
        } catch (NoSuchFieldException e) {
            log.warn(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   process  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @see     http://www.golesny.de/p/code/javagetpid
     */
    static Integer windowsProcessId(final Process process) {
        if (process.getClass().getName().equals("java.lang.Win32Process")
                    || process.getClass().getName().equals("java.lang.ProcessImpl")) {
            /* determine the pid on windows plattforms */
            try {
                final Field f = process.getClass().getDeclaredField("handle");
                f.setAccessible(true);
                final long handl = f.getLong(process);

                final Kernel32 kernel = Kernel32.INSTANCE;
                final WinNT.HANDLE handle = new WinNT.HANDLE();
                handle.setPointer(Pointer.createConstant(handl));
                final int ret = kernel.GetProcessId(handle);
                if (log.isDebugEnabled()) {
                    log.debug("Detected pid: " + ret);
                }
                return ret;
            } catch (Throwable e) {
                log.warn(e.getMessage(), e);
            }
        }
        return null;
    }
}
