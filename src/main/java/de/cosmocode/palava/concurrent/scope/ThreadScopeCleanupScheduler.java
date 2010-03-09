package de.cosmocode.palava.concurrent.scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * 
 *
 * @author Willi Schoenborn
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface ThreadScopeCleanupScheduler {

    String NAME = "thread-scope-cleanup";
    
}
