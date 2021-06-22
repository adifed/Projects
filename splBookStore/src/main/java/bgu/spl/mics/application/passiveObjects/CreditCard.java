package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class CreditCard implements Serializable {
    private int number;
    private int amount;

    public CreditCard (int n, int a) {
        number = n;
        amount = a;
    }

    public int getNumber() {
        return number;
    }

    public int getAmount() {
        return amount;
    }
}
