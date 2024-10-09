package org.studentfx.womenshop.backend;

public interface Discount {
    void applyDiscount(double discountPercentage);
    void removeDiscount();
}
