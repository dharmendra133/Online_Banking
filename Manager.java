import java.sql.*;
import java.util.Random;
import java.util.Scanner;
import java.time.*;
public class Manager extends Main {

    private Main main;

    public Manager() {
        this.main = main;
    }
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_data";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Opengmail@123";


    public static void startManager(String username){
        System.out.println("Hello Welcome to Manager Page");

        try{
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create a table to store user and admin information if it doesn't exist
            Scanner scanner = new Scanner(System.in);


            while(true) {

                System.out.println("Please Select from below options:");
                System.out.println("1. Create New User Account");
                System.out.println("2. Delete Existing User Account");
                System.out.println("3. View All User Account Details");
                System.out.println("4. View Specific Account Details"); // transaction history will be displayed here
                System.out.println("5. View All Approval Request For New Account");
                System.out.println("6. View  Approval Request For Conversion Of Saving Account to Salary Account");
                System.out.println("7. Reset your password");
                System.out.println("8. Exit");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        createNewAccount(connection);
                        break;
                    case 2:
                        deleteAccount(connection);
                        break;
                    case 3:
                        viewAllUsersDetails(connection);
                        break;
                    case 4:
                        viewSpecificUserDetails(connection);
                        break;
                    case 5:
                        viewAllApprovalRequests(connection);
                        break;
                    case 6:
                        approveOrDeleteRequests(connection);
                        break;
                    case 7:
                        resetManagerPassword(connection,username);
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
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }



    private static void createNewAccount(Connection connection) throws SQLException, InterruptedException {

        //will ask for User name
        //will ask for Aadhar Card Number
        //will ask for name According to Aadhar card
        //will ask for Age
        //will ask for Address
        //will ask for password
        //will deposit money 0 by default

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Aadhar Card Number: (12 digit only) ");
        long aadharNo=0L;
        boolean flagAadhar=true;

        while(flagAadhar) {
            System.out.println("Enter Aadhar Card Number Again");
            aadharNo = scanner.nextLong();
            String aadharNoStr = String.valueOf(aadharNo);
            while (aadharNoStr.length() != 12) {
                System.out.println("Enter the valid Aadhar Card Number: ");
                aadharNo = scanner.nextLong();
                aadharNoStr = String.valueOf(aadharNo);
            }

            //check if aadhar card is already existing in the user record
            String checkDataQuery = "SELECT COUNT(*) FROM accounts WHERE Aadhar_Card_Number = ?";
            try (PreparedStatement statement = connection.prepareStatement(checkDataQuery)) {
                statement.setLong(1, aadharNo);
                // Execute the query
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();

                int count = resultSet.getInt(1);

                if (count > 0) {
                    System.out.println("Sorry!!! Account already exists with this Aadhar Card. ");
                    flagAadhar = true;
                } else {
                    flagAadhar = false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }



        System.out.println("Enter Age: ");
        int age=scanner.nextInt();

        scanner.nextLine();
        System.out.println("Enter Name according to Aadhar Card: ");
        String Name=scanner.nextLine();

        System.out.println("Enter Permanent Address: ");
        String address=scanner.nextLine();

        System.out.println("Enter Password For Account: ");
        String password=scanner.next();

        //create_Random_Unique_userID
        boolean flag3=true;
        String userID="US";
        while(flag3) {

            Random random = new Random();
            int eightDigitNumber = random.nextInt(90000000) + 10000000;
            userID+=eightDigitNumber;

            //check whether there is managerID already existing in the database

            String checkIDQuery = "SELECT COUNT(*) FROM accounts WHERE userID = ?";
            try (PreparedStatement statement = connection.prepareStatement(checkIDQuery)) {
                statement.setString(1, userID);

                // Execute the query
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();

                int count = resultSet.getInt(1);

                if (count > 0) {
                    flag3 = true;

                } else {
                    flag3 = false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        //Create Random Unique Account Number

        boolean flag4=true;
        long accountNumber=0L;
        while(flag4){
            Random random = new Random();

            // Generate a 12-digit random number
             accountNumber = (long) (Math.random() * 9000000000L) + 100000000000L;;

            String checkAccountQuery="SELECT COUNT(*) FROM accounts WHERE Account_Number=?";
            try(PreparedStatement statement=connection.prepareStatement(checkAccountQuery)){
                statement.setLong(1,accountNumber);
                ResultSet resultSet= statement.executeQuery();
                resultSet.next();

                int count=resultSet.getInt(1);
                if(count>0){
                    flag4=true;
                }else{
                    flag4=false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        //insert into sql
        LocalDate today= java.time.LocalDate.now();
        java.sql.Date sqlDate = java.sql.Date.valueOf(today);
        String insertUserSQL = "INSERT INTO accounts (userID,Aadhar_Card_Number,Account_Number,Account_Holder_Name,Balance,Address,Age,password,Account_Open_Date) VALUES (?,?, ?, ?,?,?,?,?,?)";


        try (PreparedStatement statement = connection.prepareStatement(insertUserSQL)) {
            statement.setString(1, userID);
            statement.setLong(2,aadharNo);
            statement.setLong(3,accountNumber);
            statement.setString(4,Name);
            statement.setInt(5,0);
            statement.setString(6,address);
            statement.setInt(7,age);
            statement.setString(8,password);
            statement.setDate(9, sqlDate);
            statement.executeUpdate();

            }
            System.out.println("User Created Successfully");
        }




    private static void deleteAccount(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter User id to delete: ");
        String midToDelete = scanner.next();

        String deleteManagerSQL = "DELETE FROM accounts WHERE userID = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteManagerSQL)) {
            statement.setString(1, midToDelete);
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("User with ID " + midToDelete + " deleted successfully.");
            } else {
                System.out.println("No User found with ID " + midToDelete + ". No changes made.");
            }
        }
    }


    private static void viewAllUsersDetails(Connection connection) throws SQLException {
        String selectAllManagersSQL = "SELECT * FROM accounts";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectAllManagersSQL);

            while (resultSet.next()) {
                String userID = resultSet.getString("userID");
                Long aadharCardNum = resultSet.getLong("Aadhar_Card_Number");
                Long accountNumber=resultSet.getLong("Account_Number");
                String accountHolderName=resultSet.getString("Account_Holder_Name");
                int balance=resultSet.getInt("Balance");
                String address=resultSet.getString("Address");
                int age=resultSet.getInt("Age");
                Date account_opening_date=resultSet.getDate("Account_Open_Date");

                System.out.println("User ID: " + userID);
                System.out.println("Aadhar Card Number: " + aadharCardNum);
                System.out.println("Account Number is: : "+accountNumber);
                System.out.println("Account Holder Name: "+accountHolderName);
                System.out.println("Current Balance: "+balance);
                System.out.println("Age: "+age);
                System.out.println("Account Opening Date: "+account_opening_date);

                System.out.println("------------");
            }
        }
    }


    private static void viewSpecificUserDetails(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter User ID to view details (or 'q' to quit): ");
            String managerID = scanner.nextLine().trim();

            if (managerID.equalsIgnoreCase("q")) {
                //If User wants to quit
                return;
            }

            try {

                String selectManagerSQL = "SELECT * FROM accounts WHERE userID = ?";
                try (PreparedStatement statement = connection.prepareStatement(selectManagerSQL)) {
                    statement.setString(1, managerID);

                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {

                        String userID = resultSet.getString("userID");
                        Long aadharCardNum = resultSet.getLong("Aadhar_Card_Number");
                        Long accountNumber=resultSet.getLong("Account_Number");
                        String accountHolderName=resultSet.getString("Account_Holder_Name");
                        int balance=resultSet.getInt("Balance");
                        String address=resultSet.getString("Address");
                        int age=resultSet.getInt("Age");
                        Date account_opening_date=resultSet.getDate("Account_Open_Date");

                        System.out.println("User ID: " + userID);
                        System.out.println("Aadhar Card Number: " + aadharCardNum);
                        System.out.println("Account Number is: : "+accountNumber);
                        System.out.println("Account Holder Name: "+accountHolderName);
                        System.out.println("Current Balance: "+balance);
                        System.out.println("Age: "+age);
                        System.out.println("Account Opening Date: "+account_opening_date);

                        System.out.println("------------");


                    } else {
                        System.out.println("No User found with ID " + managerID);
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid numeric ID or 'q' to quit.");
            }
        }
    }


    private static void viewAllApprovalRequests(Connection connection) throws SQLException {
        String selectAllManagersSQL = "SELECT * FROM approval_pending";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectAllManagersSQL);

            int total=0;
            while (resultSet.next()) {
                String userID = resultSet.getString("userID");
                Long aadharCardNum = resultSet.getLong("Aadhar_Card_Number");
                Long accountNumber=resultSet.getLong("Account_Number");
                String accountHolderName=resultSet.getString("Account_Holder_Name");
                int balance=resultSet.getInt("Balance");
                String address=resultSet.getString("Address");
                int age=resultSet.getInt("Age");
                Date account_opening_date=resultSet.getDate("Account_Open_Date");

                System.out.println("Request No: "+ total++);
                System.out.println("User ID: " + userID);
                System.out.println("Aadhar Card Number: " + aadharCardNum);
                System.out.println("Account Number is: : "+accountNumber);
                System.out.println("Account Holder Name: "+accountHolderName);
                System.out.println("Current Balance: "+balance);
                System.out.println("Age: "+age);
                System.out.println("Account Opening Date: "+account_opening_date);

                System.out.println("------------");
            }
        }
    }


    private static void approveOrDeleteRequests(Connection connection) throws SQLException {
        String selectAllPendingRequestsSQL = "SELECT * FROM approval_pending";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectAllPendingRequestsSQL);

            int total = 1;
            while (resultSet.next()) {
                String userID = resultSet.getString("userID");
                Long aadharCardNum = resultSet.getLong("Aadhar_Card_Number");
                Long accountNumber = resultSet.getLong("Account_Number");
                String accountHolderName = resultSet.getString("Account_Holder_Name");
                int balance = resultSet.getInt("Balance");
                String address = resultSet.getString("Address");
                int age = resultSet.getInt("Age");
                Date account_opening_date = resultSet.getDate("Account_Open_Date");
                String password=resultSet.getString("password");


                System.out.println("Request No: " + total++);
                System.out.println("User ID: " + userID);
                System.out.println("Aadhar Card Number: " + aadharCardNum);
                System.out.println("Account Number: " + accountNumber);
                System.out.println("Account Holder Name: " + accountHolderName);
                System.out.println("Current Balance: " + balance);
                System.out.println("Age: " + age);
                System.out.println("Account Opening Date: " + account_opening_date);
                System.out.println("------------");

                System.out.println("Select from below options: ");
                System.out.println("1. Approve");
                System.out.println("2. Delete");
                System.out.println("3. Keep Pending");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");

                Scanner scanner = new Scanner(System.in);
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        String moveToAccountsSQL = "INSERT INTO accounts (userID, Aadhar_Card_Number, Account_Number, " +
                                "Account_Holder_Name, Balance, Address, Age,password,Account_Open_Date) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?,?,?)";
                        try (PreparedStatement statement1 = connection.prepareStatement(moveToAccountsSQL)) {
                            statement1.setString(1, userID);
                            statement1.setLong(2,aadharCardNum);
                            statement1.setLong(3,accountNumber);
                            statement1.setString(4,accountHolderName);
                            statement1.setInt(5,balance);
                            statement1.setString(6,address);
                            statement1.setInt(7,age);
                            statement1.setString(8,password);
                            statement1.setDate(9, account_opening_date);
                            statement1.executeUpdate();
                            System.out.println("Account approved");

                        }catch (Exception e){
                            System.out.println(e.toString());
                        }

                        String deleteFromPendingSQL = "DELETE FROM approval_pending WHERE userID = ?";
                        try{

                            PreparedStatement statement2=connection.prepareStatement(deleteFromPendingSQL);
                            statement2.setString(1,userID);
                            statement2.executeUpdate();
                        }catch (Exception e){
                            System.out.println(e.toString());
                        }
                        break;

                    case 2:
                        String deletePendingSQL = "DELETE FROM approval_pending WHERE userID = ?";
                        try{
                            PreparedStatement statement3=connection.prepareStatement(deletePendingSQL);
                            statement3.executeUpdate();
                            System.out.println("Request Declined");
                        }catch (Exception e){
                            System.out.println(e.toString());
                        }
                        break;

                    case 3:
                        // display the next record
                        continue;
//                        break;
                    case 4:
                        scanner.close();
                        connection.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
                total++;
            }
        }
    }


    private static void moveToAccounts(Connection connection,String query) throws SQLException{
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
        }


    }













    private static void resetManagerPassword(Connection connection, String username) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        String updatePasswordSQL = "UPDATE managers SET password = ? WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(updatePasswordSQL)) {
            statement.setString(1, newPassword);
            statement.setString(2, username);
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Password reset successfully for Manager: " + username);
            } else {
                System.out.println("Failed to reset password. Admin not found: " + username);
            }
        }
    }









}
