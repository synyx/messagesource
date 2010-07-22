/**
 * 
 */
package org.synyx.messagesource;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * @author Marc Kannegiesser - kannegiesser@synyx.de
 */
public class Messages {

    private Map<Locale, Map<String, String>> messages;


    public Messages(Map<Locale, Map<String, String>> messages) {

        this.messages = messages;
    }


    public Messages() {

        this.messages = new HashMap<Locale, Map<String, String>>();
    }


    public Set<Locale> getLocales() {

        return messages.keySet();
    }


    public Map<String, String> getMessages(Locale locale) {

        return messages.get(locale);
    }


    public void setMessages(Locale locale, Map<String, String> map) {

        messages.put(locale, map);

    }


    public void addMessage(Locale locale, String key, String message) {

        Map<String, String> keyToMessage = messages.get(locale);
        if (keyToMessage == null) {
            keyToMessage = new HashMap<String, String>();
            messages.put(locale, keyToMessage);
        }
        keyToMessage.put(key, message);
    }


    public void removeMessage(Locale locale, String key) {

        Map<String, String> keyToMessage = messages.get(locale);
        if (keyToMessage == null) {
            return;
        }
        keyToMessage.remove(key);
    }


    /**
     * @param locale
     * @param key
     * @return
     */
    public String getMessage(Locale locale, String key) {

        Map<String, String> localeMessages = messages.get(locale);
        if (localeMessages == null) {
            return null;
        }
        return localeMessages.get(key);
    }

}
