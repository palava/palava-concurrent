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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

import de.cosmocode.palava.core.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
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
 *   <li><b>minSize</b>: (int) core pool size</li>
 *   <li><b>maxSize</b>: (int) maximum count of threads to spawn</li>
 *   <li><b>keepAlive</b>: (long) if there are more threads than minSize configures, keep alive tells the system to stop idling threads after this time</li>
 *   <li><b>KeepAliveTimeUnit</b>: (TimeUnit) required by keepAlive</li>
 *   <li><b>queue</b>: <b>synchronized</b> fastest handling, no queue | <b>static</b> fastest queuing, requires queueMax | <b>dynamic</b> most flexible queuing, can be changed at runtime, can use queueMax</li>
 *   <li><b>queueMax</b>: (int) maximum queue size (see static|dynamic)</li>
 * </ul>
 *
 * @author Tobias Sarnowski
 */
@Singleton
class DefaultExecutorServiceFactory implements ExecutorServiceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutorServiceFactory.class);

    private static final String CONFIG_EXECUTORS = "executors.named.";


    private final Map<String, ExecutorService> configuredExecutors = Maps.newHashMap();


    @Inject
    public DefaultExecutorServiceFactory(
        @Settings Properties settings,
        Provider<ExecutorBuilder> provider) {

        Preconditions.checkNotNull(settings, "Settings");
        Preconditions.checkNotNull(provider, "Provider");

        // parse the configurations for executor configurations
        Map<String,Map<String,String>> conf = Maps.newHashMap();
        for (Map.Entry<Object,Object> entry: settings.entrySet()) {
            // filter settings for configurations of this threadpool
            if (((String)entry.getKey()).startsWith(CONFIG_EXECUTORS)) {

                String subkey = ((String)entry.getKey()).substring(CONFIG_EXECUTORS.length());
                String name = subkey.substring(0, subkey.indexOf('.'));
                String key = subkey.substring(name.length() + 1);

                LOG.trace("<<" + name + ">> " + key + ": " + entry.getValue());

                Map<String,String> c = conf.get(name);
                if (c == null) {
                     c = Maps.newHashMap();
                    conf.put(name, c);
                }
                c.put(key, ((String)entry.getValue()));
            }
        }

        LOG.debug("{}", conf);

        // create the executors
        for (Map.Entry<String,Map<String,String>> executorsConf: conf.entrySet()) {
            String executorName = executorsConf.getKey();
            Map<String,String> executorConf = executorsConf.getValue();

            LOG.debug("creating ExecutorService \"" + executorName + "\"");

            ExecutorBuilder builder = provider.get();

            // set the configuration settings
            for (Map.Entry<String,String> eC: executorConf.entrySet()) {

                if ("minSize".equals(eC.getKey())) {
                    builder.minSize(Integer.parseInt(eC.getValue()));
                    continue;
                }

                if ("maxSize".equals(eC.getKey())) {
                    builder.maxSize(Integer.parseInt(eC.getValue()));
                    continue;
                }

                if ("keepAlive".equals(eC.getKey()) || "keepAliveTimeUnit".equals(eC.getKey())) {
                    String keepAlive = executorConf.get("keepAlive");
                    String keepAliveTimeUnit = executorConf.get("keepAliveTimeUnit");
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
                    } else

                    if ("static".equals(eC.getValue())) {
                        String queueMax = executorConf.get("queueMax");
                        if (queueMax != null) {
                            queue = new ArrayBlockingQueue(Integer.parseInt(queueMax));
                        } else {
                            throw new IllegalArgumentException("static queue configured but no queueMax for executor \"" + executorName + "\"");
                        }
                    } else

                    if ("dynamic".equals(eC.getValue())) {
                        String queueMax = executorConf.get("queueMax");
                        if (queueMax != null) {
                            queue = new LinkedBlockingQueue(Integer.parseInt(queueMax));
                        } else {
                            queue = new LinkedBlockingQueue();
                        }
                    } else {
                        throw new IllegalArgumentException("unknown queue for executor \"" + executorName + "\" configured");
                    }
                    builder.queue(queue);
                    continue;
                }

                throw new IllegalArgumentException("unknown configuration \"" + eC.getKey() + "\" for executor \"" + executorName + "\" given");
            }

            // generate the threadpool
            configuredExecutors.put(executorName, builder.build());
        }
    }
    
    @Override
    public ExecutorService getExecutorService(String name) {
        // check if the executor is configured
        ExecutorService service = configuredExecutors.get(name);
        if (service != null) {
            return service;
        }
        throw new IllegalArgumentException("requested executor \"" + name + "\" not configured"); 
    }

}
