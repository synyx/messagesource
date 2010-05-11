package org.synyx.messagesource;

import java.util.Locale;
import java.util.Map;


public interface MessageProvider {

    Map<Locale, Map<String, String>> getMessages(String basename);

}
