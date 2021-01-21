package org.sechelc.loadbalancer;

import org.sechelc.provider.Provider;
import org.sechelc.provider.ProviderInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for our implementation.
 * Contains common methods for out load balancers.
 * TODO future improvements: -extract the checks into Validators,
 *                           -move the HealthCheck functionality to a different class.
 *                           -use composition instead of inheritance
 */
public abstract class BaseLoadBalancer implements LoadBalancerInterface, ProviderInterface {
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final List<Provider> registeredProviders = new CopyOnWriteArrayList<>();
    private final Set<Provider> excludedProviders = new CopyOnWriteArraySet<>();
    private final Set<Provider> pendingIncludeProviders = new CopyOnWriteArraySet<>();
    private final int checkFrequency = 10;
    private final AtomicInteger runningTasks = new AtomicInteger(0);

    public BaseLoadBalancer() {
        executorService.scheduleAtFixedRate(this::check, 0, checkFrequency, TimeUnit.SECONDS);
    }

    public abstract int getNextProviderIndex();

    @Override
    public String get() {
        checkCapacity();

        String returnValue = getRegisteredProviders().get(getNextProviderIndex()).get();

        runningTasks.decrementAndGet();
        return returnValue;

    }

    /**
     * Registers the provider in the loadBalancer
     * @param provider the provider to be registerd
     * @return true if the provider was successfully added, false if it was previously registered with the loadBalancer.
     */
    @Override
    public synchronized boolean registerProvider(Provider provider) {
        checkLoadBalancerCapacity();
        return !registeredProviders.contains(provider) && registeredProviders.add(provider);
    }

    /**
     * Excludes the provider from the loadBalancer.
     * When a provider is excluded it won't service requests anymore.
     * @param provider the provider to be registerd
     * @return true if the provider was successfully excluded, false if it was previously excluded.
     */
    @Override
    public synchronized boolean excludeProvider(Provider provider) {
        pendingIncludeProviders.remove(provider);
        return excludedProviders.add(provider);
    }

    /**
     *
     * @return All available providers that are not currently excluded.
     */
    public synchronized List<Provider> getRegisteredProviders() {
        List<Provider> differences = new ArrayList<>(registeredProviders);
        differences.removeAll(excludedProviders);
        return differences;
    }

    /**
     *
     * @return true if there is at least one provider available from processing requests.
     */
    public boolean check() {
        for (Provider registeredProvider : registeredProviders) {
            if (registeredProvider.check()) {
                if (excludedProviders.contains(registeredProvider)) {
                    if (!pendingIncludeProviders.add(registeredProvider)) {
                        unExcludeProvider(registeredProvider);
                    }
                }
            } else {
                excludeProvider(registeredProvider);
            }
        }
        return getRegisteredProviders().size() > 0;
    }

    private void checkLoadBalancerCapacity() {
        if (registeredProviders.size() == 10) {
            throw new RuntimeException("Cannot register more than 10 providers");
        }
    }

    private void checkCapacity() {
        checkMinCapacity();
        checkMaxCapacity();
    }

    private void checkMaxCapacity() {
        if (runningTasks.getAndIncrement() >= getCapacity()) {
            runningTasks.decrementAndGet();
            throw new RuntimeException("Capacity exceded");
        }
    }

    private void checkMinCapacity() {
        if (getRegisteredProviders().size() == 0) {
            throw new RuntimeException("No available providers found");
        }
    }

    private void unExcludeProvider(Provider registeredProvider) {
        pendingIncludeProviders.remove(registeredProvider);
        excludedProviders.remove(registeredProvider);
    }

    public int getCapacity() {
        return getRegisteredProviders().stream().mapToInt(Provider::getCapacity).sum();
    }
}
