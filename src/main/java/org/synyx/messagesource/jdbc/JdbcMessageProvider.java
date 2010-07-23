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
import org.synyx.messagesource.MessageAcceptor;
import org.synyx.messagesource.MessageProvider;
import org.synyx.messagesource.Messages;
import org.synyx.messagesource.util.LocaleUtils;


public class JdbcMessageProvider implements MessageProvider, MessageAcceptor {

    private JdbcTemplate template;

    private String languageColumn = "language";
    private String countryColumn = "country";
    private String variantColumn = "variant";
    private String basenameColumn = "basename";
    private String keyColumn = "key";
    private String messageColumn = "message";
    private String tableName = "Message";

    private final MessageExtractor extractor = new MessageExtractor();


    public Messages getMessages(String basename) {

        String query =
                String.format("select `%s`,`%s`,`%s`,`%s`,`%s` from `%s` where %s = '%s'", languageColumn,
                        countryColumn, variantColumn, keyColumn, messageColumn, tableName, basenameColumn, basename);
        return template.query(query, extractor);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.messagesource.MessageAcceptor#setMessages(java.lang.String, org.synyx.messagesource.Messages)
     */
    public void setMessages(String basename, Messages messages) {

        deleteMessages(basename);

        String query =
                String.format("insert into `%s` (`%s`, `%s`, `%s`, `%s`, `%s`, `%s`) VALUES (?, ?, ?, ?, ?, ?)",
                        tableName, basenameColumn, languageColumn, countryColumn, variantColumn, keyColumn,
                        messageColumn);

        for (Locale locale : messages.getLocales()) {

            insert(query, basename, LocaleUtils.getLanguage(locale), LocaleUtils.getCountry(locale), LocaleUtils
                    .getVariant(locale), messages.getMessages(locale));

        }

    }


    /**
     * @param basename
     * @param language
     * @param country
     * @param variant
     * @param key
     * @param message
     */
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


    private void deleteMessages(String basename) {

        template.execute(String.format("delete from `%s` where `%s` = '%s'", tableName, basenameColumn, basename));
    }


    public String getLanguageColumn() {

        return languageColumn;
    }


    public void setLanguageColumn(String languageColumn) {

        this.languageColumn = languageColumn;
    }


    public String getCountryColumn() {

        return countryColumn;
    }


    public void setCountryColumn(String countryColumn) {

        this.countryColumn = countryColumn;
    }


    public String getVariantColumn() {

        return variantColumn;
    }


    public void setVariantColumn(String variantColumn) {

        this.variantColumn = variantColumn;
    }


    public String getKeyColumn() {

        return keyColumn;
    }


    public void setKeyColumn(String keyColumn) {

        this.keyColumn = keyColumn;
    }


    public String getMessageColumn() {

        return messageColumn;
    }


    public void setMessageColumn(String messageColumn) {

        this.messageColumn = messageColumn;
    }


    public String getTableName() {

        return tableName;
    }


    public void setTableName(String tableName) {

        this.tableName = tableName;
    }


    public void setDataSource(DataSource dataSource) {

        this.template = new JdbcTemplate(dataSource);
    }

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
                template.queryForList(String.format("select distinct `%s` from `%s`", basenameColumn, tableName),
                        String.class);
        return basenames;
    }

}
