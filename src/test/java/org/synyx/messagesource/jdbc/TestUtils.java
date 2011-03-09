package org.synyx.messagesource.jdbc;

import java.util.Locale;

import junit.framework.Assert;

import org.springframework.jdbc.core.JdbcTemplate;
import org.synyx.messagesource.MessageProvider;
import org.synyx.messagesource.Messages;
import org.synyx.messagesource.util.LocaleUtils;


public abstract class TestUtils {

    public static void assertMessage(MessageProvider provider, String lang, String country, String variant, String key,
            String message) {

        Messages allMessages = provider.getMessages("base");

        Locale locale = LocaleUtils.toLocale(lang, country, variant);

        String returnedMessage = allMessages.getMessage(locale, key);

        Assert.assertNotNull(returnedMessage);
        Assert.assertTrue(message.equals(returnedMessage));

    }


    public static void insertMessage(JdbcTemplate template, String lang, String country, String variant, String key,
            String message) {

        template.execute("INSERT INTO `Message` (`basename` ,`language` ,`country` ,`variant` ,`key` ,`message`) "
                + "VALUES ('base', '" + lang + "', '" + country + "', '" + variant + "', '" + key + "', '" + message
                + "');");

    }


    public static void insertMessage(JdbcTemplate template, String lang, String country, String key, String message) {

        template.execute("INSERT INTO `Message` (`basename` ,`language` ,`country` ,`key` ,`message`) "
                + "VALUES ('base', '" + lang + "', '" + country + "', '" + key + "', '" + message + "');");

    }


    public static void insertMessage(JdbcTemplate template, String lang, String key, String message) {

        template.execute("INSERT INTO `Message` (`basename` ,`language`,`key` ,`message`) " + "VALUES ('base', '"
                + lang + "', '" + key + "', '" + message + "');");

    }


    public static void insertMessage(JdbcTemplate template, String key, String message) {

        template.execute("INSERT INTO `Message` (`basename` ,`key` ,`message`) " + "VALUES ('base', '" + key + "', '"
                + message + "');");

    }

}
