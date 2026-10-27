package Test;

import java.sql.*;

public class EmployeeSalaryJDBC {
    static final String DB_URL = "jdbc:mysql://localhost:3306/KrAkPayrollDB";
    static final String USER = "root";
    static final String PASS = "KrAk@6500";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            // 1. Create the table
            String createTableSQL = "CREATE TABLE IF NOT EXISTS EmployeeSalary (" +
                    "EmpID INT PRIMARY KEY, " +
                    "Name VARCHAR(50), " +
                    "BasicSalary DOUBLE, " +
                    "Allowances DOUBLE, " +
                    "GrossSalary DOUBLE, " +
                    "Deductions DOUBLE)";
            stmt.executeUpdate(createTableSQL);

            // 2. Clear the table before inserting (prevents Primary Key crash if you run this script twice)
            stmt.executeUpdate("TRUNCATE TABLE EmployeeSalary");

            // 3. Insert Records
            insertEmployee(conn, 6500, "Kumar Akshit", 65000.0, 15000.0);
            insertEmployee(conn, 6501, "Ishan", 36000.0, 8000.0);
            insertEmployee(conn, 6502, "Subhra Shefali", 58000.0, 12000.0);
            insertEmployee(conn, 6503, "Shristi Kumari", 52000.0, 14000.0);

            // 4. Update a record
            updateEmployeeSalary(conn, 6501, 44000.0, 9000.0);

            // 5. Query and format the output
            System.out.println("\n=== KrAk Family Tech: Employees with Gross Salary Above ₹50,000 ===");
            String selectSQL = "SELECT * FROM EmployeeSalary WHERE GrossSalary > 50000 ORDER BY GrossSalary DESC";

            try (ResultSet rs = stmt.executeQuery(selectSQL)) {
                // Print a clean, aligned table header
                System.out.printf("%-6s | %-16s | %-10s | %-10s | %-10s | %-10s | %-10s%n",
                        "ID", "Name", "Basic", "Allowances", "Gross", "Deductions", "Net Pay");
                System.out.println("-".repeat(90));

                // Print the rows
                while (rs.next()) {
                    double gross = rs.getDouble("GrossSalary");
                    double deductions = rs.getDouble("Deductions");
                    double netSalary = gross - deductions;

                    System.out.printf("%-6d | %-16s | ₹%-9.2f | ₹%-9.2f | ₹%-9.2f | ₹%-9.2f | ₹%-9.2f%n",
                            rs.getInt("EmpID"),
                            rs.getString("Name"),
                            rs.getDouble("BasicSalary"),
                            rs.getDouble("Allowances"),
                            gross,
                            deductions,
                            netSalary);
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void insertEmployee(Connection conn, int id, String name, double basic, double allowances) throws SQLException {
        double gross = basic + allowances;
        double deductions = gross * 0.10; // 10% standard deduction

        String insertSQL = "INSERT INTO EmployeeSalary (EmpID, Name, BasicSalary, Allowances, GrossSalary, Deductions) " +
                           "VALUES (?, ?, ?, ?, ?, ?)";
                           
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setDouble(3, basic);
            pstmt.setDouble(4, allowances);
            pstmt.setDouble(5, gross);
            pstmt.setDouble(6, deductions);
            
            pstmt.executeUpdate();
            System.out.println("Stored salary record for: " + name);
        }
    }

    public static void updateEmployeeSalary(Connection conn, int id, double newBasic, double newAllowances) throws SQLException {
        double newGross = newBasic + newAllowances;
        double newDeductions = newGross * 0.10;

        String updateSQL = "UPDATE EmployeeSalary SET BasicSalary = ?, Allowances = ?, GrossSalary = ?, Deductions = ? WHERE EmpID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setDouble(1, newBasic);
            pstmt.setDouble(2, newAllowances);
            pstmt.setDouble(3, newGross);
            pstmt.setDouble(4, newDeductions);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            System.out.println("Updated salary details for Employee ID: " + id);
        }
    }
}