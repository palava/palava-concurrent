/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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
import de.cosmocode.palava.jmx.MBeanService;

/**
 * A {@link ScheduledExecutorService} which can be configured easily configured
 * using the constructor.
 * 
 * @author Willi Schoenborn
 */
final class ConfigurableScheduledExecutorService implements ScheduledExecutorService, Initializable, Disposable, 
    ConfigurableScheduledExecutorServiceMBean {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableScheduledExecutorService.class);
    
    private final String name;
    
    private final int minPoolSize;
    
    private ThreadFactory factory;
    
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
    
    private final long shutdownTimeout;
    
    private final TimeUnit shutdownTimeoutUnit;
    
    private ScheduledThreadPoolExecutor executor;
    
    private final MBeanService mBeanService;
    
    @Inject
    public ConfigurableScheduledExecutorService(
        @Named(ExecutorConfig.NAME) String name,
        @Named(ExecutorConfig.MIN_POOL_SIZE) int minPoolSize,
        @Named(ExecutorConfig.SHUTDOWN_TIMEOUT) long shutdownTimeout,
        @Named(ExecutorConfig.SHUTDOWN_TIMEOUT_UNIT) TimeUnit shutdownTimeoutUnit,
        ThreadFactory defaultFactory,
        MBeanService mBeanService) {
        
        this.name = name;
        this.minPoolSize = minPoolSize;
        this.shutdownTimeout = shutdownTimeout;
        this.shutdownTimeoutUnit = Preconditions.checkNotNull(shutdownTimeoutUnit, "ShutdownTimeoutUnit");
        this.factory = Preconditions.checkNotNull(defaultFactory, "Factory");
        this.mBeanService = Preconditions.checkNotNull(mBeanService, "MBeanService");
    }
    
    @Inject(optional = true)
    void setFactory(@Named(ExecutorConfig.THREAD_FACTORY) ThreadFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }
    
    @Inject(optional = true)
    void setHandler(@Named(ExecutorConfig.REJECTION_HANDLER) RejectedExecutionHandler handler) {
        this.handler = Preconditions.checkNotNull(handler, "Handler");
    }
    
    @Override
    public void initialize() throws LifecycleException {
        this.executor = new ScheduledThreadPoolExecutor(
            minPoolSize, factory, handler
        );

        mBeanService.register(this, "name", name);
    }
    
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException {
        return executor.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return executor.invokeAll(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        return executor.invokeAny(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return executor.invokeAny(tasks);
    }

    @Override
    public boolean isShutdown() {
        return executor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return executor.isTerminated();
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return executor.schedule(callable, delay, unit);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return executor.schedule(command, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return executor.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return executor.shutdownNow();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return executor.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getActiveCount() {
        return executor.getActiveCount();
    }

    @Override
    public long getCompletedTaskCount() {
        return executor.getCompletedTaskCount();
    }

    @Override
    public int getCorePoolSize() {
        return executor.getCorePoolSize();
    }

    @Override
    public int getLargestPoolSize() {
        return executor.getLargestPoolSize();
    }

    @Override
    public int getMaximumPoolSize() {
        return executor.getMaximumPoolSize();
    }

    @Override
    public int getPoolSize() {
        return executor.getPoolSize();
    }

    @Override
    public long getTaskCount() {
        return executor.getTaskCount();
    }

    @Override
    public void dispose() throws LifecycleException {
        try {
            mBeanService.unregister(this, "name", name);
        } finally {
            try {
                LOG.info("Shutting down {}", this);
                executor.shutdown();
                LOG.info("Waiting {} {} for {} to shut down", new Object[] {
                    shutdownTimeout, shutdownTimeoutUnit.name().toLowerCase(), this
                });
                final boolean terminated = executor.awaitTermination(shutdownTimeout, shutdownTimeoutUnit);
                if (terminated) {
                    LOG.info("{} terminated successfully", this);
                } else {
                    LOG.warn("{} was forced to shutdown before finish", this);
                }
            } catch (InterruptedException e) {
                LOG.error("Interrupted while awaiting termination", e);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("ScheduledExecutorService [%s]", name);
    }
    
}
