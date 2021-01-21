package org.sechelc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sechelc.loadbalancer.BaseLoadBalancer;
import org.sechelc.loadbalancer.RandomLoadBalancer;
import org.sechelc.provider.Provider;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RandomLoadBalancerTest extends BaseLoadBalancerTest {
    public static final int CAPACITY = 3;
    public static final int UUID = 11;
    private final RandomLoadBalancer victim = new RandomLoadBalancer();

    /**
     * step 3 test
     */
    @Test
    void shouldInvokeARandomProviderOnGet() {
        fillLoadBalancer();

        assertThrows(RuntimeException.class, () -> victim.registerProvider(new Provider(RandomLoadBalancerTest.UUID, CAPACITY)));
    }

    /**
     * step 3 test
     */
    @Test
    void shouldReturnRandomValuesOnGet() {
        fillLoadBalancer();
        Set<String> returnedValues = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            returnedValues.add(victim.get());
        }

        assertTrue(returnedValues.size() > 1);
    }

    @Test
    void shouldReturnAllRandomValuesOnGetAfter1000runs() {
        fillLoadBalancer();
        Set<String> returnedValues = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            returnedValues.add(victim.get());
        }

        assertEquals(returnedValues.size(), 10);
    }

    @Override
    protected BaseLoadBalancer getImpl() {
        return victim;
    }
}