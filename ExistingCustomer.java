import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.sql.*;
import java.util.Scanner;

public class ExistingCustomer extends Main{
    private Main main;

    public ExistingCustomer() {
        this.main = main;
    }
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_data";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Opengmail@123";


    private static String user1ID;
    public static void startExistingCustomer(String userID){
        user1ID=userID;
        System.out.println("Welcome to Your Account");
        try{
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create a table to store user and admin information if it doesn't exist
            Scanner scanner = new Scanner(System.in);

            while(true){


                System.out.println("Please Select from below options:");

                System.out.println("1. View Account Details");
                System.out.println("2. Deposit Money");
                System.out.println("3. Withdraw Money");
                System.out.println("4. Transfer Money"); //transfer money is pending
                System.out.println("5. Reset Your Password");
                System.out.println("6. Exit");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        viewAccountDetails(connection);
                        break;
                    case 2:
                        depositMoney(connection);
                        break;
                    case 3:
                        withdrawMoney(connection);
                        break;
                    case 4:
                        transferMoney(connection);
                        break;
                    case 5:
                        resetUserPassword(connection,user1ID);
                        break;
                    case 6:
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

    private static void viewAccountDetails(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean flag=true;
        while (flag) {
            try {
                String selectManagerSQL = "SELECT * FROM accounts WHERE userID=?";
                try (PreparedStatement statement = connection.prepareStatement(selectManagerSQL)) {
                    statement.setString(1,user1ID);

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
                        flag=false;

                    } else {
                        System.out.println("No User found with ID " + user1ID);
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid numeric ID or 'q' to quit.");
            }
        }
    }


    private static void depositMoney(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int amount=1;
        try{
            while( (amount%100 !=0)  || (amount<0) || amount>100000){
                System.out.println("Enter amount in the multiple of 100 and less than equal to 1,00,000: ");
                amount=scanner.nextInt();
            }
        }catch (Exception e){
            System.out.println("Invalid Input");
        }

        boolean flag=true;
        while (flag) {
            try {
                String selectManagerSQL = "SELECT Balance FROM accounts WHERE userID=?";
                try (PreparedStatement statement = connection.prepareStatement(selectManagerSQL)) {
                    statement.setString(1,user1ID);

                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        int balance=resultSet.getInt("Balance");
                        System.out.println("Your account is credited by: "+amount);
                        amount+=balance;
                        System.out.println("New Balance is: "+amount);
                        flag=false;

                    } else {
                        System.out.println("No User found with ID " + user1ID);
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid numeric ID or 'q' to quit.");
            }
        }

        String sql = "UPDATE accounts SET Balance = ? WHERE userID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, amount);
            statement.setString(2, user1ID);

            int rowsUpdated = statement.executeUpdate();
            System.out.println("Amount Deposited  Successfully.");
        }


    }



    private static void withdrawMoney(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int amount=1;

        while( (amount%100 != 0)  || (amount<100) || amount>100000){
            System.out.println("Enter amount in the multiple of 100 and less than equal to 1,00,000: ");
            amount=scanner.nextInt();
        }

        boolean flag=true;
        while (flag) {
            try {
                String selectManagerSQL = "SELECT Balance FROM accounts WHERE userID=?";
                try (PreparedStatement statement = connection.prepareStatement(selectManagerSQL)) {
                    statement.setString(1,user1ID);

                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        int balance=resultSet.getInt("Balance");
                        if(balance>=amount){
                            System.out.println("Your account is debited by: "+amount);
                            balance-=amount;
                        }else{
                            System.out.println("Insufficient Balance: remaining balance is: "+balance);
                        }
                        flag=false;
                        amount=balance;
                    } else {
                        System.out.println("No User found with ID " + user1ID);
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid numeric ID or 'q' to quit.");
            }
        }


        String sql = "UPDATE accounts SET Balance = ? WHERE userID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, amount);
            statement.setString(2, user1ID);

            statement.executeUpdate();

        }


    }



    private static void transferMoney(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        //get data of the recipient, like account number, name and amount to send
        System.out.println("Enter Account Number of recipient: ");
        Long acc=scanner.nextLong();

        scanner.nextLine();
        System.out.println("Enter the Name of recipient: ");
        String recipient=scanner.nextLine();

        System.out.println("Enter Amount: ");
        int amt=scanner.nextInt();


        //check that account number is existing
        String query1="Select Account_Number from accounts where userID=?";
        try{
            System.out.println("Entered in try block");
            PreparedStatement pr=connection.prepareStatement(query1);
            pr.setString(1,user1ID);
            System.out.println(user1ID);
            ResultSet resultSet=pr.executeQuery();
            if(resultSet.next()){
                Long accNo=resultSet.getLong("Account_Number");
//                System.out.println("Sender account number is: "+accNo);
                debit(connection,accNo,amt,acc,recipient);
                credit(connection,accNo,amt,acc,recipient);
                System.out.println("Your account has been debited by: "+amt);
                System.out.println(amt+ " is transferred successfully to "+ recipient);
            }else{
                System.out.println("No account is found");
            }

        }catch (Exception e){
            e.toString();
        }

    }



    private static void debit(Connection connection,Long sender_accountNo, int amount,Long recAcc_No, String rec_Name) throws SQLException{


        //first check sender account number is valid or not
        //check if the sender has sufficient balance or not
        //check if the receiver account number is existing or not
        //check if the account number of receiver is matching with account holder name or not


        String checkquery="Select count(*) from accounts where Account_Number=?";
        try{
            PreparedStatement statement=connection.prepareStatement(checkquery);
            statement.setLong(1, sender_accountNo);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                //Means sender account exists, now check if the account has the amount or not
//                System.out.println("Checking Balance in sender account if exists or not");
                String query2="Select Balance from accounts where Account_Number=?";
                PreparedStatement statement2=connection.prepareStatement(query2);
                statement2.setLong(1,sender_accountNo);
                ResultSet resultSet2=statement2.executeQuery();

                //below will check whether the account has sufficient balance or not
                if(resultSet2.next()){
                    int bal=resultSet2.getInt("Balance");
                    if(bal>=amount){

                        //Now checking whether the receiver account is existing or not
                        String query3="Select count(*) from accounts where Account_Number=?";
                        PreparedStatement statement3=connection.prepareStatement(query3);
                        statement3.setLong(1,recAcc_No);
                        ResultSet resultSet3=statement3.executeQuery();
                        if(resultSet3.next()){
                            //means the Receiver account is existing
                            //Now verify the Name and account number
                            String query4="Select Account_Holder_Name from accounts where Account_Number=?";
                            PreparedStatement statement4=connection.prepareStatement(query4);
                            statement4.setLong(1,recAcc_No);
                            ResultSet resultSet4=statement4.executeQuery();
                            String dbName=resultSet4.getString("Account_Holder_Name");
                            if(dbName.equals(rec_Name)){
//                        System.out.println("Balannce is greater or equal than amount");
                                //Debit from the account of sender
                                String query="UPDATE accounts SET BALANCE=BALANCE-? where Account_Number=?";
                                PreparedStatement statement5=connection.prepareStatement(query);
                                statement5.setInt(1, amount);
                                statement5.setLong(2, sender_accountNo);
                                statement5.executeUpdate();


                            }else{
                                System.out.println(recAcc_No+" is not owned by "+rec_Name);
                                return;
                            }

                        } else{
                            System.out.println("Account Number does not exists.");
                            return;
                        }


                    }else{
                        System.out.println("Transfer failed "+bal+" is left in your account.");
                        return;
                    }

                }


            }else{
                System.out.println(sender_accountNo+" Account does not exist.");
                return;
            }


        }catch (SQLException e){
            e.printStackTrace();
        }

    }




    private static void credit(Connection connection,Long sender_accountNo, int amount,Long recAcc_No, String rec_Name) throws SQLException {


        //first check sender account number is valid or not
        //check if the sender has sufficient balance or not
        //check if the receiver account number is existing or not
        //check if the account number of receiver is matching with account holder name or not
        //then deposit money in receiver account


        String checkquery="Select count(*) from accounts where Account_Number=?";
        try{
            PreparedStatement statement=connection.prepareStatement(checkquery);
            statement.setLong(1, sender_accountNo);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                //Means sender account exists, now check if the account has the amount or not
//                System.out.println("Checking Balance in sender account if exists or not");
                String query2="Select Balance from accounts where Account_Number=?";
                PreparedStatement statement2=connection.prepareStatement(query2);
                statement2.setLong(1,sender_accountNo);
                ResultSet resultSet2=statement2.executeQuery();

                //below will check whether the account has sufficient balance or not
                if(resultSet2.next()){
                    int bal=resultSet2.getInt("Balance");
                    if(bal>=amount){

                        //Now checking whether the receiver account is existing or not
                        String query3="Select count(*) from accounts where Account_Number=?";
                        PreparedStatement statement3=connection.prepareStatement(query3);
                        statement3.setLong(1,recAcc_No);
                        ResultSet resultSet3=statement3.executeQuery();
                        if(resultSet3.next()){
                            //means the Receiver account is existing
                            //Now verify the Name and account number
                            String query4="Select Account_Holder_Name from accounts where Account_Number=?";
                            PreparedStatement statement4=connection.prepareStatement(query4);
                            statement4.setLong(1,recAcc_No);
                            ResultSet resultSet4=statement4.executeQuery();
                            String dbName=resultSet4.getString("Account_Holder_Name");
                            if(dbName.equals(rec_Name)){
//                        System.out.println("Balannce is greater or equal than amount");
                                //Debit from the account of sender
                                String query="UPDATE accounts SET BALANCE=BALANCE+? where Account_Number=?";
                                PreparedStatement statement5=connection.prepareStatement(query);
                                statement5.setInt(1, amount);
                                statement5.setLong(2, recAcc_No);
                                statement5.executeUpdate();


                            }else{
                                System.out.println(recAcc_No+" is not owned by "+rec_Name);
                                return;
                            }

                        } else{
                            System.out.println("Account Number does not exists.");
                            return;
                        }


                    }else{
                        System.out.println("Transfer failed "+bal+" is left in your account.");
                        return;
                    }

                }


            }else{
                System.out.println(sender_accountNo+" Account does not exist.");
                return;
            }


        }catch (SQLException e){
            e.printStackTrace();
        }






    }





    private static void resetUserPassword(Connection connection, String username) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        String updatePasswordSQL = "UPDATE accounts SET password = ? WHERE userID = ?";
        try (PreparedStatement statement = connection.prepareStatement(updatePasswordSQL)) {
            statement.setString(1, newPassword);
            statement.setString(2, username);
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Password reset successfully for User: " + username);
            } else {
                System.out.println("Failed to reset password. User not found: " + username);
            }
        }
    }



}
