package org.sechelc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sechelc.loadbalancer.BaseLoadBalancer;
import org.sechelc.provider.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

abstract class BaseLoadBalancerTest {

    public static final String UUID = "1";
    private static final int CAPACITY = 3;
    private BaseLoadBalancer victim;

    protected abstract BaseLoadBalancer getImpl();

    @BeforeEach
    public void init() {
        victim = getImpl();
    }

    /**
     * step 2 test
     */
    @Test
    void shouldRegisterProviders() {
        assertTrue(victim.registerProvider(new Provider(UUID)));
    }

    /**
     * step 2 test
     */
    @Test
    void shouldNotRegisterOneProviderTwoTimes() {
        Provider provider = new Provider(UUID);

        assertTrue(victim.registerProvider(provider));
        assertFalse(victim.registerProvider(provider));
    }

    /**
     * step 2 test
     */
    @Test
    void shouldNotRegisterMoreThan10Providers() {
        fillLoadBalancer();

        assertThrows(RuntimeException.class, () -> victim.registerProvider(new Provider(11, CAPACITY)));
    }

    /**
     * step 5 test
     */
    @Test
    void testExcludeProvider() {
        fillLoadBalancer();

        assertTrue(victim.excludeProvider(new Provider(5, CAPACITY)));
        assertEquals(victim.getRegisteredProviders().size(), 9);
    }

    /**
     * step 6 test
     */
    @Test
    void shouldExcludeProviderAfterMoreThan10SecondsOnStatusFalse() throws InterruptedException {
        fillLoadBalancer();
        victim.getRegisteredProviders().get(0).setStatus(false);
        Thread.sleep(11000);

        assertEquals(9, victim.getRegisteredProviders().size());
    }

    /**
     * step 7 test
     */
    @Test
    void shouldReincludeAfterTwoChecks() throws InterruptedException {
        fillLoadBalancer();
        Provider provider = victim.getRegisteredProviders().get(0);

        provider.setStatus(false);
        Thread.sleep(11000);
        assertEquals(9, victim.getRegisteredProviders().size());

        provider.setStatus(true);

        Thread.sleep(21000);
        assertEquals(10, victim.getRegisteredProviders().size());
    }

    /**
     * step 8 test
     */
    @Test
    public void shouldThrowExceptionIfCapacityExceded() {
        fillLoadBalancer();

        assertThrows(ExecutionException.class, () -> {
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            List<Future<String>> futures = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                futures.add(executorService.submit(() -> victim.get()));
            }

            for (Future<String> future : futures) {
                future.get();
            }
        });
    }

    public void fillLoadBalancer() {
        for (int i = 0; i < 10; i++) {
            assertTrue(victim.registerProvider(new Provider(i, CAPACITY)));
        }
    }
}