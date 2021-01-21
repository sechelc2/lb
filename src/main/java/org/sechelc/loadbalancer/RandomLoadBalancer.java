package org.sechelc.loadbalancer;

import java.util.Random;

/**
 * Concrete implementation of BaseLoadBalancer.
 * returns a random provider index.
 */
public class RandomLoadBalancer extends BaseLoadBalancer {

    @Override
    public int getNextProviderIndex() {
        Random random = new Random();
        return random.nextInt(getRegisteredProviders().size());
    }
}
