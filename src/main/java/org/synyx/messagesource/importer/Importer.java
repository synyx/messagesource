/**
 * 
 */
package org.synyx.messagesource.importer;

import java.io.File;

import org.synyx.messagesource.MessageAcceptor;
import org.synyx.messagesource.MessageProvider;
import org.synyx.messagesource.filesystem.FileSystemMessageProvider;


/**
 * Helper-Class that is able to import messages from a {@link MessageProvider} to a {@link MessageAcceptor}.
 * 
 * @author Marc Kannegiesser - kannegiesser@synyx.de
 */
public class Importer {

    private MessageProvider source;

    private MessageAcceptor target;


    public Importer(File basePath, MessageAcceptor target) {

        super();
        this.source = new FileSystemMessageProvider(basePath);
        this.target = target;
    }


    public Importer(MessageProvider source, MessageAcceptor target) {

        super();
        this.source = source;
        this.target = target;
    }


    public void importMessages(String basename) {

        target.setMessages(basename, source.getMessages(basename));
    }


    public void importMessages() {

        for (String basename : source.getAvailableBaseNames()) {
            importMessages(basename);
        }

        // TODO think about basenames that exist in target but not in source

    }
}
