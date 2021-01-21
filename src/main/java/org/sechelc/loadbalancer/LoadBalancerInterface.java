package org.sechelc.loadbalancer;

import org.sechelc.provider.Provider;

public interface LoadBalancerInterface{
    boolean registerProvider(Provider provider);
    boolean excludeProvider(Provider provider);
}
