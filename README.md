# Lab 5 - Stacks and Queues

## 1. Overview

This lab implements two linear data structures in Java, using a plain array as the
backing store:

* a **Stack** (Last In, First Out)
* a **circular Queue** (First In, First Out)

Both are driven from a menu-driven console program, so every operation can be tested
interactively.

**LIFO (Last In, First Out)** means the element added *most recently* comes out
*first*. A stack works like a pile of plates: you can only take the plate on top, never
one from the middle. `push()` adds to the top, `pop()` removes from the top.

**FIFO (First In, First Out)** means the element added *earliest* comes out *first*. A
queue works like a line of people at a counter: people join at the back and are served
from the front. `enqueue()` adds at the rear, `dequeue()` removes from the front.

| | Stack | Queue |
|---|---|---|
| Order | LIFO | FIFO |
| Add at | top | rear (back) |
| Remove from | top | front |
| Real-life example | pile of plates | line at a counter |

---

## 2. Files

### `ArrayStack.java`
Implements the stack. Holds an `int[] data` array and a `top` index that marks the
newest element. Provides `push()`, `pop()`, `peek()`, `isEmpty()`, `isFull()`, `size()`
and `display()`.

### `ArrayQueue.java`
Implements the circular queue. Holds an `int[] data` array, a `front` index, a `rear`
index, and a `count` variable recording how many elements are really in the queue.
Provides `enqueue()`, `dequeue()`, `peek()`, `isEmpty()`, `isFull()`, `size()` and
`display()`.

### `Main.java`
The user interface. Uses a `Scanner` to read input, creates one stack and one queue
with capacity 5, and runs a `while` loop containing a `switch` statement that maps
menu choices 1-12 to the methods above. Choice `0` exits.

Before calling `pop()`, `peek()`, `dequeue()`, or the queue `peek()`, `Main` checks
`isEmpty()` first. This is important because `-1` is a valid stored integer, so the
menu can no longer use a returned `-1` to mean "the structure was empty".

After options `6` and `12`, `Main` waits for the user to press Enter before printing the
menu again. `readInt()` also clears the rest of the input line after a valid number, so
the Enter key used for the pause is not consumed by the previous `nextInt()` call.

### `Lab5Tests.java`
A standalone test harness with automatic PASS/FAIL output. It tests normal operations,
LIFO/FIFO order, empty and full states, overflow and underflow, filling capacity,
removing all elements, circular queue wraparound and reuse, display behaviour, menu
options `6` and `12`, the display pause, and negative integers including `-1`. It exits
with status `1` if any check fails.

### `run.bat`
A Windows batch file. It checks that `java` and `javac` are available on the PATH,
compiles `ArrayStack.java`, `ArrayQueue.java`, and `Main.java`, then launches `Main`.
It uses `%~dp0` so it works from its own project folder without hard-coded paths.

All fields in `ArrayStack` and `ArrayQueue` are `private`, so `Main` cannot change
`top`, `front`, `rear` or `count` directly. All the logic stays inside the two data
structure classes.

### Running the program

Open Command Prompt in this folder and run:

```bat
run.bat
```

Or compile and run manually:

```bat
javac ArrayStack.java ArrayQueue.java Main.java
java Main
```

### Running the tests

```bat
javac ArrayStack.java ArrayQueue.java Main.java Lab5Tests.java
java Lab5Tests
```

The harness prints `PASS` or `FAIL` for every check, then a summary. Exit code `0`
means every check passed; exit code `1` means at least one check failed.

---

## 3. Stack Explanation

### `data` array
The actual storage. `Main` creates the stack with capacity 5, so `data` is an
`int[5]` and the valid indices are `0` to `4`. Java fills a new `int[]` with zeros, but
those zeros are meaningless, because `top` decides which positions are real.

### `top`
The index of the newest (most recently pushed) element. It is the single source of
truth for how much is in the stack.

