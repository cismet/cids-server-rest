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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import org.openide.util.lookup.ServiceProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.cismet.cids.server.api.types.ActionResultInfo;
import de.cismet.cids.server.api.types.ActionTask;
import de.cismet.cids.server.api.types.GenericResourceWithContentType;
import de.cismet.cids.server.api.types.User;
import de.cismet.cids.server.cores.ActionCore;
import de.cismet.cids.server.cores.CidsServerCore;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@Parameters(separators = "=")
@ServiceProvider(service = CidsServerCore.class)
public class FileSystemActionCore implements ActionCore {

    //~ Static fields/initializers ---------------------------------------------

    static final String SEP = System.getProperty("file.separator");

    @Parameter(
        names = { "-core.fs.action.actionextension", "--core.fs.action.actionextension" },
        description = "extension of the action scripts"
    )
    static String actionExtension = ".sh";

    @Parameter(
        names = { "-core.fs.action.os", "--core.fs.action.os" },
        description = "Server OS [Unix, Win]"
    )
    static String os = "Unix";

    //~ Instance fields --------------------------------------------------------

    ObjectMapper mapper = new ObjectMapper();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FileSystemActionCore object.
     */
    public FileSystemActionCore() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getBaseDir() {
        return FileSystemBaseCore.baseDir;
    }

