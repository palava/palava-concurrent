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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;

import de.cosmocode.collections.Procedure;
import de.cosmocode.palava.core.Registry;

/**
 * A {@link Registry} which provides asynchronous
 * notify* methods.
 *
 * @author Willi Schoenborn
 */
public interface ConcurrentRegistry extends Registry {

    /**
     * Notify all listeners for a specific type
     * by invoking command on every found listener in a concurrent
     * fashion.
     * 
     * <p>
     *   Using this method is equivalent to: <br />
     *   {@code registry.notifyAsync(Key.get(type), command);}
     * </p>
     * 
     * This method returns immediately. Exceptions will be
     * propagated to the {@link UncaughtExceptionHandler} of the
     * underlying {@link ExecutorService}.
     * 
     * @param <T> the generic key type
     * @param type the type's class literal
     * @param command the command being invoked on every listener
     * @throws NullPointerException if type or command is null
     */
    <T> void notifyAsync(Class<T> type, Procedure<? super T> command);
    
    /**
     * Notify all listeners for a specific binding key
     * by invoking command on every found listener in a concurrent
     * fashion.
     * 
     * @param <T> the generic type
     * @param key the binding key
     * @param command the command being invoked on every listener
     * @throws NullPointerException if key or command is null
     */
    <T> void notifyAsync(Key<T> key, Procedure<? super T> command);
    
}
