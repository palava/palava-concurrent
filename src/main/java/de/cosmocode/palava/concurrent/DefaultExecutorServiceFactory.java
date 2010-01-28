package de.cosmocode.palava.concurrent;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.internal.Maps;

import de.cosmocode.palava.core.Settings;

/**
 * 
 *
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultExecutorServiceFactory implements ExecutorServiceFactory {

    private static final Logger log = LoggerFactory.getLogger(DefaultExecutorServiceFactory.class);

    private final Map<String, ExecutorService> cache = Maps.newHashMap();
    
    private final Map<String, String> settings;
    
    private final Provider<ExecutorBuilder> provider;
    
    @Inject
    public DefaultExecutorServiceFactory(
        @Settings Map<String, String> settings,
        Provider<ExecutorBuilder> provider) {
        this.settings = Preconditions.checkNotNull(settings, "Settings");
        this.provider = Preconditions.checkNotNull(provider, "Provider");
    }
    
    @Override
    public ExecutorService create(String group) {
        // TODO check cache
        
        final ExecutorBuilder builder = provider.get();
        
        // TODO read settings
        
        // TODO call setter on builder
        
        // TODO add to cache
        return builder.build();
    }

}
