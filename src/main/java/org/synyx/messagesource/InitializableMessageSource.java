package org.synyx.messagesource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.util.Assert;
import org.synyx.messagesource.util.LocaleUtils;
import org.synyx.messagesource.util.MessageInitializationException;

import java.text.MessageFormat;
import java.util.*;


/**
 * {@link MessageSource} implementation that is able to initialize itself using a {@link MessageProvider}. This
 * {@link MessageSource} is initializable using the initialize()-method. If this is called it uses the set
 * {@link MessageProvider} to retrieve all Messages for the set basename at once. This class caches messages and
 * resolving-paths and uses the resolving-strategy also the default {@link ResourceBundle} does. When resolving a
 * message the keys are tried to resolve in the following order:
 * <ul>
 * <li>given {@link Locale}s language + country + variant</li>
 * <li>given {@link Locale}s language + country</li>
 * <li>given {@link Locale}s language</li>
 * <li>default {@link Locale}s language + country + variant (property defaultLocale, if not null)</li>
 * <li>default {@link Locale}s language + country (property defaultLocale, if not null)</li>
 * <li>default {@link Locale}s language (property defaultLocale, if not null)</li>
 * <li>Default (basename)</li>
 * </ul>
 *
 * <p>You may set no defaultLocale which leads to resolving without the lines above containing (property defaultLocale,
 * if not null).</p>
 *
 * <p>You must set a {@link MessageProvider} which gets called to read all the messages at once.</p>
 *
 * <p>You may set a basename or a List of basenames explicitly. If you do so this only messages for this basename(s) are
 * resolved (and read from the {@link MessageProvider}). If you do not provide a basename all the messages delivered
 * from the {@link MessageProvider} are used.</p>
 *
 * @author  Marc Kannegiesser - kannegiesser@synyx.de
 */
public class InitializableMessageSource extends AbstractMessageSource implements InitializingBean {

    protected Map<Locale, List<String>> resolvingPath = new HashMap<Locale, List<String>>();
    protected Map<String, Map<String, MessageFormat>> messages;
    protected Locale defaultLocale;
    protected MessageProvider messageProvider;
    protected Boolean returnUnresolvedCode = false;
    protected List<String> basenames = new ArrayList<String>();

    /**
     * If this property is set to true this initializes post-construction (spring lifecycle interface).
     */
    protected boolean autoInitialize = true;

    /**
     * Property that indicates if all basenames returned from the {@link MessageProvider} should be used (=false) or the
     * ones explicitely set using {@link #setBasename(String)} or {@link #setBasenames(List)}.
     */
    protected boolean basenameRestriction = false;

    /**
     * Initializes messages by retrieving them from the set {@link MessageProvider}. This also leads to a reset of the
     * resolving-paths used to cache lookup-paths for messages
     */
    public void initialize() {

        // reset the path-cache (default-locale could have been changed)
        resolvingPath = new HashMap<Locale, List<String>>();

        if (!basenameRestriction) {
            basenames = new ArrayList<String>();
            basenames.addAll(messageProvider.getAvailableBaseNames());
        }

        messages = new HashMap<String, Map<String, MessageFormat>>();

        for (String basename : basenames) {
            initialize(basename);
        }
    }


    /**
     * Reads all messages from the {@link MessageProvider} for the given Basename.
     *
     * @param  basename  the basename to initialize messages for
     */
    protected void initialize(String basename) {

        initializeMessages(basename);
    }

    protected void initializeMessages(String basename) throws RuntimeException {

        Messages messagesForBasename = messageProvider.getMessages(basename);

        for (Locale locale : messagesForBasename.getLocales()) {
            Map<String, String> codeToMessage = messagesForBasename.getMessages(locale);

            for (String code : codeToMessage.keySet()) {
                try {
                    addMessage(basename, locale, code, createMessageFormat(codeToMessage.get(code), locale));
                } catch (RuntimeException e) {
                    throw new MessageInitializationException(String.format(
                            "Error processing Message code=%s locale=%s basename=%s, %s", code, locale, basename,
                            e.getMessage()), e);
                }
            }
        }
    }


    private void addMessage(String basename, Locale locale, String code, MessageFormat messageFormat) {

        String localeString = basename + "_" + (locale != null ? locale.toString() : "");
        Map<String, MessageFormat> codeMap = messages.get(localeString);

        if (codeMap == null) {
            codeMap = new HashMap<String, MessageFormat>();
            messages.put(localeString, codeMap);
        }

        codeMap.put(code, messageFormat);
    }