`top` starts at **-1**. `-1` is "one before the first valid index", which is how the
code says *empty*. Starting at `0` would be wrong, because it would claim index 0 is
already occupied. Starting at `-1` also makes the two operations simple: `top + 1` is
the next free slot for a push, and `top - 1` is the slot to give up for a pop.

### `capacity`
The maximum number of elements. Stored so `isFull()` knows the limit without asking
the array for its length.

### `push(int x)`
1. Check `isFull()`. If full, print "Stack Overflow" and `return` immediately, so the
   stack is left untouched.
2. `top = top + 1;` — move up one slot and claim it.
3. `data[top] = x;` — store the value.

The order matters: increment first, then write.

### `pop()`
1. Check `isEmpty()`. If empty, print "Stack Underflow" and `return -1` without
   changing anything.
2. `int value = data[top];` — **read the value out first**, into a local variable.
3. `top = top - 1;` — move down, which logically discards that element.
4. `return value;`

Step 2 must come before step 3. Once `top` moves down, the value is gone as far as the
stack is concerned, so it has to be copied out first.

`Main` checks `stack.isEmpty()` before calling `pop()`, so the menu never uses the
returned `-1` as an empty signal. A stored value of `-1` is returned normally. The
underflow check inside `pop()` stays in place as a safety net for direct calls.

### `peek()`
Returns `data[top]` but **does not change `top`**. Peek means "tell me the top element
but leave everything as it is", like looking at the top card of a deck without taking
it. There is no `top = ...` line anywhere in `peek()`. If peek moved `top`, it would
silently destroy an element. `Main` also checks `isEmpty()` before calling `peek()`, so
a stored `-1` can be shown as a real top value.

### `isEmpty()`
Returns `top == -1`. The stack is empty exactly when `top` is still at its starting
value.

### `isFull()`
Returns `top == capacity - 1`. Valid indices are `0` to `capacity - 1`, so the stack is
full when `top` reaches the last one. With capacity 5, full means `top == 4`. One more
push would need index 5, which does not exist.

### `display()`
Prints from `top` down to `0`:

```java
for (int i = top; i >= 0; i--) { ... }
```

A stack is LIFO, so the top is the newest element. Printing downwards shows the real
order of the stack, newest first; printing upwards would show it upside down.

The loop uses a **local** variable `i` and never assigns to `top`, so `display()`
cannot change the stack. If the stack is empty, `top` is `-1`, the condition `i >= 0`
is false immediately, and the loop simply never runs — so no out-of-bounds access can
occur.

---

## 4. Circular Queue Explanation

### `data` array
The same idea as the stack: an `int[5]` in this lab, with valid indices `0` to `4`.

### `front`
The index of the oldest element, the one that will be removed next. It starts at `-1`
because index `-1` means "there is no first element yet", which is the empty state.

### `rear`
The index of the newest element, where the next item will be added. It also starts at
`-1`, for the same reason.

### `count`
The number of elements actually in the queue. It starts at `0`.

`count` is needed because in a **circular** queue the pointers alone can no longer tell
us whether the queue is empty or full. `front == rear` could mean *empty* (nothing has
been added yet) **or** *full* (it has wrapped all the way around the array). The
pointers say **where**; `count` says **how many**. Because of this, both `isEmpty()` and
`isFull()` are written in terms of `count`, so wraparound can never produce an
ambiguous answer.

### `enqueue(int x)`
1. Check `isFull()`. If full, print "Queue Overflow" and `return` without changing
   anything.
2. `rear = (rear + 1) % capacity;` — move to the next slot, wrapping around.
3. `data[rear] = x;` — store the value.
4. `if (count == 0) { front = rear; }` — if the queue was empty, move `front` to the
   same place (see section 7).
5. `count = count + 1;`

New items always go in at the **rear**, because that is what FIFO requires.

### `dequeue()`
1. Check `isEmpty()`. If empty, print "Queue Underflow" and `return -1`.
2. `int value = data[front];` — read the oldest item out first.
3. `front = (front + 1) % capacity;` — move forward, releasing that slot.
4. `count = count - 1;`
5. If `count` became `0`, reset `front = -1` and `rear = -1`, so an empty queue always
   returns to the same clean state as a brand new one.
