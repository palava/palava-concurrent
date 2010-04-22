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

/**
 * Constant holder class for executor service configuration keys.
 *
 * @author Willi Schoenborn
 */
final class ExecutorServiceConfig {

    public static final String MIN_POOL_SIZE = "minPoolSize";
    
    public static final String MAX_POOL_SIZE = "maxPoolSize";
    
    public static final String KEEP_ALIVE_TIME = "keepAliveTime";
    
    public static final String KEEP_ALIVE_TIME_UNIT = "keepAliveTimeUnit";
    
    public static final String QUEUE_MODE = "queueMode";
    
    public static final String QUEUE_CAPACITY = "queueCapacity";
    
    public static final String SHUTDOWN_TIMEOUT = "shutdownTimeout";
    
    public static final String SHUTDOWN_TIMEOUT_UNIT = "shutdownTimeoutUnit";
    
    private ExecutorServiceConfig() {
        
    }
    
}
