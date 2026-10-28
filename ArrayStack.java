public class ArrayStack {
    private int[] data;
    private int top;
    private int capacity;

    public ArrayStack(int capacity) {
        this.capacity = capacity;
        this.data = new int[capacity];
        this.top = -1;
    }

    public void push(int x) {
        if (isFull()) {
            System.out.println("Stack Overflow: stack is full, cannot push " + x);
            return;
        }
        top = top + 1;
        data[top] = x;
        System.out.println("Pushed " + x + " at index " + top);
    }

    public int pop() {
        if (isEmpty()) {
            System.out.println("Stack Underflow: stack is empty, cannot pop");
            return -1;
        }
        int value = data[top];
        System.out.println("Popped " + value);
        top = top - 1;
        return value;
    }

    public int peek() {
        if (isEmpty()) {
            System.out.println("Stack is empty, cannot peek");
            return -1;
        }
        System.out.println("Top element is " + data[top]);
        return data[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == capacity - 1;
    }

    public int size() {
        return top + 1;
    }

    public void display() {
        if (isEmpty()) {
            System.out.println("Stack is empty");
            return;
        }
        System.out.println("Stack (top -> bottom):");
        for (int i = top; i >= 0; i--) {
            System.out.println("  index " + i + " : " + data[i]);
        }
        System.out.println("Elements: " + size() + " / " + capacity);
    }
}
