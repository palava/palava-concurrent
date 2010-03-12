package de.cosmocode.palava.concurrent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.BindingAnnotation;

/**
 * Binding annotation for background {@link ScheduledExecutorService}.
 *
 * @author Willi Schoenborn
 */
@Target({
    ElementType.PARAMETER,
    ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface BackgroundScheduler {

    String NAME = "background";
    
}
