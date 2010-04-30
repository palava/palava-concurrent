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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import de.cosmocode.palava.jmx.MBeanRegistered;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;

/**
 * An {@link ExecutorService} which can be easily configured using
 * the constructor.
 *
 * @author Willi Schoenborn
 */
final class ConfigurableExecutorService extends MBeanRegistered implements ExecutorService, Initializable,
        Disposable, ConfigurableExecutorServiceMBean {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableExecutorService.class);
    
    private String name;

    private final int minPoolSize;
    
    private final int maxPoolSize;
    
    private final long keepAliveTime;
    
    private final TimeUnit keepAliveTimeUnit;
    
    private final QueueMode queueMode;
    
    private final int queueCapacity;
    
    private ThreadFactory factory;
    
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
    
    private final long shutdownTimeout;
    
    private final TimeUnit shutdownTimeoutUnit;
    
    private ThreadPoolExecutor executor;

    @Inject
    public ConfigurableExecutorService(
        @Named(ExecutorConfig.NAME) String name,
        @Named(ExecutorConfig.MIN_POOL_SIZE) int minPoolSize,
        @Named(ExecutorConfig.MAX_POOL_SIZE) int maxPoolSize,
        @Named(ExecutorConfig.KEEP_ALIVE_TIME) long keepAliveTime,
        @Named(ExecutorConfig.KEEP_ALIVE_TIME_UNIT) TimeUnit keepAliveTimeUnit,
        @Named(ExecutorConfig.QUEUE_MODE) QueueMode queueMode,
        @Named(ExecutorConfig.QUEUE_CAPACITY) int queueCapacity,
        ThreadFactory defaultFactory,
        @Named(ExecutorConfig.SHUTDOWN_TIMEOUT) long shutdownTimeout,
        @Named(ExecutorConfig.SHUTDOWN_TIMEOUT_UNIT) TimeUnit shutdownTimeoutUnit) {
        super(ConfigurableExecutorService.class, "name", Preconditions.checkNotNull(name, "Name"));

        this.name = name;
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize == -1 ? Integer.MAX_VALUE : maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.keepAliveTimeUnit = Preconditions.checkNotNull(keepAliveTimeUnit, "KeepAliveTimeUnit");
        this.queueMode = Preconditions.checkNotNull(queueMode, "QueueMode");
        this.queueCapacity = queueCapacity;
        this.factory = Preconditions.checkNotNull(defaultFactory, "Factory");
        this.shutdownTimeout = shutdownTimeout;
        this.shutdownTimeoutUnit = Preconditions.checkNotNull(shutdownTimeoutUnit, "ShutdownTimeoutUnit");
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
        executor = new ThreadPoolExecutor(
            minPoolSize, maxPoolSize,
            keepAliveTime, keepAliveTimeUnit,
            queueCapacity == -1 ? queueMode.create() : queueMode.create(queueCapacity),
            factory, handler
        );

        super.initialize();
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
        super.dispose();
        
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

    @Override
    public String toString() {
        return String.format("ExecutorService [%s]", name);
    }
    
}
