package com.example.mybank.Models;

public class Investment {
    private int _id,user_id,transaction_id;
    private Double amount,monthly_roi;
    private String init_date,finish_date,name;

    public Investment() {
    }

    public Investment(int _id, int user_id, int transaction_id, Double amount, Double monthly_roi, String init_date, String finish_date, String name) {
        this._id = _id;
        this.user_id = user_id;
        this.transaction_id = transaction_id;
        this.amount = amount;
        this.monthly_roi = monthly_roi;
        this.init_date = init_date;
        this.finish_date = finish_date;
        this.name = name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getMonthly_roi() {
        return monthly_roi;
    }

    public void setMonthly_roi(Double monthly_roi) {
        this.monthly_roi = monthly_roi;
    }

    public String getInit_date() {
        return init_date;
    }

    public void setInit_date(String init_date) {
        this.init_date = init_date;
    }

    public String getFinish_date() {
        return finish_date;
    }

    public void setFinish_date(String finish_date) {
        this.finish_date = finish_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Investment{" +
                "_id=" + _id +
                ", user_id=" + user_id +
                ", transaction_id=" + transaction_id +
                ", amount=" + amount +
                ", monthly_roi=" + monthly_roi +
                ", init_date='" + init_date + '\'' +
                ", finish_date='" + finish_date + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
