package edu.slsu.IT205;

import java.io.Console;
import java.util.Scanner;

/**
 * Small helper class for safely reading user input.
 */
public class ConsoleInput {
    private final Scanner scanner = new Scanner(System.in);

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Reads a password. In a real terminal the typing is hidden.
     * Some IDE consoles (NetBeans, Eclipse) have no Console, so it falls
     * back to a normal visible input.
     */
    public String readPassword(String prompt) {
        Console console = System.console();
        if (console != null) {
            char[] chars = console.readPassword(prompt);
            return chars == null ? "" : new String(chars);
        }
        return readLine(prompt);
    }
}