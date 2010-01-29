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

import java.util.regex.Pattern;

import org.junit.Test;

import de.cosmocode.junit.Asserts;

/**
 * Tests {@link ExecutorServiceFactory#CONFIG_PATTERN}.
 *
 * @author Willi Schoenborn
 */
public final class ExecutorServiceFactoryPatternTest {

    private final Pattern unit = ExecutorServiceFactory.CONFIG_PATTERN;
    
    @Test
    public void valid() {
        Asserts.assertMatches(unit, "executors.named.salesforce-sync.minSize = 1");
        Asserts.assertMatches(unit, "executors.named.nio-workers.maxSize = 10");
        Asserts.assertMatches(unit, "");
        Asserts.assertMatches(unit, "");
        Asserts.assertMatches(unit, "");
        Asserts.assertMatches(unit, "");
        Asserts.assertMatches(unit, "");
    }
    
    @Test
    public void invalid() {
        Asserts.assertDoesNotMatch(unit, "");
        Asserts.assertDoesNotMatch(unit, "");
        Asserts.assertDoesNotMatch(unit, "");
        Asserts.assertDoesNotMatch(unit, "");
        Asserts.assertDoesNotMatch(unit, "");
        Asserts.assertDoesNotMatch(unit, "");
        Asserts.assertDoesNotMatch(unit, "");
        Asserts.assertDoesNotMatch(unit, "");
        Asserts.assertDoesNotMatch(unit, "");
    }
    
    @Test
    public void firstGroup() {
        
    }
    
    @Test
    public void secondGroup() {
        
    }

}