    @Override
    public List<ObjectNode> getAllActions(final User user, final String role) {
        final File folder = new File(getBaseDir() + SEP + "actions");
        final ArrayList all = new ArrayList();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isHidden() && !fileEntry.isDirectory() && fileEntry.getAbsolutePath().endsWith(".json")) {
                try {
                    final ObjectNode on = (ObjectNode)mapper.readTree(fileEntry);
                    all.add(on);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return all;
    }

    @Override
    public ObjectNode getAction(final User user, final String actionKey, final String role) {
        try {
            final ObjectNode ret = (ObjectNode)(mapper.readTree(
                        new File(getBaseDir() + SEP + "actions" + SEP + actionKey + ".json")));
            return ret;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<ObjectNode> getAllTasks(final User user, final String actionKey, final String role) {
        final File folder = new File(getBaseDir() + SEP + "actions" + SEP + actionKey);
        final ArrayList all = new ArrayList();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isHidden() && !fileEntry.isDirectory() && fileEntry.getAbsolutePath().endsWith(".json")) {
                try {
                    final ObjectNode on = (ObjectNode)mapper.readTree(fileEntry);
                    all.add(on);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return all;
    }

    @Override
    public ObjectNode createNewActionTask(final User user,
            final String actionKey,
            ActionTask actionTask,
            final String role,
            final boolean requestResultingInstance) {
        if (role != null) {
            throw new UnsupportedOperationException("role not supported yet.");
        }
        if (actionTask == null) {
            actionTask = new ActionTask();
        }
        if (actionTask.getKey() == null) {
            actionTask.setKey(String.valueOf(System.currentTimeMillis()));
        }
        try {
            actionTask.setStatus(ActionTask.Status.STARTING);
            actionTask.setActionKey(actionKey);
            final File taskFile = new File(getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + actionTask.getKey()
                            + ".json");
            final File pidFile = new File(getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + actionTask.getKey()
                            + SEP
                            + "pid.txt");
            final String resultDir = getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + actionTask.getKey();
            final String stderr = getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + actionTask.getKey() + SEP
                        + "stderr.txt";
            final String stdout = getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + actionTask.getKey() + SEP
                        + "stdout.txt";
            final File resultDirFile = new File(resultDir);
            final File stderrFile = new File(stderr);
            final File stdoutFile = new File(stdout);
            mapper.writeValue(taskFile, actionTask);
            final List<String> commandWithParam = new ArrayList<String>();
            commandWithParam.add(getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + actionKey + actionExtension);
            final StringBuilder paramStringB = new StringBuilder();
            for (final Map.Entry<String, Object> entry : actionTask.getParameters().entrySet()) {
                final String key = entry.getKey();
                final String value = String.valueOf(entry.getValue());
                if (!key.startsWith("$")) {
                    paramStringB.append(key);
                    commandWithParam.add(key + "=" + String.valueOf(value));
                } else {
                    commandWithParam.add(String.valueOf(value));
                }
            }
            final ActionTask fixedTask = actionTask;
            new Thread() {

                    @Override
                    public void run() {
                        try {
                            FileUtils.forceMkdir(resultDirFile);
                            fixedTask.setStatus(ActionTask.Status.RUNNING);
                            mapper.writeValue(taskFile, fixedTask);
                            final ProcessBuilder pb = new ProcessBuilder(commandWithParam);

                            pb.directory(new File(resultDir));
                            final Map<String, String> env = pb.environment();
                            env.put("cidsActionDirectory", resultDir);
                            final Process p = pb.start();

                            final Integer i = de.flapdoodle.embed.process.runtime.Processes.processId(p);
                            String s = String.valueOf(i);

                            FileUtils.writeStringToFile(pidFile, s);
                            final BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                                        p.getInputStream()));
                            final BufferedReader stdError = new BufferedReader(new InputStreamReader(
                                        p.getErrorStream()));

                            // read the output from the command
                            final StringBuilder out = new StringBuilder();
                            while ((s = stdInput.readLine()) != null) {
                                out.append(s).append(System.getProperty("line.separator"));
                            }

                            // read any errors from the attempted command
                            final StringBuilder err = new StringBuilder();
                            while ((s = stdError.readLine()) != null) {
                                err.append(s).append(System.getProperty("line.separator"));
                            }
                            if (taskFile.exists()) {
                                if (out.length() > 0) {
                                    FileUtils.writeStringToFile(stdoutFile, out.toString());
                                }
                                if (err.length() > 0) {
                                    FileUtils.writeStringToFile(stderrFile, err.toString());
                                }
                            }
                            p.waitFor();
                            if (taskFile.exists()) {
                                final ActionTask checkForStopped = mapper.readValue(taskFile, ActionTask.class);
                                if (!checkForStopped.getStatus().equals(ActionTask.Status.CANCELING)) {
                                    fixedTask.setStatus(ActionTask.Status.FINISHED);
                                    mapper.writeValue(taskFile, fixedTask);
                                }
                            }
                            if (pidFile.exists()) {
                                FileUtils.forceDelete(pidFile);
                            }
                            if (stderrFile.exists() && (FileUtils.sizeOf(stderrFile) == 0)) {
                                FileUtils.forceDelete(stderrFile);
                            }
                            if (stdoutFile.exists() && (FileUtils.sizeOf(stdoutFile) == 0)) {
                                FileUtils.forceDelete(stdoutFile);
                            }
                            if (resultDirFile.exists() && (FileUtils.sizeOfDirectory(resultDirFile) == 0)) {
                                FileUtils.forceDelete(resultDirFile);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            fixedTask.setStatus(ActionTask.Status.ERROR);
                            try {
                                mapper.writeValue(taskFile, fixedTask);
                                if (pidFile.exists()) {
                                    FileUtils.forceDelete(pidFile);
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }.start();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        if (requestResultingInstance) {
            try {
                return (ObjectNode)mapper.readTree(new File(
                            getBaseDir()
                                    + SEP
                                    + "actions"
                                    + SEP
                                    + actionKey
                                    + SEP
                                    + actionTask.getKey()
                                    + ".json"));
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public ObjectNode getTask(final User user, final String actionKey, final String taskKey, final String role) {
        try {
            final ObjectNode ret = (ObjectNode)(mapper.readTree(
                        new File(getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + taskKey + ".json")));
            return ret;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteTask(final User user, final String actionKey, final String taskKey, final String role) {
        try {
            final File taskFile = new File(getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + taskKey + ".json");
            final File pidFile = new File(getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + taskKey + SEP
                            + "pid.txt");
            final String resultDir = getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + taskKey;
            final File resultDirFile = new File(resultDir);

            final ActionTask task = mapper.readValue(taskFile, ActionTask.class);
            if (task.getStatus().equals(ActionTask.Status.RUNNING)) {
                task.setStatus(ActionTask.Status.CANCELING);
                mapper.writeValue(taskFile, task);

                final String pid = FileUtils.readFileToString(pidFile);
                if (os.equalsIgnoreCase("win")) {
                    final ProcessBuilder killBuilder = new ProcessBuilder("taskkill", "/PID", pid, "/F", "/T");
                    killBuilder.start();
                } else {
                    final ProcessBuilder killBuilder = new ProcessBuilder("kill", pid);
                    killBuilder.start();
                }
            }

            FileUtils.forceDelete(taskFile);
            FileUtils.forceDelete(resultDirFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<ActionResultInfo> getResults(final User user,
            final String actionKey,
            final String taskKey,
            final String role) {
        final File folder = new File(getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + taskKey);
        final ArrayList all = new ArrayList();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isHidden() && !fileEntry.isDirectory()) {
                all.add(getActionResultInfoByFile(fileEntry));
            }
        }
        return all;
    }

    @Override
    public GenericResourceWithContentType getResult(final User user,
            final String actionKey,
            final String taskKey,
            final String resultKey,
            final String role) {
        // Weakness: only one result per key
        final String resultDir = getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + taskKey;
        final File folder = new File(resultDir);
        final FileFilter fileFilter = new WildcardFileFilter(resultKey + "*");
        final File[] files = folder.listFiles(fileFilter);
        if (files.length > 0) {
            final ActionResultInfo info = getActionResultInfoByFile(files[0]);
            return new GenericResourceWithContentType(info.getContentType(), files[0]);
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileEntry  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ActionResultInfo getActionResultInfoByFile(final File fileEntry) {
        final ActionResultInfo info = new ActionResultInfo();
        final String name = fileEntry.getName();
        final int dotPosition = name.lastIndexOf(".");
        final String extension = (dotPosition == -1) ? "" : name.substring(dotPosition + 1, name.length());
        final String key = (dotPosition == -1) ? name : name.substring(0, dotPosition);
        info.setKey(key);
        info.setName("Result: " + key);
        if (extension.equalsIgnoreCase("json")) {
            info.setContentType("application/json");
        } else if (extension.equalsIgnoreCase("txt")) {
            info.setContentType("text/plain");
        } else if (extension.equalsIgnoreCase("xml")) {
            info.setContentType("application/xml");
        } else if (extension.equalsIgnoreCase("dat")) {
            info.setContentType("application/octet-stream");
        } else if (extension.equalsIgnoreCase("html")) {
            info.setContentType("text/html");
        } else if (extension.equalsIgnoreCase("pdf")) {
            info.setContentType("application/pdf");
        } else if (extension.equalsIgnoreCase("png")) {
            info.setContentType("image/png");
        } else if (extension.equalsIgnoreCase("gif")) {
            info.setContentType("image/gif");
        } else if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
                    || extension.equalsIgnoreCase("jpe")) {
            info.setContentType("image/jpeg");
        } else if (extension.length() > 0) {
            info.setContentType("unknown/" + extension);
        } else {
            info.setContentType("unknown/unknown");
        }
        return info;
    }

    @Override
    public String getCoreKey() {
        return "core.fs.action"; // NOI18N
    }
}
