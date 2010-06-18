package org.synyx.messagesource;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractMessageSource;


/**
 * {@link MessageSource} implementation that is able to initialize itself using a {@link MessageProvider}. This
 * {@link MessageSource} is initializable using the initialize()-method. If this is called it uses the set
 * {@link MessageProvider} to retrieve all Messages for the set basename at once. This class caches messages and
 * resolving-paths and uses the resolving-strategy also the default {@link ResourceBundle} does. When resolving a
 * message the keys are tried to resolve in the following order:
 * <ul>
 * <li>given {@link Locale}s language + country + variant</li>
 * <li>given {@link Locale}s language + country</li>
 * <li>given {@link Locale}s language</li>
 * <li>VM-wide default {@link Locale}s language + country + variant</li>
 * <li>VM-wide default {@link Locale}s language + country</li>
 * <li>VM-wide default {@link Locale}s language</li>
 * <li>Default (basename)</li>
 * </ul>
 * 
 * @author Marc Kannegiesser - kannegiesser@synyx.de
 */
public class InitializableMessageSource extends AbstractMessageSource {

    protected Map<Locale, List<String>> resolvingPath = new HashMap<Locale, List<String>>();
    protected Map<String, Map<String, MessageFormat>> messages;
    protected Locale defaultLocale = Locale.getDefault();

    protected MessageProvider messageProvider;
    protected String basename = "message";


    /**
     * Initializes messages by retrieving them from the set {@link MessageProvider}
     */
    public void initialize() {

        messages = new HashMap<String, Map<String, MessageFormat>>();

        Map<Locale, Map<String, String>> localeToCodeToMessage = messageProvider.getMessages(basename);
        for (Locale locale : localeToCodeToMessage.keySet()) {
            Map<String, String> codeToMessage = localeToCodeToMessage.get(locale);
            for (String code : codeToMessage.keySet()) {
                addMessage(locale, code, createMessageFormat(codeToMessage.get(code), locale));
            }
        }
    }


    private void addMessage(Locale locale, String code, MessageFormat messageFormat) {

        String localeString = basename + "_" + (locale != null ? locale.toString() : "");
        Map<String, MessageFormat> codeMap = messages.get(localeString);
        if (codeMap == null) {
            codeMap = new HashMap<String, MessageFormat>();
            messages.put(localeString, codeMap);
        }

        codeMap.put(code, messageFormat);
    }


    protected Locale toLocale(String locale) {

        LocaleEditor led = new LocaleEditor();
        led.setAsText(locale);
        return (Locale) led.getValue();

    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.context.support.AbstractMessageSource#resolveCode(java.lang.String, java.util.Locale)
     */
    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {

        List<String> paths = getPath(locale);
        for (String loc : paths) {
            Map<String, MessageFormat> formatMap = messages.get(loc);
            if (formatMap != null) {
                MessageFormat format = formatMap.get(code);
                if (format != null) {
                    return format;
                }
            }
        }

        return null;
    }


    private List<String> getPath(Locale locale) {

        List<String> path = resolvingPath.get(locale);
        if (path == null) {
            path = new ArrayList<String>();
            String language = locale.getLanguage();
            String country = locale.getCountry();
            String variant = locale.getVariant();
            if (!variant.isEmpty()) {
                path.add(String.format("%s_%s_%s_%s", basename, language, country, variant));
            }
            if (!country.isEmpty()) {
                path.add(String.format("%s_%s_%s", basename, language, country));
            }
            if (!language.isEmpty()) {
                path.add(String.format("%s_%s", basename, language));
            }

            if (locale != getDefaultLocale()) {
                path.addAll(getPath(getDefaultLocale()));
                path.add(basename + "_");
            }

        }
        resolvingPath.put(locale, path);
        return path;
    }


    protected Locale getDefaultLocale() {

        return defaultLocale;
    }


    public void setDefaultLocale(Locale defaultLocale) {

        this.defaultLocale = defaultLocale;
    }


    public void setMessageProvider(MessageProvider messageProvider) {

        this.messageProvider = messageProvider;
    }


    public void setBasename(String basename) {

        this.basename = basename;
    }

}
