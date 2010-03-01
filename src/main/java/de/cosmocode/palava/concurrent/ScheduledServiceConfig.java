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

/**
 * 
 *
 * @author Willi Schoenborn
 */
public final class ScheduledServiceConfig {

    public static final String PREFIX = "scheduler.";
    
    public static final String AUTOSTART = PREFIX + "autostart";
    
    public static final String MONTH = PREFIX + "month";
    
    public static final String WEEK = PREFIX + "week";

    public static final String DAY = PREFIX + "day";

    public static final String HOUR = PREFIX + "hour";

    public static final String MINUTE = PREFIX + "minute";

    public static final String PERIOD = PREFIX + "period";

    public static final String PERIOD_UNIT = PREFIX + "periodUnit";

    private ScheduledServiceConfig() {
        
    }
    
}
