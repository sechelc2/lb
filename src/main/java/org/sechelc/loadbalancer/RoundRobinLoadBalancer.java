package org.sechelc.loadbalancer;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer extends BaseLoadBalancer {
    private AtomicInteger currentIndex = new AtomicInteger(0);

    public RoundRobinLoadBalancer() {
        super();
    }

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
