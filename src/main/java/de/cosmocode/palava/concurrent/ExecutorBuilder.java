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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import de.cosmocode.patterns.Builder;

/**
 * A {@link Builder} for {@link ExecutorService}s.
 *
 * @author Willi Schoenborn
 */
public interface ExecutorBuilder extends Builder<ExecutorService> {
    
    int DEFAULT_MIN_POOL_SIZE = 0;
    
    int DEFAULT_MAX_POOL_SIZE = Integer.MAX_VALUE;
    
    long DEFAULT_KEEP_ALIVE_TIME = 0;
    
    QueueMode DEFAULT_QUEUE_MODE = QueueMode.BLOCKING;
    
    /**
     * Sets the minimum pool size.
     * 
     * @param minPoolSize the minium pool size
     * @return this
     * @throws IllegalArgumentException if minPoolSize is less than zero or greater than maxPoolSize
     */
    ExecutorBuilder minSize(int minPoolSize);
    
    /**
     * Sets the maximum pool size.
     * 
     * @param maxPoolSize the maximum pool size
     * @return this
     * @throws IllegalArgumentException if maxPoolSize is less than zero or less than minPoolSize
     */
    ExecutorBuilder maxSize(int maxPoolSize);
    
    /**
     * Sets the keep alive time and the corresponding time unit.
     * 
     * @param time the keep alive time
     * @param unit the time unit
     * @return this
     * @throws IllegalArgumentException if time is less than zero
     * @throws NullPointerException if unit is null
     */
    ExecutorBuilder keepAlive(long time, TimeUnit unit);
    
    /**
     * Sets the work queue.
     * 
     * @param queue the work queue
     * @return this
     * @throws NullPointerException if queue is null
     */
    ExecutorBuilder queue(BlockingQueue<Runnable> queue);
    
    /**
     * Sets the work queue by using {@link QueueMode#create()}.
     * 
     * @param mode the mode which creates a new work queue
     * @return this
     * @throws NullPointerException if mode is null
     */
    ExecutorBuilder queue(QueueMode mode);
    
    /**
     * Sets the {@linkplain ThreadFactory thread factory} that will be
     * used by the executor service.
     * 
     * @param factory the thread factory
     * @return this
     * @throws NullPointerException if factory is null
     */
    ExecutorBuilder threadFactory(ThreadFactory factory);
    
    /**
     * Builds a ScheduledExecutorService.
     * 
     * @return a new {@link ScheduledExecutorService}.
     */
    ScheduledExecutorService buildScheduled();
    
}
