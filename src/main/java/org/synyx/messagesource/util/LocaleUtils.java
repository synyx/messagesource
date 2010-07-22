/**
 * 
 */
package org.synyx.messagesource.util;

import java.util.Locale;

import org.springframework.beans.propertyeditors.LocaleEditor;


/**
 * @author Marc Kannegiesser - kannegiesser@synyx.de
 */
public class LocaleUtils {

    public static Locale toLocale(String locale) {

        if (locale == null) {
            return null;
        }
        LocaleEditor led = new LocaleEditor();
        led.setAsText(locale);
        return (Locale) led.getValue();
    }


    public static String fromLocale(Locale locale) {

        if (locale == null) {
            return null; // TODO check
        }
        LocaleEditor led = new LocaleEditor();
        led.setValue(locale);
        return led.getAsText();
    }


    public static Locale toLocale(String language, String country, String variant) {

        if (variant == null) {
            if (country == null) {
                if (language == null) {
                    return toLocale(null);
                }
                return toLocale(language);
            }
            return toLocale(String.format("%s_%s", language, country));
        }

        return toLocale(String.format("%s_%s_%s", language, country, variant));

    }


    /**
     * Prevent this from being instanciated
     */
    private LocaleUtils() {

    };
}
