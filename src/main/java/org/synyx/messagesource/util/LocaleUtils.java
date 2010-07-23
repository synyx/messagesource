/**
 * 
 */
package org.synyx.messagesource.util;

import java.util.Locale;

import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.util.StringUtils;


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

    }


    /**
     * @param locale
     * @return
     */
    public static String getLanguage(Locale locale) {

        if (locale != null && StringUtils.hasLength(locale.getLanguage())) {
            return locale.getLanguage();
        }
        return "";
    }


    /**
     * @param locale
     * @return
     */
    public static String getCountry(Locale locale) {

        if (locale != null && StringUtils.hasLength(locale.getCountry())) {
            return locale.getCountry();
        }
        return "";
    }


    /**
     * @param locale
     * @return
     */
    public static String getVariant(Locale locale) {

        if (locale != null && StringUtils.hasLength(locale.getVariant())) {
            return locale.getVariant();
        }
        return "";
    }


    public static Locale getParent(Locale locale) {

        if (locale == null) {
            return null;
        }
        if (StringUtils.hasLength(locale.getVariant())) {
            return new Locale(locale.getLanguage(), locale.getCountry());
        } else if (StringUtils.hasLength(locale.getCountry())) {
            return new Locale(locale.getLanguage());
        } else {
            return null;
        }
    }
}
