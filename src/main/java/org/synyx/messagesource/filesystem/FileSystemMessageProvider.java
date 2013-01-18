/**
 *
 */
package org.synyx.messagesource.filesystem;

import org.synyx.messagesource.MessageAcceptor;
import org.synyx.messagesource.MessageProvider;
import org.synyx.messagesource.Messages;
import org.synyx.messagesource.util.LocaleUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


/**
 * {@link MessageProvider} for messages read from a directory. This acts also as a {@link MessageAcceptor} that can
 * write files to the given directory.
 *
 * @author  Marc Kannegiesser - kannegiesser@synyx.de
 */
public class FileSystemMessageProvider implements MessageProvider, MessageAcceptor {

    private static final String PROPERTY_FILE_LOADER_DEFAULT_ENCODING = "ISO-8859-1";

    private File baseDir;

    private String propertyFileLoaderEncoding = null;

    /**
     * Creates a new instance reading from the path represented by the given {@link String}.
     *
     * @param  basePath  the path to the directory to read from
     */
    public FileSystemMessageProvider(String basePath) {

        this(new File(basePath));
    }


    /**
     * Creates a new instance reading from the path represented by the given {@link File}.
     *
     * @param  baseDir  the path to the directory to read from
     */
    public FileSystemMessageProvider(File baseDir) {

        this(baseDir, PROPERTY_FILE_LOADER_DEFAULT_ENCODING);
    }


    /**
     * @param  baseDir
     * @param  propertyFileLoaderEncoding  - <br>
     *                                     default encoding- ISO-8859-1
     */
    public FileSystemMessageProvider(File baseDir, String propertyFileLoaderEncoding) {

        super();
        this.baseDir = baseDir;

        if (!baseDir.isDirectory()) {
            throw new IllegalArgumentException("Given basePath " + baseDir.getAbsolutePath()
                + " does not exist or is not a directory.");
        }

        if (propertyFileLoaderEncoding != null) {
            this.propertyFileLoaderEncoding = propertyFileLoaderEncoding;
        } else {
            this.propertyFileLoaderEncoding = PROPERTY_FILE_LOADER_DEFAULT_ENCODING;
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
     * Returns {@link BundleInfo}s for all files in the directory matching the given basename.
     *
     * @param  basename
     *
     * @return
     */
    private List<BundleInfo> resolveBundle(String basename) {

        List<BundleInfo> bundles = new ArrayList<BundleInfo>();

        File[] files = baseDir.listFiles(new BundleFilter(basename));

        for (File file : files) {
            bundles.add(new BundleInfo(file, basename, this.propertyFileLoaderEncoding));
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


    /*
     * (non-Javadoc)
     *
     * @see org.synyx.messagesource.MessageAcceptor#setMessages(java.lang.String, org.synyx.messagesource.Messages)
     */
    public void setMessages(String basename, Messages messages) {

        Set<Locale> locales = messages.getLocales();

        for (Locale locale : locales) {
            File file = getFileFor(basename, locale);

            if (file.isDirectory()) {
                throw new IllegalStateException("Cannot write messages for basename " + basename + " since "
                    + file.getAbsolutePath() + " exists and is a directory.");
            } else if (file.isFile()) {
                file.delete(); // TODO maybe backup this?
            }

            Map<String, String> msgs = messages.getMessages(locale);
            Properties properties = new Properties();

            for (Map.Entry<String, String> entry : msgs.entrySet()) {
                properties.setProperty(entry.getKey(), entry.getValue());
            }

            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(file);
                properties.store(fos, "messages for basename " + basename + " and locale " + locale);
            } catch (IOException e) {
                throw new RuntimeException("Could not store messages for basename " + basename + " and locale " + locale
                    + "to " + file.getAbsolutePath() + ": " + e.getMessage(), e);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        // intentionally left blank
                    }
                }
            }
        }
    }


    /**
     * Returns a (maybe not existing) file for the given basename and {@link Locale}.
     *
     * @param  basename
     * @param  locale
     *
     * @return
     */
    private File getFileFor(String basename, Locale locale) {

        String localePart = LocaleUtils.fromLocale(locale);

        if (localePart == null) {
            localePart = "";
        } else {
            localePart = "_" + localePart;
        }

        String fileName = String.format("%s%s.properties", basename, localePart);

        return new File(baseDir, fileName);
    }


    public static String getPropertyFileLoaderDefaultEncoding() {

        return PROPERTY_FILE_LOADER_DEFAULT_ENCODING;
    }
}
