/**
 * 
 */
package org.synyx.messagesource.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.util.StringUtils;


/**
 * Helper-Class containing util-methods to ease work with {@link Locale}s.
 * 
 * @author Marc Kannegiesser - kannegiesser@synyx.de
 */
public class LocaleUtils {

    /**
     * Prevent this from being instanciated
     */
    private LocaleUtils() {

    }


    /**
     * Creates a {@link Locale} from its String-representation.
     * 
     * @param locale the Locale-String
     * @return the {@link Locale}
     */
    public static Locale toLocale(String locale) {

        if (locale == null) {
            return null;
        }
        LocaleEditor led = new LocaleEditor();
        led.setAsText(locale);
        return (Locale) led.getValue();
    }


    /**
     * Returns a String-Representation from a {@link Locale}.
     * 
     * @param locale the {@link Locale} to turn into a String
     * @return the String representing the {@link Locale}
     */
    public static String fromLocale(Locale locale) {

        if (locale == null) {
            return null;
        }
        LocaleEditor led = new LocaleEditor();
        led.setValue(locale);
        return led.getAsText();
    }


    /**
     * Returns a {@link Locale} build from the given Strings with its parts while being aware of null-values.
     * 
     * @param language the language
     * @param country the country
     * @param variant the variant
     * @return a {@link Locale}
     */
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
     * Returns the language of the Locale or an empty String if no language
     * 
     * @param locale the Locale to return the language for
     * @return the language
     */
    public static String getLanguage(Locale locale) {

        if (locale != null && StringUtils.hasLength(locale.getLanguage())) {
            return locale.getLanguage();
        }
        return "";
    }


    /**
     * Returns the country of the Locale or an empty String if no country
     * 
     * @param locale the Locale to return the country for
     * @return the country
     */
    public static String getCountry(Locale locale) {

        if (locale != null && StringUtils.hasLength(locale.getCountry())) {
            return locale.getCountry();
        }
        return "";
    }


    /**
     * Returns the variant of the Locale or an empty String if no variant
     * 
     * @param locale the Locale to return the variant for
     * @return the variant
     */
    public static String getVariant(Locale locale) {

        if (locale != null && StringUtils.hasLength(locale.getVariant())) {
            return locale.getVariant();
        }
        return "";
    }


    /**
     * Returns the parent of a {@link Locale} or null if the locale has no parent. Examples:
     * <ul>
     * <li>de_DE would return de</li>
     * <li>de would return null</li>
     * </ul>
     * 
     * @param locale
     * @return
     */
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


    /**
     * Returns the Message-Resolving path of a given {@link Locale} and default- {@link Locale}: Example:
     * <ul>
     * <li>de_DE and en_US as default leads to: de_DE, de, en_US, en, null</li>
     * <li>de_DE and null as default leads to: de_DE, de, null</li>
     * <li>de_DE and de_DE as default leads to: de_DE, de, null</li>
     * </ul>
     * 
     * @param locale the {@link Locale} to get the path for
     * @param defaultLocale the default-locale to consider within the path
     * @return a {@link List} containing the path
     */
    public static List<Locale> getPath(Locale locale, Locale defaultLocale) {

        List<Locale> path = new ArrayList<Locale>();

        boolean localeWasNull = locale == null;

        // path down to only language (e.g. de_DE_POSIX -> de_DE -> de)
        while (locale != null) {
            path.add(locale);
            locale = getParent(locale);
        }

        if (!localeWasNull && locale != defaultLocale) {
            // path of default locale down to only language (e.g. en_US -> en )
            while (defaultLocale != null) {
                path.add(defaultLocale);
                defaultLocale = getParent(defaultLocale);
            }

        }
        // default locale
        path.add(null);

        return path;
    }

}
