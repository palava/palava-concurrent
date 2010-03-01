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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Ordering;

import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;
import de.cosmocode.palava.core.lifecycle.Startable;
import de.cosmocode.palava.core.lifecycle.Suspendable;

/**
 * Sub classes of {@link ScheduledService} are able get their {@link Runnable#run()}
 * method called in a configurable fixed rate.
 * 
 * TODO add testcase using easymock (scheduler) and expect accurate calculation results!
 * 
 * @author Willi Schoenborn
 */
public abstract class ScheduledService implements Runnable, UncaughtExceptionHandler, 
    Initializable, Startable, Suspendable {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledService.class);

    private boolean autostart;
    
    // month of year
    private int month = -1;

    // week of month
    private int week = -1;
    
    // day of week
    private int day = -1;

    // hour of day
    private int hour = -1;
    
    // minute of hour
    private int minute;
    
    private long period;
    
    private TimeUnit periodUnit = TimeUnit.MILLISECONDS;
    
    private Future<?> future;
    
    /**
     * Provides the underlying {@link ScheduledExecutorService}.
     * 
     * @return the scheduler this service uses for scheduling purposes
     */
    protected abstract ScheduledExecutorService getScheduler();

    protected void setAutostart(boolean autostart) {
        this.autostart = autostart;
    }
    
    protected void setMonth(int month) {
        this.month = month;
    }

    protected void setWeek(int week) {
        this.week = week;
    }
    
    protected void setDay(int day) {
        this.day = day;
    }

    protected void setHour(int hour) {
        this.hour = hour;
    }

    protected void setMinute(int minute) {
        this.minute = minute;
    }
    
    protected void setPeriod(long period) {
        this.period = period;
    }
    
    protected void setPeriodUnit(TimeUnit periodUnit) {
        this.periodUnit = periodUnit;
    }
    
    @Override
    public void initialize() throws LifecycleException {
        if (autostart) {
            LOG.info("Autostarting {}", this);
            start();
        }
    }
    
    @Override
    public void start() throws LifecycleException {
        final Calendar calendar = Calendar.getInstance();
        
        if (month >= Calendar.JANUARY) {
            // month in calendar start with 0, but we use 1
            final int m = month - 1;
            LOG.debug("Setting month to {}", m);
            calendar.set(Calendar.MONTH, m);
        }
        
        if (week >= 0) {
            LOG.debug("Setting week to {}", week);
            calendar.set(Calendar.WEEK_OF_MONTH, week);
        }
        
        if (day >= Calendar.SUNDAY) {
            // first day of week in calendar is sunday, but we use monday
            final int d = (day % 7) + 1;
            LOG.debug("Setting day to {}", d);
            calendar.set(Calendar.DAY_OF_WEEK, d);
        }
        
        if (hour >= 0) {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
        }
        
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        final Date start = calendar.getTime();
        final Date now = new Date();
        
        // initial delay in milliseconds
        final long delay;
        
        if (start.before(now)) {
            LOG.debug("Point already passed.");
            if (month >= Calendar.JANUARY) {
                calendar.add(Calendar.YEAR, 1);
            } else if (week >= 0) {
                calendar.add(Calendar.MONTH, 1);
            } else if (day >= Calendar.SUNDAY) {
                calendar.add(Calendar.WEEK_OF_MONTH, 1);
            } else if (hour >= 0) {
                calendar.add(Calendar.DATE, 1);
            } else {
                calendar.add(Calendar.HOUR, 1);
            }
            delay = calendar.getTimeInMillis() - now.getTime();
        } else {
            LOG.debug("Point not yet passed.");
            delay = start.getTime() - now.getTime();
        }
        
        final TimeUnit unit = getHumanTimeUnit(delay);
        LOG.info("Scheduling {} to get executed in {} {} and then periodically every {} {}", new Object[] {
            getClass().getSimpleName(),
            unit.convert(delay, TimeUnit.MILLISECONDS), unit.name().toLowerCase(),
            period, periodUnit.name().toLowerCase(),
        });
        
        future = getScheduler().scheduleAtFixedRate(
            this, 
            delay, 
            periodUnit.toMillis(period), 
            TimeUnit.MILLISECONDS
        );
        
        getScheduler().execute(new Runnable() {
            
            @Override
            public void run() {
                LOG.debug("Starting watcher thread");
                try {
                    future.get();
                    // dead code here?
                } catch (InterruptedException e) {
                    LOG.error("Scheduler was interrupted", e);
                } catch (CancellationException e) {
                    LOG.info("Watcher thread has been cancelled");
                } catch (ExecutionException e) {
                    uncaughtException(Thread.currentThread(), e);
                }
                LOG.debug("Watcher thread terminated");
            }
            
        });
    }
    
    private TimeUnit getHumanTimeUnit(long delay) {
        final Ordering<TimeUnit> ordering = Ordering.natural().reverse();
        for (TimeUnit unit : ordering.sortedCopy(Arrays.asList(TimeUnit.values()))) {
            if (unit.convert(delay, TimeUnit.MILLISECONDS) > 0) {
                return unit;
            }
        }
        return TimeUnit.MILLISECONDS;
    }
    
    @Override
    public void suspend() throws LifecycleException {
        stop();
    }
    
    @Override
    public void resume() throws LifecycleException {
        start();
    }
    
    @Override
    public void stop() throws LifecycleException {
        if (future == null) {
            LOG.debug("Nothing to stop");
        } else {
            future.cancel(false);
            LOG.debug("{} canceled", future);
            future = null;
        }
    }
    
}
