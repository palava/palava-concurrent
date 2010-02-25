package de.cosmocode.palava.concurrent;

import java.lang.annotation.Annotation;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * This module can be used to rebind general scheduler
 * configuration keys to specific ones.
 *
 * @author Willi Schoenborn
 */
public final class SchedulerModule extends PrivateModule {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerModule.class);

    private final Key<ScheduledExecutorService> key;
    
    private final String name;
    
    public SchedulerModule(Class<? extends Annotation> annotation, String name) {
        Preconditions.checkNotNull(annotation, "Annotation");
        this.key = Key.get(ScheduledExecutorService.class, annotation);
        this.name = Preconditions.checkNotNull(name, "Name");
    }
    
    public SchedulerModule(Annotation annotation, String name) {
        Preconditions.checkNotNull(annotation, "Annotation");
        this.key = Key.get(ScheduledExecutorService.class, annotation);
        this.name = Preconditions.checkNotNull(name, "Name");
    }

    @Override
    protected void configure() {
        LOG.trace("Binding scheduler configuration for {} using name {}", key, name);
        
        final ExecutorConfig config = ExecutorConfigs.named(name);

        bind(int.class).annotatedWith(Names.named(ExecutorServiceConfig.MIN_POOL_SIZE)).to(
            Key.get(int.class, Names.named(config.minPoolSize())));
        
        bind(long.class).annotatedWith(Names.named(ExecutorServiceConfig.SHUTDOWN_TIMEOUT)).to(
            Key.get(long.class, Names.named(config.shutdownTimeout())));
        
        bind(TimeUnit.class).annotatedWith(Names.named(ExecutorServiceConfig.SHUTDOWN_TIMEOUT_UNIT)).to(
            Key.get(TimeUnit.class, Names.named(config.shutdownTimeoutUnit())));
        
        bind(key).to(ConfigurableScheduledExecutorService.class).in(Singleton.class);
        expose(key);
    }

}
