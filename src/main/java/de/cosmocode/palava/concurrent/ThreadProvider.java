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

import java.util.concurrent.ThreadFactory;

import de.cosmocode.palava.core.Service;

/**
 * {@link ThreadProvider} provides a way to create {@link Thread}s
 * and {@link ThreadFactory}s while simultaneously keeping track
 * of all threads currently running. 
 *
 * @author Willi Schoenborn
 */
public interface ThreadProvider extends Service, ThreadFactory {

    /**
     * Creates a new {@link ThreadFactory} which
     * creates new Threads from runnable by using
     * the default settings of {@link Thread#Thread(Runnable)}.
     * 
     * @return a (probably cached) thread factory
     */
    ThreadFactory newThreadFactory();
    
    /**
     * Creates a new {@link ThreadFactory} which delegates
     * the actual creation to the given factory.
     * 
     * @param factory the backing factory
     * @return a decorated version of the given factory
     * @throws NullPointerException if factory is null
     */
    ThreadFactory newThreadFactory(ThreadFactory factory);
    
    /**
     * Creates {@link ThreadStatistic}s for all
     * threads currently in use.
     * 
     * @return new {@link ThreadStatistic}
     */
    ThreadStatistic createStatistic();
    
}
