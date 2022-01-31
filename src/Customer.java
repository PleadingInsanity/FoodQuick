import javax.swing.*;
import java.sql.*;

/**
 * Customer class that controls the customer object.
 * <p>
 * Methods are called in the Main class
 */
public class Customer {

    //Customer Attributes
    private String name;
    private String surname;
    private String phone;
    private String deliveryAddress;
    private String city;
    private String email;

    //Customer Methods

    /**
     * Method that allows user to enter customers details into the customers table in the QuickFoodMS database
     */
    public static void enterCustomerDetails() {

        Customer customer = new Customer();

        try {
            //Connects to the QuickFoodMS database
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost;database=QuickFoodMS",
                    "Username",
                    "Password"
            );
            Statement statement = connection.createStatement();


            customer.name = JOptionPane.showInputDialog("Please enter the customer's first name:");

            customer.surname = JOptionPane.showInputDialog("Please enter the customer's surname:");

            customer.phone = JOptionPane.showInputDialog("Please enter the customer's contact number:");

            customer.deliveryAddress = JOptionPane.showInputDialog("Please enter the customer's delivery address:");

            customer.city = JOptionPane.showInputDialog("Please enter the customer's city:");

            customer.email = JOptionPane.showInputDialog("Please enter the customer's email address:");

            //inserts into the customers table in the database
            statement.executeUpdate("INSERT INTO customers (name, surname, phone, delivery_address, city, email) " +
                    "VALUES ('" + customer.name + "', '" + customer.surname + "', '" + customer.phone + "', '" + customer.deliveryAddress + "', '" + customer.city + "', '" + customer.email + "');");


            connection.close();

        } catch (SQLException e) {
            System.out.println("Error in enterCustomerDetails method");
            e.printStackTrace();
        }

    }

    /**
     * Method that allows user to update customer details into the customers table in the QuickFoodMS database
     * <p>
     * Method requires input from the user of the customer ID that they wish to change the details of.
     */
    public static void updateCustomerDetails() {

        Customer customer = new Customer();

        try {
            //Connects to the QuickFoodMS database
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost;database=QuickFoodMS",
                    "Username",
                    "Password"
            );
            Statement statement = connection.createStatement();

            //Asks user to enter ID of customer they wish to update the details of
            int id = Integer.parseInt(JOptionPane.showInputDialog("Please enter the id of the customer you wish to " +
                    "update:"));

            //Selects the customers table entry corresponding to the entered customer ID
            ResultSet customerResults = statement.executeQuery("SELECT * FROM customers WHERE id =" + id);

            //Boolean that controls whether a customer with entered ID exists
            boolean resultFound = false;

            //Reads results from SQL query and stores them in class attributes
            while (customerResults.next()) {
                resultFound = true;
                customer.name = customerResults.getString("name");
                customer.surname = customerResults.getString("surname");
                customer.phone = customerResults.getString("phone");
                customer.deliveryAddress = customerResults.getString("delivery_address");
                customer.city = customerResults.getString("city");
                customer.email = customerResults.getString("email");
            }

            String answer;

            /*If a customer with entered id was found then the current customer details are displayed and the option
            to change them is presented*/
            if (resultFound) {
                System.out.println("Found customer with id: " + id);

                answer = JOptionPane.showInputDialog("The current customer name is: " + customer.name + ". Would you " +
                        "like to change it y/n?");
                if (answer.equals("y")) {
                    customer.name = JOptionPane.showInputDialog("Please enter the customer's updated first name:");
                }

                answer = JOptionPane.showInputDialog("The current customer surname is: " + customer.surname + ". " +
                        "Would you like to change it y/n?");
                if (answer.equals("y")) {
                    customer.surname = JOptionPane.showInputDialog("Please enter the customer's updated surname:");
                }

                answer = JOptionPane.showInputDialog("The current customer phone number is: " + customer.phone + ". " +
                        "Would you like to change it y/n?");
                if (answer.equals("y")) {
                    customer.phone = JOptionPane.showInputDialog("Please enter the customer's updated contact number:");
                }

                answer =
                        JOptionPane.showInputDialog("The current customer delivery address is: " + customer.deliveryAddress + ". Would you like to change it y/n?");
                if (answer.equals("y")) {
                    customer.deliveryAddress = JOptionPane.showInputDialog("Please enter the customer's updated " +
                            "delivery address:");
                }

                answer = JOptionPane.showInputDialog("The current customer city is: " + customer.city + ". Would you " +
                        "like to change it y/n?");
                if (answer.equals("y")) {
                    customer.city = JOptionPane.showInputDialog("Please enter the customer's updated city:");
                }

                answer = JOptionPane.showInputDialog("The current customer email is: " + customer.email + ". Would " +
                        "you like to change it y/n?");
                if (answer.equals("y")) {
                    customer.email = JOptionPane.showInputDialog("Please enter the customer's updated email address:");
                }

                Statement statement2 = connection.createStatement();

                //Updates relevant entry in customers table based on newly entered or confirmed details
                int rowsAffected = statement2.executeUpdate("UPDATE customers SET name='" + customer.name + "', " +
                        "surname='" + customer.surname + "', phone='" + customer.phone + "', delivery_address='" + customer.deliveryAddress + "', city='" + customer.city + "', email='" + customer.email + "' WHERE id=" + id + ";");
                System.out.println("Query complete, " + rowsAffected + " rows updated.");

            } else {
                //Prints out message if no customer with entered ID is found
                System.out.println("No customer found with id: " + id);
            }


        } catch (SQLException e) {
            System.out.println("Error in updateCustomerDetails method");
            e.printStackTrace();
        }
    }
}
