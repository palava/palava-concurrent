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

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;

import de.cosmocode.palava.core.inject.Settings;

/**
 * Can parse ExecutorService configurations from the framework's settings
 * with the following scheme:
 *
 * {@code executors.named.<name>.<key> = <value>}
 *
 * with <name> being the ExecutorService name, <key> and <value> the
 * executors configuration. The following configurations are possible:
 * <ul>
 *   <li>
 *     <b>minSize</b>:
 *     (int) core pool size
 *   </li>
 *   <li>
 *     <b>maxSize</b>:
 *     (int) maximum count of threads to spawn
 *   </li>
 *   <li>
 *     <b>keepAlive</b>:
 *     (long) if there are more threads than minSize configures,
 *     keep alive tells the system to stop idling threads after this time
 *   </li>
 *   <li>
 *     <b>keepAliveTimeUnit</b>:
 *     (TimeUnit) required by keepAlive
 *   </li>
 *   <li>
 *     <b>queue</b>:
 *     <ul>
 *       <li><b>{@link QueueMode#SYNCHRONOUS}</b> fastest handling, no queue</li>
 *       <li><b>{@link QueueMode#PRIORITY}</b> priority queing, requires {@link Comparable}</li>
 *       <li><b>{@link QueueMode#STATIC}</b> fastest queuing, requires queueMax</li>
 *       <li><b>{@link QueueMode#BLOCKING}</b> most flexible queuing, can be changed at runtime, can use queueMax</li>
 *     </ul>
 *   </li>
 *   <li>
 *     <b>queueMax</b>:
 *     (int) maximum queue size (see {@link QueueMode#STATIC} | {@link QueueMode#BLOCKING})
 *   </li>
 * </ul>
 *
 * @author Tobias Sarnowski
 * @author Willi Schoenborn
 */
