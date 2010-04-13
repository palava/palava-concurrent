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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
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
 * Default implementation of the {@link ConcurrentRegistry} interface.
 *
 * @author Willi Schoenborn
 */
final class DefaultConcurrentRegistry extends ForwardingRegistry implements ConcurrentRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConcurrentRegistry.class);
    
    private final Registry registry;
    
    private final ExecutorService executor;
    
    @Inject
    public DefaultConcurrentRegistry(Registry registry, @BackgroundNotifier ExecutorService executor) {
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
