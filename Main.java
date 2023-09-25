import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.ResultSet;

public class Main {
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_data";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Opengmail@123";

    public static void main(String[] args) {
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create a table to store user and admin information if it doesn't exist
            createTable(connection);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("------------------------------");
                System.out.println("Please select an option:");
                System.out.println("1. Admin");
                System.out.println("2. Manager");
                System.out.println("3. Existing Customer");
                System.out.println("4. New Customer");
                System.out.println("5. Exit");
                System.out.println("-------------------------------");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        //login
                        loginasAdmin(connection);
                        break;
                    case 2:
                        //login
                        loginasManager(connection);
                        // call Manager page
                        break;
                    case 3:
                        //login`
                        loginasCustomer(connection);
                        //call Existing customer page
                        break;
                    case 4:
                        //No login required
                        //directly call New Customer page
                        NewCustomer nc=new NewCustomer();
                        nc.startNewCustomer();

                    case 5:
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



    private static void loginasAdmin(Connection connection) throws SQLException {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username=sc.next();
        System.out.println("Enter your password: ");
        String password=sc.next();
        String selectAdminSQL = "SELECT username FROM admins WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectAdminSQL)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Logged in successfully.");
//                return resultSet.getString("username");
                Admin ad=new Admin();
                ad.startAdmin(username);

            } else {
                System.out.println("Not logged in. Invalid credentials.");
//                return null;
            }

        }
    }



    private static void loginasManager(Connection connection) throws SQLException {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username=sc.next();
        System.out.println("Enter your password: ");
        String password=sc.next();
        String selectAdminSQL = "SELECT username FROM managers WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectAdminSQL)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Logged in successfully.");
//                return resultSet.getString("username");
                Manager md=new Manager();
                md.startManager(username);
            } else {
                System.out.println("Not logged in. Invalid credentials.");
//                return null;
            }

        }
    }



    private static void loginasCustomer(Connection connection) throws SQLException {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter your userID: ");
        String username=sc.next();
        System.out.println("Enter your password: ");
        String password=sc.next();
        String selectAdminSQL = "SELECT userID FROM accounts WHERE userID = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectAdminSQL)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Logged in successfully.");
                ExistingCustomer ec=new ExistingCustomer();
                ec.startExistingCustomer(username);
            } else {
                System.out.println("Not logged in. Invalid credentials.");
            }

        }
    }





    private static void createTable(Connection connection) throws SQLException {
        String createUserTableSQL = "CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255), password VARCHAR(255))";
        String createAdminTableSQL = "CREATE TABLE IF NOT EXISTS admins (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255), password VARCHAR(255))";

        try (PreparedStatement statement = connection.prepareStatement(createUserTableSQL)) {
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement(createAdminTableSQL)) {
            statement.executeUpdate();
        }
    }

    private static void createUser(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        String insertUserSQL = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertUserSQL)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
        }

        System.out.println("User created successfully.");
    }

    private static void createAdmin(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        String insertAdminSQL = "INSERT INTO admins (username, password) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertAdminSQL)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
        }

        System.out.println("Admin created successfully.");
    }
}
