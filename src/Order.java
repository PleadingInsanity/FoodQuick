import javax.swing.*;
import java.math.BigDecimal;
import java.sql.*;

/**
 * Order class that controls the order object
 */
public class Order {

    //Order Attributes
    private int customerId;
    private int restaurantId;
    private int orderId;

    //Order Methods

    /**
     * Method that allows user to enter item details into the items_ordered table in the QuickFoodMS database and
     * create an entry in the orders table
     */
    public static void createOrder() {

        Order order = new Order();

        try {
            //Connects to the QuickFoodMS database
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost;database=QuickFoodMS",
                    "Username",
                    "Password"
            );

            //Asks user to enter ID of customer they wish to assign to the order
            order.customerId = Integer.parseInt(JOptionPane.showInputDialog("Please enter the ID of " +
                    "the customer placing the order:"));
            //Asks user to enter ID of restaurant they wish to assign to the order
            order.restaurantId = Integer.parseInt(JOptionPane.showInputDialog("Please enter the ID of " +
                    "restaurant the customer will be placing the order with:"));

            Statement statement = connection.createStatement();

            //Creates a new entry in the orders table in the QuickFoodMS database with the customer and restaurant ID
            int rowsAffected =
                    statement.executeUpdate("INSERT INTO orders (customer_id, restaurant_id) VALUES ("
                            + order.customerId + ", " + order.restaurantId + ");");
            System.out.println("Query complete, " + rowsAffected + " rows added.");

            //Selects the most recent (highest ID) order in the orders table and assigns that ID value as the orderID
            ResultSet orderIdResults = statement.executeQuery("SELECT MAX(id) AS MaxOrderId FROM orders;");
            while (orderIdResults.next()) {
                order.orderId = orderIdResults.getInt("MaxOrderId");
            }

            //Variables to be entered into the items_ordered table in the QuickFoodMS database
            String itemName;
            BigDecimal cost;
            int qty;
            String specialInstructions;

            String orderAgain;
            int rowsAffected3;

            /*Asks user for details on the food items that need to be ordered. The user can order as many as needed.
            Each is stored as a separate entry in the items_ordered table in the QuickFoodMS database.*/
            do {
                itemName = JOptionPane.showInputDialog("Which meal would you like to order?");

                cost = new BigDecimal(JOptionPane.showInputDialog("How much does this meal cost?"));

                qty = Integer.parseInt(JOptionPane.showInputDialog("How many of this item would you like to order?"));

                specialInstructions = JOptionPane.showInputDialog("Enter any special preparation instructions for " +
                        "this item:");

                //Enters a single food item order into the items_ordered table in the QuickFoodMS database
                statement.executeUpdate("INSERT INTO items_ordered (orders_id, item_name, cost, qty, " +
                        "special_instructions) " +
                        "VALUES (" + order.orderId + ", '" + itemName + "', " + cost + ", " + qty + ", '" +
                        specialInstructions + "');");

                orderAgain = JOptionPane.showInputDialog("Would you like to place another order y/n?");

            } while (orderAgain.equals("y"));


            /*Updates the relevant entry in the orders table with the total cost (summed in the SQL query) from all
            items ordered under this order ID*/
            rowsAffected3 = statement.executeUpdate("UPDATE orders " +
                    "SET total_cost = (" +
                    "SELECT SUM(items_ordered.cost*items_ordered.qty) " +
                    "FROM items_ordered WHERE orders_id = " + order.orderId + ") " +
                    "WHERE id = " + order.orderId + ";");

            System.out.println("Query complete, " + rowsAffected3 + " rows updated.");

            /*Sets order with relevant order ID's status in to ORDER PLACED in the orders table in the QuickFoodMS
            database*/
            statement.executeUpdate("UPDATE orders SET order_status = 'ORDER PLACED' " +
                    "WHERE id = " + order.orderId + ";");

            connection.close();

        } catch (SQLException e) {
            System.out.println("Error in createOrder method");
            e.printStackTrace();
        }

    }

    /**
     * Method that allows user to update item details into the items_ordered table in the QuickFoodMS database and
     * update the corresponding entry in the orders table
     * <p>
     * Method requires input from the user of the order ID that they wish to change the details of.
     */
    public static void updateOrder() {

        try {
            //Connects to the QuickFoodMS database
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost;database=QuickFoodMS",
                    "Username",
                    "Password"
            );

            Statement statement = connection.createStatement();

            //Asks user to enter ID of the order they wish to update the details of
            int id = Integer.parseInt(JOptionPane.showInputDialog("Please enter the id of the order you wish to " +
                    "update:"));

            //Selects the items_ordered table entry corresponding to the entered order ID
            ResultSet orderResults = statement.executeQuery("SELECT id, item_name, cost, qty, special_instructions " +
                    "FROM items_ordered WHERE orders_id =" + id);

            //Variables to be updated in the items_ordered table in the QuickFoodMS database
            String itemName;
            BigDecimal cost;
            int qty;
            String specialInstructions;

            int itemId;

            String answer;
            int itemNumber = 1;

            //Boolean that controls whether items ordered with entered order ID exist
            boolean resultFound = false;

            //Reads results from SQL query for each item ordered
            while (orderResults.next()) {
                resultFound = true;

                itemId = orderResults.getInt("id");
                itemName = orderResults.getString("item_name");
                cost = orderResults.getBigDecimal("cost");
                qty = orderResults.getInt("qty");
                specialInstructions = orderResults.getString("special_instructions");

                System.out.println("Found order with id: " + id);

                /*If an item ordered with entered order ID was found then the current item details are displayed
                and the option to change them is presented*/
                answer = JOptionPane.showInputDialog("Item" + itemNumber + " is: " + itemName + ". Would you like to " +
                        "change it y/n?");
                if (answer.equals("y")) {
                    itemName = JOptionPane.showInputDialog("Please enter the item's updated name:");
                }

                answer = JOptionPane.showInputDialog("Item" + itemNumber + " has a cost of: " + cost + ". Would you " +
                        "like to change it y/n?");
                if (answer.equals("y")) {
                    cost = new BigDecimal(JOptionPane.showInputDialog("Please enter the item's updated cost:"));
                }

                answer = JOptionPane.showInputDialog("Item" + itemNumber + " has a quantity of: " + qty + ". Would " +
                        "you like to change it y/n?");
                if (answer.equals("y")) {
                    qty = Integer.parseInt(JOptionPane.showInputDialog("Please enter the item's updated quantity:"));
                }

                answer =
                        JOptionPane.showInputDialog("Item" + itemNumber + " has a special instruction of: " + specialInstructions + ". Would you like to change it y/n?");
                if (answer.equals("y")) {
                    specialInstructions = JOptionPane.showInputDialog("Please enter the item's updated special " +
                            "instructions:");
                }

                Statement statement2 = connection.createStatement();

                //Updates relevant entry in items_ordered table based on newly entered or confirmed details
                int rowsAffected = statement2.executeUpdate("UPDATE items_ordered SET item_name='" + itemName +
                        "', cost=" + cost + ", qty=" + qty + ", special_instructions='" + specialInstructions + "' " +
                        "WHERE id=" + itemId + ";");
                System.out.println("Query complete, " + rowsAffected + " rows updated in items_ordered.");

                itemNumber++;
            }

            if (resultFound) {

                /*Updates the relevant entry in the orders table with the total cost (summed in the SQL query) from all
                items ordered under this order ID*/
                int rowsAffected2 = statement.executeUpdate("UPDATE orders " +
                        "SET total_cost = (" +
                        "SELECT SUM(items_ordered.cost*items_ordered.qty) " +
                        "FROM items_ordered WHERE orders_id = " + id + ") " +
                        "WHERE id = " + id + ";");
                System.out.println("Query complete, " + rowsAffected2 + " rows updated in orders.");
            } else {
                System.out.println("No order found with id: " + id);
            }


        } catch (SQLException e) {
            System.out.println("Error in updateOrder method");
            e.printStackTrace();
        }
    }

    /**
     * Method that allows user to enter view any existing orders from the orders table in the QuickFoodMS database
     * <p>
     * Method requires input from the user of the order ID of the order that they wish to view the details of.
     */
    public static void viewExistingOrderById() {

        try {
            //Connects to the QuickFoodMS database
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost;database=QuickFoodMS",
                    "Username",
                    "Password"
            );

            //Asks user to enter order ID of the order they wish to view the details of
            int orderId = Integer.parseInt(JOptionPane.showInputDialog("Please enter the ID " +
                    "of the order you wish to view:"));

            Statement statement = connection.createStatement();

            ResultSet results = statement.executeQuery("SELECT * FROM orders WHERE id = " + orderId);

            boolean resultsFound = false;

            //Prints out requested order if it exists
            while (results.next()) {
                resultsFound = true;
                System.out.println(
                        "id: " + results.getInt("id") + ", "
                                + "driver_id: " + results.getInt("driver_id") + ", "
                                + "customer_id: " + results.getInt("customer_id") + ", "
                                + "restaurant_id: " + results.getInt("restaurant_id") + ", "
                                + "total_cost: " + results.getBigDecimal("total_cost") + ", "
                                + "order_status: " + results.getString("order_status") + ", "
                                + "completion_date_time: " + results.getTimestamp("completion_date_time")
                );
            }

            //Gives error message if no order was found
            if (!resultsFound) {
                System.out.println("No order found for for order ID: " + orderId);
            }

        } catch (SQLException e) {
            System.out.println("Error in viewExistingOrderById method");
            e.printStackTrace();
        }
    }

    /**
     * Method that allows user to view existing orders from the orders table in the QuickFoodMS database by entering
     * the relevant customer name
     * <p>
     * Method requires input from the user of the customer's name and surname that they wish to view the order
     * details of.
     */
    public static void viewExistingOrderByCustomerName() {

        try {
            //Connects to the QuickFoodMS database
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost;database=QuickFoodMS",
                    "Username",
                    "Password"
            );

            //Asks user to enter the customer name for the order they wish to view the details of
            String customerName = JOptionPane.showInputDialog("Please enter the first " +
                    "name of the customer whose order you wish to view:");
            //Asks user to enter the customer surname for the order they wish to view the details of
            String customerSurname = JOptionPane.showInputDialog("Please enter the " +
                    "surname of the customer whose order you wish to view:");
            Statement statement = connection.createStatement();

            //Searches for ordered that are linked to the entered customer name and surname
            ResultSet results = statement.executeQuery("SELECT * FROM orders " +
                    "INNER JOIN customers ON orders.customer_id = customers.id " +
                    "WHERE name = '" + customerName + "' AND surname = '" + customerSurname + "';");

            boolean resultsFound = false;

            //Prints out requested order if it exists
            while (results.next()) {
                resultsFound = true;
                System.out.println(
                        "id: " + results.getInt("id") + ", "
                                + "driver_id: " + results.getInt("driver_id") + ", "
                                + "customer_id: " + results.getInt("customer_id") + ", "
                                + "restaurant_id: " + results.getInt("restaurant_id") + ", "
                                + "total_cost: " + results.getBigDecimal("total_cost") + ", "
                                + "order_status: " + results.getString("order_status") + ", "
                                + "completion_date_time: " + results.getTimestamp("completion_date_time")
                );
            }

            //Gives error message if no order was found
            if (!resultsFound) {
                System.out.println("No order found for for customer: " + customerName + " " + customerSurname);
            }

        } catch (SQLException e) {
            System.out.println("Error in viewExistingOrderByCustomerName method");
            e.printStackTrace();
        }
    }

    /**
     * Method that allows user to view all incomplete entries in the restaurants, customers, items ordered and orders
     * tables in the QuickFoodMS database
     * <p>
     * Shows any entries in the database that contain a blank or NULL field. The user can then use update options
     * from the menu to update the relevant entries.
     */
    public static void viewIncompleteOrders() {

        try {
            //Connects to the QuickFoodMS database
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost;database=QuickFoodMS",
                    "Username",
                    "Password"
            );

            Statement statement = connection.createStatement();

            System.out.println("Restaurants with incomplete entries:");
            /*SQL query that requests entries from the restaurants table in the database that contain blank or NULL
            fields.*/
            ResultSet restaurantResults = statement.executeQuery("SELECT * FROM restaurants " +
                    "WHERE name IS NULL OR LTRIM(RTRIM(name)) = '' OR " +
                    "city IS NULL OR LTRIM(RTRIM(city)) = '' OR " +
                    "phone IS NULL OR LTRIM(RTRIM(phone)) = '';");

            boolean resultFound = false;

            //If incomplete result found, prints out result
            while (restaurantResults.next()) {
                resultFound = true;
                System.out.println(
                        "id: " + restaurantResults.getInt("id") + ", "
                                + "name: " + restaurantResults.getString("name") + ", "
                                + "city: " + restaurantResults.getString("city") + ", "
                                + "phone: " + restaurantResults.getString("phone")
                );
            }

            //Prints message if no incomplete results found
            if (!resultFound) {
                System.out.println("No incomplete restaurant entries");
            } else {
                resultFound = false;
            }

            System.out.println("Customers with incomplete entries:");
            /*SQL query that requests entries from the customers table in the database that contain blank or NULL
            fields.*/
            ResultSet customerResults = statement.executeQuery("SELECT * FROM customers " +
                    "WHERE name IS NULL OR LTRIM(RTRIM(name)) = '' OR " +
                    "surname IS NULL OR LTRIM(RTRIM(surname)) = '' OR " +
                    "phone IS NULL OR LTRIM(RTRIM(phone)) = '' OR " +
                    "delivery_address IS NULL OR LTRIM(RTRIM(delivery_address)) = '' OR " +
                    "email IS NULL OR LTRIM(RTRIM(email)) = '' OR " +
                    "city IS NULL OR LTRIM(RTRIM(city)) = '';");


            //If incomplete result found, prints out result
            while (customerResults.next()) {
                resultFound = true;
                System.out.println(
                        "id: " + customerResults.getInt("id") + ", "
                                + "name: " + customerResults.getString("name") + ", "
                                + "surname: " + customerResults.getString("surname") + ", "
                                + "phone: " + customerResults.getString("phone") + ", "
                                + "delivery_address: " + customerResults.getString("delivery_address") + ", "
                                + "city: " + customerResults.getString("city")
                );
            }

            //Prints message if no incomplete results found
            if (!resultFound) {
                System.out.println("No incomplete customer entries");
            } else {
                resultFound = false;
            }

            System.out.println("Items ordered with incomplete entries:");
            /*SQL query that requests entries from the items_ordered table in the database that contain blank or NULL
            fields.*/
            ResultSet itemOrderResults = statement.executeQuery("SELECT * FROM items_ordered " +
                    "WHERE orders_id IS NULL OR LTRIM(RTRIM(orders_id)) = '' OR " +
                    "item_name IS NULL OR LTRIM(RTRIM(item_name)) = '' OR " +
                    "cost IS NULL OR LTRIM(RTRIM(cost)) = '' OR " +
                    "qty IS NULL OR LTRIM(RTRIM(qty)) = '' OR " +
                    "special_instructions IS NULL OR LTRIM(RTRIM(special_instructions)) = '';");

            //If incomplete result found, prints out result
            while (itemOrderResults.next()) {
                resultFound = true;
                System.out.println(
                        "id: " + itemOrderResults.getInt("id") + ", "
                                + "orders_id: " + itemOrderResults.getString("orders_id") + ", "
                                + "item_name: " + itemOrderResults.getString("item_name") + ", "
                                + "cost: " + itemOrderResults.getString("cost") + ", "
                                + "qty: " + itemOrderResults.getString("qty") + ", "
                                + "special_instructions: " + itemOrderResults.getString("special_instructions")
                );
            }

            //Prints message if no incomplete results found
            if (!resultFound) {
                System.out.println("No incomplete items ordered entries");
            } else {
                resultFound = false;
            }

            System.out.println("Orders with incomplete entries:");
            /*SQL query that requests entries from the orders table in the database that contain blank or NULL
            fields.*/
            ResultSet orderResults = statement.executeQuery("SELECT * FROM orders " +
                    "WHERE driver_id IS NULL OR LTRIM(RTRIM(driver_id)) = '' OR " +
                    "customer_id IS NULL OR LTRIM(RTRIM(customer_id)) = '' OR " +
                    "restaurant_id IS NULL OR LTRIM(RTRIM(restaurant_id)) = '' OR " +
                    "total_cost IS NULL OR LTRIM(RTRIM(total_cost)) = '' OR " +
                    "order_status IS NULL OR LTRIM(RTRIM(order_status)) = '' OR " +
                    "completion_date_time IS NULL OR LTRIM(RTRIM(completion_date_time)) = '';");

            //If incomplete result found, prints out result
            while (orderResults.next()) {
                resultFound = true;
                System.out.println(
                        "id: " + orderResults.getInt("id") + ", "
                                + "driver_id: " + orderResults.getString("driver_id") + ", "
                                + "customer_id: " + orderResults.getString("customer_id") + ", "
                                + "restaurant_id: " + orderResults.getString("restaurant_id") + ", "
                                + "total_cost: " + orderResults.getString("total_cost") + ", "
                                + "order_status: " + orderResults.getString("order_status") + ", "
                                + "completion_date_time: " + orderResults.getString("completion_date_time")
                );
            }

            //Prints message if no incomplete results found
            if (!resultFound) {
                System.out.println("No incomplete order entries");
            }


        } catch (SQLException e) {
            System.out.println("Error in viewIncompleteOrders method");
            e.printStackTrace();
        }
    }

}
