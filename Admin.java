import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.*;
import java.util.*;
public class Admin extends Main {

    private Main main;

    public Admin() {
        this.main = main;
    }

    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_data";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Opengmail@123";



    public static void startAdmin(String username){
        System.out.println("Hello Welcome to Admin Page");

        try{
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create a table to store user and admin information if it doesn't exist
            Scanner scanner = new Scanner(System.in);


            while(true) {

                System.out.println("Please Select from below options:");
                System.out.println("1. Create New Manager");
                System.out.println("2. Delete Existing Manager");
                System.out.println("3. View All Managers Details");
                System.out.println("4. View Specific Managers Details");
                System.out.println("5. View All Users Details");
                System.out.println("6. View Specific Users Details");
                System.out.println("7. Reset your password");
                System.out.println("8. Exit");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        createManager(connection);
                        break;
                    case 2:
                        deleteManager(connection);
                        break;
                    case 3:
                        viewAllManagersDetails(connection);
                        break;
                    case 4:
                        viewSpecificManagerDetails(connection);
                        break;
                    case 5:
//                        viewAllUsersDetails(connection);
                        break;
                    case 6:
//                        viewSpecificUserDetails(connection);
                        break;
                    case 7:
                        resetAdminPassword(connection,username);
                        break;
                    case 8:
                        // Close resources and exit
                        scanner.close();
                        connection.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    }


    private static void createManager(Connection connection) throws SQLException {

        //will ask for manager name
        //will ask for manager username
        //will ask for manager password
        //will automatically assign managerID

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Manager Full Name ");
        String managerName = scanner.nextLine();
        boolean flag=true;
        String inputData="";
        while(flag){
            System.out.println("Enter manager username. (9 characters)");
            inputData=scanner.next();
            while(inputData.length() != 9){
                System.out.println("Please enter characters equal to 9 characters without space: ");
                inputData=scanner.next();
            }
            String checkDataQuery = "SELECT COUNT(*) FROM managers WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(checkDataQuery)) {
                statement.setString(1,inputData);
                // Execute the query
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();

                int count = resultSet.getInt(1);

                if (count > 0) {
                    System.out.println("Sorry!!! username is already taken. please chose any other username. ");

                } else {
                    flag=false;
                }
            }
         catch (SQLException e) {
            e.printStackTrace();
        }

    }

        System.out.print("Set the password for Manager: ");
        String password = scanner.next();

        //generate random and unique manager id
        //first generate random managerid
        //check if managerid is already existing or not
        //if existing then generate new random managerid
        //if not existing then insert the value in db

        boolean flag2=true;
        String managerID="MN";
        while(flag2) {

        Random random = new Random();
        int fourDigitNumber = random.nextInt(9000) + 1000;
        managerID+=fourDigitNumber;

        //check whether there is managerID already existing in the database

        String checkIDQuery = "SELECT COUNT(*) FROM managers WHERE managerID = ?";
            try (PreparedStatement statement = connection.prepareStatement(checkIDQuery)) {
                statement.setString(1, managerID);

                // Execute the query
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();

                int count = resultSet.getInt(1);

                if (count > 0) {
                    flag2 = true;

                } else {
                    flag2 = false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }



        String insertUserSQL = "INSERT INTO managers (managerID,Manager_Name,username,password) VALUES (?,?, ?, ?)";


        try (PreparedStatement statement = connection.prepareStatement(insertUserSQL)) {
            statement.setString(1, managerID);
            statement.setString(2, managerName);
            statement.setString(3,inputData);
            statement.setString(4,password);
            statement.executeUpdate();
        }

        System.out.println("Manager created successfully.");
    }


    private static void deleteManager(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Manager id to delete: ");
        String midToDelete = scanner.next();

        String deleteManagerSQL = "DELETE FROM managers WHERE managerID = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteManagerSQL)) {
            statement.setString(1, midToDelete);
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Manager with ID " + midToDelete + " deleted successfully.");
            } else {
                System.out.println("No manager found with ID " + midToDelete + ". No changes made.");
            }
        }
    }


    private static void viewAllManagersDetails(Connection connection) throws SQLException {
        String selectAllManagersSQL = "SELECT * FROM managers";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectAllManagersSQL);

            while (resultSet.next()) {
                String managerID = resultSet.getString("managerID");
                String managerName = resultSet.getString("Manager_Name");
                String managerUsername=resultSet.getString("username");

                System.out.println("Manager ID: " + managerID);
                System.out.println("Manager Name: " + managerName);
                System.out.println("Manager Username: "+managerUsername);

                System.out.println("------------");
            }
        }
    }


    private static void viewSpecificManagerDetails(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter Manager ID to view details (or 'q' to quit): ");
            String managerID = scanner.nextLine().trim();

            if (managerID.equalsIgnoreCase("q")) {
                //If User wants to quit
                return;
            }

            try {


                String selectManagerSQL = "SELECT * FROM managers WHERE managerID = ?";
                try (PreparedStatement statement = connection.prepareStatement(selectManagerSQL)) {
                    statement.setString(1, managerID);

                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        String managerName = resultSet.getString("Manager_Name");
                        String managerUsername = resultSet.getString("username");

                        System.out.println("Manager ID: " + managerID);
                        System.out.println("Manager Name: " + managerName);
                        System.out.println("Manager Username: " + managerUsername);
                        System.out.println("------------");
                    } else {
                        System.out.println("No manager found with ID " + managerID);
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid numeric ID or 'q' to quit.");
            }
        }
    }




    private static void resetAdminPassword(Connection connection, String username) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        String updatePasswordSQL = "UPDATE admins SET password = ? WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(updatePasswordSQL)) {
            statement.setString(1, newPassword);
            statement.setString(2, username);
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Password reset successfully for admin: " + username);
            } else {
                System.out.println("Failed to reset password. Admin not found: " + username);
            }
        }
    }





}

