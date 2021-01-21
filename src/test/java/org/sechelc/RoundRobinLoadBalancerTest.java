package org.sechelc;

import org.junit.jupiter.api.Test;
import org.sechelc.loadbalancer.BaseLoadBalancer;
import org.sechelc.loadbalancer.RoundRobinLoadBalancer;
import org.sechelc.provider.Provider;

import static org.junit.jupiter.api.Assertions.*;

class RoundRobinLoadBalancerTest extends BaseLoadBalancerTest {

    public static final int CAPACITY = 3;
    private final RoundRobinLoadBalancer victim = new RoundRobinLoadBalancer();

    /**
     * step 4 test
     */
    @Test
    void shouldReturnValuesInARoundRobinFashion() {
        for (int i = 0; i < 10; i++) {
            assertTrue(victim.registerProvider(new Provider(i, CAPACITY)));
        }

        for (int i = 0; i < 20; i++) {
            assertEquals(victim.get(), i % 10 + "");
        }
    }

    @Override
    protected BaseLoadBalancer getImpl() {
        return victim;
    }
}