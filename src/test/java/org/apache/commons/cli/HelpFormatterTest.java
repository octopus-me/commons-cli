package org.apache.commons.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;

class HelpFormatterTest {

    private HelpFormatter formatter;
    private Options options;

    @BeforeEach
    void setUp() {
        formatter = new HelpFormatter();
        options = new Options();
    }

    @Nested
    @DisplayName("Constructor and Builder Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create HelpFormatter with default constructor")
        void shouldCreateHelpFormatterWithDefaultConstructor() {
            assertNotNull(formatter);
            assertEquals(HelpFormatter.DEFAULT_WIDTH, formatter.getWidth());
            assertEquals(HelpFormatter.DEFAULT_LEFT_PAD, formatter.getLeftPadding());
            assertEquals(HelpFormatter.DEFAULT_DESC_PAD, formatter.getDescPadding());
            assertEquals(HelpFormatter.DEFAULT_SYNTAX_PREFIX, formatter.getSyntaxPrefix());
            assertEquals(HelpFormatter.DEFAULT_OPT_PREFIX, formatter.getOptPrefix());
            assertEquals(HelpFormatter.DEFAULT_LONG_OPT_PREFIX, formatter.getLongOptPrefix());
            assertEquals(HelpFormatter.DEFAULT_ARG_NAME, formatter.getArgName());
        }
        
        @Test
        @DisplayName("Should create HelpFormatter using Builder pattern")
        void shouldCreateHelpFormatterUsingBuilderPattern() {
            HelpFormatter built = HelpFormatter.builder()
                    .setShowDeprecated(true)
                    .setShowSince(true)
                    .get();
            
            assertNotNull(built);
        }
        
        @Test
        @DisplayName("Should create HelpFormatter with custom PrintWriter")
        void shouldCreateHelpFormatterWithCustomPrintWriter() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            HelpFormatter built = HelpFormatter.builder()
                    .setPrintWriter(pw)
                    .get();
            
            assertNotNull(built);
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {
        
        @Test
        @DisplayName("Should get and set width")
        void shouldGetAndSetWidth() {
            assertEquals(HelpFormatter.DEFAULT_WIDTH, formatter.getWidth());
            
            formatter.setWidth(100);
            
            assertEquals(100, formatter.getWidth());
        }
        
        @Test
        @DisplayName("Should get and set left padding")
        void shouldGetAndSetLeftPadding() {
            assertEquals(HelpFormatter.DEFAULT_LEFT_PAD, formatter.getLeftPadding());
            
            formatter.setLeftPadding(5);
            
            assertEquals(5, formatter.getLeftPadding());
        }
        
        @Test
        @DisplayName("Should get and set description padding")
        void shouldGetAndSetDescriptionPadding() {
            assertEquals(HelpFormatter.DEFAULT_DESC_PAD, formatter.getDescPadding());
            
            formatter.setDescPadding(10);
            
            assertEquals(10, formatter.getDescPadding());
        }
        
        @Test
        @DisplayName("Should get and set syntax prefix")
        void shouldGetAndSetSyntaxPrefix() {
            assertEquals(HelpFormatter.DEFAULT_SYNTAX_PREFIX, formatter.getSyntaxPrefix());
            
            formatter.setSyntaxPrefix("Usage: ");
            
            assertEquals("Usage: ", formatter.getSyntaxPrefix());
        }
        
        @Test
        @DisplayName("Should get and set option prefix")
        void shouldGetAndSetOptionPrefix() {
            assertEquals(HelpFormatter.DEFAULT_OPT_PREFIX, formatter.getOptPrefix());
            
            formatter.setOptPrefix("/");
            
            assertEquals("/", formatter.getOptPrefix());
        }
        
        @Test
        @DisplayName("Should get and set long option prefix")
        void shouldGetAndSetLongOptionPrefix() {
            assertEquals(HelpFormatter.DEFAULT_LONG_OPT_PREFIX, formatter.getLongOptPrefix());
            
            formatter.setLongOptPrefix("---");
            
            assertEquals("---", formatter.getLongOptPrefix());
        }
        
        @Test
        @DisplayName("Should get and set argument name")
        void shouldGetAndSetArgumentName() {
            assertEquals(HelpFormatter.DEFAULT_ARG_NAME, formatter.getArgName());
            
            formatter.setArgName("parameter");
            
            assertEquals("parameter", formatter.getArgName());
        }
        
        @Test
        @DisplayName("Should get and set long option separator")
        void shouldGetAndSetLongOptionSeparator() {
            assertEquals(HelpFormatter.DEFAULT_LONG_OPT_SEPARATOR, formatter.getLongOptSeparator());
            
            formatter.setLongOptSeparator("=");
            
            assertEquals("=", formatter.getLongOptSeparator());
        }
        
        @Test
        @DisplayName("Should get and set option comparator")
        void shouldGetAndSetOptionComparator() {
            assertNotNull(formatter.getOptionComparator());
            
            Comparator<Option> customComparator = (o1, o2) -> o2.getKey().compareTo(o1.getKey());
            formatter.setOptionComparator(customComparator);
            
            assertEquals(customComparator, formatter.getOptionComparator());
        }
        
        @Test
        @DisplayName("Should handle null option comparator")
        void shouldHandleNullOptionComparator() {
            formatter.setOptionComparator(null);
            
            assertNull(formatter.getOptionComparator());
        }
    }

