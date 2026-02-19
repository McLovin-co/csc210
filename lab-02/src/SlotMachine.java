import java.io.File;
import java.util.Scanner;
public class SlotMachine {
    private char slot1;
    private char slot2;
    private char slot3;
    private double moneyPot;
    public SlotMachine() {
        moneyPot = 1000000.00;
    }
    public SlotMachine(String filename) {
        try {
            Scanner scanner = new Scanner(new File(filename));
            moneyPot = scanner.nextDouble();
            scanner.close();
        } catch (Exception e) {
            moneyPot = 1000000.00;  // Default if file not found
        }
    }
    public double pullLever(double amount) {
        // Random characters: smiley ☺ (\u263A), heart ♥ (\u2764), seven (7)
        char[] symbols = {'\u263A', '\u2764', '7'};

        // Randomly set each slot
        slot1 = symbols[(int)(Math.random() * 3)];
        slot2 = symbols[(int)(Math.random() * 3)];
        slot3 = symbols[(int)(Math.random() * 3)];

        // Check if all three match
        if (slot1 == slot2 && slot2 == slot3) {
            double winnings = amount * 10;
            moneyPot -= winnings;  // Machine loses money
            return winnings;
        }

        // No match, player loses
        moneyPot += amount;  // Machine gains the bet
        return 0.0;
    }
    public String toString() {
        return "" + slot1 + " " + slot2 + " " + slot3;
    }

    public double getMoneyPot() {
        return moneyPot;
    }

    public void save(String filename) {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(filename);
            writer.println(moneyPot);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving file: " + filename);
        }
    }
}