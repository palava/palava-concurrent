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

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.collections.Procedure;
import de.cosmocode.palava.core.ForwardingRegistry;
import de.cosmocode.palava.core.Registry;

/**
 * Default implementation of the {@link AsyncRegistry} interface.
 *
 * @author Willi Schoenborn
 */
final class DefaultAsyncRegistry extends ForwardingRegistry implements AsyncRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAsyncRegistry.class);
    
    private final Registry registry;
    
    private final ExecutorService executor;
    
    @Inject
    public DefaultAsyncRegistry(Registry registry, @BackgroundNotifier ExecutorService executor) {
        this.registry = Preconditions.checkNotNull(registry, "Registry");
        this.executor = Preconditions.checkNotNull(executor, "Executor");
    }

    @Override
    protected Registry delegate() {
        return registry;
    }
    
    @Override
    public <T> void notifyAsync(Class<T> type, Procedure<? super T> command) {
        notifyAsync(Key.get(type), command);
    }

    @Override
    public <T> void notifyAsync(final Key<T> key, final Procedure<? super T> command) {
        executor.execute(new Runnable() {
            
            @Override
            public void run() {
                LOG.trace("notifying all listeners for {} concurrently using {}", key, command);
                for (final T listener : getListeners(key)) {
                    executor.execute(new Runnable() {
                        
                        @Override
                        public void run() {
                            LOG.trace("notifying {} for {} concurrently", listener, key);
                            command.apply(listener);
                        }
                        
                    });
                }
            }
            
        });
    }

}