    @Nested
    @DisplayName("Utility Method Tests")
    class UtilityMethodTests {
        
        @Test
        @DisplayName("Should create padding of specified length")
        void shouldCreatePaddingOfSpecifiedLength() {
            String padding = formatter.createPadding(5);
            
            assertEquals("     ", padding);
        }
        
        @Test
        @DisplayName("Should create empty padding for zero length")
        void shouldCreateEmptyPaddingForZeroLength() {
            String padding = formatter.createPadding(0);
            
            assertEquals("", padding);
        }
        
        @Test
        @DisplayName("Should find wrap position in text")
        void shouldFindWrapPositionInText() {
            String text = "This is a long text that needs to be wrapped";
            int pos = formatter.findWrapPos(text, 10, 0);
            
            assertTrue(pos > 0);
            assertTrue(pos <= 10);
        }
        
        @Test
        @DisplayName("Should return -1 when no wrap needed")
        void shouldReturnMinusOneWhenNoWrapNeeded() {
            String text = "Short";
            int pos = formatter.findWrapPos(text, 10, 0);
            
            assertEquals(-1, pos);
        }
        
        @Test
        @DisplayName("Should find wrap position at newline")
        void shouldFindWrapPositionAtNewline() {
            String text = "First line\nSecond line";
            int pos = formatter.findWrapPos(text, 20, 0);
            
            assertEquals(11, pos); // Position after newline
        }
        
        @Test
        @DisplayName("Should trim trailing whitespace")
        void shouldTrimTrailingWhitespace() {
            String text = "Text with spaces   ";
            String result = formatter.rtrim(text);
            
            assertEquals("Text with spaces", result);
        }
        
        @Test
        @DisplayName("Should handle empty string in rtrim")
        void shouldHandleEmptyStringInRtrim() {
            assertEquals("", formatter.rtrim(""));
            assertNull(formatter.rtrim(null));
        }
        
        @Test
        @DisplayName("Should get description from option")
        void shouldGetDescriptionFromOption() {
            Option option = Option.builder("a").desc("Test description").build();
            String desc = HelpFormatter.getDescription(option);
            
            assertEquals("Test description", desc);
        }
        
        @Test
        @DisplayName("Should get empty description for null option description")
        void shouldGetEmptyDescriptionForNullOptionDescription() {
            Option option = Option.builder("a").build();
            String desc = HelpFormatter.getDescription(option);
            
            assertEquals("", desc);
        }
    }

