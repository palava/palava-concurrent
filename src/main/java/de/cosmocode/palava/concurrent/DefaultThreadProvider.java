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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
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
        this.threads = Collections.newSetFromMap(new MapMaker().weakKeys().<Thread, Boolean>makeMap());
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
        public Thread newThread(final Runnable runnable) {
            final Thread thread = factory.newThread(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        runnable.run();
                    /* CHECKSTYLE:OFF */
                    } catch (Throwable e) {
                    /* CHECKSTYLE:ON */
                        LOG.error("Uncaught exception in thread " + Thread.currentThread(), e);
                        throw Throwables.propagate(e);
                    }
                }
                
            });
            
            threads.add(thread);
            LOG.trace("New thread {}, {} thread(s) currently in use", thread, threadSize);
            return thread;
        }
        
    }
    
}
