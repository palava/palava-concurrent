/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;

/**
 * An {@link ExecutorService} which can be easily configured using
 * the constructor.
 *
 * @author Willi Schoenborn
 */
final class ConfigurableExecutorService implements ExecutorService, Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableExecutorService.class);
    
    private final int minPoolSize;
    
    private final int maxPoolSize;
    
    private final long keepAliveTime;
    
    private final TimeUnit keepAliveTimeUnit;
    
    private final QueueMode queueMode;
    
    private final long shutdownTimeout;
    
    private final TimeUnit shutdownTimeoutUnit;
    
    private final ExecutorService executor;
    
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
        this.shutdownTimeout = shutdownTimeout;
        this.shutdownTimeoutUnit = Preconditions.checkNotNull(shutdownTimeoutUnit, "ShutdownTimeoutUnit");
        Preconditions.checkNotNull(provider, "Provider");
        
        this.executor = new ThreadPoolExecutor(
            minPoolSize, maxPoolSize,
            keepAliveTime, keepAliveTimeUnit,
            queueCapacity == -1 ? queueMode.create() : queueMode.create(queueCapacity),
            provider.newThreadFactory()
        );
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
    public void dispose() throws LifecycleException {
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
