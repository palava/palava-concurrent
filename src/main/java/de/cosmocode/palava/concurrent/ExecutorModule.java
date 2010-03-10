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

import java.lang.annotation.Annotation;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * This module can be used to rebind general executor service
 * configuration keys to specific ones.
 *
 * @author Willi Schoenborn
 */
public class ExecutorModule extends PrivateModule {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutorModule.class);

    private final Key<ExecutorService> key;
    
    private final String name;
    
    public ExecutorModule(Class<? extends Annotation> annotation, String name) {
        this.key = Key.get(ExecutorService.class, Preconditions.checkNotNull(annotation, "Annotation"));
        this.name = Preconditions.checkNotNull(name, "Name");
    }
    
    public ExecutorModule(Annotation annotation, String name) {
        this.key = Key.get(ExecutorService.class, Preconditions.checkNotNull(annotation, "Annotation"));
        this.name = Preconditions.checkNotNull(name, "Name");
    }
    
    @Override
    public final void configure() {
        LOG.trace("Binding executor configuration for {} using name {}", key, name);
        
        final ExecutorConfig config = ExecutorConfig.named(name);

        bind(int.class).annotatedWith(Names.named(ExecutorServiceConfig.MIN_POOL_SIZE)).to(
            Key.get(int.class, Names.named(config.minPoolSize())));
        
        bind(int.class).annotatedWith(Names.named(ExecutorServiceConfig.MAX_POOL_SIZE)).to(
            Key.get(int.class, Names.named(config.maxPoolSize())));
        
        bind(long.class).annotatedWith(Names.named(ExecutorServiceConfig.KEEP_ALIVE_TIME)).to(
            Key.get(long.class, Names.named(config.keepAliveTime())));
        
        bind(TimeUnit.class).annotatedWith(Names.named(ExecutorServiceConfig.KEEP_ALIVE_TIME_UNIT)).to(
            Key.get(TimeUnit.class, Names.named(config.keepAliveTimeUnit())));
        
        bind(QueueMode.class).annotatedWith(Names.named(ExecutorServiceConfig.QUEUE_MODE)).to(
            Key.get(QueueMode.class, Names.named(config.queueMode())));
        
        bind(int.class).annotatedWith(Names.named(ExecutorServiceConfig.QUEUE_CAPACITY)).to(
            Key.get(int.class, Names.named(config.queueCapacity())));
        
        bind(long.class).annotatedWith(Names.named(ExecutorServiceConfig.SHUTDOWN_TIMEOUT)).to(
            Key.get(long.class, Names.named(config.shutdownTimeout())));
        
        bind(TimeUnit.class).annotatedWith(Names.named(ExecutorServiceConfig.SHUTDOWN_TIMEOUT_UNIT)).to(
            Key.get(TimeUnit.class, Names.named(config.shutdownTimeoutUnit())));
        
        bind(key).to(ConfigurableExecutorService.class).in(Singleton.class);
        expose(key);
    }
    
}
