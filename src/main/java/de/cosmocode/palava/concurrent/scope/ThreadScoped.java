package de.cosmocode.palava.concurrent.scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.ScopeAnnotation;

/**
 * Scope annotations for threads.
 *
 * @author Willi Schoenborn
 */
@Target({
    ElementType.TYPE,
    ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
@ScopeAnnotation
public @interface ThreadScoped {

}