6. `return value;`

The value is not deleted from the array, for the same reason as the stack: once `count`
no longer covers it, nothing can reach it.

`Main` checks `queue.isEmpty()` before calling `dequeue()`, so a stored `-1` is returned
as a real value instead of being mistaken for an empty signal. The underflow check
inside `dequeue()` remains as a safety net.

### `peek()`
Returns `data[front]` and does not change `front` or `rear`. Read-only, like the
stack's `peek()`. `Main` checks `isEmpty()` before calling it, so a stored `-1` is shown
as a real front value.

### `isEmpty()`
Returns `count == 0`.

### `isFull()`
Returns `count == capacity`. Note that it is **not** `rear == capacity - 1`: in a
circular queue the rear reaching the last index proves nothing, because the queue could
hold a single element with `rear` at index 4.

### `display()`
```java
int index = front;
for (int i = 0; i < count; i++) {
    System.out.println("  index " + index + " : " + data[index]);
    index = (index + 1) % capacity;
}
```

It copies `front` into a **local** variable `index` and walks that copy forward with the
same `%` operation. Because `index` is a separate variable, the real `front` and `rear`
are never assigned, so `display()` cannot destroy the queue while printing it. If it
had written `front = (front + 1) % capacity` instead, it would slowly consume the queue
every time it was called.

The loop runs `count` times, not "until rear is reached", because after a wraparound the
distance from `front` to `rear` is no longer meaningful. `count` is the exact number of
live elements. Starting at `front` and moving forward prints **oldest first**, which is
the correct FIFO order.

### Why the modulo operator `%` is used
With capacity 5, valid indices are `0, 1, 2, 3, 4`. Normally, after `rear = 4` the next
position would be index 5, which does not exist and would cause an
`ArrayIndexOutOfBoundsException`.

`%` is the remainder operator. `x % 5` gives the leftover after dividing by 5, so the
result is always between 0 and 4:

| rear | `(rear + 1) % 5` |
|---|---|
| 0 | 1 |
| 3 | 4 |
| **4** | **0** (wraps back to the start) |
| -1 | 0 |

So the array behaves like a ring: after the last slot it goes back to the first. This
matters because the slots near the front of the array were freed by earlier dequeues.
Without `%`, the program would report "Queue Overflow" even though indices 0, 1 and 2
were sitting empty. With `%`, a size-5 queue can accept 5 more items after being
emptied. That is what makes it a **circular** queue.

---

## 5. Manual Trace

### Stack trace (LIFO)

Capacity 5, starting with `top = -1`.

| Step | Operation | `top` before | Action | `top` after | Answer |
|---|---|---|---|---|---|
| 1 | `PUSH 10` | -1 | `top = 0`, `data[0] = 10` | 0 | - |
| 2 | `PUSH 20` | 0 | `top = 1`, `data[1] = 20` | 1 | - |
| 3 | `POP` | 1 | read `data[1] = 20` | 0 | **20** |

**Expected result: `POP` returns 20, not 10.**

This is LIFO. 20 was pushed last, so it sits on top and comes out first. 10 is still in
the stack at index 0 and would be returned by the next `POP`.

### Queue trace (FIFO) — conceptual, letters stand in for numbers

> **Note:** this queue trace uses the letters **A** and **B**, as given in the
> laboratory guide. The actual `ArrayQueue` class in this lab stores **`int`** values,
> not characters, so the real program uses numbers. The trace below is therefore a
> **conceptual manual trace** showing the ordering only. In the real program, A would be
> enqueued as a number such as `1` and B as `2`, and the ordering behaviour is exactly
> the same.

Capacity 5, starting with `front = -1`, `rear = -1`, `count = 0`.

