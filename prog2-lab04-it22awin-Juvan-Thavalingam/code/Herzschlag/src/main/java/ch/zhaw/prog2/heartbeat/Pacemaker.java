package ch.zhaw.prog2.heartbeat;
/**
 * This class represents a pacemaker for a human hart.
 *
 * This is an incomplete implementation, reduced to the essentials for the content of the lab.
 * There would be a lot more code needed to implement a pacemaker, but this is not part of this lab :-)
 *
 * @author muon
 *
 */
public class Pacemaker {
    Heart heart;

    /**
     * Constructor of Pacemaker
     * @param heart
     */
    public Pacemaker(Heart heart) {
        this.heart = heart;
    }

    /**
     * Sets the heart rate to the parameter value in bpm (beats per minute).
     * Rate has to be inside range >30 && <220
     * If frequency can be applied on heart, the current frequency will be returned.
     * If heart does reject the change the method throws an exception.
     *
     * @param frequencyInBpm heart rate to be applied on heart
     * @return current heart rate of the heart
     * @throws IllegalArgumentException when heart does reject the change
     */
    public int setHeartRate(int frequencyInBpm) {
       if(!heart.setHeartRate(frequencyInBpm)){
           throw new IllegalArgumentException("Frequency could not be set.");
       }
       return heart.getHeartRate();
    }

    /*
        ...
        There would be a lot more code needed to implement a pacemaker, but this is not part of this lab :-)
        ...
     */
}
