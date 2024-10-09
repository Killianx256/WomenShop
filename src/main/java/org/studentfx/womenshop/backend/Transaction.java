package org.studentfx.womenshop.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Transaction {
    private int productId;
    private String transactionType;
    private int quantity;
    private double priceAtTransaction;

    public Transaction(int productId, String transactionType, int quantity, double priceAtTransaction) {
        this.productId = productId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.priceAtTransaction = priceAtTransaction;
    }

    public void saveToDatabase() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO Transaction (product_id, transaction_type, quantity, price_at_transaction) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, productId);
            stmt.setString(2, transactionType);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, priceAtTransaction);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
