//This class represents the invoice object. Its methods are called in the Main class. The invoice class generateInvoice
//method takes input of the restaurant, customer, driver and mealOrder objects.
//The class currently has no attributes.

import javax.swing.*;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Formatter;

/**
 * Invoice class that controls the invoice object
 */
public class Invoice {

    //Invoice attributes
    private int orderId;

    //Invoice methods

    /**
     * Method that allows user to generate a text file invoice and finalise an order in the orders table in the
     * QuickFoodMS database
     * <p>
     * Writes the text generated by the generateInvoiceText method to a text file named invoice[orderId]. User should
     * have completed the process in the correct order and checked for incomplete orders before this method is called.
     */
    public static void generateInvoice() {

        Invoice invoice = new Invoice();
        String invoiceText;

        try {

            //Asks user to input which order they would like to finalise
            invoice.orderId = Integer.parseInt(JOptionPane.showInputDialog("Enter the order number " +
                    "you wish to finalise:"));

            //Calls the generateInvoiceText method to prepare the text to be written to the invoice
            invoiceText = generateInvoiceText(invoice);


            //Formats invoice .txt file
            Formatter invoiceFormatter = new Formatter("invoice" + invoice.orderId + ".txt");

            //Writes generated invoice text to .txt file
            invoiceFormatter.format("%s", invoiceText);

            invoiceFormatter.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error formatting invoice.txt in getInvoice method");
        }

        try {
            //Connects to the QuickFoodMS database
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost;database=QuickFoodMS",
                    "Username",
                    "Password"
            );

            Statement statement = connection.createStatement();

            statement.executeUpdate("UPDATE orders SET order_status = 'COMPLETED', completion_date_time = " +
                    "current_timestamp WHERE id = " + invoice.orderId + ";");

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error finalising order in generateInvoice Method");
        }
    }

    /**
     * Method that generates the text required for the invoice generated in the generateInvoice method
     * <p>
     * Uses the orders table to find all required information about the customer, restaurant, items ordered and
     * driver associated with the order and writes them to a formatted string ready to be written to a .txt file
     *
     * @param invoice current invoice object needed to retrieve orderId
     * @return string to be written to text document
     */
    public static String generateInvoiceText(Invoice invoice) {

        String output = "";

        try {
            //Connects to the QuickFoodMS database
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost;database=QuickFoodMS",
                    "Username",
                    "Password"
            );

            Statement statement = connection.createStatement();

            //SQL query requesting all customer details associated with the relevant orderID
            ResultSet customerDetailsResults = statement.executeQuery("SELECT name, surname, phone, delivery_address," +
                    " email, city " +
                    "FROM orders " +
                    "INNER JOIN customers " +
                    "ON orders.customer_id = customers.id " +
                    "WHERE orders.id = " + invoice.orderId + ";");

            String customerName = "";
            String customerSurname = "";
            String customerPhone = "";
            String customerDeliveryAddress = "";
            String customerEmail = "";
            String customerCity = "";

            while (customerDetailsResults.next()) {
                customerName = customerDetailsResults.getString("name");
                customerSurname = customerDetailsResults.getString("surname");
                customerPhone = customerDetailsResults.getString("phone");
                customerDeliveryAddress = customerDetailsResults.getString("delivery_address");
                customerEmail = customerDetailsResults.getString("email");
                customerCity = customerDetailsResults.getString("city");
            }

            //SQL query requesting all restaurant details associated with the relevant orderID
            ResultSet restaurantDetailsResults = statement.executeQuery("SELECT name, city, phone FROM orders " +
                    "INNER JOIN restaurants " +
                    "ON orders.restaurant_id = restaurants.id " +
                    "WHERE orders.id = " + invoice.orderId + ";");

            String restaurantName = "";
            String restaurantCity = "";
            String restaurantPhone = "";

            while (restaurantDetailsResults.next()) {
                restaurantName = restaurantDetailsResults.getString("name");
                restaurantCity = restaurantDetailsResults.getString("city");
                restaurantPhone = restaurantDetailsResults.getString("phone");
            }

            //SQL query requesting all driver details associated with the relevant orderID
            ResultSet driverDetailsResults = statement.executeQuery("SELECT name, surname FROM orders " +
                    "INNER JOIN drivers " +
                    "ON orders.driver_id = drivers.id " +
                    "WHERE orders.id = " + invoice.orderId + ";");

            String driverName = "";
            String driverSurname = "";

            while (driverDetailsResults.next()) {
                driverName = driverDetailsResults.getString("name");
                driverSurname = driverDetailsResults.getString("surname");
            }

            //SQL query requesting all items ordered details associated with the relevant orderID
            ResultSet itemsOrdered = statement.executeQuery("SELECT item_name, cost, qty, special_instructions " +
                    "FROM items_ordered WHERE orders_id = " + invoice.orderId + ";");

            String itemsAndAmounts = "";

            /*Formats the items ordered into the relevant format for the invoice, adding each new item in as its
            result is read*/
            while (itemsOrdered.next()) {
                itemsAndAmounts += "\n" + itemsOrdered.getString("item_name") + " x " +
                        itemsOrdered.getInt("qty") +
                        " (R" + itemsOrdered.getBigDecimal("cost") + ")\n" +
                        "Special Instructions: " + itemsOrdered.getString("special_instructions");
            }

            //SQL query requesting the total cost associated with the relevant orderID
            ResultSet totalCostResults =
                    statement.executeQuery("SELECT total_cost FROM orders WHERE id = " + invoice.orderId + ";");

            BigDecimal totalCost = new BigDecimal(0);

            while (totalCostResults.next()) {
                totalCost = totalCostResults.getBigDecimal("total_cost");
            }

            //Creates a string with details to be written to the invoice text file
            output = "Order number " + invoice.orderId +
                    "\nCustomer: " + customerName + " " + customerSurname +
                    "\nEmail: " + customerEmail +
                    "\nPhone number: " + customerPhone +
                    "\nLocation: " + customerCity + "\n" +
                    "\nYou have ordered the following from " + restaurantName + " in " + restaurantCity + ":\n" +
                    itemsAndAmounts + "\n" +
                    "\nTotal: R" + String.format("%.2f", totalCost) + "\n" +
                    "\n" + driverName + " " + driverSurname + " is nearest to the restaurant and so they will be " +
                    "delivering your order to you at:" + "\n" +
                    "\n" + customerDeliveryAddress + "\n" +
                    "\nIf you need to contact the restaurant, their number is: " + restaurantPhone;


            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in generateInvoiceText Method");
        }

        return output;
    }

}