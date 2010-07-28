/**
 * 
 */
package org.synyx.messagesource.filesystem;

import java.io.File;
import java.io.FilenameFilter;


/**
 * @author Marc Kannegiesser - kannegiesser@synyx.de
 */
class BundleFilter implements FilenameFilter {

    private String basename;


    public BundleFilter(String basename) {

        this.basename = basename;
    }


    public boolean accept(File dir, String name) {

        return name.startsWith(basename) && name.endsWith(".properties");
    }
}
