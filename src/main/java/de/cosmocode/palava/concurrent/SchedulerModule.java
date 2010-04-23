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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * This module can be used to rebind general scheduler
 * configuration keys to specific ones.
 *
 * @author Willi Schoenborn
 */
public final class SchedulerModule extends PrivateModule {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerModule.class);

    private final Key<ScheduledExecutorService> key;
    
    private final String name;
    
    /**
     * Creates a new {@link SchedulerModule} which uses the given name to rebind configuration
     * entries and binds the configured {@link ScheduledExecutorService} using {@link Names#named(String)}.
     * 
     * @since 2.2
     * @param name the desired name
     */
    public SchedulerModule(String name) {
        this(Names.named(name), name);
    }
    
    public SchedulerModule(Class<? extends Annotation> annotation, String name) {
        Preconditions.checkNotNull(annotation, "Annotation");
        this.key = Key.get(ScheduledExecutorService.class, annotation);
        this.name = Preconditions.checkNotNull(name, "Name");
    }
    
    public SchedulerModule(Annotation annotation, String name) {
        Preconditions.checkNotNull(annotation, "Annotation");
        this.key = Key.get(ScheduledExecutorService.class, annotation);
        this.name = Preconditions.checkNotNull(name, "Name");
    }

    @Override
    protected void configure() {
        LOG.trace("Binding scheduler configuration for {} using name {}", key, name);
        
        final ExecutorConfig config = ExecutorConfig.named(name);

        bind(int.class).annotatedWith(Names.named(ExecutorServiceConfig.MIN_POOL_SIZE)).to(
            Key.get(int.class, Names.named(config.minPoolSize())));
        
        bind(long.class).annotatedWith(Names.named(ExecutorServiceConfig.SHUTDOWN_TIMEOUT)).to(
            Key.get(long.class, Names.named(config.shutdownTimeout())));
        
        bind(TimeUnit.class).annotatedWith(Names.named(ExecutorServiceConfig.SHUTDOWN_TIMEOUT_UNIT)).to(
            Key.get(TimeUnit.class, Names.named(config.shutdownTimeoutUnit())));
        
        bind(key).to(ConfigurableScheduledExecutorService.class).in(Singleton.class);
        expose(key);
    }

}
