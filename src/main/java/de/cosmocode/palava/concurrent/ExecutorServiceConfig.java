package de.cosmocode.palava.concurrent;


/**
 * Constant holder class for executor service configuration keys.
 *
 * @author Willi Schoenborn
 */
final class ExecutorServiceConfig {

    public static final String MIN_POOL_SIZE = "minPoolSize";
    
    public static final String MAX_POOL_SIZE = "maxPoolSize";
    
    public static final String KEEP_ALIVE_TIME = "keepAliveTime";
    
    public static final String KEEP_ALIVE_TIME_UNIT = "keepAliveTimeUnit";
    
    public static final String QUEUE_MODE = "queueMode";
    
    public static final String QUEUE_CAPACITY = "queueCapacity";
    
    public static final String THREAD_FACTORY = "threadFactory";
    
    public static final String SHUTDOWN_TIMEOUT = "shutdownTimeout";
    
    public static final String SHUTDOWN_TIMEOUT_UNIT = "shutdownTimeoutUnit";
    
    private ExecutorServiceConfig() {
        
    }
    
}
