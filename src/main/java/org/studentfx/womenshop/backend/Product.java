public abstract class Product implements Discount, Comparable<Product>
{

    private static int counter = 0;
    protected int number;
    protected String name;
    protected double purchasePrice;
    protected double sellPrice;
    protected double discountPrice;
    protected int nbItems;

    protected static double capital = 0;
    protected static double income = 0;
    protected static double cost = 0;

    public Product(String name, double purchasePrice, double sellPrice) {
        this.number = ++counter;
        this.name = name;
        if (purchasePrice < 0 || sellPrice < 0) {
            throw new IllegalArgumentException("Negative price!");
        }
        this.purchasePrice = purchasePrice;
        this.sellPrice = sellPrice;
        this.discountPrice = 0;
        this.nbItems = 0;
    }

    public String getName()
    {
        return name;
    }
    public double getPurchasePrice()
    {
        return purchasePrice;
    }

    public double getSellPrice()
    {
        return sellPrice;
    }
    public double getDiscountPrice()
    {
        return discountPrice;
    }

    public int getNbItems()
    {
        return nbItems;
    }

    public void setSellPrice(double sellPrice) {
        if (sellPrice < 0) throw new IllegalArgumentException("Negative price!");
        this.sellPrice = sellPrice;
    }

    public void setNbItems(int nbItems) {
        if (nbItems < 0) throw new IllegalArgumentException("Negative number of items!");
        this.nbItems = nbItems;
    }

    public void sell(int nbItems) {
        if (this.nbItems < nbItems) {
            throw new IllegalArgumentException("Product Unavailable");
        }
        this.nbItems -= nbItems;
        income += nbItems * this.sellPrice;
    }

    public void purchase(int nbItems) {
        if (nbItems < 0) throw new IllegalArgumentException("Negative number of items!");
        this.nbItems += nbItems;
        cost += nbItems * this.purchasePrice;
    }

    @Override
    public int compareTo(Product otherProduct) {
        return Double.compare(this.sellPrice, otherProduct.sellPrice);
    }

    @Override
    public String toString() {
        return "Product{" +
                "number=" + number +
                ", name='" + name + '\'' +
                ", sellPrice=" + sellPrice +
                ", discountPrice=" + discountPrice +
                ", nbItems=" + nbItems +
                '}';
    }
}
