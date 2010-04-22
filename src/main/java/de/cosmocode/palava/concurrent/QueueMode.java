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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import de.cosmocode.patterns.Factory;

/**
 * Enum style factory for differnt {@link BlockingQueue} implementations.
 *
 * @author Willi Schoenborn
 */
public enum QueueMode implements Factory<BlockingQueue<Runnable>> {

    BLOCKING {
        
        @Override
        public BlockingQueue<Runnable> create() {
            return new LinkedBlockingQueue<Runnable>();
        }
        
        @Override
        public BlockingQueue<Runnable> create(int capacity) {
            return new LinkedBlockingQueue<Runnable>(capacity);
        }
        
    },
    
    STATIC {
        
        @Override
        public BlockingQueue<Runnable> create() {
            throw new UnsupportedOperationException(name() + " needs a capacity");
        }
        
        @Override
        public BlockingQueue<Runnable> create(int capacity) {
            return new ArrayBlockingQueue<Runnable>(capacity);
        }
        
    },
    
    SYNCHRONOUS {
      
        @Override
        public BlockingQueue<Runnable> create() {
            return new SynchronousQueue<Runnable>();
        }
        
        @Override
        public BlockingQueue<Runnable> create(int capacity) {
            throw new UnsupportedOperationException(name() + " is not applicable with a capacity");
        }
        
    },
    
    PRIORITY {
        
        @Override
        public BlockingQueue<Runnable> create() {
            return new PriorityBlockingQueue<Runnable>();
        }
        
        @Override
        public BlockingQueue<Runnable> create(int capacity) {
            throw new UnsupportedOperationException(name() + " is not applicable with a capacity");
        }
        
    };
    
    @Override
    public abstract BlockingQueue<Runnable> create();
    
    /**
     * Createsa new {@link BlockingQueue} using the semantics of
     * this mode and limits the capacity to the specified value. 
     * 
     * @param capacity the maximum size
     * @return a new {@link BlockingQueue}
     * @throws UnsupportedOperationException if this mode does now allow
     *         the capacity setting
     */
    public abstract BlockingQueue<Runnable> create(int capacity);
    
}
