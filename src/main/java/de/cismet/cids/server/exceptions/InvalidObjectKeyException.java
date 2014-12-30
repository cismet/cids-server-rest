package de.cismet.cids.server.exceptions;

/**
 *
 * @author martin.scholl@cismet.de
 */
public final class InvalidObjectKeyException extends Exception
{
    private final String objectKey;
    
    /**
     * Creates a new instance of <code>InvalidObjectKeyException</code> without detail message.
     */
    public InvalidObjectKeyException(final String objectKey)
    {
        this(null, objectKey, null);
    }

    /**
     * Constructs an instance of <code>InvalidObjectKeyException</code> with the specified detail 
     * message.
     *
     * @param msg the detail message.
     */
    public InvalidObjectKeyException(final String msg, final String objectKey)
    {
        this(msg, objectKey, null);
    }

    /**
     * Constructs an instance of <code>InvalidObjectKeyException</code> with the specified detail
     * message and the specified cause.
     *
     * @param msg the detail message.
     * @param cause the exception cause
     */
    public InvalidObjectKeyException(final String msg, final String objectKey, final Throwable cause)
    {
        super(msg, cause);
        
        this.objectKey = objectKey;
    }
}