| Step | Operation | `front` | `rear` | `count` | Answer |
|---|---|---|---|---|---|
| 1 | `ENQUEUE A` | 0 | 0 | 1 | - |
| 2 | `ENQUEUE B` | 0 | 1 | 2 | - |
| 3 | `DEQUEUE` | 0 | 1 | 2 | **A** |

**Expected result: `DEQUEUE` returns A, not B.**

This is FIFO. A was enqueued first, so it is at the front and is removed first. B stays
in the queue at index 1 and would be returned by the next `DEQUEUE`.

### The contrast

With the same two values added in the same order:

* the **stack** returns the *last* value added (20),
* the **queue** returns the *first* value added (A).

That single difference is the whole point of the lab.

---

## 6. Test Evidence

The results in this section were produced by the current implementation on
2026-10-28 in the Lab 5 folder using Windows CMD commands.

### Compilation

```bat
javac ArrayStack.java ArrayQueue.java Main.java Lab5Tests.java
```

The command finished with exit code `0` and no errors.

### Test harness

```bat
java Lab5Tests
```

The harness ran 19 test groups containing 138 individual checks:

```text
Total checks: 138
Passed: 138
Failed: 0
RESULT: PASS
```

Exit code: `0`.

| Test group | Checks | Result |
|---|---|---|
| Stack new state | 3 | PASS |
| Stack push, pop, and LIFO order | 8 | PASS |
| Stack fill and overflow | 6 | PASS |
| Stack underflow | 5 | PASS |
| Stack negative values including `-1` | 12 | PASS |
| Stack display | 7 | PASS |
| Stack remove all and reuse | 5 | PASS |
| Queue new state | 3 | PASS |
| Queue enqueue, dequeue, and FIFO order | 8 | PASS |
| Queue fill and overflow | 6 | PASS |
| Queue underflow | 5 | PASS |
| Queue negative values including `-1` | 7 | PASS |
| Queue circular wraparound | 21 | PASS |
| Queue display | 8 | PASS |
| Queue remove all and reuse | 5 | PASS |
| Main handles `-1` as a stored value | 8 | PASS |
| Main checks empty before pop, peek, dequeue, and peek | 5 | PASS |
| Main stack display pauses before the menu | 8 | PASS |
| Main queue display pauses before the menu | 8 | PASS |
| **Total** | **138** | **PASS** |

### Application smoke test

```bat
(echo 1&echo -1&echo 3&echo 2&echo 7&echo -1&echo 9&echo 8&echo 0) | java Main
```

This pushed, peeked at, and popped `-1` on the stack, then enqueued, peeked at, and
dequeued `-1` on the queue. The program printed `Removed value: -1` in both cases and
ended with `Program ended. Goodbye!`. Exit code: `0`.

### Display pause smoke test

```bat
(echo 1&echo 10&echo 6&echo.&echo 12&echo.&echo 0) | java Main
```

The stack display and queue display both appeared before
`Press Enter to return to the menu...`. The blank input lines represented the Enter key.
After each pause, the program printed the menu again and continued normally. Exit code:
`0`.

### `run.bat` smoke test

```bat
echo 0|run.bat
```

`run.bat` compiled the application, launched `Main`, and exited with code `0`.

---

## 7. Earlier Bug Found and Correction

> **Historical note:** This section records a bug found during earlier development of
> the project and is kept because it explains why `enqueue()` contains the
> `if (count == 0) { front = rear; }` lines. It is **not** part of the current test
> evidence in section 6, and the earlier runs described here were not repeated as part
> of this implementation.

### The bug
The queue's `front` index was never initialised when the first element was enqueued.

`enqueue()` advanced `rear` and stored the value, but nothing set `front`. Because
`front` starts at `-1`, it stayed at `-1` even after items had been added. So
`display()`, `dequeue()` and `peek()` all tried to read `data[-1]`.

### How it was found
The first full test run crashed instead of finishing. The test input was ordinary —
push, pop, enqueue, dequeue, then display the queue:

```
Enter your choice: Queue (front -> rear):
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index -1 out of bounds for length 5
	at ArrayQueue.display(ArrayQueue.java:72)
	at Main.main(Main.java:70)
```

