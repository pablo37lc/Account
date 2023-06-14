package com.hypnos.account;

public class MonthItem {

    String idx, date, income, outcome;

    public MonthItem(String idx, String date, String income, String outcome) {
        this.idx = idx;
        this.date = date;
        this.income = income;
        this.outcome = outcome;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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