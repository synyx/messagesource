/**
 * 
 */
package org.synyx.messagesource.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.synyx.messagesource.MessageProvider;
import org.synyx.messagesource.Messages;


/**
 * @author Marc Kannegiesser - kannegiesser@synyx.de
 */
public class FileSystemMessageProvider implements MessageProvider {

    private File baseDir;


    public FileSystemMessageProvider(String basePath) {

        this(new File(basePath));

    }


    public FileSystemMessageProvider(File baseDir) {

        super();
        this.baseDir = baseDir;
        if (!baseDir.isDirectory()) {
            throw new IllegalArgumentException("Given basePath " + baseDir.getAbsolutePath()
                    + " does not exist or is not a directory.");
        }

    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.messagesource.MessageProvider#getMessages(java.lang.String)
     */
    public Messages getMessages(String basename) {

        Messages messages = new Messages();

        List<BundleInfo> bundleInfos = resolveBundle(basename);

        for (BundleInfo bundleInfo : bundleInfos) {
            Map<String, String> map = bundleInfo.getMessages();
            messages.setMessages(bundleInfo.locale, map);

        }

        return messages;
    }


    /**
     * @param basename
     * @return
     */
    private List<BundleInfo> resolveBundle(String basename) {

        List<BundleInfo> bundles = new ArrayList<BundleInfo>();

        File[] files = baseDir.listFiles(new BundleFilter(basename));

        for (File file : files) {
            bundles.add(new BundleInfo(file, basename));
        }
        return bundles;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.messagesource.MessageProvider#getAvailableBaseNames()
     */
    public Collection<String> getAvailableBaseNames() {

        File[] files = baseDir.listFiles(new ExtensionFilter("properties"));

        Set<String> basenames = new HashSet<String>();
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.contains("_")) {
                int underscorePos = fileName.indexOf("_");
                fileName = fileName.substring(0, underscorePos);
            } else {
                int dotPos = fileName.indexOf(".");
                fileName = fileName.substring(0, dotPos);
            }
            basenames.add(fileName);
        }

        return basenames;
    }

}
