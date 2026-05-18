package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the incentive bonus returned by the Incentive API for a valid transaction
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Incentive {

    private float amount;

    public Incentive() {}

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
