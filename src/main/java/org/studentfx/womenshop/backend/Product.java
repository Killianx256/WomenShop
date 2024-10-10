package org.studentfx.womenshop.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Product implements Discount, Comparable<Product> {

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

    public String getName() {
        return name;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public int getNbItems() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT stock FROM Product WHERE name = ?")) {
            stmt.setString(1, this.name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                this.nbItems = rs.getInt("stock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.nbItems;
    }

    public int getProductId() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT product_id FROM Product WHERE name = ? AND (shoe_size = ? OR ? IS NULL) AND (clothing_size = ? OR ? IS NULL)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, getName());

            if (this instanceof Shoes) {
                int shoeSize = ((Shoes) this).getShoeSize();
                stmt.setInt(2, shoeSize);
                stmt.setInt(3, shoeSize);
                stmt.setNull(4, java.sql.Types.INTEGER);
                stmt.setNull(5, java.sql.Types.INTEGER);
            } else if (this instanceof Clothes) {
                int clothingSize = ((Clothes) this).getSize();
                stmt.setNull(2, java.sql.Types.INTEGER);
                stmt.setNull(3, java.sql.Types.INTEGER);
                stmt.setInt(4, clothingSize);
                stmt.setInt(5, clothingSize);
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
                stmt.setNull(3, java.sql.Types.INTEGER);
                stmt.setNull(4, java.sql.Types.INTEGER);
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("product_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public void setSellPrice(double sellPrice) {
        if (sellPrice < 0) {
            throw new IllegalArgumentException("Negative price!");
        }
        this.sellPrice = sellPrice;
        updateSellPriceAndDiscountInDatabase();
    }

    public void setNbItems(int nbItems) {
        if (nbItems < 0) {
            throw new IllegalArgumentException("Negative number of items!");
        }
        this.nbItems = nbItems;
        updateStockInDatabase();
    }

    public void sell(int nbItems) {
        if (this.nbItems < nbItems) {
            throw new IllegalArgumentException("Product unavailable");
        }
        this.nbItems -= nbItems;
        income += nbItems * this.sellPrice;
        updateStockInDatabase();
    }

    public void purchase(int nbItems) {
        if (nbItems < 0) {
            throw new IllegalArgumentException("Negative number of items!");
        }
        this.nbItems += nbItems;
        cost += nbItems * this.purchasePrice;
        updateStockInDatabase();
    }

    @Override
    public void applyDiscount(double discountPercentage) {
        if (discountPercentage > 0 && discountPercentage <= 100) {
            this.discountPrice = discountPercentage;
            this.sellPrice = this.sellPrice * (1 - discountPercentage / 100);
            updateSellPriceAndDiscountInDatabase();
        }
    }

    @Override
    public void removeDiscount() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT discount_price FROM Product WHERE name = ?")) {
            stmt.setString(1, this.name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                this.discountPrice = rs.getDouble("discount_price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (this.discountPrice > 0) {
            this.sellPrice = this.sellPrice / (1 - (this.discountPrice / 100));
            this.discountPrice = 0;
            updateSellPriceAndDiscountInDatabase();
            System.out.println("Discount removed. Restored selling price: " + this.sellPrice);
        } else {
            System.out.println("No discount to remove.");
        }
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

    // Database methods

    public void saveToDatabase() {
        String sql = "INSERT INTO Product (name, category_id, price, stock, cost_price, shoe_size, clothing_size) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, getName());
            stmt.setInt(2, getCategoryId());
            stmt.setDouble(3, getSellPrice());
            stmt.setInt(4, getNbItems());
            stmt.setDouble(5, getPurchasePrice());


            if (this instanceof Shoes) {
                stmt.setInt(6, ((Shoes) this).getShoeSize());
                stmt.setNull(7, java.sql.Types.INTEGER);
            } else if (this instanceof Clothes) {
                stmt.setNull(6, java.sql.Types.INTEGER);
                stmt.setInt(7, ((Clothes) this).getSize());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();
            System.out.println("Product saved to database successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected int getCategoryId() {
        if (this instanceof Shoes) {
            return 1;
        } else if (this instanceof Clothes) {
            return 2;
        } else if (this instanceof Accessories) {
            return 3;
        } else {
            throw new IllegalStateException("Unknown product type");
        }
    }

    protected void updateStockInDatabase() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE Product SET stock = ? WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, this.nbItems);
            stmt.setString(2, this.name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateSellPriceAndDiscountInDatabase() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE Product SET price = ?, discount_price = ? WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, this.sellPrice);
            stmt.setDouble(2, this.discountPrice);
            stmt.setString(3, this.name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static Product getProductByName(String name) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM Product WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String productName = rs.getString("name");
                double costPrice = rs.getDouble("cost_price");
                double sellPrice = rs.getDouble("price");
                double discountPrice = rs.getDouble("discount_price"); // Récupérer la remise
                int stock = rs.getInt("stock");
                int categoryId = rs.getInt("category_id");

                Product product;
                if (categoryId == 1) {
                    int shoeSize = rs.getInt("shoe_size");
                    product = new Shoes(productName, costPrice, sellPrice, shoeSize);
                } else if (categoryId == 2) {
                    int clothingSize = rs.getInt("clothing_size");
                    product = new Clothes(productName, costPrice, sellPrice, clothingSize);
                } else {
                    product = new Accessories(productName, costPrice, sellPrice);
                }

                product.setNbItems(stock);
                product.discountPrice = discountPrice;
                System.out.println("Produit récupéré : " + productName + ", Stock : " + stock);

                return product;
            } else {
                System.out.println("Produit non trouvé dans la base de données : " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Product getProductByNameAndSize(String name, Integer shoeSize, Integer clothingSize) {
        String query = "SELECT * FROM Product WHERE name = ? AND (shoe_size = ? OR ? IS NULL) AND (clothing_size = ? OR ? IS NULL)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setObject(2, shoeSize, java.sql.Types.INTEGER);
            stmt.setObject(3, shoeSize, java.sql.Types.INTEGER);
            stmt.setObject(4, clothingSize, java.sql.Types.INTEGER);
            stmt.setObject(5, clothingSize, java.sql.Types.INTEGER);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String productName = rs.getString("name");
                double costPrice = rs.getDouble("cost_price");
                double sellPrice = rs.getDouble("price");
                int stock = rs.getInt("stock");

                if (shoeSize != null) {
                    return new Shoes(productName, costPrice, sellPrice, shoeSize);
                } else if (clothingSize != null) {
                    return new Clothes(productName, costPrice, sellPrice, clothingSize);
                } else {
                    return new Accessories(productName, costPrice, sellPrice);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void deleteFromDatabase() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "DELETE FROM Product WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, this.name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
