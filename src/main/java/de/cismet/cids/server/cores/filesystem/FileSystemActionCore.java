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
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import de.cismet.cids.server.api.types.Action;
import de.cismet.cids.server.api.types.ActionResultInfo;
import de.cismet.cids.server.api.types.ActionTask;
import de.cismet.cids.server.api.types.GenericResourceWithContentType;
import de.cismet.cids.server.cores.ActionCore;
import de.cismet.cids.server.cores.CidsServerCore;

import de.cismet.commons.concurrency.CismetExecutors;

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
    @Parameter(
        names = { "-core.fs.action.output.encoding", "--core.fs.action.output.encoding" },
        description =
            "Encoding of the output of the action (on windows it is most likely \"Cp1252\" or \"ISO-8859-1\" or \"ISO-850\")"
    )
    static String outputEncoding = "UTF-8";

    static ConcurrentHashMap<String, ExecutorService> actionExecutorServices =
        new ConcurrentHashMap<String, ExecutorService>();

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
    public List<Action> getAllActions() {
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
    public Action getAction(final String actionKey) {
        try {
            final Action ret = mapper.readValue(new File(getBaseDir() + SEP + "actions" + SEP + actionKey + ".json"),
                    Action.class);
            return ret;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<ActionTask> getAllTasks(final String actionKey) {
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
    public ActionTask createNewActionTask(final String actionKey,
            ActionTask actionTask,
            final boolean requestResultingInstance,
            final InputStream... attachmentIS) {
        Action action = null;
        if (actionTask == null) {
            actionTask = new ActionTask();
        }
        if (actionTask.getKey() == null) {
            actionTask.setKey(String.valueOf(System.currentTimeMillis()));
        }
        try {
            actionTask.setStatus(ActionTask.Status.STARTING);
            actionTask.setActionKey(actionKey);
            final File taskFile;
            final File f = new File(getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + actionTask.getKey()
                            + ".json");
            /*
             * It can happen, that the time in ms is not a unique key, especially when creating multiple ActionTasks in
             * parallell. We must check if there is already a file that has the same key, and append a large unique
             * number in that case to ensure a unique id.
             */
            if (f.exists()) {
                actionTask.setKey(actionTask.getKey() + "_" + Math.floor(Math.random() * 100000));
                taskFile = new File(getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + actionTask.getKey()
                                + ".json");
            } else {
                taskFile = f;
            }
            final File actionFile = new File(getBaseDir() + SEP + "actions" + SEP + actionKey + ".json");
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
            if ((attachmentIS != null) && (attachmentIS.length > 0) && (attachmentIS[0] != null)) {
                final File attachmentFile = new File(getBaseDir() + SEP + "actions" + SEP + actionKey + SEP
                                + actionTask.getKey()
                                + SEP
                                + "attachment.file");

                FileUtils.copyInputStreamToFile(attachmentIS[0], attachmentFile);
            }
            m.writeValue(taskFile, actionTask);
            action = m.readValue(actionFile, Action.class);
            final List<String> commandWithParam = new ArrayList<String>();
            commandWithParam.add(getBaseDir() + SEP + "actions" + SEP + actionKey + SEP + actionKey + actionExtension);
            final StringBuilder paramStringB = new StringBuilder();
            if (actionTask.getParameters() != null) {
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
            }
            final ActionTask fixedTask = actionTask;

            // get the right ExcutorService
            ExecutorService es = actionExecutorServices.get(actionKey);
            if (es == null) {
                if (action.getMaxConcurrentThreads() == 1) {
                    actionExecutorServices.putIfAbsent(actionKey, CismetExecutors.newSingleThreadExecutor());
                } else {
                    actionExecutorServices.putIfAbsent(
                        actionKey,
                        CismetExecutors.newFixedThreadPool(action.getMaxConcurrentThreads()));
                }
                es = actionExecutorServices.get(actionKey);
            }

            final Runnable actionRunner = new Runnable() {

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
                                        p.getInputStream(),
                                        outputEncoding));
                            final BufferedReader stdError = new BufferedReader(new InputStreamReader(
                                        p.getErrorStream(),
                                        outputEncoding));

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
                                    FileUtils.writeStringToFile(
                                        stdoutFile,
                                        out.toString(),
                                        FileSystemBaseCore.fsEncoding);
                                }

                                if (err.length() > 0) {
                                    FileUtils.writeStringToFile(
                                        stderrFile,
                                        err.toString(),
                                        FileSystemBaseCore.fsEncoding);
                                }
                            }
                            p.waitFor();
                            if (taskFile.exists()) {
                                final ActionTask checkForStopped = m.readValue(taskFile, ActionTask.class);
                                if (!checkForStopped.getStatus().equals(ActionTask.Status.CANCELING)) {
                                    fixedTask.setStatus(ActionTask.Status.FINISHED);
                                    m.writeValue(taskFile, fixedTask);
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
                                m.writeValue(taskFile, fixedTask);
                                if (pidFile.exists()) {
                                    FileUtils.forceDelete(pidFile);
                                }
                            } catch (Exception ex) {
                                throw new RuntimeException();
                            }
                        }
                    }
                };
            es.execute(actionRunner);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        if (requestResultingInstance) {
            try {
                return mapper.readValue(new File(
                            getBaseDir()
                                    + SEP
                                    + "actions"
                                    + SEP
                                    + actionKey
                                    + SEP
                                    + actionTask.getKey()
                                    + ".json"),
                        ActionTask.class);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public ActionTask getTask(final String actionKey, final String taskKey) {
        try {
            return mapper.readValue(new File(
                        getBaseDir()
                                + SEP
                                + "actions"
                                + SEP
                                + actionKey
                                + SEP
                                + taskKey
                                + ".json"),
                    ActionTask.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteTask(final String actionKey, final String taskKey) {
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
    public List<ActionResultInfo> getResults(final String actionKey, final String taskKey) {
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
    public GenericResourceWithContentType getResult(final String actionKey,
            final String taskKey,
            final String resultKey) {
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
