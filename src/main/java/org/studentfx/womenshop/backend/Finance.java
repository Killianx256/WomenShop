package org.studentfx.womenshop.backend;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Finance {
    private double capital;
    private double globalIncome;
    private double globalCost;

    public Finance() {
        this.capital = 1000;
        this.globalIncome = 0.0;
        this.globalCost = 0.0;
    }

    public double getCapital() {
        return capital;
    }

    public double getGlobalIncome() {
        return globalIncome;
    }

    public double getGlobalCost() {
        return globalCost;
    }

    public void addIncome(double income) {
        this.globalIncome += income;
        this.capital += income;
        updateDatabase();
    }

    public void addCost(double cost) {
        this.globalCost += cost;
        this.capital -= cost;
        updateDatabase();
    }

    private void updateDatabase() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE Financial SET total_income = ?, total_cost = ?, capital = ?")) {

            stmt.setDouble(1, this.globalIncome);
            stmt.setDouble(2, this.globalCost);
            stmt.setDouble(3, this.capital);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void displayFinancials() {
        System.out.printf("Capital: %.2f, Global Income: %.2f, Global Cost: %.2f%n", capital, globalIncome, globalCost);
    }

    public void loadFromDatabase() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT total_income, total_cost, capital FROM Financial")) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.globalIncome = rs.getDouble("total_income");
                this.globalCost = rs.getDouble("total_cost");
                this.capital = rs.getDouble("capital");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initializeInDatabase() {
        try (Connection conn = DatabaseManager.getConnection()) {

            String checkQuery = "SELECT COUNT(*) AS count FROM Financial";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt("count");

            if (count == 0) {
                String insertQuery = "INSERT INTO Financial (capital, total_income, total_cost) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setDouble(1, 1000.00); // Initialisation du capital à 1000
                insertStmt.setDouble(2, 0.00);    // Revenus totaux initialisés à 0
                insertStmt.setDouble(3, 0.00);    // Coûts totaux initialisés à 0
                insertStmt.executeUpdate();
                System.out.println("Financial data initialized in the database.");
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

}
