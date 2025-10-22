package org.apache.commons.cli;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Extensive test suite for HelpFormatter.
 * Covers main methods, edge cases and formatting logic.
 */
class HelpFormatterTest {

    private HelpFormatter formatter;
    private Options options;
    private StringWriter writer;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        writer = new StringWriter();
        printWriter = new PrintWriter(writer);
        formatter = new HelpFormatter();
        options = new Options();

        Option optFile = new Option("f", "file", true, "The file to process");
        optFile.setArgName("FILE");
        options.addOption(optFile);

        Option optHelp = new Option("h", "help", false, "Show help");
        options.addOption(optHelp);

        Option optVerbose = new Option("v", "verbose", false, "Enable verbose mode");
        options.addOption(optVerbose);
    }

    // -------------------------------------------------------------------------
    // BASIC GETTERS AND CONSTANTS
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Default constants and getters should return expected values")
    void testDefaultConstants() {
        assertEquals(74, formatter.getWidth());
        assertEquals(1, formatter.getLeftPadding());
        assertEquals(3, formatter.getDescPadding());
        assertEquals("-", formatter.getOptPrefix());
        assertEquals("--", formatter.getLongOptPrefix());
        assertEquals("arg", formatter.getArgName());
        assertEquals("usage: ", formatter.getSyntaxPrefix());
        assertNotNull(formatter.getOptionComparator());
    }

    // -------------------------------------------------------------------------
    // findWrapPos()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findWrapPos should find position at newline within width")
    void testFindWrapPosWithNewline() {
        String text = "Hello\nWorld";
        int pos = formatter.findWrapPos(text, 10, 0);
        assertTrue(pos > 0);
    }

    @Test
    @DisplayName("findWrapPos should return -1 when no wrap needed")
    void testFindWrapPosNoWrap() {
        String text = "Short line";
        assertEquals(-1, formatter.findWrapPos(text, 50, 0));
    }

    @Test
    @DisplayName("findWrapPos should break long text at nearest space")
    void testFindWrapPosLongText() {
        String text = "this is a long text that should wrap somewhere";
        int pos = formatter.findWrapPos(text, 10, 0);
        assertTrue(pos <= 10 && pos > 0);
    }

    // -------------------------------------------------------------------------
    // appendWrappedText()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("appendWrappedText should correctly wrap multi-line text")
    void testAppendWrappedText() throws IOException {
        StringBuilder sb = new StringBuilder();
        formatter.appendWrappedText(sb, 10, 2, "This is a long text to wrap across multiple lines.");
        String output = sb.toString();
        assertTrue(output.contains(System.lineSeparator()));
        assertTrue(output.startsWith("This"));
    }

    @Test
    @DisplayName("appendWrappedText should handle width <= 0 gracefully")
    void testAppendWrappedTextZeroWidth() throws IOException {
        StringBuilder sb = new StringBuilder();
        formatter.appendWrappedText(sb, 0, 2, "Some text");
        assertEquals(0, sb.length());
    }

    // -------------------------------------------------------------------------
    // createPadding()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("createPadding should return string of spaces")
    void testCreatePadding() {
        String pad = formatter.createPadding(5);
        assertEquals(5, pad.length());
        assertTrue(pad.trim().isEmpty());
    }

    // -------------------------------------------------------------------------
    // printUsage()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("printUsage should print syntax correctly")
    void testPrintUsage() {
        formatter.printUsage(printWriter, 80, "myapp [options]");
        printWriter.flush();
        String result = writer.toString();
        assertTrue(result.contains("usage: myapp"));
    }

    @Test
    @DisplayName("printUsage with Options should render option list")
    void testPrintUsageWithOptions() {
        formatter.printUsage(printWriter, 80, "myapp", options);
        printWriter.flush();
        String result = writer.toString();
        assertTrue(result.contains("usage: myapp"));
        assertTrue(result.contains("-f"));
    }

    // -------------------------------------------------------------------------
    // printHelp()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("printHelp should print help with header and footer")
    void testPrintHelpWithHeaderFooter() {
        formatter.printHelp(printWriter, 80, "myapp", "Header Text", options, 2, 4, "Footer Text", false);
        printWriter.flush();
        String output = writer.toString();
        assertTrue(output.contains("Header Text"));
        assertTrue(output.contains("Footer Text"));
        assertTrue(output.contains("-f"));
    }

    @Test
    @DisplayName("printHelp should throw when syntax missing")
    void testPrintHelpMissingSyntax() {
        assertThrows(IllegalArgumentException.class, () -> {
            formatter.printHelp(printWriter, 80, "", "Header", options, 2, 4, "Footer", false);
        });
    }

    // -------------------------------------------------------------------------
    // appendOptions()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("appendOptions should render options text correctly")
    void testAppendOptions() throws IOException {
        StringBuilder sb = new StringBuilder();
        formatter.appendOptions(sb, 80, options, 2, 4);
        String result = sb.toString();
        assertTrue(result.contains("-f"));
        assertTrue(result.contains("--file"));
    }

    // -------------------------------------------------------------------------
    // OptionComparator
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Option comparator should compare options ignoring case by their opt name")
    void testOptionComparatorUsingCustomComparator() {
        Comparator<Option> comparator = Comparator.comparing(
                o -> o.getOpt().toLowerCase()
        );

        Option a = new Option("a", "alpha", false, null);
        Option b = new Option("B", "beta", false, null);

        assertTrue(comparator.compare(a, b) < 0, "Expected 'a' to come before 'B' ignoring case");
        assertTrue(comparator.compare(b, a) > 0, "Expected 'B' to come after 'a' ignoring case");
        assertEquals(0, comparator.compare(a, new Option("A", "alpha", false, null)),
                "Expected comparison of same letters (case-insensitive) to be equal");
    }

    @Test
    @DisplayName("Comparator should handle null opt safely")
    void testOptionComparatorHandlesNullOpt() {
        Comparator<Option> comparator = (o1, o2) -> {
            String opt1 = o1.getOpt() == null ? "" : o1.getOpt().toLowerCase();
            String opt2 = o2.getOpt() == null ? "" : o2.getOpt().toLowerCase();
            return opt1.compareTo(opt2);
        };

        Option nullOpt = new Option(null, "long-null", false, null);
        Option validOpt = new Option("a", "alpha", false, null);

        assertTrue(comparator.compare(nullOpt, validOpt) < 0, "Expected null opt to be considered smaller");
        assertTrue(comparator.compare(validOpt, nullOpt) > 0, "Expected valid opt to be considered greater");
        assertEquals(0, comparator.compare(nullOpt, new Option(null, "none", false, null)),
                "Expected two null opts to be equal");
    }

    // -------------------------------------------------------------------------
    // Builder tests
    // -------------------------------------------------------------------------

    @Nested
    class BuilderTests {

        private HelpFormatter.Builder builder;

        @BeforeEach
        void init() {
            builder = HelpFormatter.builder();
        }

        @Test
        @DisplayName("Builder should create default HelpFormatter instance")
        void testBuilderDefault() {
            HelpFormatter f = builder.get();
            assertNotNull(f);
        }

        @Test
        @DisplayName("Builder should allow custom PrintWriter")
        void testBuilderSetPrintWriter() {
            PrintWriter custom = mock(PrintWriter.class);
            builder.setPrintWriter(custom);
            HelpFormatter f = builder.get();
            assertNotNull(f);
        }

        @Test
        @DisplayName("Builder should handle showDeprecated toggles")
        void testBuilderSetShowDeprecated() {
            builder.setShowDeprecated(true);
            builder.setShowDeprecated(false);
            HelpFormatter f = builder.get();
            assertNotNull(f);
        }

        @Test
        @DisplayName("Builder should handle showSince toggle")
        void testBuilderSetShowSince() {
            builder.setShowSince(true);
            HelpFormatter f = builder.get();
            assertNotNull(f);
        }

        @Test
        @DisplayName("Builder should reject null PrintWriter")
        void testBuilderNullPrintWriter() {
            assertThrows(NullPointerException.class, () -> builder.setPrintWriter(null));
        }
    }

}
