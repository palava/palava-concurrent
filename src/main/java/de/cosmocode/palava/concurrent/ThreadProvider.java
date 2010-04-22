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

import java.util.concurrent.ThreadFactory;

/**
 * {@link ThreadProvider} provides a way to create {@link Thread}s
 * and {@link ThreadFactory}s while simultaneously keeping track
 * of all threads currently running. 
 *
 * @author Willi Schoenborn
 */
public interface ThreadProvider extends ThreadFactory {

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
    
}
