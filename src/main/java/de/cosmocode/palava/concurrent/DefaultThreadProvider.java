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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;

/**
 * Default implementation of the {@link ThreadProvider} interface.
 *
 * @author Oliver Lorenz
 * @author Willi Schoenborn
 */
final class DefaultThreadProvider implements ThreadProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultThreadProvider.class);

    private final ThreadFactory cachedFactory = new Factory(Executors.defaultThreadFactory());
    
    private final Set<Thread> threads;
    
    private final Object threadSize = new Object() {

        @Override
        public String toString() {
            return Integer.toString(threads.size());
        }
        
    };
    
    public DefaultThreadProvider() {
        final MapMaker maker = new MapMaker().softKeys();
        final Map<Thread, Boolean> map = maker.makeMap();
        this.threads = Collections.newSetFromMap(map);
    }

    @Override
    public Thread newThread(Runnable r) {
        return cachedFactory.newThread(r);
    }
    
    @Override
    public ThreadFactory newThreadFactory() {
        return cachedFactory;
    }

    @Override
    public ThreadFactory newThreadFactory(ThreadFactory threadFactory) {
        return new Factory(threadFactory);
    }
    
    /**
     * Implementation of the {@link ThreadFactory} interface which
     * can decorate an existing thread factory or create threads 
     * on its own. References of all created threads will be stored.
     *
     * @author Oliver Lorenz
     * @author Willi Schoenborn
     */
    private class Factory implements ThreadFactory {
        
        private final ThreadFactory factory;
        
        public Factory(ThreadFactory factory) {
            this.factory = Preconditions.checkNotNull(factory, "Factory");
        }
        
        @Override
        public Thread newThread(Runnable runnable) {
            final Thread thread = factory.newThread(runnable);
            threads.add(thread);
            LOG.trace("New thread {}, {} thread(s) currently in use", thread, threadSize);
            return thread;
        }
        
    }
    
}
