package com.rsmnm.Models;

public class CardItem {

    public String card_token;
    public String last_digits;
    public String id;


    public CardItem(String card_token, String last_digits) {
        this.card_token = card_token;
        this.last_digits = last_digits;
    }
}
