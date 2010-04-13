package de.cosmocode.palava.concurrent;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.collections.Procedure;
import de.cosmocode.palava.core.ForwardingRegistry;
import de.cosmocode.palava.core.Registry;

/**
 * Default implementation of the {@link ConcurrentRegistry} interface.
 *
 * @author Willi Schoenborn
 */
final class DefaultConcurrentRegistry extends ForwardingRegistry implements ConcurrentRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConcurrentRegistry.class);
    
    private final Registry registry;
    
    private final ExecutorService executor;
    
    @Inject
    public DefaultConcurrentRegistry(Registry registry, @BackgroundNotifier ExecutorService executor) {
        this.registry = Preconditions.checkNotNull(registry, "Registry");
        this.executor = Preconditions.checkNotNull(executor, "Executor");
    }

    @Override
    protected Registry delegate() {
        return registry;
    }
    
    @Override
    public <T> void notifyAsync(Class<T> type, Procedure<? super T> command) {
        notifyAsync(Key.get(type), command);
    }

    @Override
    public <T> void notifyAsync(final Key<T> key, final Procedure<? super T> command) {
        executor.execute(new Runnable() {
            
            @Override
            public void run() {
                LOG.trace("notifying all listeners for {} concurrently using {}", key, command);
                for (final T listener : getListeners(key)) {
                    executor.execute(new Runnable() {
                        
                        @Override
                        public void run() {
                            LOG.trace("notifying {} for {} concurrently", listener, key);
                            command.apply(listener);
                        }
                        
                    });
                }
            }
            
        });
    }

}
