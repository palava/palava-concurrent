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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
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
            return new LinkedBlockingDeque<Runnable>();
        }
        
    },
    
    SYNCHRONOUS {
      
        @Override
        public BlockingQueue<Runnable> create() {
            return new SynchronousQueue<Runnable>();
        }
        
    },
    
    PRIORITY {
        
        @Override
        public BlockingQueue<Runnable> create() {
            return new PriorityBlockingQueue<Runnable>();
        }
        
    };
    
    @Override
    public abstract BlockingQueue<Runnable> create();
    
}
