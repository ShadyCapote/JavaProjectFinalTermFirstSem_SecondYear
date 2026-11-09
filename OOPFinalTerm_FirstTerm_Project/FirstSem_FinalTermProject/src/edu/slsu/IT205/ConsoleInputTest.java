package edu.slsu.IT205;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for ConsoleInput: reading numbers and text from the keyboard, and
 * asking again when something that is not a number is typed.
 * The "keyboard" is replaced by a fixed text, so no one has to type.
 */
class ConsoleInputTest {
    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream printed;

    @BeforeEach
    void saveTheRealStreams() {
        originalIn = System.in;
        originalOut = System.out;
        printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed));
    }

    @AfterEach
    void putTheRealStreamsBack() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /** Makes a ConsoleInput that "types" the given text, one line per \n. */
    private ConsoleInput typing(String text) {
        System.setIn(new ByteArrayInputStream(text.getBytes()));
        return new ConsoleInput();
    }

    /** Counts how many times a text appears in what was printed. */
    private int timesPrinted(String text) {
        String all = printed.toString();
        int count = 0;
        for (int i = all.indexOf(text); i >= 0; i = all.indexOf(text, i + 1))
            count++;
        return count;
    }

    // ---------- readInt: a valid number ----------

    @Test
    void readIntReturnsTheNumberThatWasTyped() {
        assertEquals(42, typing("42\n").readInt("Enter: "));
    }

    @Test
    void readIntAcceptsSpacesAroundTheNumber() {
        assertEquals(8, typing("  8  \n").readInt("Enter: "));
    }

    @Test
    void readIntAcceptsANegativeNumber() {
        // the range (for example age 1 to 120) is checked by the booking code, not here
        assertEquals(-3, typing("-3\n").readInt("Enter: "));
    }

    @Test
    void noErrorMessageIsShownForAValidNumber() {
        typing("5\n").readInt("Enter: ");
        assertEquals(0, timesPrinted("Please enter a valid number."));
    }

    // ---------- readInt: asking again ----------

    @Test
    void readIntAsksAgainAfterLettersAndReturnsTheNextNumber() {
        assertEquals(7, typing("abc\n7\n").readInt("Enter: "));
        assertEquals(1, timesPrinted("Please enter a valid number."));
    }

    @Test
    void readIntKeepsAskingUntilANumberIsTyped() {
        assertEquals(3, typing("a\nb\n!\n3\n").readInt("Enter: "));
        assertEquals(3, timesPrinted("Please enter a valid number."));
    }

    @Test
    void readIntDoesNotAcceptADecimalNumber() {
        assertEquals(4, typing("2.5\n4\n").readInt("Enter: "));
        assertEquals(1, timesPrinted("Please enter a valid number."));
    }

    @Test
    void readIntDoesNotAcceptAnEmptyLine() {
        assertEquals(5, typing("\n5\n").readInt("Enter: "));
        assertEquals(1, timesPrinted("Please enter a valid number."));
    }

    @Test
    void readIntDoesNotAcceptANumberThatIsTooBig() {
        assertEquals(6, typing("99999999999\n6\n").readInt("Enter: "));
        assertEquals(1, timesPrinted("Please enter a valid number."));
    }

    @Test
    void thePromptIsShownAgainEachTimeItAsksAgain() {
        typing("abc\ndef\n9\n").readInt("Enter age: ");
        assertEquals(3, timesPrinted("Enter age: "));
    }

    // ---------- readLine ----------

    @Test
    void readLineReturnsTheTextWithoutSpacesAtTheEnds() {
        assertEquals("hello world", typing("  hello world  \n").readLine("Name: "));
    }

    @Test
    void readLineShowsThePromptAndReturnsAnEmptyLineAsEmptyText() {
        assertEquals("", typing("\n").readLine("Name: "));
        assertEquals(1, timesPrinted("Name: "));
    }

    @Test
    void readLineAcceptsTextThatLooksLikeANumberOrASymbol() {
        assertEquals("123", typing("123\n").readLine("Value: "));
        assertEquals("abc!", typing("abc!\n").readLine("Value: "));
    }

    // ---------- closed keyboard ----------

    @Test
    void readIntGivesAnExceptionWhenTheInputHasEnded() {
        ConsoleInput input = typing("");
        assertThrows(NoSuchElementException.class, () -> input.readInt("Enter: "));
    }

    @Test
    void readIntGivesAnExceptionWhenTheInputEndsAfterABadNumber() {
        // "abc" is rejected, and then there is nothing left to read
        ConsoleInput input = typing("abc\n");
        assertThrows(NoSuchElementException.class, () -> input.readInt("Enter: "));
        assertEquals(1, timesPrinted("Please enter a valid number."));
    }
}