package com.example.mybank.Models;

public class Transaction {
    private  Double amount;
    private int user_id,_id;
    private  String date,recipient,description,type;

    public Transaction(Double amount, int user_id, int _id, String date, String recipient, String description, String type) {
        this.amount = amount;
        this.user_id = user_id;
        this._id = _id;
        this.date = date;
        this.recipient = recipient;
        this.description = description;
        this.type = type;
    }

    public Transaction() {

    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public int getUser_id() {
        return user_id;
    }

    public int get_id() {
        return _id;
    }

    public String getDate() {
        return date;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", user_id=" + user_id +
                ", _id=" + _id +
                ", date='" + date + '\'' +
                ", recipient='" + recipient + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
