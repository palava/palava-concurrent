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

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Singleton;

/**
 * Installs the {@link BackgroundScheduler}.
 *
 * @author Willi Schoenborn
 */
public class BackgroundSchedulerModule implements Module {

    @Override
    @SuppressWarnings("deprecation")
    public void configure(Binder binder) {
        binder.install(new SchedulerModule(BackgroundScheduler.class, BackgroundScheduler.NAME));
        
        final Key<ScheduledExecutorService> key = Key.get(ScheduledExecutorService.class, BackgroundScheduler.class);
        
        binder.bind(Executor.class).annotatedWith(Background.class).to(key).in(Singleton.class);
        binder.bind(ExecutorService.class).annotatedWith(Background.class).to(key).in(Singleton.class);
        binder.bind(ScheduledExecutorService.class).annotatedWith(Background.class).to(key).in(Singleton.class);
    }

}
