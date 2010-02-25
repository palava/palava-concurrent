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
 * Static factory class for dynamic executor configurations.
 * 
 * @author Tobias Sarnowski
 * @author Willi Schoenborn
 */
public final class ExecutorConfigs {

    public static final String PREFIX = "executors.";
    
    private ExecutorConfigs() {
        
    }

    /**
     * Create an {@link ExecutorConfig} using the given name.
     * 
     * @param name the configured name
     * @throws NullPointerException if name is null
     * @return an {@link ExecutorConfig} which uses the given name to
     *         create named config keys
     */
    public static ExecutorConfig named(String name) {
        Preconditions.checkNotNull(name, "Name");
        return new ExecutorConfig(name);
    }

}
