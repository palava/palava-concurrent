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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import de.cosmocode.junit.Asserts;

/**
 * Tests {@link ExecutorServiceFactory#CONFIG_PATTERN}.
 *
 * @author Willi Schoenborn
 */
public final class ExecutorServiceFactoryPatternTest {

    private final Pattern unit = ExecutorServiceFactory.CONFIG_PATTERN;
    
    /**
     * Tests valid inputs.
     */
    @Test
    public void valid() {
        Asserts.assertMatches(unit, ExecutorConfig.named("salesforce-sync").minSize());
        Asserts.assertMatches(unit, ExecutorConfig.named("nio-worker").maxSize());
        Asserts.assertMatches(unit, ExecutorConfig.named("publishing").keepAliveTime());
        Asserts.assertMatches(unit, ExecutorConfig.named("background-tasks").keepAliveTimeUnit());
        Asserts.assertMatches(unit, ExecutorConfig.named("custom-single-thread").queue());
        Asserts.assertMatches(unit, ExecutorConfig.named("cleanup-threads").queueMax());
    }
    
    /**
     * Tests invalid inputs.
     */
    @Test
    public void invalid() {
        Asserts.assertDoesNotMatch(unit, "executor.named.salesforce-sync.minSize");
        Asserts.assertDoesNotMatch(unit, "core.application = de.cosmocode.palava.core.EmptyModule");
        Asserts.assertDoesNotMatch(unit, "executors.my-named-threadpool.minSize");
        Asserts.assertDoesNotMatch(unit, "mail.template-root");
        Asserts.assertDoesNotMatch(unit, "socket.port");
    }

    /**
     * Tests the first group provided by the matcher.
     */
    @Test
    public void firstGroup1() {
        final Matcher matcher = unit.matcher(ExecutorConfig.named("salesforce-sync").minSize());
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("salesforce-sync", matcher.group(1));
    }

    /**
     * Tests the first group provided by the matcher.
     */
    @Test
    public void firstGroup2() {
        final Matcher matcher = unit.matcher(ExecutorConfig.named("nio-workers").maxSize());
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("nio-workers", matcher.group(1));
    }

    /**
     * Tests the first group provided by the matcher.
     */
    @Test
    public void firstGroup3() {
        final Matcher matcher = unit.matcher(ExecutorConfig.named("publishing").keepAliveTime());
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("publishing", matcher.group(1));
    }

    /**
     * Tests the first group provided by the matcher.
     */
    @Test
    public void firstGroup4() {
        final Matcher matcher = unit.matcher(ExecutorConfig.named("background-tasks").keepAliveTimeUnit());
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("background-tasks", matcher.group(1));
    }

    /**
     * Tests the first group provided by the matcher.
     */
    @Test
    public void firstGroup5() {
        final Matcher matcher = unit.matcher(ExecutorConfig.named("custom-single-thread").queue());
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("custom-single-thread", matcher.group(1));
    }

    /**
     * Tests the first group provided by the matcher.
     */
    @Test
    public void firstGroup6() {
        final Matcher matcher = unit.matcher(ExecutorConfig.named("cleanup-threads").queueMax());
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("cleanup-threads", matcher.group(1));
    }

    /**
     * Tests the second group provided by the matcher.
     */
    @Test
    public void secondGroup1() {
        final Matcher matcher = unit.matcher(ExecutorConfig.named("salesforce-sync").minSize());
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("minSize", matcher.group(2));
    }

    /**
     * Tests the second group provided by the matcher.
     */
    @Test
    public void secondGroup2() {
        final Matcher matcher = unit.matcher(ExecutorConfig.named("nio-worker").maxSize());
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("maxSize", matcher.group(2));
    }

    /**
     * Tests the second group provided by the matcher.
     */
    @Test
    public void secondGroup3() {
        final Matcher matcher = unit.matcher(ExecutorConfig.named("publishing").keepAliveTime());
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("keepAliveTime", matcher.group(2));
    }

    /**
     * Tests the second group provided by the matcher.
     */
    @Test
    public void secondGroup4() {
        final Matcher matcher = unit.matcher(ExecutorConfig.named("background-tasks").keepAliveTimeUnit());
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("keepAliveTimeUnit", matcher.group(2));
    }

    /**
     * Tests the second group provided by the matcher.
     */
    @Test
    public void secondGroup5() {
        final Matcher matcher = unit.matcher(ExecutorConfig.named("custom-single-thread").queue());
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("queue", matcher.group(2));
    }

    /**
     * Tests the second group provided by the matcher.
     */
    @Test
    public void secondGroup6() {
        final Matcher matcher = unit.matcher(ExecutorConfig.named("cleanup-threads").queueMax());
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("queueMax", matcher.group(2));
    }

}
