/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

/**
 * Default implementation of the {@link ExecutorServiceBuilder} interface.
 *
 * @author Willi Schoenborn
 */
final class DefaultExecutorServiceBuilder implements ExecutorServiceBuilder {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutorServiceBuilder.class);

    private final ThreadProvider provider;
    
    private int minPoolSize = DEFAULT_MIN_POOL_SIZE;
    private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
    private long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
    private TimeUnit keepAliveTimeUnit = TimeUnit.NANOSECONDS;
    private BlockingQueue<Runnable> workQueue;
    private ThreadFactory threadFactory;
    
    @Inject
    public DefaultExecutorServiceBuilder(ThreadProvider provider) {
        this.provider = Preconditions.checkNotNull(provider, "Provider");
    }
    
    @Override
    public ExecutorServiceBuilder minSize(int size) {
        Preconditions.checkArgument(size >= 0, "Minimum pool size must be greater than 0");
        Preconditions.checkArgument(size <= maxPoolSize, "Minimum pool size must be less than maximum pool size");
        this.minPoolSize = size;
        return this;
    }
    
    @Override
    public ExecutorServiceBuilder maxSize(int size) {
        Preconditions.checkArgument(size >= 0, "Maximum pool size must be greater than 0");
        Preconditions.checkArgument(size >= minPoolSize, "Maximum pool size must be greater than minimum pool size");
        this.maxPoolSize = size;
        return this;
    }
    
    @Override
    public ExecutorServiceBuilder keepAlive(long time, TimeUnit unit) {
        Preconditions.checkArgument(time >= 0, "Keep alive time must be greater than 0");
        Preconditions.checkNotNull(unit, "Unit");
        this.keepAliveTime = time;
        this.keepAliveTimeUnit = unit;
        return this;
    }
    
    @Override
    public ExecutorServiceBuilder queue(BlockingQueue<Runnable> queue) {
        this.workQueue = Preconditions.checkNotNull(queue, "Queue");
        return this;
    }
    
    @Override
    public ExecutorServiceBuilder queue(QueueMode mode) {
        Preconditions.checkNotNull(mode, "Mode");
        this.workQueue = Preconditions.checkNotNull(mode, "Mode").create();
        return this;
    }
    
    @Override
    public ExecutorServiceBuilder threadFactory(ThreadFactory factory) {
        Preconditions.checkNotNull(factory, "Factory");
        this.threadFactory = Preconditions.checkNotNull(factory, "Factory");
        return this;
    }
    
    @Override
    public ExecutorService build() {
        LOG.debug("Creating executor with min size {}, max size {} and a keep alive time of {} {}", new Object[] {
            minPoolSize,
            maxPoolSize,
            keepAliveTime,
            keepAliveTimeUnit.name().toLowerCase(),
        });
        return new ThreadPoolExecutor(
            minPoolSize,
            maxPoolSize,
            keepAliveTime,
            keepAliveTimeUnit,
            workQueue == null ? DEFAULT_QUEUE_MODE.create() : workQueue,
            threadFactory == null ? provider.newThreadFactory() : provider.newThreadFactory(threadFactory)
        );
    }
    
    @Override
    public ScheduledExecutorService buildScheduled() {
        return new ScheduledThreadPoolExecutor(
            minPoolSize,
            threadFactory == null ? provider.newThreadFactory() : provider.newThreadFactory(threadFactory)
        );
    }
    
}
