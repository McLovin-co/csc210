public class Hex {
    public static void main(String[] args){

        if (args.length ==0) {
            System.err.println("Error: No argument provided");
            System.exit(1);
        }

        String input = args[0];

        if (!isValidHex(input)) {
            System.err.println("Error: Not a valid hexadecimal number");
            System.exit(1);
        }

        long decimal = hexToDecimal(input);
        System.out.println(decimal);
    }
    public static boolean isValidHex(String hex) {
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);

            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
                return false;
            }
        }
        return true;
    }
    public static long hexToDecimal(String hex) {
        long decimal = 0;
        int power = 0;

        for (int i = hex.length() - 1; i >= 0; i--) {
            char c = hex.charAt(i);
            int digit;

            if (c >= '0' && c <= '9') {
                digit = c - '0';
            } else if (c >= 'a' && c <= 'f') {
                digit = c - 'a' + 10;
            } else {
                digit = c - 'A' + 10;
            }

            decimal += digit * Math.pow(16, power);
            power++;
        }

        return decimal;
    }
}