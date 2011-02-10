package org.synyx.messagesource.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.Assert;
import org.synyx.messagesource.MessageAcceptor;
import org.synyx.messagesource.MessageProvider;
import org.synyx.messagesource.Messages;
import org.synyx.messagesource.util.LocaleUtils;


/**
 * {@link MessageProvider} implementation that reads messages out of a database. The table to be used as well as the
 * names of the columns is configurable.
 * 
 * @author Marc Kannegießer - kannegiesser@synyx.de
 */
public class JdbcMessageProvider implements MessageProvider, MessageAcceptor {

    private static final String QUERY_INSERT =
            "INSERT INTO `%s` (`%s`, `%s`, `%s`, `%s`, `%s`, `%s`) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String QUERY_DELETE = "DELETE FROM `%s` WHERE `%s` = ?";
    private static final String QUERY_SELECT_BASENAMES = "SELECT DISTINCT `%s` from `%s`";
    private static final String QUERY_SELECT_MESSAGES = "SELECT `%s`,`%s`,`%s`,`%s`,`%s` FROM `%s` WHERE %s = ?";

    private JdbcTemplate template;

    private String languageColumn = "language";
    private String countryColumn = "country";
    private String variantColumn = "variant";
    private String basenameColumn = "basename";
    private String keyColumn = "key";
    private String messageColumn = "message";
    private String tableName = "Message";

    private final MessageExtractor extractor = new MessageExtractor();


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.messagesource.MessageProvider#getMessages(java.lang.String)
     */
    public Messages getMessages(String basename) {

        String query =
                String.format(QUERY_SELECT_MESSAGES, languageColumn, countryColumn, variantColumn, keyColumn,
                        messageColumn, tableName, basenameColumn);

        return template.query(query, new Object[] { basename }, extractor);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.messagesource.MessageAcceptor#setMessages(java.lang.String, org.synyx.messagesource.Messages)
     */
    public void setMessages(String basename, Messages messages) {

        deleteMessages(basename);

        String query =
                String.format(QUERY_INSERT, tableName, basenameColumn, languageColumn, countryColumn, variantColumn,
                        keyColumn, messageColumn);

        for (Locale locale : messages.getLocales()) {

            insert(query, basename, LocaleUtils.getLanguage(locale), LocaleUtils.getCountry(locale),
                    LocaleUtils.getVariant(locale), messages.getMessages(locale));

        }

    }


    private void insert(String query, final String basename, final String language, final String country,
            final String variant, final Map<String, String> messages) {

        final Iterator<Map.Entry<String, String>> messagesIterator = messages.entrySet().iterator();

        template.batchUpdate(query, new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {

                Map.Entry<String, String> entry = messagesIterator.next();
                ps.setString(1, basename);
                ps.setString(2, language);
                ps.setString(3, country);
                ps.setString(4, variant);
                ps.setString(5, entry.getKey());
                ps.setString(6, entry.getValue());

            }


            public int getBatchSize() {

                return messages.size();
            }
        });

    }


    private void deleteMessages(final String basename) {

        String query = String.format(QUERY_DELETE, tableName, basenameColumn);

        template.update(query, basename);

    }


    /**
     * Returns the name of the column holding the information about the language (string-type)
     * 
     * @return the name of the column holding the information about the language (string-type)
     */
    public String getLanguageColumn() {

        return languageColumn;
    }


    /**
     * Sets the name of the column holding the information about the language (string-type)
     * 
     * @param languageColumn the name of the language-column
     */
    public void setLanguageColumn(String languageColumn) {

        Assert.notNull(languageColumn);

        this.languageColumn = languageColumn;
    }


    /**
     * Returns the name of the column holding the information about the country (string-type)
     * 
     * @return the name of the column holding the information about the country (string-type)
     */
    public String getCountryColumn() {

        return countryColumn;
    }


    /**
     * Sets the name of the column holding the information about the country (string-type)
     * 
     * @param countryColumn the name of the country-column
     */
    public void setCountryColumn(String countryColumn) {

        Assert.notNull(countryColumn);

        this.countryColumn = countryColumn;
    }


    /**
     * Returns the name of the column holding the information about the variant (string-type)
     * 
     * @return the name of the column holding the information about the variant (string-type)
     */
    public String getVariantColumn() {

        return variantColumn;
    }


    /**
     * Sets the name of the column holding the information about the variant (string-type)
     * 
     * @param variantColumn the name of the variant-column
     */
    public void setVariantColumn(String variantColumn) {

        Assert.notNull(variantColumn);
        this.variantColumn = variantColumn;
    }


    /**
     * Returns the name of the column holding the information about the key (string-type)
     * 
     * @return the name of the column holding the information about the key (string-type)
     */
    public String getKeyColumn() {

        return keyColumn;
    }


    /**
     * Sets the name of the column holding the information about the key aka the name of the message-code (string-type)
     * 
     * @param keyColumn the name of the key-column
     */
    public void setKeyColumn(String keyColumn) {

        Assert.notNull(keyColumn);

        this.keyColumn = keyColumn;
    }


    /**
     * Returns the name of the column holding the information about the message (string-type)
     * 
     * @return the name of the column holding the information about the message (string-type)
     */
    public String getMessageColumn() {

        return messageColumn;
    }


    /**
     * Sets the name of the column holding the information about the message-value aka the message itself (string-type)
     * 
     * @param messageColumn the name of the message-column
     */
    public void setMessageColumn(String messageColumn) {

        Assert.notNull(messageColumn);
        this.messageColumn = messageColumn;
    }


    /**
     * Returns the name of the table containing the messages
     * 
     * @return the name of the table containing the messages
     */
    public String getTableName() {

        return tableName;
    }


    /**
     * Sets the name of the table containing the messages
     * 
     * @param tableName the name of the table containing the messages
     */
    public void setTableName(String tableName) {

        Assert.notNull(tableName);
        this.tableName = tableName;
    }


    /**
     * Sets the {@link DataSource} where connections can be created to the database containing the table with messages
     * 
     * @param dataSource the {@link DataSource} to set
     */
    public void setDataSource(DataSource dataSource) {

        Assert.notNull(dataSource);
        this.template = new JdbcTemplate(dataSource);
    }

    /*
     * Helper that extracts messages from a resultset
     */
    class MessageExtractor implements ResultSetExtractor<Messages> {

        public Messages extractData(ResultSet rs) throws SQLException, DataAccessException {

            Messages messages = new Messages();

            while (rs.next()) {
                String language = rs.getString(languageColumn);
                String country = rs.getString(countryColumn);
                String variant = rs.getString(variantColumn);
                String key = rs.getString(keyColumn);
                String message = rs.getString(messageColumn);

                Locale locale = LocaleUtils.toLocale(language, country, variant);
                messages.addMessage(locale, key, message);
            }

            return messages;
        }

    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.messagesource.MessageProvider#getAvailableBaseNames()
     */
    public List<String> getAvailableBaseNames() {

        List<String> basenames =
                template.queryForList(String.format(QUERY_SELECT_BASENAMES, basenameColumn, tableName), String.class);
        return basenames;
    }

}
