package de.cosmocode.palava.concurrent.scope;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.internal.Maps;
import com.google.inject.name.Named;

import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;

/**
 * {@link Scope} implementation for threads.
 * 
 * @author Willi Schoenborn
 */
public final class ThreadScope implements Scope, Runnable, Initializable {
    
    private static final Logger LOG = LoggerFactory.getLogger(ThreadScope.class);

    // TODO does this map work?
    private final ConcurrentMap<Thread, Map<Object, Object>> contexts = new MapMaker().makeMap();
    
    private ScheduledExecutorService scheduler;
    
    private long period = 5;
    
    private TimeUnit periodUnit = TimeUnit.SECONDS;
    
    @Inject
    public void setScheduler(@ThreadScopeCleanupScheduler ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }
    
    @Inject(optional = true)
    public void setPeriod(@Named(ThreadScopeConfig.PERIOD) long period) {
        this.period = period;
    }
    
    @Inject(optional = true)
    public void setPeriodUnit(@Named(ThreadScopeConfig.PERIOD_UNIT) TimeUnit periodUnit) {
        this.periodUnit = periodUnit;
    }
    
    @Override
    public void initialize() throws LifecycleException {
        LOG.info("Scheduling thread scope cleanup to run every {} {}", period, periodUnit.name().toLowerCase());
        // TODO handle returned future
        scheduler.scheduleAtFixedRate(this, 0, period, periodUnit);
    }
    
    @Override
    public void run() {
        final Iterator<Thread> iterator = contexts.keySet().iterator();
        while (iterator.hasNext()) {
            final Thread thread = iterator.next();
            if (!thread.isAlive()) {
                LOG.trace("Removing context of dead thread {}", thread);
                iterator.remove();
                // TODO handle destroy/suspend
            }
        }
    }
    
    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> provider) {
        final Map<Object, Object> context = getContext();
        return new Provider<T>() {
            
            @Override
            public T get() {
                @SuppressWarnings("unchecked")
                final T scoped = (T) context.get(key);
                if (scoped == null) {
                    final T unscoped = provider.get();
                    context.put(key, unscoped);
                    return unscoped;
                } else {
                    return scoped;
                }
            }
            
        };
    }
    
    private Map<Object, Object> getContext() {
        final Thread thread = Thread.currentThread();
        if (contexts.get(thread) == null) {
            final Map<Object, Object> context = Maps.newHashMap();
            // TODO handle resume
            contexts.put(thread, context);
            return context;
        } else {
            return contexts.get(thread);
        }
    }
    
}
