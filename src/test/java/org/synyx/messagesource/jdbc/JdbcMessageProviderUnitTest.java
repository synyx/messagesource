package org.synyx.messagesource.jdbc;

import java.sql.SQLException;
import java.util.Locale;

import junit.framework.Assert;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.synyx.messagesource.Messages;
import org.synyx.messagesource.util.LocaleUtils;


public class JdbcMessageProviderUnitTest {

    private JdbcTemplate template;
    private JdbcMessageProvider provider;


    @Before
    public void before() throws ClassNotFoundException, SQLException {

        Class.forName("org.h2.Driver");

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:~/test");

        template = new JdbcTemplate(ds);

        template.execute("DROP Table Message IF EXISTS");
        template.execute("CREATE TABLE `Message` (" + "`basename` VARCHAR( 31 ) NOT NULL ,"
                + "`language` VARCHAR( 7 ) NULL ," + "`country` VARCHAR( 7 ) NULL ," + "`variant` VARCHAR( 7 ) NULL ,"
                + "`key` VARCHAR( 255 ) NULL ," + "`message` TEXT NULL" + ")");

        provider = new JdbcMessageProvider();
        provider.setDataSource(ds);

    }


    private void insertMessage(String lang, String country, String variant, String key, String message) {

        template.execute("INSERT INTO `Message` (`basename` ,`language` ,`country` ,`variant` ,`key` ,`message`) "
                + "VALUES ('base', '" + lang + "', '" + country + "', '" + variant + "', '" + key + "', '" + message
                + "');");

    }


    @Test
    public void testReturnsDefaultMessage() {

        insertMessage("foo", "bar");
        assertMessage(null, null, null, "foo", "bar");

    }


    @Test
    public void testReturnsLanguage() {

        insertMessage("de", "foo", "bar");
        assertMessage("de", null, null, "foo", "bar");

    }


    @Test
    public void testReturnsLanguageCountry() {

        insertMessage("de", "DE", "foo", "bar");
        assertMessage("de", "DE", null, "foo", "bar");

    }


    @Test
    public void testMoreMessages() {

        insertMessage("de", "DE", "foo", "bar");
        insertMessage("de", "DE", "key", "value");
        insertMessage("de", "foo2", "bar2");
        insertMessage("de", "key2", "value2");
        insertMessage("x", "y");

        assertMessage("de", "DE", null, "foo", "bar");
        assertMessage("de", "DE", null, "key", "value");
        assertMessage("de", null, null, "foo2", "bar2");
        assertMessage("de", null, null, "key2", "value2");
        assertMessage(null, null, null, "x", "y");

    }


    @Test
    public void testReturnsLanguageCountryVariant() {

        insertMessage("de", "DE", "X", "foo", "bar");
        assertMessage("de", "DE", "X", "foo", "bar");

    }


    private void assertMessage(String lang, String country, String variant, String key, String message) {

        Messages allMessages = provider.getMessages("base");

        Locale locale = LocaleUtils.toLocale(lang, country, variant);

        String returnedMessage = allMessages.getMessage(locale, key);

        Assert.assertNotNull(returnedMessage);
        Assert.assertTrue(message.equals(returnedMessage));

    }


    private void insertMessage(String lang, String country, String key, String message) {

        template.execute("INSERT INTO `Message` (`basename` ,`language` ,`country` ,`key` ,`message`) "
                + "VALUES ('base', '" + lang + "', '" + country + "', '" + key + "', '" + message + "');");

    }


    private void insertMessage(String lang, String key, String message) {

        template.execute("INSERT INTO `Message` (`basename` ,`language`,`key` ,`message`) " + "VALUES ('base', '"
                + lang + "', '" + key + "', '" + message + "');");

    }


    private void insertMessage(String key, String message) {

        template.execute("INSERT INTO `Message` (`basename` ,`key` ,`message`) " + "VALUES ('base', '" + key + "', '"
                + message + "');");

    }


    @After
    public void after() {

        template.execute("DROP Table Message IF EXISTS");
    }


    @Test
    public void testSetup() {

        insertMessage("key", "message");
        insertMessage("de", "key", "message");
        insertMessage("de", "de", "key", "message");
        insertMessage("de", "de", "POSIX", "key", "message");

    }
}
