public class ArrayQueue {
    private int[] data;
    private int front;
    private int rear;
    private int count;
    private int capacity;

    public ArrayQueue(int capacity) {
        this.capacity = capacity;
        this.data = new int[capacity];
        this.front = -1;
        this.rear = -1;
        this.count = 0;
    }

    public void enqueue(int x) {
        if (isFull()) {
            System.out.println("Queue Overflow: queue is full, cannot enqueue " + x);
            return;
        }
        rear = (rear + 1) % capacity;
        data[rear] = x;
        if (count == 0) {
            front = rear;
        }
        count = count + 1;
        System.out.println("Enqueued " + x + " at index " + rear);
    }

    public int dequeue() {
        if (isEmpty()) {
            System.out.println("Queue Underflow: queue is empty, cannot dequeue");
            return -1;
        }
        int value = data[front];
        System.out.println("Dequeued " + value);
        front = (front + 1) % capacity;
        count = count - 1;
        if (count == 0) {
            front = -1;
            rear = -1;
        }
        return value;
    }

    public int peek() {
        if (isEmpty()) {
            System.out.println("Queue is empty, cannot peek");
            return -1;
        }
        System.out.println("Front element is " + data[front]);
        return data[front];
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == capacity;
    }

    public int size() {
        return count;
    }

    public void display() {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return;
        }
        System.out.println("Queue (front -> rear):");
        int index = front;
        for (int i = 0; i < count; i++) {
            System.out.println("  index " + index + " : " + data[index]);
            index = (index + 1) % capacity;
        }
        System.out.println("Elements: " + count + " / " + capacity);
    }
}