class DefaultExecutorServiceFactory implements ExecutorServiceFactory {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutorServiceFactory.class);
    
    private final Map<String, ExecutorService> configuredExecutors = Maps.newHashMap();

    private final Provider<ExecutorServiceBuilder> provider;
    
    @Inject
    public DefaultExecutorServiceFactory(@Settings Properties settings, Provider<ExecutorServiceBuilder> provider) {
        Preconditions.checkNotNull(settings, "Settings");
        this.provider = Preconditions.checkNotNull(provider, "Provider");

        final Set<Object> propertyNames = Sets.filter(settings.keySet(), new Predicate<Object>() {
            
            @Override
            public boolean apply(Object input) {
                return input == null ? false : CONFIG_PATTERN.matcher(input.toString()).matches();
            }
            
        });
        
        final Map<String, Map<String, String>> configs = Maps.newHashMap();
        
        for (Object propertyName : propertyNames) {
            final Matcher matcher = CONFIG_PATTERN.matcher(propertyName.toString());
            final boolean matches = matcher.matches();
            assert matches : "Key should match";
            final String name = matcher.group(1);
            if (!configs.containsKey(name)) {
                final Map<String, String> map = Maps.newHashMap();
                configs.put(name, map);
            }
            final String key = matcher.group(2);
            final String value = settings.getProperty(propertyName.toString());
            configs.get(name).put(key, value);
            LOG.trace("Executor configuration: {}.{} = {}", new Object[] {
                name, key, value
            });
        }
        
        LOG.debug("{}", configs);
        
        for (Entry<String, Map<String, String>> namedConfig : configs.entrySet()) {
            final String name = namedConfig.getKey();
            configuredExecutors.put(name, build(namedConfig));
        }
    }
    
    private ExecutorService build(Entry<String, Map<String, String>> namedConfig) {
        final ExecutorServiceBuilder builder = provider.get();
        final String name = namedConfig.getKey();
        final Map<String, String> config = namedConfig.getValue();
        LOG.debug("Creating executor service {}", name);
        for (Entry<String, String> entry : config.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if (MIN_SIZE.equals(key)) {
                builder.minSize(Integer.parseInt(value));
            } else if (MAX_SIZE.equals(key)) {
                builder.maxSize(Integer.parseInt(value));
            } else if (KEEP_ALIVE_TIME.equals(key) || KEEP_ALIVE_TIME_UNIT.equals(key)) {
                final String keepAlive = config.get(KEEP_ALIVE_TIME);
                final String keepAliveTimeUnit = config.get(KEEP_ALIVE_TIME_UNIT);
                if (keepAlive == null) {
                    throw new IllegalArgumentException("keepAliveTime not set");
                }
                if (keepAliveTimeUnit == null) {
                    throw new IllegalArgumentException("keepAliveTimeUnit not set");
                }
                builder.keepAlive(Long.parseLong(keepAlive), TimeUnit.valueOf(keepAliveTimeUnit));
            } else if (QUEUE.equals(key)) {
                final QueueMode mode = QueueMode.valueOf(value);
                final String queueMax = config.get(QUEUE_MAX);
                final BlockingQueue<Runnable> queue;
                if (queueMax == null) {
                    queue = mode.create();
                } else {
                    queue = mode.create(Integer.parseInt(queueMax));
                }
                builder.queue(queue);
            }
        }
        return builder.build();
    }

    @Override
    public ExecutorService getExecutorService(String name) {
        Preconditions.checkNotNull(name, "Name");
        final ExecutorService service = configuredExecutors.get(name);
        if (service == null) {
            throw new IllegalArgumentException("requested executor '" + name + "' not configured"); 
        } else {
            return service;
        }
    }
    
    @Override
    public ExecutorServiceBuilder getScheduledExecutorService(String name) {
        Preconditions.checkNotNull(name, "Name");
        final ExecutorService cached = configuredExecutors.get(name);
        if (cached == null) {
            final ExecutorServiceBuilder builder = provider.get();
            return new InterceptingExecutorServiceBuilder(name, builder);
        } else {
            throw new IllegalArgumentException(
                String.format("There is already a configured executor service with the name '%s'", name)
            );
        }
    }

    /**
     * An {@link ExecutorServiceBuilder} which intercepts the build-methods and
     * keeps a reference to the constructed executor service.
     * 
     * <p>
     *   Multiple calls to the build method will fail.
     * </p>
     *
     * @author Willi Schoenborn
     */
    private final class InterceptingExecutorServiceBuilder extends ForwardingExecutorServiceBuilder {
        
        private static final String ERROR = "An executor service with the name '%s' has been configured concurrently";
        
        private final String name;
        
        private final ExecutorServiceBuilder builder;
        
        public InterceptingExecutorServiceBuilder(String name, ExecutorServiceBuilder builder) {
            this.name = Preconditions.checkNotNull(name, "Name");
            this.builder = Preconditions.checkNotNull(builder, "Builder");
        }

        @Override
        protected ExecutorServiceBuilder delegate() {
            return builder;
        }
        
        @Override
        public ExecutorService build() {
            final ExecutorService cached = configuredExecutors.get(name);
            if (cached == null) {
                final ExecutorService service = builder.build();
                configuredExecutors.put(name, service);
                return service;
            } else {
                return cached;
            }
        }

        @Override
        public ScheduledExecutorService buildScheduled() {
            final ExecutorService cached = configuredExecutors.get(name);
            if (cached == null) {
                final ScheduledExecutorService service = builder.buildScheduled();
                configuredExecutors.put(name, service);
                return service;
            } else if (cached instanceof ScheduledExecutorService) {
                return ScheduledExecutorService.class.cast(cached);
            } else {
                throw new IllegalArgumentException(String.format(
                    "%s is not a %s", cached, ScheduledExecutorService.class.getName()
                ));
            }
        }
        
        private void checkConcurrentCreation() {
            Preconditions.checkState(!configuredExecutors.containsKey(name), ERROR, name);
        }

        @Override
        public ExecutorServiceBuilder keepAlive(long time, TimeUnit unit) {
            checkConcurrentCreation();
            return super.keepAlive(time, unit);
        }

        @Override
        public ExecutorServiceBuilder maxSize(int maxPoolSize) {
            checkConcurrentCreation();
            return super.maxSize(maxPoolSize);
        }

        @Override
        public ExecutorServiceBuilder minSize(int minPoolSize) {
            checkConcurrentCreation();
            return super.minSize(minPoolSize);
        }

        @Override
        public ExecutorServiceBuilder queue(BlockingQueue<Runnable> queue) {
            checkConcurrentCreation();
            return super.queue(queue);
        }

        @Override
        public ExecutorServiceBuilder queue(QueueMode mode) {
            checkConcurrentCreation();
            return super.queue(mode);
        }

        @Override
        public ExecutorServiceBuilder threadFactory(ThreadFactory factory) {
            checkConcurrentCreation();
            return super.threadFactory(factory);
        }
        
    }

}
