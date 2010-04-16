package de.cosmocode.palava.concurrent;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;

/**
 * Binds {@link ThreadProvider} to {@link DefaultThreadProvider}.
 *
 * @author Willi Schoenborn
 */
public final class DefaultThreadProviderModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ThreadProvider.class).to(DefaultThreadProvider.class).in(Singleton.class);
    }

}
