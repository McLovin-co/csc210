import java.util.Scanner;

public class GoodCasino {

    public static double play(Customer customer, SlotMachine machine, double amount) {
        double actualBet = customer.spend(amount);
        double winnings = machine.pullLever(actualBet);
        return winnings;
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Load customer and machine from files
        Customer customer = new Customer("../customer.txt");
        SlotMachine machine = new SlotMachine("../slot-machine.txt");

        System.out.println("Welcome to the Good Casino!");
        System.out.println("Customer wallet: $" + customer.checkWallet());
        System.out.println("Machine pot: $" + machine.getMoneyPot());

        // Game loop
        while (true) {
            System.out.print("\nHow much money would you like to bet? (or type 'quit'): ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("quit")) {
                break;
            }

            try {
                double bet = Double.parseDouble(input);

                // Check if customer or casino is broke
                if (customer.checkWallet() <= 0) {
                    System.out.println("You're out of money!");
                    break;
                }
                if (machine.getMoneyPot() <= 0) {
                    System.out.println("Casino is broke!");
                    break;
                }

                // Play the game
                double winnings = play(customer, machine, bet);

                // Show results
                System.out.println("Slots: " + machine.toString());
                System.out.println("Winnings: $" + winnings);

                // Give winnings to customer
                customer.receive(winnings);

                System.out.println("Your wallet: $" + customer.checkWallet());
                System.out.println("Machine pot: $" + machine.getMoneyPot());

            } catch (Exception e) {
                System.out.println("Invalid input!");
            }
        }

        // Save state to files
        customer.save("../customer.txt");
        machine.save("../slot-machine.txt");

        System.out.println("\nGame over! Progress saved.");
        scanner.close();
    }
}