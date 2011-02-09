/**
 * 
 */
package org.synyx.messagesource.filesystem;

import java.io.File;
import java.io.FilenameFilter;


/**
 * {@link FilenameFilter} that matches files starting with the basename-property and ending with .properties. Example:
 * if basename is "foo" this would return true for files like
 * <ul>
 * <li>foo_de.properties</li>
 * <li>foo_en_US.properties</li>
 * </ul>
 * 
 * @author Marc Kannegiesser - kannegiesser@synyx.de
 */
class BundleFilter implements FilenameFilter {

    private String basename;


    /**
     * Creates a new instance
     * 
     * @param basename the basename (prefix) of the files to return true for
     */
    public BundleFilter(String basename) {

        this.basename = basename;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(File dir, String name) {

        return name.startsWith(basename) && name.endsWith(".properties");
    }
}
