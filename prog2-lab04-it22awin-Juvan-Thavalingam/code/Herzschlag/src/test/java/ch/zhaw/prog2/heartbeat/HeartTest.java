/*
 * Test Class for Heart
 */
package ch.zhaw.prog2.heartbeat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.zhaw.prog2.heartbeat.parts.Valve;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.zhaw.prog2.heartbeat.Heart.State;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

class HeartTest {

    @Mock
    Half rightHalf;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    /**
     * This is a very simple test to check if Junit and Mockito are properly set up.
     */
    @Test
    void testTheTest() {
        Heart classUnderTest = new Heart();
        assertNotNull(classUnderTest.getState(), "The heart must have a state.");
    }

    /**
     * Tests a single heartbeat
     */
    @Test
    void testHeartBeat() throws Heart.HeartBeatDysfunctionException, Valve.IllegalValveStateException {
        Heart heart = new Heart();
        State startState = heart.getState();

        heart.executeHeartBeat();

        // after one heartbeat, the heart must be in the same state as before
        assertEquals(startState, heart.getState());
    }

    /**
     * Tests if the valves are open or closed depending on the status of the heart
     */
    @Test
    void testValveStatus() throws Heart.HeartBeatDysfunctionException, Valve.IllegalValveStateException {
        Heart heart = new Heart();

        heart.executeHeartBeat();

        State state = heart.getState();

        if (state.equals(Heart.State.DIASTOLE)) {
            for (Half half : heart.getHalves()) {
                assertFalse(half.isAtrioventricularValveOpen());
                assertTrue(half.isSemilunarValveOpen());
            }
        } else if ((state.equals(Heart.State.SYSTOLE))) {
            for (Half half : heart.getHalves()) {
                assertTrue(half.isAtrioventricularValveOpen());
                assertFalse(half.isSemilunarValveOpen());
            }
        }
    }

    /**
     * Tests if the hart throws the appropriate Exception, when malfunction was detected during hartBeat
     */
    @Test
    void testExecuteHeartBeatErrorBehavior() {
        Heart heart = new Heart();
        // prepare error situation due to wrong initialization
        heart.setState(State.SYSTOLE);

        assertThrows(Heart.HeartBeatDysfunctionException .class, // verification using lambda
            () -> heart.executeHeartBeat());
    }

    /**
     * Tests if the hart throws the appropriate Exception, when malfunction was detected during hartBeat
     * with exception Stubbing using Mock-Objekts. Exercise 2.3 b.
     */
    @Test
    void testExecuteHeartBeatErrorBehaviorWithMocking() throws Valve.IllegalValveStateException {
        Half leftHalf = mock(Half.class);

        Heart heart = new Heart(leftHalf, rightHalf);
        heart.setState(State.SYSTOLE);

        doThrow(Valve.IllegalValveStateException.class).when(leftHalf).openAtrioventricularValve();
        assertThrows(Heart.HeartBeatDysfunctionException.class, heart::executeHeartBeat);
    }

    /**
     * We test if Heart::executeHeartbeat() sends the right signals to both of its
     * halves. Exercise 2.3 a
     *
     * When Half::contractVentricle() is called, Half::closeAtrioventricularValve()
     * and Half::openSemilunarValve() must have been called earlier.
     *
     */
    @Test
    void testValvesBehavior() throws Valve.IllegalValveStateException, Heart.HeartBeatDysfunctionException {
        Half leftHalf = mock(Half.class);

        InOrder inOrderLeft = inOrder(leftHalf);
        InOrder inOrderRight = inOrder(rightHalf);

        Heart heart = new Heart(leftHalf, rightHalf);

        heart.setState(State.DIASTOLE);
        heart.executeHeartBeat();

        inOrderLeft.verify(leftHalf).openSemilunarValve();
        inOrderLeft.verify(leftHalf).closeAtrioventricularValve();
        inOrderLeft.verify(leftHalf).contractVentricle();

        inOrderRight.verify(rightHalf).openSemilunarValve();
        inOrderRight.verify(rightHalf).closeAtrioventricularValve();
        inOrderRight.verify(rightHalf).contractVentricle();

        inOrderLeft.verifyNoMoreInteractions();
        inOrderRight.verifyNoMoreInteractions();
    }


    /**
     * Tests if an exception is thrown in the diastole if the valve position is wrong.
     * Exercise 2.3 c
     */
    @Test
    void testDiastoleException () {
        Half leftHalf = new Half(Half.Side.LEFT);

        Heart heart = new Heart(leftHalf, rightHalf);
        heart.setState(State.SYSTOLE);

        assertThrows(Valve.IllegalValveStateException.class, heart::executeDiastole);
    }

    /**
     * Tests if an exception is thrown in the systole if the valve position is wrong.
     * Exercise 2.3 c
     */
    @Test
    void testSystoleException () {
        Half leftHalf = new Half(Half.Side.LEFT);

        Heart heart = new Heart(leftHalf, rightHalf);
        heart.setState(State.SYSTOLE);

        assertThrows(Valve.IllegalValveStateException.class, heart::executeSystole);
    }

}
