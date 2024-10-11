package org.studentfx.womenshop.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Finance finance = new Finance();
        finance.initializeInDatabase();
        finance.loadFromDatabase();
        boolean running = true;

        while (running) {
            System.out.println("\n=== MENU ===");
            System.out.println("1. Add a new product");
            System.out.println("2. Add stock to a product");
            System.out.println("3. Remove stock from a product");
            System.out.println("4. Purchase a product");
            System.out.println("5. Sell a product");
            System.out.println("6. Apply a discount");
            System.out.println("7. Remove a discount");
            System.out.println("8. View stock");
            System.out.println("9. View financials");
            System.out.println("10. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addProduct(scanner, finance);
                    break;
                case 2:
                    modifyStock(scanner, true);
                    break;
                case 3:
                    modifyStock(scanner, false);
                    break;
                case 4:
                    purchaseProduct(scanner, finance);
                    break;
                case 5:
                    sellProduct(scanner, finance);
                    break;
                case 6:
                    modifyDiscount(scanner, true);
                    break;
                case 7:
                    modifyDiscount(scanner, false);
                    break;
                case 8:
                    viewStock();
                    break;
                case 9:
                    finance.displayFinancials();
                    break;
                case 10:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private static void addProduct(Scanner scanner, Finance finance) {
        System.out.println("\n=== Add a New Product ===");
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();
        System.out.print("Enter purchase price: ");
        double purchasePrice = scanner.nextDouble();
        System.out.print("Enter selling price: ");
        double sellPrice = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("Select product type:");
        System.out.println("1. Shoes");
        System.out.println("2. Clothes");
        System.out.println("3. Accessories");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter initial stock quantity: ");
        int initialStock = scanner.nextInt();
        scanner.nextLine();

        Product product;
        switch (choice) {
            case 1:
                System.out.print("Enter shoe size (36-50): ");
                int shoeSize = scanner.nextInt();
                scanner.nextLine();
                product = new Shoes(name, purchasePrice, sellPrice, shoeSize);
                break;
            case 2:
                System.out.print("Enter clothing size (34-54, even numbers only): ");
                int size = scanner.nextInt();
                scanner.nextLine();
                product = new Clothes(name, purchasePrice, sellPrice, size);
                break;
            case 3:
                product = new Accessories(name, purchasePrice, sellPrice);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                return;
        }

        product.setNbItems(initialStock);
        product.saveToDatabase();
        System.out.println("Product added successfully: " + product);

        double totalCost = purchasePrice * initialStock;


        Transaction transaction = new Transaction(product.getProductId(), "purchase", initialStock, product.getPurchasePrice());
        transaction.saveToDatabase();

        finance.addCost(totalCost);
        updateFinancialsInDatabase(finance);

        System.out.println("Financial data updated: Total Cost added = " + totalCost);
    }



    private static void modifyStock(Scanner scanner, boolean addStock) {
        System.out.println("\n=== " + (addStock ? "Add Stock" : "Remove Stock") + " ===");
        System.out.print("Enter the product name: ");
        String name = scanner.nextLine();

        System.out.println("Is the product type Shoes or Clothes?");
        System.out.println("1. Shoes");
        System.out.println("2. Clothes");
        System.out.println("3. Neither");
        int choice = scanner.nextInt();
        scanner.nextLine();

        Integer shoeSize = null;
        Integer clothingSize = null;

        if (choice == 1) {
            System.out.print("Enter the shoe size: ");
            shoeSize = scanner.nextInt();
            scanner.nextLine();
        } else if (choice == 2) {
            System.out.print("Enter the clothing size: ");
            clothingSize = scanner.nextInt();
            scanner.nextLine();
        }

        System.out.print("Enter the quantity to " + (addStock ? "add" : "remove") + ": ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        Product product = Product.getProductByNameAndSize(name, shoeSize, clothingSize);
        if (product != null) {
            int currentStock = product.getNbItems();
            System.out.println("Stock actuel : " + currentStock);

            int newStock = addStock ? currentStock + quantity : currentStock - quantity;

            if (newStock < 0) {
                System.out.println("Not enough stock to remove that amount.");
                return;
            }

            if (shoeSize != null) {
                updateStockInDatabase(name, shoeSize, null, newStock);
            } else if (clothingSize != null) {
                updateStockInDatabase(name, null, clothingSize, newStock);
            } else {
                updateStockInDatabase(name, null, null, newStock);
            }

            product.setNbItems(newStock);
            System.out.println("Stock " + (addStock ? "added." : "removed.") + ". Updated product: " + product);
        } else {
            System.out.println("Product not found.");
        }
    }

    private static void updateStockInDatabase(String productName, Integer shoeSize, Integer clothingSize, int newStock) {
        String updateQuery;

        if (shoeSize != null) {
            updateQuery = "UPDATE Product SET stock = ? WHERE name = ? AND shoe_size = ?";
        } else if (clothingSize != null) {
            updateQuery = "UPDATE Product SET stock = ? WHERE name = ? AND clothing_size = ?";
        } else {
            updateQuery = "UPDATE Product SET stock = ? WHERE name = ?";
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setInt(1, newStock);
            stmt.setString(2, productName);

            if (shoeSize != null) {
                stmt.setInt(3, shoeSize);
            } else if (clothingSize != null) {
                stmt.setInt(3, clothingSize);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Database stock updated successfully for product: " + productName);
            } else {
                System.out.println("No matching product found for stock update.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateFinancialsInDatabase(Finance finance) {
        String updateQuery = "UPDATE Financial SET total_income = ?, total_cost = ?, capital = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setDouble(1, finance.getGlobalIncome());
            stmt.setDouble(2, finance.getGlobalCost());
            stmt.setDouble(3, finance.getCapital());
            stmt.executeUpdate();

            System.out.println("Financial data updated successfully in the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void purchaseProduct(Scanner scanner, Finance finance) {
        System.out.println("\n=== Purchase a Product ===");
        System.out.print("Enter the product name to purchase: ");
        String name = scanner.nextLine();
        System.out.print("Enter the quantity to purchase: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        Product product = Product.getProductByName(name);
        if (product != null) {
            Integer shoeSize = null;
            Integer clothingSize = null;

            if (product instanceof Shoes) {
                shoeSize = ((Shoes) product).getShoeSize();
            } else if (product instanceof Clothes) {
                clothingSize = ((Clothes) product).getSize();
            }

            double totalCost = product.getPurchasePrice() * quantity;
            int newStock = product.getNbItems() + quantity;

            updateStockInDatabase(name, shoeSize, clothingSize, newStock);

            product.setNbItems(newStock);

            Transaction transaction = new Transaction(product.getProductId(), "purchase", quantity, product.getPurchasePrice());
            transaction.saveToDatabase();

            finance.addCost(totalCost);
            updateFinancialsInDatabase(finance);

            System.out.println("Product purchased successfully. New stock: " + newStock);
        } else {
            System.out.println("Product not found.");
        }
    }

    private static void sellProduct(Scanner scanner, Finance finance) {
        System.out.println("\n=== Sell a Product ===");
        System.out.print("Enter the product name to sell: ");
        String name = scanner.nextLine();
        System.out.print("Enter the quantity to sell: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        Product product = Product.getProductByName(name);
        if (product != null) {
            int currentStock = product.getNbItems();

            if (quantity <= currentStock) {
                double totalIncome = product.getSellPrice() * quantity;
                int newStock = currentStock - quantity;

                if (product instanceof Shoes) {
                    updateStockInDatabase(product.getName(), ((Shoes) product).getShoeSize(), null, newStock);
                } else if (product instanceof Clothes) {
                    updateStockInDatabase(product.getName(), null, ((Clothes) product).getSize(), newStock);
                } else {
                    updateStockInDatabase(product.getName(), null, null, newStock);
                }

                product.setNbItems(newStock);

                saveFinancialTransaction(product, "sale", quantity, product.getSellPrice(), finance);
                System.out.println("Product sold successfully. Updated stock: " + product.getNbItems());

                updateFinancialsInDatabase(finance);
            } else {
                System.out.print(currentStock);
                System.out.println("Not enough stock to sell that amount.");
            }
        } else {
            System.out.println("Product not found.");
        }
    }
    private static void saveFinancialTransaction(Product product, String transactionType, int quantity, double priceAtTransaction, Finance finance) {
        int productId = product.getProductId();
        if (productId == -1) {
            System.out.println("Product ID not found, cannot save financial transaction.");
            return;
        }

        Transaction transaction = new Transaction(productId, transactionType, quantity, priceAtTransaction);

        transaction.saveToDatabase();

        if (transactionType.equals("sale")) {
            finance.addIncome(priceAtTransaction * quantity);
        } else if (transactionType.equals("purchase")) {
            finance.addCost(priceAtTransaction * quantity);
        }

        updateFinancialsInDatabase(finance);
    }


    private static void modifyDiscount(Scanner scanner, boolean applyDiscount) {
        System.out.println("\n=== " + (applyDiscount ? "Apply Discount" : "Remove Discount") + " ===");
        System.out.print("Enter the product name: ");
        String name = scanner.nextLine();

        Product product = Product.getProductByName(name);

        if (product != null) {
            if (applyDiscount) {
                System.out.print("Enter discount percentage: ");
                double discount = scanner.nextDouble();
                scanner.nextLine();
                product.applyDiscount(discount);
                System.out.println("Discount applied. Updated product: " + product);
            } else {
                product.removeDiscount();
                System.out.println("Discount removed. Updated product: " + product);
            }
        } else {
            System.out.println("Product not found.");
        }
    }

    private static void viewStock() {
        System.out.println("\n=== Current Stock by Product Type ===");

        boolean shoesHeaderPrinted = false;
        boolean clothesHeaderPrinted = false;
        boolean accessoriesHeaderPrinted = false;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name, category_id, price, discount_price, stock, shoe_size, clothing_size FROM Product");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                int categoryId = rs.getInt("category_id");
                double price = rs.getDouble("price");
                double discountPrice = rs.getDouble("discount_price");
                int stock = rs.getInt("stock");

                Product product;
                switch (categoryId) {
                    case 1:
                        if (!shoesHeaderPrinted) {
                            System.out.println("\n--- Shoes ---");
                            shoesHeaderPrinted = true;
                        }
                        int shoeSize = rs.getInt("shoe_size");
                        product = new Shoes(name, price, discountPrice, shoeSize);
                        product.setNbItems(stock);
                        System.out.printf("Product: %s, Price: %.2f, Discount Price: %.2f, Stock: %d%n",
                                name, price, discountPrice, stock);
                        break;
                    case 2:
                        if (!clothesHeaderPrinted) {
                            System.out.println("\n--- Clothes ---");
                            clothesHeaderPrinted = true;
                        }
                        int clothingSize = rs.getInt("clothing_size");
                        product = new Clothes(name, price, discountPrice, clothingSize);
                        product.setNbItems(stock);
                        System.out.printf("Product: %s, Price: %.2f, Discount Price: %.2f, Stock: %d%n",
                                name, price, discountPrice, stock);
                        break;
                    case 3:
                        if (!accessoriesHeaderPrinted) {
                            System.out.println("\n--- Accessories ---");
                            accessoriesHeaderPrinted = true;
                        }
                        product = new Accessories(name, price, discountPrice);
                        product.setNbItems(stock);
                        System.out.printf("Product: %s, Price: %.2f, Discount Price: %.2f, Stock: %d%n",
                                name, price, discountPrice, stock);
                        break;
                    default:
                        System.out.println("Unknown category for product: " + name);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void removeProduct(Scanner scanner) {
        System.out.println("\n=== Remove Product ===");
        System.out.print("Enter the product name: ");
        String name = scanner.nextLine();

        System.out.println("Is the product type Shoes or Clothes?");
        System.out.println("1. Shoes");
        System.out.println("2. Clothes");
        System.out.println("3. Neither");
        int choice = scanner.nextInt();
        scanner.nextLine();

        Integer shoeSize = null;
        Integer clothingSize = null;

        if (choice == 1) {
            System.out.print("Enter the shoe size: ");
            shoeSize = scanner.nextInt();
            scanner.nextLine();
        } else if (choice == 2) {
            System.out.print("Enter the clothing size: ");
            clothingSize = scanner.nextInt();
            scanner.nextLine();
        }

        deleteProductFromDatabase(name, shoeSize, clothingSize);
    }

    private static void deleteProductFromDatabase(String name, Integer shoeSize, Integer clothingSize) {
        String deleteQuery = "DELETE FROM Product WHERE name = ? AND (shoe_size = ? OR ? IS NULL) AND (clothing_size = ? OR ? IS NULL)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setString(1, name);
            stmt.setObject(2, shoeSize, java.sql.Types.INTEGER);
            stmt.setObject(3, shoeSize, java.sql.Types.INTEGER);
            stmt.setObject(4, clothingSize, java.sql.Types.INTEGER);
            stmt.setObject(5, clothingSize, java.sql.Types.INTEGER);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.printf("Product '%s' with specified size successfully deleted from the database.%n", name);
            } else {
                System.out.println("No matching product found for deletion.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}