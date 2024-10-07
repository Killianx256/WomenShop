public class Clothes extends Product {
    private int size;

    public Clothes(String name, double purchasePrice, double sellPrice, int size) {
        super(name, purchasePrice, sellPrice);
        if (size < 34 || size > 54 || size % 2 != 0) {
            throw new IllegalArgumentException("Wrong size!");
        }
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size < 34 || size > 54 || size % 2 != 0) {
            throw new IllegalArgumentException("Wrong size!");
        }
        this.size = size;
    }

    @Override
    public void applyDiscount() {
        this.discountPrice = this.sellPrice * 0.70; // 30% discount
    }

    @Override
    public String toString() {
        return super.toString() + " Clothes{" + "size=" + size + '}';
    }
}
