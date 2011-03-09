package org.synyx.messagesource.jdbc;

import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;


public class JdbcMessageProviderDelimiterUnitTest {

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
    public void worksWithNoDelimiter() {

        provider.setDelimiter("");
        testMessages();
    }


    @Test(expected = IllegalArgumentException.class)
    public void failsWithNullDelimiter() {

        provider.setDelimiter(null);
    }


    @Test
    public void worksWithBackTickDelimiter() {

        provider.setDelimiter("`");
        testMessages();
    }


    @Test
    public void worksWithSpaceDelimiter() {

        provider.setDelimiter(" ");
        testMessages();
    }


    @Test(expected = BadSqlGrammarException.class)
    public void failsWithWrongDelimiter() {

        provider.setDelimiter("'");
        testMessages();
    }


    public void testMessages() {

        TestUtils.insertMessage(template, "de", "DE", "foo", "bar");
        TestUtils.insertMessage(template, "de", "DE", "key", "value");
        TestUtils.insertMessage(template, "de", "foo2", "bar2");
        TestUtils.insertMessage(template, "de", "key2", "value2");
        TestUtils.insertMessage(template, "x", "y");

        TestUtils.assertMessage(provider, "de", "DE", null, "foo", "bar");
        TestUtils.assertMessage(provider, "de", "DE", null, "key", "value");
        TestUtils.assertMessage(provider, "de", null, null, "foo2", "bar2");
        TestUtils.assertMessage(provider, "de", null, null, "key2", "value2");
        TestUtils.assertMessage(provider, null, null, null, "x", "y");

    }


    @After
    public void after() {

        template.execute("DROP Table Message IF EXISTS");
    }

}
