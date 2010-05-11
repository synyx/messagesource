package org.synyx.messagesource;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.context.support.AbstractMessageSource;


public class InitializableMessageSource extends AbstractMessageSource {

    protected Map<Locale, List<String>> resolvingPath =
            new HashMap<Locale, List<String>>();
    protected Map<String, Map<String, MessageFormat>> messages;
    protected Locale defaultLocale = Locale.getDefault();

    protected MessageProvider messageProvider;
    private String basename = "message";


    public void initialize() {

        messages = new HashMap<String, Map<String, MessageFormat>>();

        Map<Locale, Map<String, String>> localeToCodeToMessage =
                messageProvider.getMessages(basename);
        for (Locale locale : localeToCodeToMessage.keySet()) {
            Map<String, String> codeToMessage =
                    localeToCodeToMessage.get(locale);
            for (String code : codeToMessage.keySet()) {
                addMessage(locale, code, createMessageFormat(codeToMessage
                        .get(code), locale));
            }
        }
    }


    private void addMessage(Locale locale, String code,
            MessageFormat messageFormat) {

        String localeString =
                basename + "_" + (locale != null ? locale.toString() : "");
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
                path.add(String.format("%s_%s_%s_%s", basename, language,
                        country, variant));
            }
            if (!country.isEmpty()) {
                path
                        .add(String.format("%s_%s_%s", basename, language,
                                country));
            }
            if (!language.isEmpty()) {
                path.add(String.format("%s_%s", basename, language));
            }

            if (locale != getDefaultLocale()) {
                path.addAll(getPath(getDefaultLocale()));
                path.add(basename + "_");
            }

        }
        return path;
    }


    protected Locale getDefaultLocale() {

        return defaultLocale;
    }


    public void setDefaultLocale(Locale defaultLocale) {

        this.defaultLocale = defaultLocale;
    }


    public MessageProvider getMessageProvider() {

        return messageProvider;
    }


    public void setMessageProvider(MessageProvider messageProvider) {

        this.messageProvider = messageProvider;
    }


    public String getBasename() {

        return basename;
    }


    public void setBasename(String basename) {

        this.basename = basename;
    }

}
