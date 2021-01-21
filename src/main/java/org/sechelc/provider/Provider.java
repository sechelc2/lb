package org.sechelc.provider;

import java.util.Objects;

/**
 * Basic implementation of the ProviderInterface
 */
public class Provider implements ProviderInterface {
    private final String uuid;
    private int capacity;
    private boolean status = true;

    public Provider(String uuid) {
        this.uuid = uuid;
    }

    public Provider(int i, int capacity) {
        this.uuid = i + "";
        this.capacity = capacity;
    }

    /**
     *
     * @return the UUID of the provider.
     */
    @Override
    public String get() {
        simulateExecution();
        return uuid;
    }

    private void simulateExecution() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean check() {
        return status;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Provider provider = (Provider) o;
        return uuid.equals(provider.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "Provider{" +
                "uuid='" + uuid + '\'' +
                '}';
    }
}
