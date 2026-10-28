# Agent Log - Lab 5

Date of work: 2026-10-28

Project folder: `C:\Users\Lance\OneDrive\Documents\vscode\Lab5`

Assistant used: OpenCode (`opencode/deepseek-v4.1-flash-free`)

## Task

Implement the approved revised Lab 5 plan:

* fix the `-1` sentinel problem in the menu
* add a standalone automatic test harness
* add a simple Windows launcher
* compile and run the application and tests with CMD-compatible commands
* correct and update the README with actual results
* create this agent log

## Files inspected before changes

* `ArrayStack.java`
* `ArrayQueue.java`
* `Main.java`
* `README.md`
* `.vscode/settings.json`

The existing `ArrayStack.java` and `ArrayQueue.java` already had overflow and
underflow checks. They were not rewritten. Their method signatures and beginner-level
style were kept.

## Changes made

### 1. `Main.java`

The menu previously used the return value `-1` to decide whether `pop()`, `peek()`,
`dequeue()`, or queue `peek()` had found an empty structure. That made a real stored
value of `-1` impossible to distinguish from an empty result.

The four menu cases now check `isEmpty()` before calling the operation:

* stack pop checks `stack.isEmpty()`
* stack peek checks `stack.isEmpty()`
* queue dequeue checks `queue.isEmpty()`
* queue peek checks `queue.isEmpty()`

When the structure is empty, the menu prints the same underflow or empty message as
before and does not call the operation. When the structure is not empty, the returned
value is printed normally, including `-1`.

The existing `isEmpty()`, `isFull()`, overflow, and underflow protections in
`ArrayStack.java` and `ArrayQueue.java` remain in place.

### 2. `Lab5Tests.java`

Created a standalone test harness with no JUnit, Maven, Gradle, or GUI. It uses normal
`javac` and `java` commands.

The harness:

* prints `PASS` or `FAIL` for every check
* prints a summary with total, passed, and failed counts
* calls `System.exit(1)` when any check fails
* exits with code `0` when all checks pass

It covers normal operations, LIFO and FIFO order, empty and full states, overflow,
underflow, filling capacity, removing all elements, circular queue wraparound and reuse,
display output, negative integers including `-1`, and the `Main` menu's empty checks.

### 3. `run.bat`

Created a Windows batch launcher that:

* changes to its own folder with `cd /d "%~dp0"` instead of using a hard-coded path
* checks that `java` is available with `where java`
* checks that `javac` is available with `where javac`
* prints a clear error and exits with code `1` if either command is missing
* compiles `ArrayStack.java`, `ArrayQueue.java`, and `Main.java`
* prints a clear error and exits with code `1` if compilation fails
* launches `Main` with `java Main`

### 4. `README.md`

Updated the README to:

* describe `Main` as using a `while` loop instead of the incorrect `do-while`
* describe the new `isEmpty()` checks in `Main`
* explain why stored `-1` values now work correctly
* list `Lab5Tests.java` and `run.bat`
* add simple compile, run, and test instructions
* replace the old test section with the actual results from this implementation
* keep the required manual stack and queue traces
* keep the circular queue explanation
* mark the earlier queue bug section as historical and state that its earlier runs were
  not repeated during this implementation
* remove the incorrect claim that non-numeric menu input crashes the program, because
  `Main.readInt()` already catches `InputMismatchException`
* remove the fabricated claim that the student had already reviewed and verified the
  work

### 5. Follow-up display pause fix

The full menu was reprinted immediately after options 6 and 12. On a short console
window, that pushed the stack or queue display off the visible screen. `Main.java` now:

* reads the rest of the input line after every valid `nextInt()` in `readInt()`, so the
  newline left behind by the menu choice is cleared before the pause
* calls a new `waitForEnter(input)` helper after `stack.display()` and
  `queue.display()`
* prints `Press Enter to return to the menu...`, waits for the user to press Enter, and
  then prints a newline before returning to the menu

`Lab5Tests.java` now has two new Main-level tests:

* `Main stack display pauses before the menu`
* `Main queue display pauses before the menu`

Each test drives the real menu with input containing a blank line for the pause, checks
the display output and pause prompt, confirms the next menu operation still runs, and
confirms no invalid-input message is produced.

The existing stack and circular queue logic in `ArrayStack.java` and `ArrayQueue.java`
was not changed.

