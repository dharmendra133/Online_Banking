import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.*;
import java.util.*;
import java.time.*;
public class NewCustomer {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_data";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Opengmail@123";

    public static void startNewCustomer() {
        System.out.println("Welcome to Our Bank");

        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create a table to store user and admin information if it doesn't exist
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Please Select from below options:");
                System.out.println("1. Create New Account: ");
                System.out.println("2. Know about us: ");
                System.out.println("3. Check Account Activation Status Using User ID");
                System.out.println("4. Exit");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        createNewAccount(connection);
                        break;
                    case 2:
                        knowaboutUs();
                        break;
                    case 3:
//                        accountStatus(); //ask for user id from the customer
                        break;
                    case 4:
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



        private static void createNewAccount(Connection connection) throws SQLException {

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
            String insertUserSQL = "INSERT INTO approval_pending (userID,Aadhar_Card_Number,Account_Number,Account_Holder_Name,Balance,Address,Age,password,Account_Open_Date) VALUES (?,?, ?, ?,?,?,?,?,?)";


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
                System.out.println("Your user ID is: "+userID);


            }
            System.out.println("Account Created Successfully");
            System.out.println("Account status: Pending");
        }

        private static void knowaboutUs(){
            System.out.println("Amdocs Bank, is a prominent private sector bank headquartered in Gurgaon, India. Established in 1982, it has grown to become one of the largest and most significant financial institutions in the country.\n" +
                    "\n" +
                    "The bank provides a wide range of banking and financial services to individuals, businesses, and corporates. These services encompass retail and corporate banking, investment banking, asset management, insurance, venture capital, and other related offerings.\n" +
                    "\n" +
                    "Amdocs Bank is recognized for its extensive network of branches and ATMs, making it easily accessible to customers across India and even in some international locations. Its customer-centric approach and innovative use of technology have helped it maintain a strong market presence.\n" +
                    "\n" +
                    "Over the years, Amdocs Bank has played a crucial role in the development of the Indian banking sector. It has embraced technological advancements to offer digital banking solutions, ensuring convenience and efficiency for customers. Additionally, the bank has actively participated in various social and community development initiatives, showcasing a commitment to corporate social responsibility.\n" +
                    "\n" +
                    "With a focus on customer satisfaction, a broad spectrum of financial products, and a robust presence in the Indian banking landscape, Amdocs Bank continues to be a key player, contributing significantly to the growth and progress of the Indian economy.");
            System.out.println("Please Select From Below Options");
            while (true){
                System.out.println("1. Know About Benefits of Saving Account in Amdocs Bank.");
                System.out.println("2. Know About Benefits of Salary Account in Amdocs Bank.");
                System.out.println("3. Know About Benefits of Debit Card of Amdocs Bank.");
                System.out.println("4. Know About Benefits of Credit Card of Amdocs Bank.");
                System.out.println("5. Know About Other Benfits");
                System.out.println("6. Exit");
                Scanner sc=new Scanner(System.in);
                int choice= sc.nextInt();
                switch (choice){
                    case 1:
                        explainSavingAccount();
                        break;
                    case 2:
                        explainSalaryAccount();
                        break;
                    case 3:
                        explainDebitCard();
                        break;
                    case 4:
                        explainCreditCard();
                        break;
                    case 5:
                        explainOtherBenefits();
                        break;
                    case 6:
                        break;
                    default:
                        System.out.println("Thank You For Visiting Us.");
                }
            }

        }


    private static void explainSavingAccount(){
        System.out.println("\n" +
                "Competitive Interest Rates: Amdocs Bank offers attractive interest rates on savings account balances, helping your savings grow steadily.\n" +
                "Easy Access: With a wide network of ATMs and branches, accessing your funds is convenient and hassle-free.\n" +
                "Secure Deposits: Amdocs Bank provides deposit insurance, ensuring the safety of your savings up to a specified limit, instilling confidence in your banking experience.\n" +
                "Digital Banking: The bank's advanced online and mobile banking platforms enable effortless transactions, fund transfers, and account management, saving time and effort.\n" +
                "Customized Solutions: Amdocs Bank tailors savings accounts to suit varied needs, offering specialized accounts for students, senior citizens, and specific target groups, enhancing customer satisfaction and benefits.");

    }

