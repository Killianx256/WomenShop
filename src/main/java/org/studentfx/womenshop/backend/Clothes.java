package org.studentfx.womenshop.backend;

public class Clothes extends Product {
    private int size;

    public Clothes(String name, double purchasePrice, double sellPrice, int size) {
        super(name, purchasePrice, sellPrice);
        setSize(size);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size < 34 || size > 54 || size % 2 != 0) {
            throw new IllegalArgumentException("Wrong size! Size should be between 34 and 54 and even.");
        }
        this.size = size;
    }

    @Override
    public String toString() {
        return super.toString() + " Clothes{" + "size=" + size + '}';
    }
}
