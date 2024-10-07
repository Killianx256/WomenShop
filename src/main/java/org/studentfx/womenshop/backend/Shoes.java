public class Shoes extends Product {
    private int shoeSize;

    public Shoes(String name, double purchasePrice, double sellPrice, int shoeSize) {
        super(name, purchasePrice, sellPrice);
        if (shoeSize < 36 || shoeSize > 50) {
            throw new IllegalArgumentException("Wrong shoe size!");
        }
        this.shoeSize = shoeSize;
    }

    public int getShoeSize() {
        return shoeSize;
    }

    public void setShoeSize(int shoeSize) {
        if (shoeSize < 36 || shoeSize > 50) {
            throw new IllegalArgumentException("Wrong shoe size!");
        }
        this.shoeSize = shoeSize;
    }

    @Override
    public void applyDiscount() {
        this.discountPrice = this.sellPrice * 0.80;
    }

    @Override
    public String toString() {
        return super.toString() + " Shoes{" + "shoeSize=" + shoeSize + '}';
    }
}