    private static void explainSalaryAccount(){
        System.out.println("Amdocs Bank's salary accounts cater to the specific financial needs of salaried individuals, providing a range of benefits that enhance their banking experience:\n" +
                "\n" +
                "Zero Balance Account: Amdocs Bank often offers salary accounts with zero minimum balance requirements, ensuring that individuals need not maintain a minimum balance, making it ideal for those just starting their careers.\n" +
                "\n" +
                "Customized Packages: The bank provides tailored packages based on the employer's tie-up, offering specialized benefits, features, and privileges such as higher withdrawal limits, preferential forex rates, and discounted locker facilities.\n" +
                "\n" +
                "Instant Account Opening: Quick and easy account opening process, often done digitally, allowing employees to access their salary accounts promptly and without extensive paperwork.\n" +
                "\n" +
                "Attractive Interest Rates: Amdocs Bank salary accounts often provide competitive interest rates on the account balance, allowing account holders to earn interest on their savings.\n" +
                "\n" +
                "Comprehensive Debit Card: Account holders receive a feature-rich debit card with benefits like cashback offers, discounts on shopping, dining, and other perks, enhancing their spending capabilities.\n" +
                "\n" +
                "Access to ATMs and Branches: A vast network of ATMs and branches nationwide ensures easy accessibility for cash withdrawals and other banking services, providing convenience to account holders.\n" +
                "\n" +
                "Loan and Credit Card Offers: Salary account holders are often eligible for pre-approved loans, credit cards, or overdraft facilities at favorable terms due to their stable source of income, aiding their financial requirements.\n" +
                "\n" +
                "Discounts and Offers: Avail exclusive discounts and offers on various lifestyle and healthcare services, travel, dining, and shopping through tie-ups and partnerships with Amdocs Bank.\n" +
                "\n" +
                "Online Banking Services: Access to advanced online banking platforms, enabling account holders to manage their finances, pay bills, transfer funds, and access account statements with ease, enhancing banking efficiency.\n" +
                "\n" +
                "Amdocs Bank's salary accounts are designed to simplify financial management for salaried individuals, offering a range of features and benefits that cater to their unique banking needs.\n" +
                "\n" +
                "\n");

    }

    private static void explainDebitCard(){
        System.out.println("Wide Acceptance: Amdocs Bank's debit cards are widely accepted, allowing seamless transactions at millions of merchants, both online and offline, globally.\n" +
                "Cash Withdrawals: Access to a vast network of ATMs for convenient cash withdrawals, providing flexibility and accessibility wherever needed.\n" +
                "Contactless Payments: Many Amdocs debit cards come with contactless payment technology, enabling quick and secure transactions by simply tapping the card on the terminal.\n" +
                "Online Shopping: Easy and secure online shopping with the debit card, enabling purchases from various e-commerce platforms and websites.\n" +
                "Reward Programs: Debit card transactions often accrue reward points, redeemable for discounts, vouchers, or gifts, enhancing the cardholder's overall banking experience.");
    }

    private static void explainCreditCard(){
        System.out.println("Reward Points: Amdocs Bank credit cards offer reward points for purchases, redeemable for gifts, vouchers, air miles, or cashback, enhancing value for spending.\n" +
                "Cashback Offers: Many Amdocs credit cards provide cashback on specific purchases, saving on transactions and boosting overall savings.\n" +
                "Travel Benefits: Travel-specific credit cards offer benefits like airport lounge access, travel insurance, and discounts on flights and hotels, enhancing the travel experience.\n" +
                "Interest-Free Period: Enjoy an interest-free period on credit card purchases, usually up to 45-50 days, allowing flexibility in managing finances and payments.\n" +
                "EMI Options: Convert high-value purchases into easy monthly installments, offering financial convenience and flexibility in repayment.\n" +
                "Global Acceptance: Amdocs credit cards are widely accepted worldwide, enabling hassle-free transactions during domestic and international travel.");

    }

    private static void explainOtherBenefits(){
        System.out.println("Amdocs Bank offers several additional benefits to its account holders, enhancing their overall banking experience and financial well-being:\n" +
                "\n" +
                "Comprehensive Product Range: Amdocs Bank provides a wide array of financial products and services, including savings accounts, current accounts, fixed deposits, loans, insurance, investments, and more, allowing customers to meet diverse financial needs from one institution.\n" +
                "\n" +
                "Digital Banking Solutions: Access to cutting-edge digital platforms such as iMobile and Internet Banking, enabling seamless transactions, fund transfers, bill payments, and account management from the comfort of one's home or on the go.\n" +
                "\n" +
                "Customer Support: Round-the-clock customer service and helplines for assistance with banking inquiries, account-related concerns, and more, ensuring continuous and responsive support.\n" +
                "\n" +
                "Personalized Services: Tailored financial solutions based on individual requirements, including wealth management services, priority banking, and customized investment advice, providing a personalized approach to financial planning.\n" +
                "\n" +
                "Educational Resources: Access to educational resources, workshops, webinars, and financial literacy programs to enhance financial knowledge and promote informed decision-making.\n" +
                "\n" +
                "Discounts and Offers: Exclusive discounts, offers, and cashback deals on shopping, dining, travel, and lifestyle experiences through tie-ups and partnerships, providing additional value to account holders.\n" +
                "\n" +
                "Special Accounts for Specific Segments: Specialized accounts catering to students, senior citizens, and specific target groups, offering tailored features and benefits to meet their unique banking needs.\n" +
                "\n" +
                "Secure Banking: Stringent security measures to safeguard account and transaction data, including two-factor authentication, secure login features, and real-time transaction alerts, ensuring a safe and secure banking environment.\n" +
                "\n" +
                "Community Initiatives: Involvement in various social and community development initiatives, showcasing a commitment to corporate social responsibility and contributing to the betterment of society.\n" +
                "\n" +
                "Amdocs Bank strives to offer holistic financial solutions, convenience, and a customer-centric approach, making it a preferred choice for individuals seeking a reliable and comprehensive banking experience.");
    }



    }

