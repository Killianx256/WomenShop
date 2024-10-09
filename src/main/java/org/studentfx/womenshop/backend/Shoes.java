package org.studentfx.womenshop.backend;

public class Shoes extends Product {
    private int shoeSize;

    public Shoes(String name, double purchasePrice, double sellPrice, int shoeSize) {
        super(name, purchasePrice, sellPrice);
        setShoeSize(shoeSize);
    }

    public int getShoeSize() {
        return shoeSize;
    }

    public void setShoeSize(int shoeSize) {
        if (shoeSize < 36 || shoeSize > 50) {
            throw new IllegalArgumentException("Wrong shoe size! Shoe size should be between 36 and 50.");
        }
        this.shoeSize = shoeSize;
    }

    @Override
    public String toString() {
        return super.toString() + " Shoes{" + "shoeSize=" + shoeSize + '}';
    }
}
