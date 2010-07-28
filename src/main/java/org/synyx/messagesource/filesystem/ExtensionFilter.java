/**
 * 
 */
package org.synyx.messagesource.filesystem;

import java.io.File;
import java.io.FilenameFilter;


/**
 * @author Marc Kannegiesser - kannegiesser@synyx.de
 */
class ExtensionFilter implements FilenameFilter {

    private String extension;


    public ExtensionFilter(String extension) {

        this.extension = extension;
    }


    public boolean accept(File dir, String name) {

        return name.endsWith("." + extension);
    }
}
