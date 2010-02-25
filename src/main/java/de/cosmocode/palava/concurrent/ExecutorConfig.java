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
 * 
 *
 * @author Tobias Sarnowski
 * @author Willi Schoenborn
 */
public final class ExecutorConfig {
    
    public static final String BASE_PREFIX = ExecutorsConfig.PREFIX + "named.";

    private final String prefix;

    ExecutorConfig(String name) {
        Preconditions.checkNotNull(name, "Name");
        this.prefix = BASE_PREFIX + name + ".";
    }

    /**
     * @deprecated use {@link ExecutorConfig#minPoolSize()} instead
     * @return
     */
    @Deprecated
    public String minSize() {
        return prefix + "minSize";
    }
    
    public String minPoolSize() {
        return prefix + ExecutorServiceConfig.MIN_POOL_SIZE;
    }

    /**
     * @deprecated use {@link ExecutorConfig#maxPoolSize()} instead
     * @return
     */
    @Deprecated
    public String maxSize() {
        return prefix + "maxSize";
    }
    
    public String maxPoolSize() {
        return prefix + ExecutorServiceConfig.MAX_POOL_SIZE;
    }

    public String keepAliveTime() {
        return prefix + ExecutorServiceConfig.KEEP_ALIVE_TIME;
    }

    public String keepAliveTimeUnit() {
        return prefix + ExecutorServiceConfig.KEEP_ALIVE_TIME_UNIT;
    }

    /**
     * @deprecated use {@link ExecutorConfig#queueMode()} instead
     * @return
     */
    @Deprecated
    public String queue() {
        return prefix + "queue";
    }
    
    public String queueMode() {
        return prefix + ExecutorServiceConfig.QUEUE_MODE;
    }

    /**
     * @deprecated use {@link ExecutorConfig#queueCapacity()} instead
     * @return
     */
    @Deprecated
    public String queueMax() {
        return prefix + "queueMax";
    }

    public String queueCapacity() {
        return prefix + ExecutorServiceConfig.QUEUE_CAPACITY;
    }
    
    public String threadFactory() {
        return prefix + ExecutorServiceConfig.THREAD_FACTORY;
    }
    
    public String shutdownTimeout() {
        return prefix + ExecutorServiceConfig.SHUTDOWN_TIMEOUT;
    }
    
    public String shutdownTimeoutUnit() {
        return prefix + ExecutorServiceConfig.SHUTDOWN_TIMEOUT_UNIT;
    }
    
}
