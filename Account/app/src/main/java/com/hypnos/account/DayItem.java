package com.hypnos.account;

public class DayItem {

    String idx, tag, use, income, outcome;

    public DayItem(String idx, String tag, String use, String income, String outcome) {
        this.idx = idx;
        this.tag = tag;
        this.use = use;
        this.income = income;
        this.outcome = outcome;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
}
