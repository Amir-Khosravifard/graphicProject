package com.test.liftoff.Model.Entity;

public class SoulVessel {
    private int soul = 0;
    private static final int MAX_SOUL = 99;
    private static final int HEAL_COST = 33;

    public int getSoul() {
        return soul;
    }

    public void setSoul(int soul) {
        this.soul = Math.max(0, Math.min(MAX_SOUL, soul));
    }

    public void addSoul(int amount) {
        this.soul = Math.min(MAX_SOUL, this.soul + amount);
    }

    public boolean canAffordHeal() {
        return this.soul >= HEAL_COST;
    }

    public void consumeSoulForHeal() {
        if (canAffordHeal()) {
            this.soul -= HEAL_COST;
        }
    }
}
