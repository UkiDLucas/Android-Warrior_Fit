package com.warriorfitapp.model.v2;

/**
 * @author Andrii Kovalov
 */
public class Exercise {
    private String id;
    private boolean active;
    private int displayOrder;
    private boolean trackDistance;
    private boolean trackRepetitions;
    private boolean trackTime;
    private boolean trackWeight;
    private boolean trackCalories;
    private String youtubeId;
    private String name;
    private String description;
    private boolean ignoreYoutubeText;
    private boolean mapRequired;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isTrackDistance() {
        return trackDistance;
    }

    public void setTrackDistance(boolean trackDistance) {
        this.trackDistance = trackDistance;
    }

    public boolean isTrackRepetitions() {
        return trackRepetitions;
    }

    public void setTrackRepetitions(boolean trackRepetitions) {
        this.trackRepetitions = trackRepetitions;
    }

    public boolean isTrackTime() {
        return trackTime;
    }

    public void setTrackTime(boolean trackTime) {
        this.trackTime = trackTime;
    }

    public boolean isTrackWeight() {
        return trackWeight;
    }

    public void setTrackWeight(boolean trackWeight) {
        this.trackWeight = trackWeight;
    }

    public boolean isTrackCalories() {
        return trackCalories;
    }

    public void setTrackCalories(boolean trackCalories) {
        this.trackCalories = trackCalories;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIgnoreYoutubeText() {
        return ignoreYoutubeText;
    }

    public void setIgnoreYoutubeText(boolean ignoreYoutubeText) {
        this.ignoreYoutubeText = ignoreYoutubeText;
    }

    public boolean isMapRequired() {
        return mapRequired;
    }

    public void setMapRequired(boolean mapRequired) {
        this.mapRequired = mapRequired;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", active=" + active +
                ", displayOrder=" + displayOrder +
                ", trackDistance=" + trackDistance +
                ", trackRepetitions=" + trackRepetitions +
                ", trackTime=" + trackTime +
                ", trackWeight=" + trackWeight +
                ", youtubeId='" + youtubeId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ignoreYoutubeText=" + ignoreYoutubeText +
                ", mapRequired=" + mapRequired +
                '}';
    }
}
