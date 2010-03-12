package de.cosmocode.palava.concurrent;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Installs the {@link BackgroundScheduler}.
 *
 * @author Willi Schoenborn
 */
public class BackgroundSchedulerModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new SchedulerModule(BackgroundScheduler.class, BackgroundScheduler.NAME));
    }

}
