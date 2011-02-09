/**
 * 
 */
package org.synyx.messagesource;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * Wrapper class that holds messages.
 * 
 * @author Marc Kannegiesser - kannegiesser@synyx.de
 */
public class Messages {

    private Map<Locale, Map<String, String>> messages;


    /**
     * Creates a new instance. The map might also contain a null-Value indicating default-messages (for all other
     * {@link Locale}s).
     * 
     * @param messages a {@link Map} of {@link Locale}s to a Map of key=value pairs (message-code to message)
     */
    public Messages(Map<Locale, Map<String, String>> messages) {

        this.messages = messages;
    }


    /**
     * Creates a new instance.
     */
    public Messages() {

        this.messages = new HashMap<Locale, Map<String, String>>();
    }


    /**
     * Returns all {@link Locale}s this instance holds messages for. This might also contain a null-Value indicating
     * default-messages (for all other {@link Locale}s).
     * 
     * @return all available Locales
     */
    public Set<Locale> getLocales() {

        return messages.keySet();
    }


    /**
     * Returns a {@link Map} containing message-code to message pairs for a given {@link Locale} or null if there are no
     * messages for the given {@link Locale}.
     * 
     * @param locale the {@link Locale} to return messages for
     * @return a {@link Map} containing messages (String, String).
     */
    public Map<String, String> getMessages(Locale locale) {

        return messages.get(locale);
    }


    /**
     * Sets messages (message-code to message) for a given {@link Locale}. The locale may be null indicating default
     * messages (for all other {@link Locale}s).
     * 
     * @param locale the locale to set messages for or null for default
     * @param map a {@link Map} of {@link String} to {@link String} containing messages for the given {@link Locale}
     */
    public void setMessages(Locale locale, Map<String, String> map) {

        messages.put(locale, map);

    }


    /**
     * Adds a message for a given {@link Locale} with the given message-code
     * 
     * @param locale the {@link Locale} to add the message for
     * @param key the {@link String} representing the key for the message
     * @param message the {@link String} representing the message itself
     */
    public void addMessage(Locale locale, String key, String message) {

        Map<String, String> keyToMessage = messages.get(locale);
        if (keyToMessage == null) {
            keyToMessage = new HashMap<String, String>();
            messages.put(locale, keyToMessage);
        }
        keyToMessage.put(key, message);
    }


    /**
     * Removes the message-value with the given message-code for the given {@link Locale}
     * 
     * @param locale the {@link Locale} to remove the message for
     * @param key the message-key to remove
     */
    public void removeMessage(Locale locale, String key) {

        Map<String, String> keyToMessage = messages.get(locale);
        if (keyToMessage == null) {
            return;
        }
        keyToMessage.remove(key);
    }


    /**
     * Returns the value of a given message-code for the given {@link Locale} or null if not found
     * 
     * @param locale the {@link Locale} to return the message for or null for default
     * @param key the message-code to return the message for
     * @return
     */
    public String getMessage(Locale locale, String key) {

        Map<String, String> localeMessages = messages.get(locale);
        if (localeMessages == null) {
            return null;
        }
        return localeMessages.get(key);
    }


    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        for (Locale locale : getLocales()) {
            String localeString = locale == null ? "default (base)" : locale.toString();
            buffer.append(String.format("%d messages for locale %s\n", getMessages(locale).size(), localeString));
        }
        return buffer.toString();

    }


    public String getFullInfo() {

        StringBuffer buffer = new StringBuffer();

        for (Locale locale : getLocales()) {
            String localeString = locale == null ? "default (base)" : locale.toString();
            buffer.append("Messages for locale " + localeString + "\n");

            buffer.append("====================================================================================\n");

            for (Map.Entry<String, String> msg : getMessages(locale).entrySet()) {
                String value = msg.getValue();
                if (value.contains("\n")) {
                    value = value.replace("\n", "\n  ");
                }
                buffer.append(String.format("%s=%s\n", msg.getKey(), value));

            }
            buffer.append("\n\n");
        }

        return buffer.toString();

    }


    /**
     * Returns true if the given message-code is defined for the given {@link Locale}.
     * 
     * @param locale the {@link Locale} to search the key for or null for default
     * @param key the message-code to search for
     * @return true if there is a message for the given {@link Locale} and message-code, false otherwise
     */
    public boolean hasMessage(Locale locale, String key) {

        return getMessage(locale, key) != null;
    }

}
