package de.cosmocode.palava.concurrent.scope;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scope;

import de.cosmocode.palava.concurrent.SchedulerModule;

/**
 * Binds the thread scope.
 *
 * @author Willi Schoenborn
 */
public final class ThreadScopeModule implements Module {

    @Override
    public void configure(Binder binder) {
        final Scope threadScope = new ThreadScope();
        binder.requestInjection(threadScope);
        binder.bindScope(ThreadScoped.class, threadScope);
        binder.install(new SchedulerModule(ThreadScopeCleanupScheduler.class, ThreadScopeCleanupScheduler.NAME));
    }

}
