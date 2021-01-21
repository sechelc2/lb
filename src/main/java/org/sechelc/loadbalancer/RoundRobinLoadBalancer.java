package org.sechelc.loadbalancer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Concrete implementation of BaseLoadBalancer.
 * Implements a round robin strategy for the provider index.
 */
public class RoundRobinLoadBalancer extends BaseLoadBalancer {
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    /**
     *
     * @return a provider index in a round robin fashion.
     */
    public int getNextProviderIndex() {
        return currentIndex.getAndUpdate(this::incrementCurrentIndex);

    }

    private int incrementCurrentIndex(int currentIndex) {
        if (currentIndex < getRegisteredProviders().size() - 1) {
            return currentIndex + 1;
        } else {
            return 0;
        }
    }
}