`Index -1 out of bounds for length 5` was the clue. The queue held three elements at
that point, so reading index `-1` was only possible if `front` was still `-1` — which
confirmed that `front` had never been set.

The program exited with code 1, so every test after that point in the run was skipped.

### The correction
Three lines were added to `enqueue()`, immediately after storing the value and before
incrementing `count`:

```java
rear = (rear + 1) % capacity;
data[rear] = x;
if (count == 0) {
    front = rear;
}
count = count + 1;
```

### Why this correction is necessary
Trace the very first enqueue on an empty queue, with capacity 5:

| Step | `front` | `rear` | `count` | Array |
|---|---|---|---|---|
| Start | -1 | -1 | 0 | empty |
| `rear = (-1 + 1) % 5` → `0` | -1 | 0 | 0 | empty |
| `data[0] = 1` | -1 | 0 | 0 | `1` stored |
| *(without the fix)* | **-1** | 0 | 0 | `1` stored |
| `if (count == 0) { front = rear; }` | **0** | 0 | 0 | `1` stored |
| `count = count + 1` | 0 | 0 | 1 | `1` stored |

Without the correction, the value is stored correctly at index 0 and `rear` is correct,
but `front` is still `-1`. The very next `dequeue()` or `display()` would then execute
`data[front]`, which is `data[-1]`, and throw `ArrayIndexOutOfBoundsException` — exactly
the crash that was observed.

The `if (count == 0)` condition is what makes this correct. It fires only on the
transition from empty to non-empty, which is the only moment `front` needs to be dragged
into position. On every later enqueue `count` is already greater than 0, so the
condition is false and `front` is left alone to advance normally through `dequeue()`.

### Earlier verification (historical)
After adding the three lines at that time, the code was compiled again and the **same
failing test input** was re-run. The program completed normally and printed correct
results throughout, including the wraparound case:

```
Queue (front -> rear):
  index 2 : 3
  index 3 : 4
  index 4 : 5
  index 0 : 6
Elements: 4 / 5
```

Exit code 0, ending with `Program ended. Goodbye.`

A useful side-check: before the fix, the crash was reported at `ArrayQueue.java:72`.
After the fix, that same statement sits at line 75. The three-line shift matches exactly
the three lines that were added.

---

## 8. AI-Use Declaration

**Instructor-approved model:**

OpenCode

**OpenCode was used for:**

* inspecting the existing Lab 5 files
* updating `Main.java` so pop, peek, dequeue, and queue peek check `isEmpty()` first
* creating `Lab5Tests.java`
* creating `run.bat`
* compiling and running the application and tests with Windows CMD commands
* updating this README and `AGENT_LOG.md`

**Details of what was done during this implementation:**

* *`Main.java` changes* — the menu now checks `stack.isEmpty()` before calling `pop()`
  and `peek()`, and `queue.isEmpty()` before calling `dequeue()` and `peek()`. This
  removes the old `-1` sentinel logic from the menu, so a real stored `-1` is handled
  correctly. The overflow and underflow checks inside `ArrayStack` and `ArrayQueue` were
  kept. Options `6` and `12` now pause until Enter is pressed, and `readInt()` clears the
  rest of the line so the pause is not skipped.
* *Testing* — a standalone harness was written and run with CMD commands. It reported
  138 checks: 138 passed, 0 failed, exit code `0`. The application was also run with
  stored `-1` values, with both display options, and with `run.bat`.
* *Documentation* — this README and `AGENT_LOG.md` were updated with the actual commands
  and results.

**Student review and verification:**

Student review and verification have **not** been performed as part of this
implementation. The student should read the changed files, run `run.bat`, run
`Lab5Tests`, and confirm the results before submission. No student sign-off is claimed
in this document.

---

## 9. Viva Preparation

**1. What is LIFO?**
LIFO means Last In, First Out. The element you add last is the first one you get out.
It is the rule a stack follows. A real example is a pile of plates: you can only take the
top plate.

