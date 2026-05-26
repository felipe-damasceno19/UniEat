package com.example.unieat.model;

import java.util.Map;

public class Coupon {

    private String code;
    private double value;
    private int maxUses;
    private Map<String, Boolean> usedBy;

    public Coupon() {}

    public Coupon(String code, double value, int maxUses) {
        this.code = code;
        this.value = value;
        this.maxUses = maxUses;
    }

    public String getCode()    { return code; }
    public double getValue()   { return value; }
    public int getMaxUses()    { return maxUses; }
    public Map<String, Boolean> getUsedBy() { return usedBy; }

    public void setCode(String code)       { this.code = code; }
    public void setValue(double value)     { this.value = value; }
    public void setMaxUses(int maxUses)    { this.maxUses = maxUses; }
    public void setUsedBy(Map<String, Boolean> usedBy) { this.usedBy = usedBy; }

    public int getUsedCount() { return usedBy != null ? usedBy.size() : 0; }
    public boolean isUsedBy(String userId) { return usedBy != null && usedBy.containsKey(userId); }
    public boolean isExhausted() { return getUsedCount() >= maxUses; }
}
