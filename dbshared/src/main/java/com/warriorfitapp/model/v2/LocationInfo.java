package com.warriorfitapp.model.v2;

/**
 * @author Andrii Kovalov
 */
public class LocationInfo {
    private Long id;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Float accuracy;
    private Float bearing;
    private Double speed;
    private Double pace;
    private Long timestamp;
    private LocationType type;
    private Double currentDistance;
    private Long exerciseHistoryRecordId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    public Float getBearing() {
        return bearing;
    }

    public void setBearing(Float bearing) {
        this.bearing = bearing;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getPace() {
        return pace;
    }

    public void setPace(Double pace) {
        this.pace = pace;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public LocationType getType() {
        return type;
    }

    public void setType(LocationType type) {
        this.type = type;
    }

    public Double getCurrentDistance() {
        return currentDistance;
    }

    public void setCurrentDistance(Double currentDistance) {
        this.currentDistance = currentDistance;
    }

    public Long getExerciseHistoryRecordId() {
        return exerciseHistoryRecordId;
    }

    public void setExerciseHistoryRecordId(Long exerciseHistoryRecordId) {
        this.exerciseHistoryRecordId = exerciseHistoryRecordId;
    }

    @Override
    public String toString() {
        return "LocationInfo{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", accuracy=" + accuracy +
                ", bearing=" + bearing +
                ", speed=" + speed +
                ", pace=" + pace +
                ", timestamp=" + timestamp +
                ", type=" + type +
                ", currentDistance=" + currentDistance +
                ", exerciseHistoryRecordId=" + exerciseHistoryRecordId +
                '}';
    }

    public enum LocationType {
        START, IN_PROGRESS, PAUSE, PAUSED, RESUME, FINISH
    }
}
