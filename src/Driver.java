import javax.swing.*;
import java.sql.*;
import java.util.Arrays;

/**
 * Driver class that controls the driver object
 */
public class Driver {

    //Driver Attributes
    private int chosenDriverId;
    private int currentLoad;


    //Driver constructor

    /**
     * Driver constructor
     *
     * @param chosenDriverId initial ID chosen for driver, 0 used as stand-in value till ID selected
     * @param currentLoad    initial load chosen for driver, 100 used as stand-in value till driver selected and
     *                       relevant load selected
     */
    public Driver(int chosenDriverId, int currentLoad) {
        this.chosenDriverId = chosenDriverId;
        this.currentLoad = currentLoad;
    }

    //Driver Methods

    /**
     * Method that allows user to assign a driver from into the drivers table in the QuickFoodMS database to an order
     * <p>
     * Method requires input from the user of the order ID that they wish to assign a driver to.
     */
    public static void assignDriver() {

        //Initiates the driver object with an ID of 0 and load of 100
        Driver driver = new Driver(0, 100);


        try {
            //Connects to the QuickFoodMS database
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost;database=QuickFoodMS",
                    "Username",
                    "Password"
            );

            //Requests ID of order that user wishes to assign a driver to
            int orderId = Integer.parseInt(JOptionPane.showInputDialog("Please enter the ID of the " +
                    "order which you would like to assign a driver to:"));

            Statement statement = connection.createStatement();
            //SQL query that selects the city of the customer assigned to the order ID entered
            ResultSet customerCityResults = statement.executeQuery("SELECT city " +
                    "FROM orders " +
                    "INNER JOIN customers " +
                    "ON orders.customer_id = customers.id " +
                    "WHERE orders.id = " + orderId + ";");


            boolean customerFound = false;
            String customerCity = "No customer city";

            if (customerCityResults.next()) {
                customerFound = true;
                customerCity = customerCityResults.getString("city");
            } else {
                System.out.println("No customer found for order ID: " + orderId);
            }

            //SQL query that selects the city of the restaurant assigned to the order ID entered
            ResultSet restaurantCityResults = statement.executeQuery("SELECT city " +
                    "FROM orders " +
                    "INNER JOIN restaurants " +
                    "ON orders.restaurant_id = restaurants.id " +
                    "WHERE orders.id = " + orderId + ";");

            boolean restaurantFound = false;
            String restaurantCity = "No restaurant city";

            if (restaurantCityResults.next()) {
                restaurantFound = true;
                restaurantCity = restaurantCityResults.getString("city");
            } else {
                System.out.println("No restaurant found for order ID: " + orderId);
            }

            //Checks if restaurant and customer are in the same city and if not prints error message
            if (!restaurantCity.equals(customerCity)) {

                //SQL query changes order_status in the orders table
                statement.executeUpdate("UPDATE orders SET order_status = 'DRIVER NOT ASSIGNED' " +
                        "WHERE id = " + orderId + ";");
                System.out.println("Driver not assigned. Restaurant and customer are not in the same city.");

            //If restaurant or customer not found for order ID does not assign driver
            } else if (!customerFound || !restaurantFound) {

                //SQL query changes order_status in the orders table
                statement.executeUpdate("UPDATE orders SET order_status = 'DRIVER NOT ASSIGNED' " +
                        "WHERE id = " + orderId + ";");
                System.out.println("Driver not assigned.");

            //Passed checks and attempts to assign driver to order
            } else {

                /*SQL query selecting the id, city and current_load from all drivers in the drivers table in the
                database*/
                ResultSet driverResults = statement.executeQuery("SELECT id, city, current_load FROM drivers;");

                //temporary variables for driver chosen
                int temporaryDriverId;
                int temporaryLoad;
                String temporaryCity;

                //Arrays for all driver IDs in correct city and driver loads in correct city
                int[] driverIdsInCorrectCity = new int[0];
                int[] driverCurrentLoadsInCorrectCity = new int[0];

                int i = 0;

                //Takes any driver results and assigns them to the temporary variables.
                while (driverResults.next()) {
                    temporaryDriverId = driverResults.getInt("id");
                    temporaryCity = driverResults.getString("city");
                    temporaryLoad = driverResults.getInt("current_load");

                    /*If driver and restaurant cities align creates a new array for driver and corresponding load.
                    Customer has already been checked against restaurant*/
                    if (temporaryCity.equals(restaurantCity)) {
                        driverIdsInCorrectCity = Arrays.copyOf(driverIdsInCorrectCity, i + 1);
                        driverIdsInCorrectCity[i] = temporaryDriverId;
                        driverCurrentLoadsInCorrectCity = Arrays.copyOf(driverCurrentLoadsInCorrectCity, i + 1);
                        driverCurrentLoadsInCorrectCity[i] = temporaryLoad;
                        i++;
                    }
                }

                //Checks that drivers have been added to ID and load arrays
                if (i != 0) {
                    /*Finds driver with the lowest load from those in the same city as the restaurant and assigns them
                    to the class attributes*/
                    for (int j = 0; j < i; j++) {
                        if (driverCurrentLoadsInCorrectCity[j] < driver.currentLoad) {
                            driver.chosenDriverId = driverIdsInCorrectCity[j];
                            driver.currentLoad = driverCurrentLoadsInCorrectCity[j];
                        }
                    }

                // If arrays are empty then no driver was found
                } else {
                    System.out.println("No driver found in same city as customer and restaurant");
                    //SQL query changes order_status in the orders table
                    statement.executeUpdate("UPDATE orders SET order_status = 'DRIVER NOT ASSIGNED' " +
                            "WHERE id = " + orderId + ";");
                }

                //Checks that a driver was found
                if (driver.chosenDriverId != 0 && driver.currentLoad != 100) {


                    //SQL query adds one to the chosen drivers load in the drivers table
                    statement.executeUpdate("UPDATE drivers SET current_load = " + (driver.currentLoad + 1) +
                            " WHERE id = " + driver.chosenDriverId + ";");

                    //SQL query assigns the chosen driver_id and changes order_status in the orders table
                    statement.executeUpdate("UPDATE orders SET driver_id = " + driver.chosenDriverId +
                            ", order_status = 'DRIVER ASSIGNED' " +
                            "WHERE id = " + orderId + ";");
                    System.out.println("Driver Assigned");
                }
            }

            connection.close();

        } catch (SQLException e) {
            System.out.println("Error in assignDriver method");
            e.printStackTrace();
        }
    }
}

