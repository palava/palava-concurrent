/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
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
