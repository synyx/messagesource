/**
 *
 */
package org.synyx.messagesource.filesystem;

import org.synyx.messagesource.util.LocaleUtils;

import java.io.*;

import java.util.*;


/**
 * Helper-Class that holds informations about a Bundle.
 *
 * @author  Marc Kannegiesser - kannegiesser@synyx.de
 */

class BundleInfo {

    Locale locale;
    File file;

    private String propertyFileLoaderEncoding = null;

    /**
     * Creates a new instance.
     *
     * @param  file
     * @param  basename
     */
    public BundleInfo(File file, String basename) {

        this(file, basename, FileSystemMessageProvider.getPropertyFileLoaderDefaultEncoding());
    }


    public BundleInfo(File file, String basename, String propertyFileLoaderEncoding) {

        this.file = file;

        int prefixLength = basename.length();
        int postfixLength = ".properties".length();

        String fileName = file.getName();
        String localeString = fileName.substring(prefixLength, fileName.length() - postfixLength);

        locale = LocaleUtils.toLocale(localeString);
        this.propertyFileLoaderEncoding = propertyFileLoaderEncoding;
    }

    /**
     * Returns a {@link Map} containing the messages for the given file.
     *
     * @return  the messages for the given bundle
     */
    public Map<String, String> getMessages() {

        Properties properties = loadProperties(file, this.propertyFileLoaderEncoding);
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
     *
     * @param  file  the file to load from
     *
     * @return  the {@link Properties} loaded
     */

    private Properties loadProperties(File file, String encoding) {

        FileInputStream fis = null;

        try {
            Properties properties = new Properties();

            if (encoding == null) {
                fis = new FileInputStream(file);
                properties.load(fis);
            } else {
                Reader in = new InputStreamReader(new FileInputStream(file), encoding);
                properties.load(in);
            }

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
}
