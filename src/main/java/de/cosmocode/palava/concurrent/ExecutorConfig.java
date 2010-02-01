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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExecutorConfig {
    private final String name;

    public final String BasePrefix = ExecutorsConfig.Prefix + "named.";

    protected ExecutorConfig(String name) {
        this.name = name;
        this.Prefix = BasePrefix + name + ".";
    }

    public final String Prefix;

    public String minSize() {
        return Prefix + "minSize";
    }

    public String maxSize() {
        return Prefix + "maxSize";
    }

    public String keepAliveTime() {
        return Prefix + "keepAliveTime";
    }

    public String keepAliveTimeUnit() {
        return Prefix + "keepAliveTimeUnit";
    }

    public String queue() {
        return Prefix + "queue";
    }

    public String queueMax() {
        return Prefix + "queueMax";
    }
}
