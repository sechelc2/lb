package org.sechelc;

import org.junit.jupiter.api.Test;
import org.sechelc.provider.Provider;

import static org.junit.jupiter.api.Assertions.*;

class ProviderTest {
    public static final String UUID = "1";
    private Provider victim;

    @Test
    /**
     * step 1 test
     */
    void shouldReturnTheUIIDOnGet() {
        victim = new Provider(UUID);

        assertEquals(victim.get(), UUID);
    }
}