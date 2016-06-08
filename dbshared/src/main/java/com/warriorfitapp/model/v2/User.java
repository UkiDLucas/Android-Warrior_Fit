package com.warriorfitapp.model.v2;

/**
 * @author Andrii Kovalov
 */
public class User {
    private Long id;
    private Double weight;
    private Double height;
    private Double waist;
    private Double buttocks;
    private boolean isMale = true;
    private Long birthday;
    private String username;
    private String displayName;
    private Float currentBodyFat;
    private Float desiredBodyFat;
    private Integer age;
    private boolean active;
    private String imageUri;
    private AccountType accountType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean hasWeight() {
        return weight != null && weight > 0;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setIsMale(boolean isMale) {
        this.isMale = isMale;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean hasHeight() {
        return height != null && height > 0;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public boolean hasCurrentBodyFat() {
        return currentBodyFat != null;
    }

    public Float getCurrentBodyFat() {
        return currentBodyFat;
    }

    public void setCurrentBodyFat(Float currentBodyFat) {
        this.currentBodyFat = currentBodyFat;
    }

    public boolean hasDesiredBodyFat() {
        return desiredBodyFat != null;
    }

    public Float getDesiredBodyFat() {
        return desiredBodyFat;
    }

    public void setDesiredBodyFat(Float desiredBodyFat) {
        this.desiredBodyFat = desiredBodyFat;
    }

    public boolean hasAge() {
        return age != null && age > 0;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Double getButtocks() {
        return buttocks;
    }

    public void setButtocks(Double buttocks) {
        this.buttocks = buttocks;
    }

    public Double getWaist() {
        return waist;
    }

    public void setWaist(Double waist) {
        this.waist = waist;
    }

    public boolean hasWaist() {
        return waist != null;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", weight=" + weight +
                ", isMale=" + isMale +
                ", birthday=" + birthday +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", height=" + height +
                ", currentBodyFat=" + currentBodyFat +
                ", desiredBodyFat=" + desiredBodyFat +
                ", age=" + age +
                ", active=" + active +
                ", imageUri='" + imageUri + '\'' +
                ", accountType=" + accountType +
                ", male=" + isMale() +
                '}';
    }
}