    @Nested
    @DisplayName("Print Help Tests")
    class PrintHelpTests {
        
        @BeforeEach
        void setUp() {
            options.addOption("f", "file", true, "The input file");
            options.addOption("v", "verbose", false, "Enable verbose output");
            options.addOption("h", "help", false, "Show help");
        }
        
        @Test
        @DisplayName("Should print basic help")
        void shouldPrintBasicHelp() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printHelp(pw, 80, "myapp", "Header text", options, 1, 3, "Footer text", false);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("usage: myapp"));
            assertTrue(result.contains("Header text"));
            assertTrue(result.contains("Footer text"));
            assertTrue(result.contains("-f,--file"));
            assertTrue(result.contains("-v,--verbose"));
            assertTrue(result.contains("-h,--help"));
        }
        
        @Test
        @DisplayName("Should print help with auto usage")
        void shouldPrintHelpWithAutoUsage() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printHelp(pw, 80, "myapp", "Header", options, 1, 3, "Footer", true);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("usage: myapp"));
        }
        
        @Test
        @DisplayName("Should print help without header and footer")
        void shouldPrintHelpWithoutHeaderAndFooter() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printHelp(pw, 80, "myapp", null, options, 1, 3, null, false);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("usage: myapp"));
            assertFalse(result.contains("null"));
        }
        
        @Test
        @DisplayName("Should print help with empty header and footer")
        void shouldPrintHelpWithEmptyHeaderAndFooter() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printHelp(pw, 80, "myapp", "", options, 1, 3, "", false);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("usage: myapp"));
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException for empty command line syntax")
        void shouldThrowIllegalArgumentExceptionForEmptyCommandLineSyntax() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            assertThrows(IllegalArgumentException.class, () -> {
                formatter.printHelp(pw, 80, "", "Header", options, 1, 3, "Footer", false);
            });
        }
    }

    @Nested
    @DisplayName("Print Usage Tests")
    class PrintUsageTests {
        
        @Test
        @DisplayName("Should print simple usage")
        void shouldPrintSimpleUsage() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printUsage(pw, 80, "myapp -f file");
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("usage: myapp -f file"));
        }
        
        @Test
        @DisplayName("Should print usage with options")
        void shouldPrintUsageWithOptions() {
            options.addOption("f", "file", true, "File option");
            options.addOption("v", false, "Verbose option");
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printUsage(pw, 80, "myapp", options);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("usage: myapp"));
            assertTrue(result.contains("-f"));
            assertTrue(result.contains("-v"));
        }
        
        @Test
        @DisplayName("Should print usage with required options")
        void shouldPrintUsageWithRequiredOptions() {
            options.addOption(Option.builder("f").required(true).desc("Required file").build());
            options.addOption(Option.builder("o").desc("Optional").build());
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printUsage(pw, 80, "myapp", options);
            
            String result = sw.toString();
            assertNotNull(result);
            // Required option should not be in brackets
            assertTrue(result.contains("-f"));
            // Optional option should be in brackets
            assertTrue(result.contains("[-o]"));
        }
        
        @Test
        @DisplayName("Should print usage with option groups")
        void shouldPrintUsageWithOptionGroups() {
            OptionGroup group = new OptionGroup();
            group.addOption(Option.builder("a").desc("Option A").build());
            group.addOption(Option.builder("b").desc("Option B").build());
            options.addOptionGroup(group);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printUsage(pw, 80, "myapp", options);
            
            String result = sw.toString();
            assertNotNull(result);
            // Option group should be displayed with pipe separator
            assertTrue(result.contains("-a | -b"));
        }
        
        @Test
        @DisplayName("Should print usage with required option groups")
        void shouldPrintUsageWithRequiredOptionGroups() {
            OptionGroup group = new OptionGroup();
            group.setRequired(true);
            group.addOption(Option.builder("a").desc("Option A").build());
            group.addOption(Option.builder("b").desc("Option B").build());
            options.addOptionGroup(group);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printUsage(pw, 80, "myapp", options);
            
            String result = sw.toString();
            assertNotNull(result);
            // Required option group should not be in brackets
            assertTrue(result.contains("-a | -b"));
            assertFalse(result.contains("[-a | -b]"));
        }
    }

    @Nested
    @DisplayName("Print Options Tests")
    class PrintOptionsTests {
        
        @Test
        @DisplayName("Should print options with descriptions")
        void shouldPrintOptionsWithDescriptions() {
            options.addOption("f", "file", true, "The input file for processing");
            options.addOption("v", "verbose", false, "Enable verbose output mode");
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printOptions(pw, 80, options, 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("-f,--file"));
            assertTrue(result.contains("-v,--verbose"));
            assertTrue(result.contains("The input file"));
            assertTrue(result.contains("Enable verbose output"));
        }
        
        @Test
        @DisplayName("Should print options with argument names")
        void shouldPrintOptionsWithArgumentNames() {
            Option option = Option.builder("f").longOpt("file").hasArg().argName("FILENAME").desc("Input file").build();
            options.addOption(option);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printOptions(pw, 80, options, 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("<FILENAME>"));
        }
        
        @Test
        @DisplayName("Should print options with default argument name")
        void shouldPrintOptionsWithDefaultArgumentName() {
            Option option = Option.builder("f").longOpt("file").hasArg().desc("Input file").build();
            options.addOption(option);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printOptions(pw, 80, options, 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("<arg>"));
        }
        
        @Test
        @DisplayName("Should handle options without descriptions")
        void shouldHandleOptionsWithoutDescriptions() {
            Option option = Option.builder("s").longOpt("silent").build();
            options.addOption(option);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printOptions(pw, 80, options, 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("-s,--silent"));
        }
        
        @Test
        @DisplayName("Should sort options alphabetically")
        void shouldSortOptionsAlphabetically() {
            options.addOption("z", "zeta", false, "Zeta option");
            options.addOption("a", "alpha", false, "Alpha option");
            options.addOption("m", "middle", false, "Middle option");
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printOptions(pw, 80, options, 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            int alphaPos = result.indexOf("-a,--alpha");
            int middlePos = result.indexOf("-m,--middle");
            int zetaPos = result.indexOf("-z,--zeta");
            
            assertTrue(alphaPos < middlePos);
            assertTrue(middlePos < zetaPos);
        }
    }

    @Nested
    @DisplayName("Print Wrapped Text Tests")
    class PrintWrappedTextTests {
        
        @Test
        @DisplayName("Should print wrapped text")
        void shouldPrintWrappedText() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            String longText = "This is a very long text that should be wrapped when displayed in the help output to fit within the specified width limit.";
            
            formatter.printWrapped(pw, 40, longText);
            
            String result = sw.toString();
            assertNotNull(result);
            // Should contain newlines for wrapping
            assertTrue(result.contains(System.lineSeparator()));
        }
        
        @Test
        @DisplayName("Should print wrapped text with tab stop")
        void shouldPrintWrappedTextWithTabStop() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            String text = "This text should be wrapped with a specific tab stop position for subsequent lines.";
            
            formatter.printWrapped(pw, 50, 10, text);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains(System.lineSeparator()));
        }
        
        @Test
        @DisplayName("Should handle short text that doesn't need wrapping")
        void shouldHandleShortTextThatDoesntNeedWrapping() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            String shortText = "Short text";
            
            formatter.printWrapped(pw, 50, shortText);
            
            String result = sw.toString();
            assertEquals("Short text" + System.lineSeparator(), result);
        }
        
        @Test
        @DisplayName("Should handle empty text")
        void shouldHandleEmptyText() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printWrapped(pw, 50, "");
            
            String result = sw.toString();
            assertEquals(System.lineSeparator(), result);
        }
    }

    @Nested
    @DisplayName("Deprecated Options Tests")
    class DeprecatedOptionsTests {
        
        @Test
        @DisplayName("Should handle deprecated options with builder")
        void shouldHandleDeprecatedOptionsWithBuilder() {
            HelpFormatter customFormatter = HelpFormatter.builder()
                    .setShowDeprecated(true)
                    .get();
            
            Option deprecatedOption = Option.builder("d").longOpt("deprecated").deprecated().desc("Old option").build();
            options.addOption(deprecatedOption);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            customFormatter.printOptions(pw, 80, options, 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("[Deprecated]"));
        }
        
        @Test
        @DisplayName("Should handle deprecated options with custom format")
        void shouldHandleDeprecatedOptionsWithCustomFormat() {
            HelpFormatter customFormatter = HelpFormatter.builder()
                    .setShowDeprecated(o -> "DEPRECATED: " + o.getLongOpt())
                    .get();
            
            Option deprecatedOption = Option.builder("d").longOpt("oldoption").deprecated().desc("Old option").build();
            options.addOption(deprecatedOption);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            customFormatter.printOptions(pw, 80, options, 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("DEPRECATED: oldoption"));
        }
        
        @Test
        @DisplayName("Should not show deprecated options when disabled")
        void shouldNotShowDeprecatedOptionsWhenDisabled() {
            HelpFormatter customFormatter = HelpFormatter.builder()
                    .setShowDeprecated(false)
                    .get();
            
            Option deprecatedOption = Option.builder("d").longOpt("deprecated").deprecated().desc("Old option").build();
            Option normalOption = Option.builder("n").longOpt("normal").desc("Normal option").build();
            options.addOption(deprecatedOption);
            options.addOption(normalOption);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            customFormatter.printOptions(pw, 80, options, 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("normal"));
            // Should not contain deprecated marker
            assertFalse(result.contains("[Deprecated]"));
        }
    }

    @Nested
    @DisplayName("Since Field Tests")
    class SinceFieldTests {
        
        @Test
        @DisplayName("Should show since field when enabled")
        void shouldShowSinceFieldWhenEnabled() {
            HelpFormatter customFormatter = HelpFormatter.builder()
                    .setShowSince(true)
                    .get();
            
            Option optionWithSince = Option.builder("f").longOpt("file").since("1.2.0").desc("File option").build();
            options.addOption(optionWithSince);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            customFormatter.printOptions(pw, 80, options, 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("Since"));
            assertTrue(result.contains("1.2.0"));
        }
        
        @Test
        @DisplayName("Should handle options without since field")
        void shouldHandleOptionsWithoutSinceField() {
            HelpFormatter customFormatter = HelpFormatter.builder()
                    .setShowSince(true)
                    .get();
            
            Option optionWithoutSince = Option.builder("v").longOpt("verbose").desc("Verbose option").build();
            options.addOption(optionWithoutSince);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            customFormatter.printOptions(pw, 80, options, 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("Since"));
            assertTrue(result.contains("-")); // Dash for no since value
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle options with only long names")
        void shouldHandleOptionsWithOnlyLongNames() {
            Option longOnlyOption = Option.builder().longOpt("longonly").desc("Long only option").build();
            options.addOption(longOnlyOption);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printOptions(pw, 80, options, 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("--longonly"));
        }
        
        @Test
        @DisplayName("Should handle options with only short names")
        void shouldHandleOptionsWithOnlyShortNames() {
            Option shortOnlyOption = Option.builder("s").desc("Short only option").build();
            options.addOption(shortOnlyOption);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printOptions(pw, 80, options, 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("-s"));
            assertFalse(result.contains("--")); // Should not have long option
        }
        
        @Test
        @DisplayName("Should handle options with multiple arguments")
        void shouldHandleOptionsWithMultipleArguments() {
            Option multiArgOption = Option.builder("D").hasArgs().desc("Properties").build();
            options.addOption(multiArgOption);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printOptions(pw, 80, options, 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            assertTrue(result.contains("-D"));
        }
        
        @Test
        @DisplayName("Should handle empty options")
        void shouldHandleEmptyOptions() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printOptions(pw, 80, new Options(), 1, 3);
            
            String result = sw.toString();
            assertNotNull(result);
            // Should not crash with empty options
        }
        
        @Test
        @DisplayName("Should handle very small width")
        void shouldHandleVerySmallWidth() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            options.addOption("a", "alpha", false, "Simple option");
            
            formatter.printHelp(pw, 10, "cmd", "Header", options, 1, 3, "Footer", false);
            
            String result = sw.toString();
            assertNotNull(result);
            // Should handle very narrow width without crashing
        }
        
        @Test
        @DisplayName("Should handle zero width")
        void shouldHandleZeroWidth() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            options.addOption("a", "alpha", false, "Simple option");
            
            formatter.printWrapped(pw, 0, "Test text");
            
            String result = sw.toString();
            assertNotNull(result);
            // Should handle zero width without crashing
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should generate complete help output")
        void shouldGenerateCompleteHelpOutput() {
            // Setup comprehensive options
            options.addOption(Option.builder("f").longOpt("file").required(true).hasArg().argName("FILE").desc("Input file to process").build());
            options.addOption(Option.builder("v").longOpt("verbose").desc("Enable verbose output").build());
            options.addOption(Option.builder("h").longOpt("help").desc("Show this help message").build());
            options.addOption(Option.builder("o").longOpt("output").hasArg().argName("OUTPUT").desc("Output file name").build());
            
            OptionGroup formatGroup = new OptionGroup();
            formatGroup.addOption(Option.builder("j").longOpt("json").desc("JSON format").build());
            formatGroup.addOption(Option.builder("x").longOpt("xml").desc("XML format").build());
            options.addOptionGroup(formatGroup);
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            String header = "Process input files with various options and output formats.";
            String footer = "Report bugs to: support@example.com";
            
            formatter.printHelp(pw, 80, "myapp", header, options, 1, 3, footer, true);
            
            String result = sw.toString();
            assertNotNull(result);
            
            // Verify all components are present
            assertTrue(result.contains("usage: myapp"));
            assertTrue(result.contains(header));
            assertTrue(result.contains(footer));
            assertTrue(result.contains("-f,--file"));
            assertTrue(result.contains("-v,--verbose"));
            assertTrue(result.contains("-h,--help"));
            assertTrue(result.contains("-o,--output"));
            assertTrue(result.contains("-j,--json"));
            assertTrue(result.contains("-x,--xml"));
            assertTrue(result.contains("Input file to process"));
            assertTrue(result.contains("Enable verbose output"));
            assertTrue(result.contains("Show this help message"));
        }
        
        @Test
        @DisplayName("Should handle complex option configurations")
        void shouldHandleComplexOptionConfigurations() {
            // Mix of different option types
            options.addOption(Option.builder("D").hasArgs().desc("System properties").build());
            options.addOption(Option.builder("d").longOpt("debug").deprecated().desc("Debug mode (deprecated)").build());
            options.addOption(Option.builder().longOpt("version").since("1.0.0").desc("Show version").build());
            options.addOption(Option.builder("c").hasArg().argName("COUNT").type(Integer.class).desc("Number of iterations").build());
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            formatter.printHelp(pw, 80, "complex", "Complex application with various options", options, 2, 4, "End of help", false);
            
            String result = sw.toString();
            assertNotNull(result);
            
            // Should handle all option types without issues
            assertTrue(result.contains("-D"));
            assertTrue(result.contains("--debug"));
            assertTrue(result.contains("--version"));
            assertTrue(result.contains("-c"));
            assertTrue(result.contains("<COUNT>"));
        }
    }
}