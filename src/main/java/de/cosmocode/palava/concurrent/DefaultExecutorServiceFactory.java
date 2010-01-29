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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.internal.Maps;

import de.cosmocode.palava.core.Settings;

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
 *     <b>KeepAliveTimeUnit</b>:
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
@Singleton
class DefaultExecutorServiceFactory implements ExecutorServiceFactory {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutorServiceFactory.class);
    
    private static final String CONFIG_EXECUTORS = "executors.named.";

    private final Map<String, ExecutorService> configuredExecutors = Maps.newHashMap();

    private final Provider<ExecutorServiceBuilder> provider;
    
    @Inject
    public DefaultExecutorServiceFactory(
        @Settings Properties settings,
        Provider<ExecutorServiceBuilder> provider) {

        Preconditions.checkNotNull(settings, "Settings");
        this.provider = Preconditions.checkNotNull(provider, "Provider");

        // parse the configurations for executor configurations
        
        final Map<String, Map<String, String>> conf = Maps.newHashMap();
        for (Map.Entry<Object, Object> entry : settings.entrySet()) {
            // filter settings for configurations of this threadpool
            if (((String) entry.getKey()).startsWith(CONFIG_EXECUTORS)) {

                final String subkey = ((String) entry.getKey()).substring(CONFIG_EXECUTORS.length());
                final String name = subkey.substring(0, subkey.indexOf('.'));
                final String key = subkey.substring(name.length() + 1);

                LOG.trace("<<" + name + ">> " + key + ": " + entry.getValue());

                Map<String, String> c = conf.get(name);
                if (c == null) {
                    c = Maps.newHashMap();
                    conf.put(name, c);
                }
                c.put(key, (String) entry.getValue());
            }
        }

        LOG.debug("{}", conf);

        // create the executors
        for (Map.Entry<String, Map<String, String>> executorsConf : conf.entrySet()) {
            final String executorName = executorsConf.getKey();
            final Map<String, String> executorConf = executorsConf.getValue();

            LOG.debug("creating ExecutorService \"" + executorName + "\"");

            final ExecutorServiceBuilder builder = provider.get();

            // set the configuration settings
            for (Map.Entry<String, String> eC : executorConf.entrySet()) {

                if ("minSize".equals(eC.getKey())) {
                    builder.minSize(Integer.parseInt(eC.getValue()));
                    continue;
                }

                if ("maxSize".equals(eC.getKey())) {
                    builder.maxSize(Integer.parseInt(eC.getValue()));
                    continue;
                }

                if ("keepAlive".equals(eC.getKey()) || "keepAliveTimeUnit".equals(eC.getKey())) {
                    final String keepAlive = executorConf.get("keepAlive");
                    final String keepAliveTimeUnit = executorConf.get("keepAliveTimeUnit");
                    if (keepAlive == null) {
                        throw new IllegalArgumentException("keepAliveTimeUnit without keepAlive given");
                    }
                    if (keepAliveTimeUnit == null) {
                        throw new IllegalArgumentException("keepAlive without keepAliveTimeUnit given");
                    }
                    builder.keepAlive(Long.parseLong(keepAlive), TimeUnit.valueOf(keepAliveTimeUnit));
                    continue;
                }

                if ("queue".equals(eC.getKey())) {
                    final BlockingQueue<Runnable> queue;
                    
                    if ("synchronized".equals(eC.getValue())) {
                        queue = new SynchronousQueue<Runnable>();
                    } else if ("static".equals(eC.getValue())) {
                        final String queueMax = executorConf.get("queueMax");
                        if (queueMax != null) {
                            queue = new ArrayBlockingQueue<Runnable>(Integer.parseInt(queueMax));
                        } else {
                            throw new IllegalArgumentException(
                                "static queue configured but no queueMax for executor \"" + executorName + "\"");
                        }
                    } else if ("dynamic".equals(eC.getValue())) {
                        final String queueMax = executorConf.get("queueMax");
                        if (queueMax != null) {
                            queue = new LinkedBlockingQueue<Runnable>(Integer.parseInt(queueMax));
                        } else {
                            queue = new LinkedBlockingQueue<Runnable>();
                        }
                    } else {
                        throw new IllegalArgumentException(
                            "unknown queue for executor \"" + executorName + "\" configured");
                    }
                    builder.queue(queue);
                    continue;
                }

                throw new IllegalArgumentException(
                    "unknown configuration \"" + eC.getKey() + "\" for executor \"" + executorName + "\" given");
            }

            // generate the threadpool
            configuredExecutors.put(executorName, builder.build());
        }
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
    public ExecutorServiceBuilder buildExecutorService(String name) {
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
