package org.synyx.messagesource.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.synyx.messagesource.MessageProvider;


public class JdbcMessageProvider implements MessageProvider {

    private JdbcTemplate template;

    private String languageColumn = "language";
    private String countryColumn = "country";
    private String variantColumn = "variant";
    private String basenameColumn = "basename";
    private String keyColumn = "key";
    private String messageColumn = "message";
    private String tableName = "Message";

    private final MessageExtractor extractor = new MessageExtractor();


    public Map<Locale, Map<String, String>> getMessages(String basename) {

        String query =
                String.format("select %s,%s,%s,%s,%s from %s where %s = %s",
                        languageColumn, countryColumn, variantColumn,
                        keyColumn, messageColumn, tableName, basenameColumn,
                        basename);
        return template.query(query, extractor);
    }

    class MessageExtractor implements
            ResultSetExtractor<Map<Locale, Map<String, String>>> {

        public Map<Locale, Map<String, String>> extractData(ResultSet rs)
                throws SQLException, DataAccessException {

            Map<Locale, Map<String, String>> map =
                    new HashMap<Locale, Map<String, String>>();

            while (rs.next()) {
                String language = rs.getString(languageColumn);
                String country = rs.getString(countryColumn);
                String variant = rs.getString(variantColumn);
                String key = rs.getString(keyColumn);
                String message = rs.getString(messageColumn);

                Locale locale = toLocale(language, country, variant);
                addToMap(locale, key, message, map);
            }

            return map;
        }


        protected Locale toLocale(String language, String country,
                String variant) {

            return new Locale(language, country, variant);

        }


        private void addToMap(Locale locale, String key, String message,
                Map<Locale, Map<String, String>> map) {

            Map<String, String> keyToMessage = map.get(locale);
            if (keyToMessage == null) {
                keyToMessage = new HashMap<String, String>();
                map.put(locale, keyToMessage);
            }
            keyToMessage.put(key, message);

        }
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

}
