/**
 * 
 */
package org.synyx.messagesource.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.synyx.messagesource.MessageProvider;
import org.synyx.messagesource.Messages;
import org.synyx.messagesource.util.LocaleUtils;


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

    private static class BundleInfo {

        public BundleInfo(File file, String basename) {

            this.file = file;

            int prefixLength = basename.length();
            int postfixLength = ".properties".length();

            String fileName = file.getName();
            String localeString = fileName.substring(prefixLength, fileName.length() - postfixLength);

            locale = LocaleUtils.toLocale(localeString);
            // TODO Auto-generated constructor stub
        }


        /**
         * @return
         */
        public Map<String, String> getMessages() {

            Properties properties = loadProperties(file);
            Map<String, String> messages = new HashMap<String, String>(properties.size());

            Enumeration<Object> keys = properties.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = (String) properties.getProperty(key);
                messages.put(key, value);
            }

            return messages;
        }


        /**
         * Loads {@link Properties} from the given {@link File} while handling errors.
         */
        private Properties loadProperties(File file) {

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                Properties properties = new Properties();
                properties.load(fis);
                return properties;
            } catch (IOException e) {
                throw new RuntimeException("Could not load messages from " + file + ": " + e.getMessage(), e);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        // intentionally left blank
                    }
                }
            }

        }

        Locale locale;
        File file;
    }

    private static class BundleFilter implements FilenameFilter {

        private String basename;


        public BundleFilter(String basename) {

            this.basename = basename;
        }


        public boolean accept(File dir, String name) {

            return name.startsWith(basename) && name.endsWith(".properties");
        }
    };

}
