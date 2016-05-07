package com.cyberwalkabout.cyberfit.model.v2;

/**
 * @author Andrii Kovalov
 */
public class SocialProfile {
    private Long id;
    private Long userId;
    private String socialId;
    private AccountType type;
    private String url;
    private String email;
    private String token;
    private boolean primary;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public String toString() {
        return "SocialProfile{" +
                "id=" + id +
                ", userId=" + userId +
                ", socialId='" + socialId + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", primary=" + primary +
                '}';
    }
}
