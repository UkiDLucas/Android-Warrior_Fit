package com.warriorfitapp.model.v2;

/**
 * @author Andrii Kovalov
 */
public class ExerciseSession {
    private Long id;
    private Integer repetitions;
    private Double distance;
    private Double weight;
    private Long time;
    private Long timestampStarted;
    private Long timestampCompleted;
    private Long lastTimestampStarted;
    private ExerciseState state;
    private String exerciseId;
    // private String youtubeId;
    private Double avgPace;
    private Double avgSpeed;
    private Double avgAltitude;
    private Double topAltitude;
    private Double lowestAltitude;
    private Double topSpeed;
    private Double topPace;
    private String userNote;
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean hasRepetitions() {
        return repetitions != null;
    }

    public Integer getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(Integer repetitions) {
        this.repetitions = repetitions;
    }

    public boolean hasDistance() {
        return distance != null;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public boolean hasWeight() {
        return weight != null;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public boolean hasTime() {
        return time != null;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTimestampCompleted() {
        return timestampCompleted;
    }

    public void setTimestampCompleted(Long timestampCompleted) {
        this.timestampCompleted = timestampCompleted;
    }

    public Long getTimestampStarted() {
        return timestampStarted;
    }

    public void setTimestampStarted(Long timestampStarted) {
        this.timestampStarted = timestampStarted;
    }

    public Long getLastTimestampStarted() {
        return lastTimestampStarted;
    }

    public void setLastTimestampStarted(Long lastTimestampStarted) {
        this.lastTimestampStarted = lastTimestampStarted;
    }

    public ExerciseState getState() {
        return state;
    }

    public void setState(ExerciseState state) {
        this.state = state;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Double getAvgPace() {
        return avgPace;
    }

    public void setAvgPace(Double avgPace) {
        this.avgPace = avgPace;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Double getAvgAltitude() {
        return avgAltitude;
    }

    public void setAvgAltitude(Double avgAltitude) {
        this.avgAltitude = avgAltitude;
    }

    public Double getTopAltitude() {
        return topAltitude;
    }

    public void setTopAltitude(Double topAltitude) {
        this.topAltitude = topAltitude;
    }

    public Double getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(Double topSpeed) {
        this.topSpeed = topSpeed;
    }

    public Double getTopPace() {
        return topPace;
    }

    public void setTopPace(Double topPace) {
        this.topPace = topPace;
    }

    public String getUserNote() {
        return userNote;
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getLowestAltitude() {
        return lowestAltitude;
    }

    public void setLowestAltitude(Double lowestAltitude) {
        this.lowestAltitude = lowestAltitude;
    }

    @Override
    public String toString() {
        return "ExerciseSession{" +
                "id=" + id +
                ", repetitions=" + repetitions +
                ", distance=" + distance +
                ", weight=" + weight +
                ", time=" + time +
                ", timestampStarted=" + timestampStarted +
                ", timestampCompleted=" + timestampCompleted +
                ", lastTimestampStarted=" + lastTimestampStarted +
                ", state=" + state +
                ", exerciseId=" + exerciseId +
                ", avgPace=" + avgPace +
                ", avgSpeed=" + avgSpeed +
                ", avgAltitude=" + avgAltitude +
                ", topAltitude=" + topAltitude +
                ", topSpeed=" + topSpeed +
                ", topPace=" + topPace +
                ", userNote='" + userNote + '\'' +
                '}';
    }
}
