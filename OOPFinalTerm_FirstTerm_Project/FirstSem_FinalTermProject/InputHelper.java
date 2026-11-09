package edu.slsu.IT205;

import java.util.Scanner;

/**
 * Reads numbers from the console without crashing when the user types
 * something that is not a number. Each method keeps asking until it gets
 * a valid answer.
 * <p>
 * It uses the {@link Scanner} it is given, so the whole program can share
 * one Scanner on {@code System.in}.
 */
public class InputHelper {
    private final Scanner scanner;

    /**
     * Creates a helper that reads from the given scanner.
     *
     * @param scanner the scanner the program already uses for console input
     */
    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Asks for a whole number and repeats until one is typed.
     *
     * @param prompt the text shown before the user types, including the
     *               space at the end, for example {@code "Age: "}
     * @return the number the user typed
     */
    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    /**
     * Asks for a whole number inside a range and repeats until the number
     * is valid.
     *
     * @param prompt the text shown before the user types
     * @param min    the smallest accepted number
     * @param max    the largest accepted number
     * @return a number from {@code min} to {@code max}
     */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Please enter a number from " + min + " to " + max + ".");
        }
    }

    /**
     * Asks for a decimal number, such as a fare, and repeats until one is
     * typed.
     *
     * @param prompt the text shown before the user types
     * @return the number the user typed
     */
    public double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(text);
                if (!Double.isNaN(value) && !Double.isInfinite(value)) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // fall through to the message below
            }
            System.out.println("Please enter a number, for example 150 or 150.50.");
        }
    }
}