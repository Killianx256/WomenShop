public class Accessories extends Product {

    public Accessories(String name, double purchasePrice, double sellPrice) {
        super(name, purchasePrice, sellPrice);
    }

    @Override
    public void applyDiscount() {
        this.discountPrice = this.sellPrice * 0.50;
    }

    @Override
    public String toString() {
        return super.toString() + " Accessories{}";
    }
}
