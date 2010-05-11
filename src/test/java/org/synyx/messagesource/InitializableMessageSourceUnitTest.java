package org.synyx.messagesource;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class InitializableMessageSourceUnitTest {

    private Locale defaultLocale;

    private MessageProvider messageProvider;

    Map<Locale, Map<String, String>> messages;

    InitializableMessageSource messageSource;

    private String basename = "foo";


    @Before
    public void init() {

        defaultLocale = new Locale("de", "DE");

        messages = new HashMap<Locale, Map<String, String>>();
        messageSource = new InitializableMessageSource();
        messageSource.setDefaultLocale(defaultLocale);
        messageSource.setBasename(basename);
        messageProvider = Mockito.mock(MessageProvider.class);
        Mockito.when(messageProvider.getMessages(basename))
                .thenReturn(messages);
        messageSource.setMessageProvider(messageProvider);

    }


    @Test
    public void fetchesMessagesFromProvider() {

        messageSource.initialize();
        Mockito.verify(messageProvider, Mockito.times(1)).getMessages(basename);
    }


    @Test
    public void resolvesMessage() {

        addMessage(Locale.GERMAN, "foo", "bar");
        messageSource.initialize();
        String resolved =
                messageSource.getMessage("foo", new Object[] {}, Locale.GERMAN);
        Assert.assertEquals("bar", resolved);
    }


    @Test
    public void resolvesMessageCascade() {

        addMessage(new Locale("en"), "foo", "bar");
        messageSource.initialize();
        String resolved =
                messageSource.getMessage("foo", new Object[] {}, new Locale(
                        "en", "GB", "foo"));
        Assert.assertEquals("bar", resolved);
    }


    @Test
    public void resolvesMessageCascadeToDefault() {

        addMessage(defaultLocale, "foo", "bar");
        messageSource.initialize();
        String resolved =
                messageSource.getMessage("foo", new Object[] {}, new Locale(
                        "en", "GB", "foo"));
        Assert.assertEquals("bar", resolved);
    }


    @Test
    public void resolvesMessageCascadeToBase() {

        addMessage(null, "foo", "bar");
        messageSource.initialize();
        String resolved =
                messageSource.getMessage("foo", new Object[] {}, new Locale(
                        "en", "GB", "foo"));
        Assert.assertEquals("bar", resolved);
    }


    private void addMessage(Locale locale, String key, String value) {

        Map<String, String> map = messages.get(locale);
        if (map == null) {
            map = new HashMap<String, String>();
            messages.put(locale, map);
        }
        map.put(key, value);

    }
}
