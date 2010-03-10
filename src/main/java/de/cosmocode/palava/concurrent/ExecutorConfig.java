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
     * Create a prefixed config key for minSize.
     * 
     * @deprecated use {@link ExecutorConfig#minPoolSize()} instead
     * @return the prefixed minSize config key
     */
    @Deprecated
    public String minSize() {
        return prefix + "minSize";
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
     * Create a prefixed config key for maxSize.
     * 
     * @deprecated use {@link ExecutorConfig#maxPoolSize()} instead
     * @return the prefixed maxSize config key
     */
    @Deprecated
    public String maxSize() {
        return prefix + "maxSize";
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
     * Create a prefixed config key for queue.
     * 
     * @deprecated use {@link ExecutorConfig#queueMode()} instead
     * @return the prefixed queue config key
     */
    @Deprecated
    public String queue() {
        return prefix + "queue";
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
     * Create a prefixed config key for queueMax.
     * 
     * @deprecated use {@link ExecutorConfig#queueCapacity()} instead
     * @return the prefixed queueMax config key
     */
    @Deprecated
    public String queueMax() {
        return prefix + "queueMax";
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
