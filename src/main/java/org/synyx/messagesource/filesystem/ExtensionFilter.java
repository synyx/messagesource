/**
 * 
 */
package org.synyx.messagesource.filesystem;

import java.io.File;
import java.io.FilenameFilter;


/**
 * {@link FilenameFilter} that matches files ending with a given extension.
 * 
 * @author Marc Kannegiesser - kannegiesser@synyx.de
 */
class ExtensionFilter implements FilenameFilter {

    private String extension;


    /**
     * Creates a new instance
     * 
     * @param extension the extension to match for (without the dot .).
     */
    public ExtensionFilter(String extension) {

        this.extension = extension;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(File dir, String name) {

        return name.endsWith("." + extension);
    }
}
