//Please place this project's files in a java src file and the driver.txt file one folder up in order to run the
// program

//Note that the driver Spike Fenton in the driver.txt file was assigned no load thus he was given a load of 0 when he
// was imported into the database

/*This program simulates a meal delivery company's order system. The user can enter a customer's details, update
customer details by entering the customer id, enter restaurant details and update restaurant details by entering the
restaurant id. They can also create a new food order for an existing customer using their id, update an existing food
 order, assign a driver to a food order who is in the vicinity and has the lowest load and finalise that order. The
 user can search for incomplete orders (orders with missing details) which can then be fixed using the update options
 mentioned above. For more information read the README file*/

import javax.swing.*;

/**
 * Main class that controls input choice from user
 * <p>
 * Provides a menu for user to navigate the program. The menu uses a switch statement to call methods from the
 * Customer, Restaurant, Driver, Order and Invoice classes.
 *
 * @author Philippa Colly
 * @version 1.00, 28 January 2022
 */

public class Main {

    /**
     * Main method that controls the menu options for the program and calls methods from the Restaurant, Invoice
     * Driver, Customer and Order classes
     *
     * @param args an array of command-line arguments for the application
     */
    public static void main(String[] args) {

        int inputChoice;

        do {

            /*Provides a menu for the user to enter and edit customer, restaurant and order details. It is arranged
            in the order that the user should be following when creating an order.FOr further details on menu usage
            please view the README*/
            inputChoice = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of the option you wish to " +
                    "select:\r\n" +
                    "1. Enter Customer Details\r\n" +
                    "2. Update Customer Details\r\n" +
                    "3. Enter Restaurant Details\r\n" +
                    "4. Update Restaurant Details\r\n" +
                    "5. Create Order\r\n" +
                    "6. Update Order\r\n" +
                    "7. Assign Driver To Order\r\n" +
                    "8. View All Incomplete Orders\r\n" +
                    "9. Finalise Order\r\n" +
                    "10. View An Existing Order\r\n" +
                    "0. Exit"));

            switch (inputChoice) {
                case 1:
                    Customer.enterCustomerDetails();
                    break;
                case 2:
                    Customer.updateCustomerDetails();
                    break;
                case 3:
                    Restaurant.enterRestaurantDetails();
                    break;
                case 4:
                    Restaurant.updateRestaurantDetails();
                    break;
                case 5:
                    Order.createOrder();
                    break;
                case 6:
                    Order.updateOrder();
                    break;
                case 7:
                    Driver.assignDriver();
                    break;
                case 8:
                    Order.viewIncompleteOrders();
                    break;
                case 9:
                    Invoice.generateInvoice();
                    break;
                case 10:
                    //Allows user to choose how they want to search and calls the respective method
                    inputChoice = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of the option " +
                            "you wish to select:\r\n" +
                            "1. Search by customer ID\r\n" +
                            "2. Search by customer name\r\n"));
                    if (inputChoice == 1) {
                        Order.viewExistingOrderById();
                    } else if (inputChoice == 2) {
                        Order.viewExistingOrderByCustomerName();
                    } else {
                        break;
                    }
                case 0:
                    System.out.println("Program exited.");
                    break;
                default:
                    System.out.println("Please enter a valid option.");

            }

        } while (inputChoice != 0);

    }
}


