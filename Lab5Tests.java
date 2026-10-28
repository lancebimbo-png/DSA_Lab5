import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class Lab5Tests {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("===== LAB 5 TEST HARNESS =====");

        runTest("Stack new state", () -> testStackNew());
        runTest("Stack push, pop, and LIFO order", () -> testStackPushPopLifo());
        runTest("Stack fill and overflow", () -> testStackFillAndOverflow());
        runTest("Stack underflow", () -> testStackUnderflow());
        runTest("Stack negative values including -1", () -> testStackNegativeValues());
        runTest("Stack display", () -> testStackDisplay());
        runTest("Stack remove all and reuse", () -> testStackRemoveAllAndReuse());

        runTest("Queue new state", () -> testQueueNew());
        runTest("Queue enqueue, dequeue, and FIFO order", () -> testQueueEnqueueDequeueFifo());
        runTest("Queue fill and overflow", () -> testQueueFillAndOverflow());
        runTest("Queue underflow", () -> testQueueUnderflow());
        runTest("Queue negative values including -1", () -> testQueueNegativeValues());
        runTest("Queue circular wraparound", () -> testQueueCircularWraparound());
        runTest("Queue display", () -> testQueueDisplay());
        runTest("Queue remove all and reuse", () -> testQueueRemoveAllAndReuse());

        runTest("Main handles -1 as a stored value", () -> testMainNegativeOne());
        runTest("Main checks empty before pop, peek, dequeue, and peek", () -> testMainEmptyGuards());
        runTest("Main stack display pauses before the menu", () -> testMainStackDisplayPause());
        runTest("Main queue display pauses before the menu", () -> testMainQueueDisplayPause());

        System.out.println();
        System.out.println("Total checks: " + (passed + failed));
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            System.out.println("RESULT: FAIL");
            System.exit(1);
        }

        System.out.println("RESULT: PASS");
    }

    static void runTest(String name, Runnable test) {
        try {
            test.run();
        } catch (Throwable error) {
            failed++;
            System.out.println("FAIL: " + name + " threw " + error);
        }
    }

    static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS: " + name);
        } else {
            failed++;
            System.out.println("FAIL: " + name);
        }
    }

    static void checkEquals(String name, int expected, int actual) {
        if (expected == actual) {
            check(name, true);
        } else {
            check(name + " (expected " + expected + ", got " + actual + ")", false);
        }
    }

    static void checkContains(String name, String text, String expectedPart) {
        if (text.contains(expectedPart)) {
            check(name, true);
        } else {
            check(name + " (missing: " + expectedPart + ")", false);
        }
    }

    static String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream capture = new PrintStream(buffer);
        try {
            System.setOut(capture);
            action.run();
        } finally {
            System.setOut(originalOut);
            capture.close();
        }
        return buffer.toString();
    }

    static String runMainWithInput(String input) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream capture = new PrintStream(buffer);
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            System.setOut(capture);
            Main.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
            capture.close();
        }
        return buffer.toString();
    }

    static void testStackNew() {
        ArrayStack stack = new ArrayStack(3);
        check("stack starts empty", stack.isEmpty());
        checkEquals("stack starts at size 0", 0, stack.size());
        check("stack starts not full", !stack.isFull());
    }

    static void testStackPushPopLifo() {
        ArrayStack stack = new ArrayStack(5);
        captureOutput(() -> {
            stack.push(10);
            stack.push(20);
            stack.push(30);
        });

        checkEquals("stack size after three pushes", 3, stack.size());
        checkEquals("stack peek returns 30", 30, stack.peek());
        checkEquals("stack size unchanged by peek", 3, stack.size());
        checkEquals("stack first pop returns 30", 30, stack.pop());
        checkEquals("stack second pop returns 20", 20, stack.pop());
        checkEquals("stack third pop returns 10", 10, stack.pop());
        check("stack empty after popping all", stack.isEmpty());
        checkEquals("stack size after popping all", 0, stack.size());
    }

    static void testStackFillAndOverflow() {
        ArrayStack stack = new ArrayStack(5);
        captureOutput(() -> {
            stack.push(10);
            stack.push(20);
            stack.push(30);
            stack.push(40);
        });

        check("stack not full at 4 of 5", !stack.isFull());
        captureOutput(() -> stack.push(50));
        check("stack full at 5 of 5", stack.isFull());
        checkEquals("stack size at full capacity", 5, stack.size());

        String output = captureOutput(() -> stack.push(99));
        checkContains("stack overflow message", output, "Stack Overflow");
        checkEquals("stack size unchanged after overflow", 5, stack.size());
        checkEquals("stack top unchanged after overflow", 50, stack.peek());
    }

    static void testStackUnderflow() {
        ArrayStack stack = new ArrayStack(2);
        final int[] result = new int[1];

        String popOutput = captureOutput(() -> result[0] = stack.pop());
        checkEquals("empty stack pop returns -1", -1, result[0]);
        checkContains("stack underflow message", popOutput, "Stack Underflow");
        check("stack still empty after underflow pop", stack.isEmpty());

        String peekOutput = captureOutput(() -> result[0] = stack.peek());
        checkEquals("empty stack peek returns -1", -1, result[0]);
        checkContains("empty stack peek message", peekOutput, "Stack is empty, cannot peek");
    }

    static void testStackNegativeValues() {
        ArrayStack stack = new ArrayStack(3);
        captureOutput(() -> {
            stack.push(-1);
            stack.push(-5);
            stack.push(0);
        });

        check("stack with negative values is not empty", !stack.isEmpty());
        checkEquals("stack size with negative values", 3, stack.size());
        checkEquals("stack peek returns top 0", 0, stack.peek());
        checkEquals("stack pop returns 0", 0, stack.pop());
        checkEquals("stack pop returns -5", -5, stack.pop());
        checkEquals("stack pop returns stored -1", -1, stack.pop());
        check("stack empty after negative values removed", stack.isEmpty());

        ArrayStack single = new ArrayStack(2);
        captureOutput(() -> single.push(-1));
        check("stack not empty when -1 is stored", !single.isEmpty());
        checkEquals("stack size 1 after pushing -1", 1, single.size());
        checkEquals("stack peek sees stored -1", -1, single.peek());
        checkEquals("stack pop returns stored -1", -1, single.pop());
        check("stack empty only after stored -1 is popped", single.isEmpty());
    }

    static void testStackDisplay() {
        ArrayStack stack = new ArrayStack(5);
        captureOutput(() -> {
            stack.push(1);
            stack.push(2);
            stack.push(3);
        });

        String output = captureOutput(() -> stack.display());
        checkContains("stack display header", output, "Stack (top -> bottom):");
        checkContains("stack display shows top first", output, "index 2 : 3");
        checkContains("stack display shows middle", output, "index 1 : 2");
        checkContains("stack display shows bottom", output, "index 0 : 1");
        checkContains("stack display shows count", output, "Elements: 3 / 5");
        checkEquals("stack display does not change size", 3, stack.size());

        String emptyOutput = captureOutput(() -> new ArrayStack(2).display());
        checkContains("empty stack display message", emptyOutput, "Stack is empty");
    }

    static void testStackRemoveAllAndReuse() {
        ArrayStack stack = new ArrayStack(3);
        captureOutput(() -> {
            stack.push(7);
            stack.push(8);
            stack.push(9);
            stack.pop();
            stack.pop();
            stack.pop();
        });

        check("stack empty after removing every element", stack.isEmpty());
        checkEquals("stack size 0 after removing every element", 0, stack.size());

        String output = captureOutput(() -> stack.push(42));
        checkContains("stack reuse stores at index 0", output, "Pushed 42 at index 0");
        checkEquals("stack size after reuse", 1, stack.size());
        checkEquals("stack peek after reuse", 42, stack.peek());
    }

    static void testQueueNew() {
        ArrayQueue queue = new ArrayQueue(3);
        check("queue starts empty", queue.isEmpty());
        checkEquals("queue starts at size 0", 0, queue.size());
        check("queue starts not full", !queue.isFull());
    }

    static void testQueueEnqueueDequeueFifo() {
        ArrayQueue queue = new ArrayQueue(5);
        captureOutput(() -> {
            queue.enqueue(10);
            queue.enqueue(20);
            queue.enqueue(30);
        });

        checkEquals("queue size after three enqueues", 3, queue.size());
        checkEquals("queue peek returns 10", 10, queue.peek());
        checkEquals("queue size unchanged by peek", 3, queue.size());
        checkEquals("queue first dequeue returns 10", 10, queue.dequeue());
        checkEquals("queue second dequeue returns 20", 20, queue.dequeue());
        checkEquals("queue third dequeue returns 30", 30, queue.dequeue());
        check("queue empty after dequeuing all", queue.isEmpty());
        checkEquals("queue size after dequeuing all", 0, queue.size());
    }

    static void testQueueFillAndOverflow() {
        ArrayQueue queue = new ArrayQueue(5);
        captureOutput(() -> {
            queue.enqueue(10);
            queue.enqueue(20);
            queue.enqueue(30);
            queue.enqueue(40);
        });

        check("queue not full at 4 of 5", !queue.isFull());
        captureOutput(() -> queue.enqueue(50));
        check("queue full at 5 of 5", queue.isFull());
        checkEquals("queue size at full capacity", 5, queue.size());

        String output = captureOutput(() -> queue.enqueue(99));
        checkContains("queue overflow message", output, "Queue Overflow");
        checkEquals("queue size unchanged after overflow", 5, queue.size());
        checkEquals("queue front unchanged after overflow", 10, queue.peek());
    }

    static void testQueueUnderflow() {
        ArrayQueue queue = new ArrayQueue(2);
        final int[] result = new int[1];

        String dequeueOutput = captureOutput(() -> result[0] = queue.dequeue());
        checkEquals("empty queue dequeue returns -1", -1, result[0]);
        checkContains("queue underflow message", dequeueOutput, "Queue Underflow");
        check("queue still empty after underflow dequeue", queue.isEmpty());

        String peekOutput = captureOutput(() -> result[0] = queue.peek());
        checkEquals("empty queue peek returns -1", -1, result[0]);
        checkContains("empty queue peek message", peekOutput, "Queue is empty, cannot peek");
    }

    static void testQueueNegativeValues() {
        ArrayQueue queue = new ArrayQueue(3);
        captureOutput(() -> queue.enqueue(-1));

        check("queue with -1 is not empty", !queue.isEmpty());
        checkEquals("queue size after enqueue -1", 1, queue.size());
        checkEquals("queue peek sees stored -1", -1, queue.peek());
        checkEquals("queue dequeue returns stored -1", -1, queue.dequeue());
        check("queue empty after stored -1 is dequeued", queue.isEmpty());

        captureOutput(() -> {
            queue.enqueue(-5);
            queue.enqueue(0);
        });
        checkEquals("queue dequeue returns -5", -5, queue.dequeue());
        checkEquals("queue dequeue returns 0", 0, queue.dequeue());
    }

    static void testQueueCircularWraparound() {
        ArrayQueue queue = new ArrayQueue(5);
        captureOutput(() -> {
            queue.enqueue(1);
            queue.enqueue(2);
            queue.enqueue(3);
            queue.enqueue(4);
            queue.enqueue(5);
        });

        check("queue full before wraparound", queue.isFull());

        final int[] removed = new int[2];
        captureOutput(() -> {
            removed[0] = queue.dequeue();
            removed[1] = queue.dequeue();
        });
        checkEquals("wraparound first dequeue is 1", 1, removed[0]);
        checkEquals("wraparound second dequeue is 2", 2, removed[1]);
        check("queue not full after two dequeues", !queue.isFull());

        String output = captureOutput(() -> {
            queue.enqueue(6);
            queue.enqueue(7);
        });
        checkContains("queue enqueue 6 wraps to index 0", output, "Enqueued 6 at index 0");
        checkContains("queue enqueue 7 wraps to index 1", output, "Enqueued 7 at index 1");
        check("queue full again after wrapped enqueues", queue.isFull());

        String display = captureOutput(() -> queue.display());
        checkContains("wrapped display index 2 is 3", display, "index 2 : 3");
        checkContains("wrapped display index 3 is 4", display, "index 3 : 4");
        checkContains("wrapped display index 4 is 5", display, "index 4 : 5");
        checkContains("wrapped display index 0 is 6", display, "index 0 : 6");
        checkContains("wrapped display index 1 is 7", display, "index 1 : 7");

        final int[] order = new int[5];
        captureOutput(() -> {
            for (int i = 0; i < 5; i++) {
                order[i] = queue.dequeue();
            }
        });
        checkEquals("wrapped dequeue order first", 3, order[0]);
        checkEquals("wrapped dequeue order second", 4, order[1]);
        checkEquals("wrapped dequeue order third", 5, order[2]);
        checkEquals("wrapped dequeue order fourth", 6, order[3]);
        checkEquals("wrapped dequeue order fifth", 7, order[4]);
        check("queue empty after wraparound drain", queue.isEmpty());

        String reuseOutput = captureOutput(() -> queue.enqueue(8));
        checkContains("wrapped queue reuses index 0 after drain", reuseOutput, "Enqueued 8 at index 0");
        checkEquals("wrapped queue size after reuse", 1, queue.size());
        checkEquals("wrapped queue peek after reuse", 8, queue.peek());
    }

    static void testQueueDisplay() {
        ArrayQueue queue = new ArrayQueue(5);
        captureOutput(() -> {
            queue.enqueue(1);
            queue.enqueue(2);
            queue.enqueue(3);
        });

        String output = captureOutput(() -> queue.display());
        checkContains("queue display header", output, "Queue (front -> rear):");
        checkContains("queue display shows oldest first", output, "index 0 : 1");
        checkContains("queue display shows second", output, "index 1 : 2");
        checkContains("queue display shows third", output, "index 2 : 3");
        checkContains("queue display shows count", output, "Elements: 3 / 5");
        checkEquals("queue display does not change size", 3, queue.size());

        String secondOutput = captureOutput(() -> queue.display());
        check("queue display is repeatable", output.equals(secondOutput));

        String emptyOutput = captureOutput(() -> new ArrayQueue(2).display());
        checkContains("empty queue display message", emptyOutput, "Queue is empty");
    }

    static void testQueueRemoveAllAndReuse() {
        ArrayQueue queue = new ArrayQueue(3);
        captureOutput(() -> {
            queue.enqueue(10);
            queue.enqueue(20);
            queue.dequeue();
            queue.dequeue();
        });

        check("queue empty after removing every element", queue.isEmpty());
        checkEquals("queue size 0 after removing every element", 0, queue.size());

        String output = captureOutput(() -> queue.enqueue(99));
        checkContains("queue reuse stores at index 0", output, "Enqueued 99 at index 0");
        checkEquals("queue size after reuse", 1, queue.size());
        checkEquals("queue peek after reuse", 99, queue.peek());
    }

    static void testMainNegativeOne() {
        String input = "1\n-1\n3\n2\n7\n-1\n9\n8\n0\n";
        String output = runMainWithInput(input);

        checkContains("Main pushes -1", output, "Pushed -1 at index 0");
        checkContains("Main stack peek reports -1", output, "Top value is -1 and it was not removed.");
        checkContains("Main stack pop reports -1", output, "Popped -1");
        checkContains("Main stack reports removed -1", output, "Removed value: -1");
        checkContains("Main enqueues -1", output, "Enqueued -1 at index 0");
        checkContains("Main queue peek reports -1", output, "Front value is -1 and it was not removed.");
        checkContains("Main queue dequeue reports -1", output, "Dequeued -1");
        checkContains("Main exits cleanly with -1 values", output, "Program ended. Goodbye!");
    }

    static void testMainEmptyGuards() {
        String input = "2\n3\n8\n9\n0\n";
        String output = runMainWithInput(input);

        checkContains("Main stack pop empty guard", output, "Stack Underflow: stack is empty, cannot pop");
        checkContains("Main stack peek empty guard", output, "Stack is empty, cannot peek");
        checkContains("Main queue dequeue empty guard", output, "Queue Underflow: queue is empty, cannot dequeue");
        checkContains("Main queue peek empty guard", output, "Queue is empty, cannot peek");
        checkContains("Main empty guards exit cleanly", output, "Program ended. Goodbye!");
    }

    static void testMainStackDisplayPause() {
        String input = "1\n10\n6\n\n1\n20\n0\n";
        String output = runMainWithInput(input);

        checkContains("Main stack display header", output, "--- STACK: DISPLAY ---");
        checkContains("Main stack display contents", output, "Stack (top -> bottom):");
        checkContains("Main stack display shows pushed value", output, "index 0 : 10");
        checkContains("Main stack display pause prompt", output, "Press Enter to return to the menu...");

        int displayPosition = output.indexOf("index 0 : 10");
        int pausePosition = output.indexOf("Press Enter to return to the menu...");
        int nextOperationPosition = output.indexOf("Pushed 20 at index 1");
        check("Main stack display appears before pause", displayPosition >= 0 && pausePosition > displayPosition);
        check("Main stack display continues after Enter", pausePosition >= 0 && nextOperationPosition > pausePosition);
        check("Main stack display pause does not eat next choice", !output.contains("Invalid input. Please enter a number."));
        checkContains("Main stack display exits cleanly", output, "Program ended. Goodbye!");
    }

    static void testMainQueueDisplayPause() {
        String input = "7\n10\n12\n\n7\n20\n0\n";
        String output = runMainWithInput(input);

        checkContains("Main queue display header", output, "--- QUEUE: DISPLAY ---");
        checkContains("Main queue display contents", output, "Queue (front -> rear):");
        checkContains("Main queue display shows enqueued value", output, "index 0 : 10");
        checkContains("Main queue display pause prompt", output, "Press Enter to return to the menu...");

        int displayPosition = output.indexOf("index 0 : 10");
        int pausePosition = output.indexOf("Press Enter to return to the menu...");
        int nextOperationPosition = output.indexOf("Enqueued 20 at index 1");
        check("Main queue display appears before pause", displayPosition >= 0 && pausePosition > displayPosition);
        check("Main queue display continues after Enter", pausePosition >= 0 && nextOperationPosition > pausePosition);
        check("Main queue display pause does not eat next choice", !output.contains("Invalid input. Please enter a number."));
        checkContains("Main queue display exits cleanly", output, "Program ended. Goodbye!");
    }
}
