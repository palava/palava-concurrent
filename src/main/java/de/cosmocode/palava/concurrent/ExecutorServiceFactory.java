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

import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

/**
 * 
 *
 * @author Willi Schoenborn
 */
public interface ExecutorServiceFactory {

    String MIN_SIZE = "minSize";

    String MAX_SIZE = "maxSize";

    String KEEP_ALIVE_TIME = "keepAliveTime";

    String KEEP_ALIVE_TIME_UNIT = "keepAliveTimeUnit";

    String QUEUE = "queue";

    String QUEUE_MAX = "queueMax";
    
    Pattern CONFIG_PATTERN = Pattern.compile("^executors\\.named\\.([^\\.]+)\\.([^\\.]+)");

    /**
     * 
     * @param name
     * @throws
     * @return the threadpool configured by its name 
     */
    ExecutorService getExecutorService(String name);
    
    /**
     * 
     * @param name
     * @return
     */
    ExecutorServiceBuilder buildExecutorService(String name);
    
}
