package org.sechelc.loadbalancer;

import java.util.Random;

public class RandomLoadBalancer extends BaseLoadBalancer {

    public RandomLoadBalancer() {
        super();
    }

    @Override
    public int getNextProviderIndex() {
        Random random = new Random();
        return random.nextInt(getRegisteredProviders().size());
    }
}
