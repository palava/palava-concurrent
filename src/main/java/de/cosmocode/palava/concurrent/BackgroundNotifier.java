package de.cosmocode.palava.concurrent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutorService;

import com.google.inject.BindingAnnotation;

/**
 * {@link BindingAnnotation} for {@link ExecutorService}s used by {@link DefaultConcurrentRegistry}.
 *
 * @author Willi Schoenborn
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.PARAMETER,
    ElementType.METHOD
})
@BindingAnnotation
public @interface BackgroundNotifier {

}
