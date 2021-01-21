package org.sechelc.loadbalancer;

import org.sechelc.provider.Provider;
import org.sechelc.provider.ProviderInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseLoadBalancer implements LoadBalancerInterface, ProviderInterface {
    private List<Provider> registeredProviders = new CopyOnWriteArrayList<>();
    private Set<Provider> excludedProviders = new CopyOnWriteArraySet<>();
    private Set<Provider> pendingIncludeProviders = new CopyOnWriteArraySet<>();
    private int checkFrequency = 10;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private AtomicInteger runningTasks = new AtomicInteger(0);

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

    @Override
    public boolean registerProvider(Provider provider) {
        if (registeredProviders.size() == 10) {
            throw new RuntimeException("Cannot register more than 10 providers");
        }

        if (registeredProviders.contains(provider)) {
            return false;
        }

        return registeredProviders.add(provider);
    }

    @Override
    public boolean excludeProvider(Provider provider) {
        pendingIncludeProviders.remove(provider);
        return excludedProviders.add(provider);
    }

    public List<Provider> getRegisteredProviders() {
        List<Provider> differences = new ArrayList<>(registeredProviders);
        differences.removeAll(excludedProviders);
        return differences;
    }

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

    private void unExcludeProvider(Provider registeredProvider) {
        pendingIncludeProviders.remove(registeredProvider);
        excludedProviders.remove(registeredProvider);
    }

    public void checkCapacity() {
        if (runningTasks.getAndIncrement() >= getCapacity()) {
            runningTasks.decrementAndGet();
            throw new RuntimeException("Capacity exceded");
        }
    }

    public void setCheckFrequency(int checkFrequency) {
        this.checkFrequency = checkFrequency;
    }

    public int getCapacity() {
        return getRegisteredProviders().stream().mapToInt(Provider::getCapacity).sum();
    }
}