    /*
     * (non-Javadoc)
     *
     * @see org.springframework.context.support.AbstractMessageSource#resolveCode(java.lang.String, java.util.Locale)
     */
    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {

        for (String basename : basenames) {
            List<String> paths = getPath(locale);
            for (String loc : paths) {
                Map<String, MessageFormat> formatMap = messages.get(basename + loc);
                if (formatMap != null) {
                    MessageFormat format = formatMap.get(code);
                    if (format != null) {
                    	if(format.getLocale()==null){
                            format.setLocale(defaultLocale);
                          }
                        return format;
                    }
                }
            }
        }

        if (getReturnUnresolvedCode()) {
            return createMessageFormat(code, locale);
        } else {
            return null;
        }
    }


    private List<String> getPath(Locale locale) {

        List<String> path = resolvingPath.get(locale);
        if (path == null) {
            path = new ArrayList<String>();

            List<Locale> localePath = LocaleUtils.getPath(locale, getDefaultLocale());
            for (Locale loc : localePath) {
                if (loc == null) {
                    path.add("_");
                } else {

                    String language = LocaleUtils.getLanguage(loc);
                    String country = LocaleUtils.getCountry(loc);
                    String variant = LocaleUtils.getVariant(loc);
                    if (!variant.isEmpty()) {
                        path.add(String.format("_%s_%s_%s", language, country, variant));
                    } else if (!country.isEmpty()) {
                        path.add(String.format("_%s_%s", language, country));
                    } else if (!language.isEmpty()) {
                        path.add(String.format("_%s", language));
                    }
                }

            }

            resolvingPath.put(locale, path);
        }
        return path;
    }


    public Locale getDefaultLocale() {

        return defaultLocale;
    }


    /**
     * Sets the default {@link Locale} used during message-resolving. If for a given Locale the message is not found the
     * message gets looked up for the default-locale. If the message is not found then the "base-message" is used. This
     * is allowed to be null which then means "no default locale"
     *
     * @param  defaultLocale  the Locale to use as default or null if no default-locale should be used
     */
    public void setDefaultLocale(Locale defaultLocale) {

        this.defaultLocale = defaultLocale;
    }


    /**
     * Sets the {@link MessageProvider} for this which is asked for all its Messages during initialisation.
     *
     * @param  messageProvider  the {@link MessageProvider} to use
     */
    public void setMessageProvider(MessageProvider messageProvider) {

        Assert.notNull(messageProvider);

        this.messageProvider = messageProvider;
    }


    /**
     * Sets a single basename for this. This cannot be used in combination with {@link #setBasenames(List)}. If neither
     * {@link #setBasename(String)} nor {@link #setBasenames(List)} is called the basenames are looked up from the
     * {@link MessageProvider}
     *
     * @param  basename  the single basename to use for this instance
     */
    public void setBasename(String basename) {

        basenameRestriction = true;
        this.basenames = new ArrayList<String>();
        basenames.add(basename);
    }


    /**
     * Sets a {@link List} of basenames to use for this instance. This cannot be used in combination with
     * {@link #setBasename(String)}. If neither {@link #setBasename(String)} nor {@link #setBasenames(List)} is called
     * the basenames are looked up from the {@link MessageProvider}
     *
     * @param  basenames  the {@link List} of basenames
     */
    public void setBasenames(List<String> basenames) {

        Assert.notNull(basenames);

        if (!basenames.isEmpty()) {
            basenameRestriction = true;
            this.basenames = basenames;
        }
    }


    /**
     * Callback to call {@link #initialize()} after construction of this using a Spring-Callback.
     */
    public void afterPropertiesSet() throws Exception {

        if (autoInitialize) {
            initialize();
        }

    }


    /**
     * Sets the.
     *
     * @param  autoInitialize
     */
    public void setAutoInitialize(boolean autoInitialize) {

        this.autoInitialize = autoInitialize;
    }


    /**
     * @return  <br>
     *          Default value is false.If message could not be resolved returns null<br>
     *          if set to true- will return message code if the message could not be resolved
     */
    public Boolean getReturnUnresolvedCode() {

        return this.returnUnresolvedCode;
    }


    /**
     * @param  returnUnresolvedCode  -<br>
     *                               Default value is false.If message could not be resolved returns null<br>
     *                               if set to true- will return message code if the message could not be resolved
     */
    public void setReturnUnresolvedCode(Boolean returnUnresolvedCode) {

        this.returnUnresolvedCode = returnUnresolvedCode;
    }
}
