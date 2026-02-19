import java.io.File;
import java.util.Scanner;

public class Customer {
    private double wallet;

    public Customer() {
        wallet = 500.00;
    }

    public Customer(String filename) {
        try {
            Scanner scanner = new Scanner(new File(filename));
            wallet = scanner.nextDouble();
            scanner.close();
        } catch (Exception e) {
            wallet = 500.00;
        }
    }

    public double spend(double amount) {
        if (amount > wallet) {
            double remaining = wallet;
            wallet = 0;
            return remaining;
        } else {
            wallet -= amount;
            return amount;
        }
    }

    public void receive(double amount) {
        wallet += amount;
    }

    public double checkWallet() {
        return wallet;
    }

    public void save(String filename) {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(filename);
            writer.println(wallet);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving file: " + filename);
        }
    }
}  // ← This closes the class - everything must be inside!