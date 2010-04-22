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

import com.google.common.base.Preconditions;

/**
 * A configuration helper class which prevents
 * typing errors.
 *
 * @author Tobias Sarnowski
 * @author Willi Schoenborn
 */
public final class ExecutorConfig {
    
    public static final String PREFIX = "executors.named.";

    private final String prefix;

    ExecutorConfig(String name) {
        Preconditions.checkNotNull(name, "Name");
        this.prefix = PREFIX + name + ".";
    }

    /**
     * Create an {@link ExecutorConfig} using the given name.
     * 
     * @param name the configured name
     * @throws NullPointerException if name is null
     * @return an {@link ExecutorConfig} which uses the given name to
     *         create named config keys
     */
    public static ExecutorConfig named(String name) {
        Preconditions.checkNotNull(name, "Name");
        return new ExecutorConfig(name);
    }

    /**
     * Create a prefixed config key for minPoolSize.
     * 
     * @return the prefixed minPoolSize config key
     */
    public String minPoolSize() {
        return prefix + ExecutorServiceConfig.MIN_POOL_SIZE;
    }

    /**
     * Create a prefixed config key for maxPoolSize.
     * 
     * @return the prefixed maxPoolSize config key
     */
    public String maxPoolSize() {
        return prefix + ExecutorServiceConfig.MAX_POOL_SIZE;
    }

    /**
     * Create a prefixed config key for keepAliveTime.
     * 
     * @return the prefixed keepAliveTime config key
     */
    public String keepAliveTime() {
        return prefix + ExecutorServiceConfig.KEEP_ALIVE_TIME;
    }

    /**
     * Create a prefixed config key for keepAliveTimeUnit.
     * 
     * @return the prefixed keepAliveTimeUnit config key
     */
    public String keepAliveTimeUnit() {
        return prefix + ExecutorServiceConfig.KEEP_ALIVE_TIME_UNIT;
    }

    /**
     * Create a prefixed config key for queueMode.
     * 
     * @return the prefixed queueMode config key
     */
    public String queueMode() {
        return prefix + ExecutorServiceConfig.QUEUE_MODE;
    }

    /**
     * Create a prefixed config key for queueCapacity.
     * 
     * @return the prefixed queueCapacity config key
     */
    public String queueCapacity() {
        return prefix + ExecutorServiceConfig.QUEUE_CAPACITY;
    }
    
    /**
     * Create a prefixed config key for shutdownTimeout.
     * 
     * @return the prefixed shutdownTimeout config key
     */
    public String shutdownTimeout() {
        return prefix + ExecutorServiceConfig.SHUTDOWN_TIMEOUT;
    }

    /**
     * Create a prefixed config key for shutdownTimeoutUnit.
     * 
     * @return the prefixed shutdownTimeoutUnit config key
     */
    public String shutdownTimeoutUnit() {
        return prefix + ExecutorServiceConfig.SHUTDOWN_TIMEOUT_UNIT;
    }
    
}
