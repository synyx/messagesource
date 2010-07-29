/**
 * 
 */
package org.synyx.messagesource.importer;

import java.io.File;
import java.io.PrintStream;

import org.synyx.messagesource.MessageAcceptor;
import org.synyx.messagesource.MessageProvider;
import org.synyx.messagesource.Messages;
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


    public static void main(String[] args) {

        PrintStream out = System.out;
        out.println("Messageimporter");
        if (args.length < 2) {
            System.err.println("Usage: MessageImporter <basepath> <basename> [<basename2> [ <basename3> ... ] ]");
        }
        String dir = null;
        for (String basename : args) {
            if (dir == null) {
                dir = basename;
                continue;
            }
            FileSystemMessageProvider importer = new FileSystemMessageProvider(new File(dir).getAbsolutePath());
            Messages messages = importer.getMessages(basename);

            printMessages(messages, basename, out);
        }
    }


    public void importMessages() {

        for (String basename : source.getAvailableBaseNames()) {
            importMessages(basename);
        }

        // TODO think about basenames that exist in target but not in source

    }
}
