package de.cosmocode.palava.concurrent;

import java.util.concurrent.ExecutorService;

public interface ExecutorServiceFactory {

    ExecutorService create(String group);
    
}
