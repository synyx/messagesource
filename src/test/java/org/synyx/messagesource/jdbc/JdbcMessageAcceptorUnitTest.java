package org.synyx.messagesource.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.synyx.messagesource.Messages;
import org.synyx.messagesource.util.LocaleUtils;


public class JdbcMessageAcceptorUnitTest {

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


    @Test
    public void testHasMessage() {

        List<Locale> locales = new ArrayList<Locale>();
        locales.add(Locale.GERMAN);
        locales.add(new Locale("en", "US", "foo"));
        locales.add(Locale.GERMANY);
        locales.add(Locale.CANADA_FRENCH);
        locales.add(new Locale("en", "US", "bar"));

        Messages messages = new Messages();

        for (Locale locale : locales) {

            messages.addMessage(locale, "somekey", "somevalue" + locale.toString());
            messages.addMessage(locale, "anotherkey", "anothervalue" + locale.toString());

        }
        provider.setMessages("base", messages);

        for (Locale locale : locales) {

            assertMessage(locale.getLanguage(), locale.getCountry(), locale.getVariant(), "somekey", "somevalue"
                    + locale.toString());
            assertMessage(locale.getLanguage(), locale.getCountry(), locale.getVariant(), "anotherkey", "anothervalue"
                    + locale.toString());
        }

    }


    private void assertMessage(String lang, String country, String variant, String key, String message) {

        Messages messages = provider.getMessages("base");

        Locale locale = LocaleUtils.toLocale(lang, country, variant);

        String returnedMessage = messages.getMessage(locale, key);
        Assert.assertNotNull(returnedMessage);
        Assert.assertTrue(message.equals(returnedMessage));

    }


    @After
    public void after() {

        template.execute("DROP Table Message IF EXISTS");
    }

}
