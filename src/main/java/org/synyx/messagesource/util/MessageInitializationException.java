package org.synyx.messagesource.util;

/**
 * Runtimeexception that indicates something went wrong with message-resolving during the initialization-phase.
 *
 * @author  Marc Kannegiesser - kannegiesser@synyx.de
 */
public class MessageInitializationException extends RuntimeException {

    public MessageInitializationException(String message, RuntimeException e) {
        super(message, e);
    }
}