**2. What is FIFO?**
FIFO means First In, First Out. The element you add first is the first one you get out.
It is the rule a queue follows. A real example is people standing in a line at a
counter.

**3. Why does `top` start at -1?**
Because `-1` is the position just before the first valid index, so it means "there is
nothing in the stack yet" — the empty state. If I started at `0`, the code would think
index 0 already had a value in it. Starting at `-1` also makes the operations simple:
adding one gives the next free slot for a push, and subtracting one removes the top.

**4. How do stack empty and full work?**
Empty means `top == -1`, which is the starting value, so nothing has been pushed.
Full means `top == capacity - 1`. The valid indices are `0` to `capacity - 1`, so once
`top` reaches the last one the stack is full. In this lab with capacity 5, full means
`top == 4`. One more push would need index 5, which does not exist, so `isFull()` stops
it and prints "Stack Overflow".

**5. What are `front` and `rear`?**
`front` is the index of the oldest element, the one that will be removed next, and
`rear` is the index of the newest element, where the next item will be added. Both start
at `-1` to mean empty. In `enqueue()` the value goes in at `rear` and `rear` moves
forward. In `dequeue()` the value comes out from `front` and `front` moves forward. That
is what makes it FIFO: things enter at the back and leave at the front.

**6. What is a circular queue, and why use `% capacity`?**
A circular queue treats the array like a ring instead of a straight line. Normally, once
`rear` reaches the last index there is nowhere to go, even if earlier positions have been
emptied by dequeues. The modulo operator `%` fixes this. `%` gives the remainder after a
division, so `(rear + 1) % capacity` always produces a valid index between 0 and
`capacity - 1`. With capacity 5, after index 4 it wraps back to index 0. That lets the
queue reuse the slots that were freed by earlier dequeues, so it can keep accepting new
items instead of falsely reporting "Queue Overflow".

**7. Why is `count` used?**
Because after wraparound, the pointers alone cannot tell us whether the queue is empty or
full. If `front` and `rear` point to the same place, that could mean the queue is empty,
or it could mean the queue is completely full after wrapping all the way around. Both
cases look identical if I only look at the pointers. So I keep a separate `count` that
records the real number of elements: empty is `count == 0` and full is
`count == capacity`. That removes the ambiguity completely.

**8. Why can old array values remain after a dequeue?**
Because `count` decides what is actually in the queue. When we dequeue, we return the
value and then decrease `count` and move `front` forward, so that position is no longer
counted as part of the queue. The old number is still physically sitting in the array,
but nothing can reach it, because every method uses `front` and `count` to decide which
positions are live. The only way to reach that position again is to enqueue enough items
to come back around, and `data[rear] = x` overwrites the old value. So leaving it there
is harmless, and it saves extra work.

**9. Why should `display()` not change the queue indexes?**
Because `display()` should only read, never modify. If it changed `front` while printing,
it would destroy the queue just by showing it to the user. So `display()` copies `front`
into a local variable and advances only that local copy. That is why calling `display()`
any number of times always gives the same output and the queue is still intact
afterwards.

**10. What was the actual bug, and how was it corrected?**
During earlier development testing the program crashed with
`ArrayIndexOutOfBoundsException: Index -1 out of bounds for length 5` at
`ArrayQueue.display()`. The reason was that in the queue's `enqueue()` method I was
advancing `rear` and storing the value, but I never set `front` for the very first
element. So `front` stayed at `-1`, and when `display()` tried to read the front element
it read `data[-1]`, which does not exist.

The fix was to add three lines to `enqueue()`:

```java
if (count == 0) {
    front = rear;
}
```

This says: if the queue was empty before this insert, then this new element is both the
first and the last, so `front` must point to the same place as `rear`. The `count == 0`
check makes sure this only happens on the first insert; after that, `front` advances
normally through `dequeue()`. I then recompiled and re-ran the same input, and this
time the whole program ran correctly, including the circular wraparound.
