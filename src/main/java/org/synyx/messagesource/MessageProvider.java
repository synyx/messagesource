package org.synyx.messagesource;

import java.util.Collection;


public interface MessageProvider {

    Messages getMessages(String basename);


    Collection<String> getAvailableBaseNames();
}