## Commands actually run and results

### Java availability check

Command:

```bat
cmd /c "java -version 2>&1 & echo. & javac -version 2>&1"
```

Result:

```text
openjdk version "25.0.5" 2026-10-20 LTS
OpenJDK Runtime Environment Temurin-25.0.5+8 (build 25.0.5+8-LTS)
OpenJDK 64-Bit Server VM Temurin-25.0.5+8 (build 25.0.5+8-LTS, mixed mode, sharing)

javac 25.0.5
```

### Compile the application and tests

Command:

```bat
cmd /c "javac ArrayStack.java ArrayQueue.java Main.java Lab5Tests.java"
```

Result: exit code `0`, no compilation errors.

### Run the test harness

Command:

```bat
cmd /c "java Lab5Tests"
```

Result summary:

```text
Total checks: 138
Passed: 138
Failed: 0
RESULT: PASS
```

Exit code: `0`.

No test failures occurred in this run.

### Verify the display pause through the menu

Command:

```bat
cmd /c "(echo 1&echo 10&echo 6&echo.&echo 12&echo.&echo 0) | java Main"
```

Result: exit code `0`. The actual output included:

```text
--- STACK: DISPLAY ---
Stack (top -> bottom):
  index 0 : 10
Elements: 1 / 5
Press Enter to return to the menu...
==========================================
                MAIN MENU
...
--- QUEUE: DISPLAY ---
Queue is empty
Press Enter to return to the menu...
==========================================
                MAIN MENU
...
Program ended. Goodbye!
```

The stack display and queue display were both printed before the pause prompt. The blank
input line after each pause represented the Enter key. The program then continued to the
next menu instead of treating that blank line as an invalid menu choice.

### Run the application with stored `-1` values

Command:

```bat
cmd /c "(echo 1&echo -1&echo 3&echo 2&echo 7&echo -1&echo 9&echo 8&echo 0) | java Main"
```

Result: exit code `0`.

Relevant actual output:

```text
Pushed -1 at index 0
Top element is -1
Top value is -1 and it was not removed.
Popped -1
Removed value: -1
Enqueued -1 at index 0
Front element is -1
Front value is -1 and it was not removed.
Dequeued -1
Removed value: -1
Program ended. Goodbye!
```

### Invalid input check

The old README claimed that non-numeric menu input crashed the program. That claim was
not accurate because `Main.readInt()` already catches `InputMismatchException`. This
was checked directly with CMD:

```bat
cmd /c "(echo abc&echo 0) | java Main"
```

Result: exit code `0`. The program printed:

```text
Enter your choice: Invalid input. Please enter a number.
Enter your choice:
Program ended. Goodbye!
```

### Run `run.bat`

Command:

```bat
cmd /c "echo 0|run.bat"
```

Result: exit code `0`.

Relevant actual output:

```text
Compiling Lab 5 files...
Starting Lab 5 program...
...
Enter your choice:
Program ended. Goodbye!
```

### Remove generated class files after testing

Command:

```bat
cmd /c "del /q ArrayStack.class ArrayQueue.class Main.class Lab5Tests.class"
```

Result: exit code `0`. The four `.class` files generated during testing were removed, so
the folder contains source files, the README, this log, and `run.bat` again.

### Git check

Command:

```bat
git status
```

Result:

```text
fatal: not a git repository (or any of the parent directories): .git
```

No commit or push was performed. The project folder is not a Git repository.

## Actual test coverage executed

The harness ran 19 test groups with 138 individual checks:

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

## Assistance used

OpenCode was used to inspect the existing files, edit `Main.java`, create
`Lab5Tests.java`, create `run.bat`, run the CMD commands shown above, add the display
pause and menu display tests, and update `README.md` and this log.

No prompts are reproduced here. This log records the actions and results that actually
occurred. No test result was invented, and no student review or sign-off was invented.

## Student verification status

Student verification has **not** been performed as part of this implementation. The
student still needs to:

1. Read the changed source files.
2. Run `run.bat` and try the menu operations, including `-1`, option 6, and option 12.
   Confirm that each display stays on screen until Enter is pressed.
3. Run the test harness and confirm the `138 passed, 0 failed` result. The `.class`
   files were removed after testing, so the test harness must be compiled again first.
4. Check the README against the professor's submission requirements.
5. Commit and push only when the student is ready and the professor's instructions allow
   it.
