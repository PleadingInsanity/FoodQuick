import javax.swing.*;
import java.sql.*;

/**
 * Restaurant class that controls the restaurant object
 * <p>
 * Methods are called in the Main class
 */
public class Restaurant {

    //Restaurant Attributes
    private String name;
    private String city;
    private String phone;


    //Restaurant Methods

    /**
     * Method that allows user to enter restaurant details into the restaurants table in the QuickFoodMS database
     */
    public static void enterRestaurantDetails() {

        Restaurant restaurant = new Restaurant();

        try {
            //Connects to the QuickFoodMS database
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost;database=QuickFoodMS",
                    "Username",
                    "Password"
            );
            Statement statement = connection.createStatement();

            restaurant.name = JOptionPane.showInputDialog("Please enter the name of the restaurant you wish to order " +
                    "from:");

            restaurant.city = JOptionPane.showInputDialog("Please enter the city in which the restaurant is located:");

            restaurant.phone = JOptionPane.showInputDialog("Please enter the restaurant's contact number:");

            statement.executeUpdate("INSERT INTO restaurants (name, city, phone) " +
                    "VALUES ('" + restaurant.name + "', '" + restaurant.city + "', '" + restaurant.phone + "');");

            connection.close();

        } catch (SQLException e) {
            System.out.println("Error in enterRestaurantDetails method");
            e.printStackTrace();
        }

    }

    /**
     * Method that allows user to update restaurant details into the restaurants table in the QuickFoodMS database
     * <p>
     * Method requires input from the user of the restaurant ID that they wish to change the details of.
     */
    public static void updateRestaurantDetails() {

        Restaurant restaurant = new Restaurant();

        try {
            //Connects to the QuickFoodMS database
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost;database=QuickFoodMS",
                    "Username",
                    "Password"
            );

            Statement statement = connection.createStatement();

            //Asks user to enter ID of restaurant they wish to update the details of
            int id = Integer.parseInt(JOptionPane.showInputDialog("Please enter the id of the restaurant you wish to " +
                    "update:"));

            //Selects the restaurants table entry corresponding to the entered customer ID
            ResultSet restaurantResults = statement.executeQuery("SELECT * FROM restaurants WHERE id =" + id);

            //Boolean that controls whether a restaurant with entered ID exists
            boolean resultFound = false;

            //Reads results from SQL query and stores them in class attributes
            while (restaurantResults.next()) {
                resultFound = true;
                restaurant.name = restaurantResults.getString("name");
                restaurant.city = restaurantResults.getString("city");
                restaurant.phone = restaurantResults.getString("phone");
            }

            String answer;

            /*If a restaurant with entered id was found then the current restaurant details are displayed and the option
            to change them is presented*/
            if (resultFound) {
                System.out.println("Found restaurant with id: " + id);

                answer = JOptionPane.showInputDialog("The current restaurant name is: " + restaurant.name + ". Would " +
                        "you like to change it y/n?");
                if (answer.equals("y")) {
                    restaurant.name = JOptionPane.showInputDialog("Please enter the restaurant's updated first name:");
                }

                answer = JOptionPane.showInputDialog("The current restaurant city is: " + restaurant.city + ". Would " +
                        "you like to change it y/n?");
                if (answer.equals("y")) {
                    restaurant.city = JOptionPane.showInputDialog("Please enter the restaurant's updated city:");
                }

                answer = JOptionPane.showInputDialog("The current restaurant phone number is: " + restaurant.phone +
                        ". Would you like to change it y/n?");
                if (answer.equals("y")) {
                    restaurant.phone = JOptionPane.showInputDialog("Please enter the restaurant's updated contact " +
                            "number:");
                }

                //Updates relevant entry in restaurants table based on newly entered or confirmed details
                int rowsAffected = statement.executeUpdate("UPDATE restaurants SET name='" + restaurant.name + "', " +
                        "phone='" + restaurant.phone + "', city='" + restaurant.city + "' WHERE id=" + id + ";");
                System.out.println("Query complete, " + rowsAffected + " rows updated.");

            } else {
                //Prints out message if no restaurant with entered ID is found
                System.out.println("No restaurant found with id: " + id);
            }


        } catch (SQLException e) {
            System.out.println("Error in updateRestaurantDetails method");
            e.printStackTrace();
        }
    }
}


