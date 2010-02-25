package de.cosmocode.palava.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;

/**
 * A configurable executor service.
 * 
 * TODO what about {@link ScheduledExecutorService}s?
 *
 * @author Willi Schoenborn
 */
final class ConfigurableExecutorService implements ExecutorService, Initializable, Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableExecutorService.class);
    
    private final ThreadProvider provider;

    private final int minPoolSize;
    
    private final int maxPoolSize;
    
    private final long keepAliveTime;
    
    private final TimeUnit keepAliveTimeUnit;
    
    private final QueueMode queueMode;
    
    private final Integer queueCapacity;
    
    private ThreadFactory factory;
    
    private final long shutdownTimeout;
    
    private final TimeUnit shutdownTimeoutUnit;
    
    private ExecutorService executor;
    
    @Inject
    public ConfigurableExecutorService(
        @Named(ExecutorServiceConfig.MIN_POOL_SIZE) int minPoolSize,
        @Named(ExecutorServiceConfig.MAX_POOL_SIZE) int maxPoolSize,
        @Named(ExecutorServiceConfig.KEEP_ALIVE_TIME) long keepAliveTime,
        @Named(ExecutorServiceConfig.KEEP_ALIVE_TIME_UNIT) TimeUnit keepAliveTimeUnit,
        @Named(ExecutorServiceConfig.QUEUE_MODE) QueueMode queueMode,
        @Named(ExecutorServiceConfig.QUEUE_CAPACITY) int queueCapacity,
        @Named(ExecutorServiceConfig.SHUTDOWN_TIMEOUT) long shutdownTimeout,
        @Named(ExecutorServiceConfig.SHUTDOWN_TIMEOUT_UNIT) TimeUnit shutdownTimeoutUnit,
        ThreadProvider provider) {
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.keepAliveTimeUnit = Preconditions.checkNotNull(keepAliveTimeUnit, "KeepAliveTimeUnit");
        this.queueMode = Preconditions.checkNotNull(queueMode, "QueueMode");
        this.queueCapacity = queueCapacity;
        this.shutdownTimeout = shutdownTimeout;
        this.shutdownTimeoutUnit = Preconditions.checkNotNull(shutdownTimeoutUnit, "ShutdownTimeoutUnit");
        this.provider = Preconditions.checkNotNull(provider, "Provider");
    }
    
    @Inject(optional = true)
    void setFactory(@Named(ExecutorServiceConfig.THREAD_FACTORY) ThreadFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }
    
    @Override
    public void initialize() throws LifecycleException {
        checkNotInitialized();
        this.executor = new ThreadPoolExecutor(
            minPoolSize, maxPoolSize,
            keepAliveTime, keepAliveTimeUnit,
            queueCapacity == null ? queueMode.create() : queueMode.create(queueCapacity),
            factory == null ? provider.newThreadFactory() : provider.newThreadFactory(factory)
        );
    }
    
    private void checkNotInitialized() {
        Preconditions.checkState(executor == null, "%s has already been initialized", this);
    }
    
    private void checkInitialized() {
        Preconditions.checkState(executor != null, "%s has not been initialized", this);
    }
    
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        checkInitialized();
        return executor.awaitTermination(timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        checkInitialized();
        executor.execute(command);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException {
        checkInitialized();
        return executor.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        checkInitialized();
        return executor.invokeAll(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        checkInitialized();
        return executor.invokeAny(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        checkInitialized();
        return executor.invokeAny(tasks);
    }

    @Override
    public boolean isShutdown() {
        checkInitialized();
        return executor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        checkInitialized();
        return executor.isTerminated();
    }

    @Override
    public void shutdown() {
        checkInitialized();
        executor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        checkInitialized();
        return executor.shutdownNow();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        checkInitialized();
        return executor.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        checkInitialized();
        return executor.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        checkInitialized();
        return executor.submit(task);
    }
    
    @Override
    public void dispose() throws LifecycleException {
        checkInitialized();
        try {
            LOG.info("Shutting down ExecutorService");
            executor.shutdown();
            LOG.info("Waiting {} {} for ExecutorService to shut down", 
                shutdownTimeout, shutdownTimeoutUnit.name().toLowerCase()
            );
            final boolean terminated = executor.awaitTermination(shutdownTimeout, shutdownTimeoutUnit);
            if (terminated) {
                LOG.info("ExecutorService terminated successfully");
            } else {
                LOG.warn("ExecutorService was forced to shutdown before finish");
            }
        } catch (InterruptedException e) {
            LOG.error("Interrupted while awaiting termination", e);
        }
    }

    @Override
    public String toString() {
        return String.format("ConfigurableExecutorService [" +
            "minPoolSize=%s, maxPoolSize=%s, " +
            "keepAliveTime=%s, keepAliveTimeUnit=%s, " +
            "queueMode=%s, " +
            "shutdownTimeout=%s, shutdownTimeoutUnit=%s]",
            minPoolSize, maxPoolSize, 
            keepAliveTime, keepAliveTimeUnit, 
            queueMode, 
            shutdownTimeout, shutdownTimeoutUnit);
    }
    
}
