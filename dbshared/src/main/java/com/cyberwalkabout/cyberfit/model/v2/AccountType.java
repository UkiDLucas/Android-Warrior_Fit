package com.cyberwalkabout.cyberfit.model.v2;

/**
 * @author Andrii Kovalov
 */
public enum AccountType {
    LOCAL(-1), FACEBOOK(0), GOOGLE(1);

    private int type;

    AccountType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
