public class Creature {
    String name;
    String size;
    int age;

    public Creature(String name, String size, int age) {
        this.name = name;
        this.size = size;
        this.age = age;
    }

    public void eat(String food) {
        System.out.println(name + " is eating " + food + "!");
    }

    public void speak(String message) {
        System.out.println(name + " says: " + message);
    }

    public void move(String direction) {
        System.out.println(name + " moves " + direction + ".");
    }

    public static void main(String[] args) {
        Creature dragon = new Creature("Smaug", "Large", 500);

        dragon.speak("I am a mighty dragon!");
        dragon.eat("gold coins");
        dragon.move("towards the mountain");
    }
}