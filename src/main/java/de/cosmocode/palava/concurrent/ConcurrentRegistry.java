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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
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
