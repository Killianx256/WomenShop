import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {

        Product clothes1 = new Clothes("T-Shirt", 20, 30, 38);
        Product shoes1 = new Shoes("Sneakers", 50, 70, 42);
        Product accessory1 = new Accessories("Watch", 10, 25);

        ArrayList<Product> products = new ArrayList<>();
        products.add(clothes1);
        products.add(shoes1);
        products.add(accessory1);

        System.out.println("Before discount:");
        for (Product product : products) {
            System.out.println(product);
        }

        for (Product product : products) {
            product.applyDiscount();
        }

        System.out.println("\nAfter discount:");
        for (Product product : products) {
            System.out.println(product);
        }

        clothes1.purchase(10);
        shoes1.purchase(5);
        accessory1.purchase(15);

        clothes1.sell(3);
        shoes1.sell(1);

        System.out.println("\nStock after operations:");
        for (Product product : products) {
            System.out.println(product.getName() + " stock: " + product.getNbItems());
        }

        System.out.println("\nIncome: " + Product.income);
        System.out.println("Cost: " + Product.cost);
        System.out.println("Capital: " + (Product.capital + Product.income - Product.cost));

        Collections.sort(products);

        System.out.println("\nProducts sorted by price:");
        for (Product product : products) {
            System.out.println(product);
        }
    }
}
