package ch.zhaw.prog2.heartbeat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/*
 * Test Class for Pacemaker
 */
public class PacemakerTest {

    @Mock Heart heart;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        when(heart.setHeartRate(anyInt())).thenCallRealMethod();
    }

    /**
     * Test if setHeartRate does throw correct exception when rate is rejected (because frequency is out of range)
     * Exercise 2.3 d
     */
    @Test
    void testSetHeartRateRejectsFrequenciesOutOfRange() {
        Pacemaker pacemaker = new Pacemaker(heart);
        assertThrows(IllegalArgumentException.class, () -> pacemaker.setHeartRate(1));
    }


    /**
     * Test if setHeartRate does correctly set the rate when frequency is inside range
     * Exercise 2.3 d
     */
    @Test
    void testSetHeartRateAppliesFrequenciesInsideRange() {
        Pacemaker pacemaker = new Pacemaker(heart);
        assertDoesNotThrow(() -> pacemaker.setHeartRate(100));
    }

}
