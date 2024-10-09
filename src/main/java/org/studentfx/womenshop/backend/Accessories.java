package org.studentfx.womenshop.backend;

public class Accessories extends Product {

    public Accessories(String name, double purchasePrice, double sellPrice) {
        super(name, purchasePrice, sellPrice);
    }

    @Override
    public String toString() {
        return super.toString() + " Accessories{}";
    }
}
