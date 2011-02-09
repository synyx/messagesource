package org.synyx.messagesource;

import java.util.List;

import org.synyx.messagesource.importer.Importer;


/**
 * Interface for instances that are able to accept a {@link List} of {@link Messages} for a given basename This is used
 * by {@link Importer} to import {@link Messages} from an {@link MessageProvider} to a {@link MessageAcceptor}.
 * 
 * @author Marc Kannegießer - kannegiesser@synyx.de
 */
public interface MessageAcceptor {

    /**
     * Set the {@link Messages} for the given basename.
     * 
     * @param basename the basename
     * @param messages the messages
     */
    void setMessages(String basename, Messages messages);

}
