package de.cosmocode.palava.concurrent;

import java.util.Arrays;

import com.google.common.base.Preconditions;

/**
 * 
 *
 * @author Willi Schoenborn
 */
public final class Runnables {

    /**
     * 
     * @param first
     * @param second
     * @param rest
     * @return
     * @throws NullPointerException if first, second or rest is null
     */
    public static Runnable chain(final Runnable first, final Runnable second, final Runnable... rest) {
        Preconditions.checkNotNull(first, "First");
        Preconditions.checkNotNull(second, "Second");
        Preconditions.checkNotNull(rest, "Rest");
        return new Runnable() {
            
            @Override
            public void run() {
                first.run();
                second.run();
                for (Runnable runnable : rest) {
                    runnable.run();
                }
            }
            
            @Override
            public String toString() {
                return String.format("Runnables.chain(%s, %s, %s)", first, second, Arrays.asList(rest));
            }
            
        };
    }

}
