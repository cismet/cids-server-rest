/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.server.exceptions;

import lombok.Getter;

import de.cismet.cids.server.cores.ActionCore;

/**
 * Exception thrown if any error occurs when dealing with tasks.
 *
 * @author   martin.scholl@cismet.de
 * @version  0.1
 * @see      ActionCore
 */
@Getter
public final class TaskException extends RuntimeException {

    //~ Instance fields --------------------------------------------------------

    private final String taskKey;
    private final String actionKey;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>TaskException</code> without detail message.
     */
    public TaskException() {
        this(null);
    }

    /**
     * Constructs an instance of <code>TaskException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public TaskException(final String msg) {
        this(msg, null);
    }

    /**
     * Constructs an instance of <code>TaskException</code> with the specified detail message and the specified cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public TaskException(final String msg, final Throwable cause) {
        super(msg, cause);

        taskKey = null;
        actionKey = null;
    }

    /**
     * Constructs an instance of <code>TaskException</code> with the specified taskKey, the specified actionKey, the
     * specified detail message and the specified cause.
     *
     * @param  taskKey    the taskKey involved in the exception
     * @param  actionKey  the actionKey involved in the exception
     * @param  msg        the detail message.
     * @param  cause      the exception cause
     */
    // cannot use lombok on this, would miss the message and cause
    public TaskException(final String taskKey, final String actionKey, final String msg, final Throwable cause) {
        super(msg, cause);
        this.taskKey = taskKey;
        this.actionKey = actionKey;
    }
}
