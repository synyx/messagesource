/**
 * 
 */
package org.synyx.messagesource.importer;

import java.io.File;

import org.synyx.messagesource.MessageAcceptor;
import org.synyx.messagesource.MessageProvider;
import org.synyx.messagesource.filesystem.FileSystemMessageProvider;


/**
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

}
