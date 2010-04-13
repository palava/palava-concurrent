package de.cosmocode.palava.concurrent;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;

/**
 * Binds {@link ConcurrentRegistry} to {@link DefaultConcurrentRegistry}.
 *
 * @author Willi Schoenborn
 */
public final class DefaultConcurrentRegistryModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ConcurrentRegistry.class).to(DefaultConcurrentRegistry.class).in(Singleton.class);
    }

}
