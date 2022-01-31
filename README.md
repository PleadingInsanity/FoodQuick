# FoodQuick

This project is a Java program for a fictional food delivery system for a company called “Food Quick”. Food Quick is the company that receives the orders and distributes them to a driver based on their current load and their location. This program would help them keep track of the orders and distribute accordingly. 

1. General Usage
1. Menu Usage
1. Customer Options
1. Restaurant Options
1. Order Options
1. Driver Options
1. Finalisation of Order Options
1. View Options
1. Installation
1. Author and Update Details 

### GENERAL USAGE

Running the program should present the user with a menu of funtions available. The general order in which the menu should be used is explained under the *Menu* Usage section. Details about each option are given in the *Customer Options*, *Restaurant Options*, *Order Options*, *Driver Options*, *Finalisation of Order Options* and *View Options* sections.

### MENU USAGE

The menu that pops up on program start (pictured below) is intended to be used mostly in the order in whick it appears. The general order which the user with follow to create an order with entirely new details is: enter customer details, enter restaurant details, create order, assign driver to order and finalise order.

If the customer and restaurant are in the database the process will be: create order, assign driver to order and finalise order.

All of the functionalities mentioned here and some additional features are outlined in the sections below.

Once user is done with the program they can select the Exit option to exit the program.

![Image of menu](/readme-images/menu.png)

### CUSTOMER OPTIONS

The *Enter Customer Details* option will allow the user to create a new customer in the customers table in the QUICKFOODMS database and enter their details.

The *Update Customer Details* option will allow the user to update an existing customer's details in the database. For this option the user will need to know the customer ID of the customer whose details they wish to update.


### RESTAURANT OPTIONS  

The *Enter Restaurant Details* option will allow the user to create a new restaurant in the restaurants table in the QUICKFOODMS database and enter their details.

The *Update Restaurant Details* option will allow the user to update an existing restaurant's details in the database. For this option the user will need to know the restaurant ID of the restaurant whose details they wish to update.

### ORDER OPTIONS

The *Create Order* option will ask the user for the customer ID and restaurant ID they wish to link to the food order they are creating. The user will then be prompted to enter the details of the food that needs to be ordered from the restaurant. Each food type will create an entry in the items_ordered table in the QUICKFOODMS database. Once the final food item has been ordered an entry will be created in the orders table in the QUICKFOODMS database and it will be given the status ORDER PLACED.

The *Update Order* option will require the user to enter the order ID of the order they wish to update. It will then allow the user to update any food items in that order which will change the items_ordered table and the orders table in the database.

### DRIVER OPTIONS

The *Assign Driver to Order* option will ask the user to enter the ID of the order that needs a driver assigned to it. It will then look at the city of the customer in that order and assign the driver from the drivers table in the database who has the lowest current load to the order. If no driver is found in the same city an error message will display. If the process is completed successfullly 1 will be added to the drivers current load drivers table in the database as well as the order_status updated to DRIVER ASSIGNED in the orders table. 

### FINALISATION OF ORDER OPTIONS

The *Finalise Order* option will ask the user to enter an order ID and then change the status of an order in the orders table in the database to COMPLETE as well as add a completion timestamp to that order. FInally a text file called invoice[order ID].txt will be generated in the main program folder. An example of an invoice is pictured below. WARNING: The user must check that all is in order by using the *View Incomplete Orders* option before taking the *Finalise Order* step. Also note that if no driver was assigned then the *Finalise Order* command will not work as expected. 

![Image of invoice](/readme-images/invoice-example.png)

### VIEW OPTIONS

The user can use the *Vew Existing Order* option to view any order in orders table in the database. The user can find an order using the order ID or a customer's name and surname. 

The *View All Incomplete Orders* option can be used to see any entries in the customers, restaurants, items_ordered or orders tables in the database that contain blank or NULL entries. The program will display all orders meeting this criteria. From there the user can use the update options in the menu to fix any of these incomplete entries. It is recommended that the user perform this step before finalising an order. 

### INSTALLATION

This program requires a suitable Java IDE (program was built using IntelliJ) and a database called QuickFoodMS whose structure you can see in the *Database Diagrams* folder. Alternatively the text file MSSQLSetupCommands.sql has the MS SQL server 2012 commands to set up the database.

### AUTHOR AND UPDATE DETAILS

This program was created by Philippa Colly and last updated in January 2022