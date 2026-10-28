import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    static final int CAPACITY = 5;
    static final String LINE = "==========================================";

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        ArrayStack stack = new ArrayStack(CAPACITY);
        ArrayQueue queue = new ArrayQueue(CAPACITY);

        System.out.println(LINE);
        System.out.println("      LAB 5 - STACK AND QUEUE PROGRAM");
        System.out.println(LINE);
        System.out.println("The stack and the queue can each hold " + CAPACITY + " values.");
        System.out.println();

        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt(input, "Enter your choice: ");
            System.out.println();

            switch (choice) {

                case 1:
                    System.out.println("--- STACK: PUSH ---");
                    int pushValue = readInt(input, "Enter value to push: ");
                    stack.push(pushValue);
                    reportStack(stack);
                    break;

                case 2:
                    System.out.println("--- STACK: POP ---");
                    if (stack.isEmpty()) {
                        System.out.println("Stack Underflow: stack is empty, cannot pop");
                    } else {
                        int poppedValue = stack.pop();
                        System.out.println("Removed value: " + poppedValue);
                    }
                    reportStack(stack);
                    break;

                case 3:
                    System.out.println("--- STACK: PEEK ---");
                    if (stack.isEmpty()) {
                        System.out.println("Stack is empty, cannot peek");
                    } else {
                        int topValue = stack.peek();
                        System.out.println("Top value is " + topValue + " and it was not removed.");
                    }
                    break;

                case 4:
                    System.out.println("--- STACK: CHECK EMPTY ---");
                    System.out.println("Is the stack empty? " + stack.isEmpty());
                    break;

                case 5:
                    System.out.println("--- STACK: CHECK FULL ---");
                    System.out.println("Is the stack full? " + stack.isFull());
                    break;

                case 6:
                    System.out.println("--- STACK: DISPLAY ---");
                    stack.display();
                    waitForEnter(input);
                    break;

                case 7:
                    System.out.println("--- QUEUE: ENQUEUE ---");
                    int enqueueValue = readInt(input, "Enter value to enqueue: ");
                    queue.enqueue(enqueueValue);
                    reportQueue(queue);
                    break;

                case 8:
                    System.out.println("--- QUEUE: DEQUEUE ---");
                    if (queue.isEmpty()) {
                        System.out.println("Queue Underflow: queue is empty, cannot dequeue");
                    } else {
                        int dequeuedValue = queue.dequeue();
                        System.out.println("Removed value: " + dequeuedValue);
                    }
                    reportQueue(queue);
                    break;

                case 9:
                    System.out.println("--- QUEUE: PEEK ---");
                    if (queue.isEmpty()) {
                        System.out.println("Queue is empty, cannot peek");
                    } else {
                        int frontValue = queue.peek();
                        System.out.println("Front value is " + frontValue + " and it was not removed.");
                    }
                    break;

                case 10:
                    System.out.println("--- QUEUE: CHECK EMPTY ---");
                    System.out.println("Is the queue empty? " + queue.isEmpty());
                    break;

                case 11:
                    System.out.println("--- QUEUE: CHECK FULL ---");
                    System.out.println("Is the queue full? " + queue.isFull());
                    break;

                case 12:
                    System.out.println("--- QUEUE: DISPLAY ---");
                    queue.display();
                    waitForEnter(input);
                    break;

                case 0:
                    running = false;
                    System.out.println("Program ended. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a number from 0 to 12.");
                    System.out.println("Type 0 to exit the program.");
                    break;
            }
        }

        input.close();
    }

    static void printMenu() {
        System.out.println(LINE);
        System.out.println("                MAIN MENU");
        System.out.println(LINE);
        System.out.println();
        System.out.println("--- STACK (LIFO) --- last in, first out");
        System.out.println("  1. Push a value");
        System.out.println("  2. Pop a value");
        System.out.println("  3. Peek at the top value");
        System.out.println("  4. Check if the stack is empty");
        System.out.println("  5. Check if the stack is full");
        System.out.println("  6. Display the stack");
        System.out.println();
        System.out.println("--- QUEUE (FIFO) --- first in, first out");
        System.out.println("  7. Enqueue a value");
        System.out.println("  8. Dequeue a value");
        System.out.println("  9. Peek at the front value");
        System.out.println(" 10. Check if the queue is empty");
        System.out.println(" 11. Check if the queue is full");
        System.out.println(" 12. Display the queue");
        System.out.println();
        System.out.println("  0. Exit the program");
        System.out.println();
        System.out.println("Choose an operation by entering its number.");
        System.out.println(LINE);
    }

    static int readInt(Scanner input, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = input.nextInt();
                input.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                input.nextLine();
            }
        }
    }

    static void waitForEnter(Scanner input) {
        System.out.print("Press Enter to return to the menu...");
        input.nextLine();
        System.out.println();
    }

    static void reportStack(ArrayStack stack) {
        System.out.println("The stack now holds " + stack.size() + " of " + CAPACITY + " values.");
    }

    static void reportQueue(ArrayQueue queue) {
        System.out.println("The queue now holds " + queue.size() + " of " + CAPACITY + " values.");
    }
}