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

import com.google.common.base.Preconditions;

/**
 * 
 *
 * @author Willi Schoenborn
 */
abstract class ForwardingExecutorServiceBuilder implements ExecutorServiceBuilder {

    protected abstract ExecutorServiceBuilder delegate();

    @Override
    public ExecutorService build() {
        return delegate().build();
    }

    @Override
    public ScheduledExecutorService buildScheduled() {
        return delegate().buildScheduled();
    }

    @Override
    public ExecutorServiceBuilder keepAlive(long time, TimeUnit unit) {
        delegate().keepAlive(time, unit);
        return this;
    }

    @Override
    public ExecutorServiceBuilder maxSize(int maxPoolSize) {
        delegate().maxSize(maxPoolSize);
        return this;
    }

    @Override
    public ExecutorServiceBuilder minSize(int minPoolSize) {
        delegate().minSize(minPoolSize);
        return this;
    }

    @Override
    public ExecutorServiceBuilder queue(BlockingQueue<Runnable> queue) {
        delegate().queue(queue);
        return this;
    }

    @Override
    public ExecutorServiceBuilder queue(QueueMode mode) {
        delegate().queue(mode);
        return this;
    }

    @Override
    public ExecutorServiceBuilder threadFactory(ThreadFactory factory) {
        delegate().threadFactory(factory);
        return this;
    }
    
}
