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
