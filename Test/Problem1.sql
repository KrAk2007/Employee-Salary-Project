CREATE DATABASE IF NOT EXISTS KrAkPayrollDB;
USE KrAkPayrollDB;

CREATE TABLE IF NOT EXISTS EmployeeSalary (
    EmpID INT PRIMARY KEY,
    Name VARCHAR(50),
    BasicSalary DOUBLE,
    Allowances DOUBLE,
    GrossSalary DOUBLE,
    Deductions DOUBLE
);