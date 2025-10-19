package org.apache.commons.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Properties;
import java.util.function.Consumer;

class DefaultParserTest {

    private DefaultParser parser;
    private Options options;

    @BeforeEach
    void setUp() {
        parser = new DefaultParser();
        options = new Options();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create DefaultParser with default constructor")
        void shouldCreateDefaultParserWithDefaultConstructor() {
            assertNotNull(parser);
        }
        
        @Test
        @DisplayName("Should create DefaultParser with partial matching enabled")
        void shouldCreateDefaultParserWithPartialMatchingEnabled() {
            DefaultParser customParser = new DefaultParser(true);
            assertNotNull(customParser);
        }
        
        @Test
        @DisplayName("Should create DefaultParser with partial matching disabled")
        void shouldCreateDefaultParserWithPartialMatchingDisabled() {
            DefaultParser customParser = new DefaultParser(false);
            assertNotNull(customParser);
        }
        
        @Test
        @DisplayName("Should create DefaultParser using Builder pattern")
        void shouldCreateDefaultParserUsingBuilderPattern() {
            DefaultParser builtParser = DefaultParser.builder()
                    .setAllowPartialMatching(false)
                    .setStripLeadingAndTrailingQuotes(true)
                    .get();
            
            assertNotNull(builtParser);
        }
        
        @Test
        @DisplayName("Should create DefaultParser with deprecated handler")
        void shouldCreateDefaultParserWithDeprecatedHandler() {
            Consumer<Option> handler = mock(Consumer.class);
            DefaultParser builtParser = DefaultParser.builder()
                    .setDeprecatedHandler(handler)
                    .get();
            
            assertNotNull(builtParser);
        }
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {
        
        @Test
        @DisplayName("Should build parser with custom configuration")
        void shouldBuildParserWithCustomConfiguration() {
            DefaultParser.Builder builder = DefaultParser.builder();
            
            DefaultParser parser = builder
                    .setAllowPartialMatching(false)
                    .setStripLeadingAndTrailingQuotes(true)
                    .get();
            
            assertNotNull(parser);
        }
        
        @Test
        @DisplayName("Should handle null stripLeadingAndTrailingQuotes")
        void shouldHandleNullStripLeadingAndTrailingQuotes() {
            DefaultParser parser = DefaultParser.builder()
                    .setStripLeadingAndTrailingQuotes(null)
                    .get();
            
            assertNotNull(parser);
        }
        
        @Test
        @DisplayName("Should use deprecated build method")
        void shouldUseDeprecatedBuildMethod() {
            DefaultParser parser = DefaultParser.builder()
                    .setAllowPartialMatching(true)
                    .build();
            
            assertNotNull(parser);
        }
    }

    @Nested
    @DisplayName("Basic Option Parsing Tests")
    class BasicOptionParsingTests {
        
        @Test
        @DisplayName("Should parse short option without argument")
        void shouldParseShortOptionWithoutArgument() throws ParseException {
            options.addOption("a", "alpha", false, "Alpha option");
            String[] args = {"-a"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertTrue(cmd.hasOption("a"));
            assertTrue(cmd.hasOption("alpha"));
        }
        
        @Test
        @DisplayName("Should parse short option with argument")
        void shouldParseShortOptionWithArgument() throws ParseException {
            options.addOption("f", "file", true, "File option");
            String[] args = {"-f", "filename.txt"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertTrue(cmd.hasOption("f"));
            assertEquals("filename.txt", cmd.getOptionValue("f"));
        }
        
        @Test
        @DisplayName("Should parse long option without argument")
        void shouldParseLongOptionWithoutArgument() throws ParseException {
            options.addOption("v", "verbose", false, "Verbose option");
            String[] args = {"--verbose"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertTrue(cmd.hasOption("v"));
            assertTrue(cmd.hasOption("verbose"));
        }
        
        @Test
        @DisplayName("Should parse long option with argument")
        void shouldParseLongOptionWithArgument() throws ParseException {
            options.addOption("f", "file", true, "File option");
            String[] args = {"--file", "filename.txt"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertTrue(cmd.hasOption("f"));
            assertEquals("filename.txt", cmd.getOptionValue("file"));
        }
        
        @Test
        @DisplayName("Should parse option with equals sign")
        void shouldParseOptionWithEqualsSign() throws ParseException {
            options.addOption("f", "file", true, "File option");
            String[] args = {"--file=filename.txt"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertTrue(cmd.hasOption("f"));
            assertEquals("filename.txt", cmd.getOptionValue("file"));
        }
        
        @Test
        @DisplayName("Should parse short option with equals sign")
        void shouldParseShortOptionWithEqualsSign() throws ParseException {
            options.addOption("f", "file", true, "File option");
            String[] args = {"-f=filename.txt"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertTrue(cmd.hasOption("f"));
            assertEquals("filename.txt", cmd.getOptionValue("f"));
        }
    }

    @Nested
    @DisplayName("Concatenated Options Tests")
    class ConcatenatedOptionsTests {
        
        @Test
        @DisplayName("Should parse concatenated short options")
        void shouldParseConcatenatedShortOptions() throws ParseException {
            options.addOption("a", false, "Alpha option");
            options.addOption("b", false, "Beta option");
            options.addOption("c", false, "Gamma option");
            String[] args = {"-abc"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertTrue(cmd.hasOption("a"));
            assertTrue(cmd.hasOption("b"));
            assertTrue(cmd.hasOption("c"));
        }
        
        @Test
        @DisplayName("Should parse concatenated short options with argument at end")
        void shouldParseConcatenatedShortOptionsWithArgumentAtEnd() throws ParseException {
            options.addOption("a", false, "Alpha option");
            options.addOption("b", false, "Beta option");
            options.addOption("f", "file", true, "File option");
            String[] args = {"-abf", "filename.txt"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertTrue(cmd.hasOption("a"));
            assertTrue(cmd.hasOption("b"));
            assertTrue(cmd.hasOption("f"));
            assertEquals("filename.txt", cmd.getOptionValue("f"));
        }
        
        @Test
        @DisplayName("Should parse concatenated short options with argument")
        void shouldParseConcatenatedShortOptionsWithArgument() throws ParseException {
            options.addOption("a", false, "Alpha option");
            options.addOption("f", "file", true, "File option");
            String[] args = {"-af", "filename.txt"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertTrue(cmd.hasOption("a"));
            assertTrue(cmd.hasOption("f"));
            assertEquals("filename.txt", cmd.getOptionValue("f"));
        }
    }

    @Nested
    @DisplayName("Partial Matching Tests")
    class PartialMatchingTests {
        
        @Test
        @DisplayName("Should match partial long option when enabled")
        void shouldMatchPartialLongOptionWhenEnabled() throws ParseException {
            DefaultParser partialParser = new DefaultParser(true);
            options.addOption("d", "debug", false, "Debug option");
            options.addOption("e", "extract", false, "Extract option");
            String[] args = {"--deb"};
            
            CommandLine cmd = partialParser.parse(options, args);
            
            assertTrue(cmd.hasOption("debug"));
        }
        
        @Test
        @DisplayName("Should throw AmbiguousOptionException for ambiguous partial match")
        void shouldThrowAmbiguousOptionExceptionForAmbiguousPartialMatch() {
            DefaultParser partialParser = new DefaultParser(true);
            options.addOption("d", "debug", false, "Debug option");
            options.addOption("d", "deploy", false, "Deploy option");
            String[] args = {"--de"};
            
            assertThrows(AmbiguousOptionException.class, () -> {
                partialParser.parse(options, args);
            });
        }
        
        @Test
        @DisplayName("Should not match partial long option when disabled")
        void shouldNotMatchPartialLongOptionWhenDisabled() {
            DefaultParser noPartialParser = new DefaultParser(false);
            options.addOption("d", "debug", false, "Debug option");
            String[] args = {"--deb"};
            
            assertThrows(UnrecognizedOptionException.class, () -> {
                noPartialParser.parse(options, args);
            });
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should throw UnrecognizedOptionException for unknown option")
        void shouldThrowUnrecognizedOptionExceptionForUnknownOption() {
            options.addOption("a", "alpha", false, "Alpha option");
            String[] args = {"-x"};
            
            assertThrows(UnrecognizedOptionException.class, () -> {
                parser.parse(options, args);
            });
        }
        
        @Test
        @DisplayName("Should throw MissingArgumentException for option requiring argument")
        void shouldThrowMissingArgumentExceptionForOptionRequiringArgument() {
            options.addOption("f", "file", true, "File option");
            String[] args = {"-f"};
            
            assertThrows(MissingArgumentException.class, () -> {
                parser.parse(options, args);
            });
        }
        
        @Test
        @DisplayName("Should throw MissingOptionException for missing required option")
        void shouldThrowMissingOptionExceptionForMissingRequiredOption() {
            options.addOption(Option.builder("r").required(true).desc("Required option").build());
            String[] args = {};
            
            assertThrows(MissingOptionException.class, () -> {
                parser.parse(options, args);
            });
        }
        
        @Test
        @DisplayName("Should throw AlreadySelectedException for conflicting option group")
        void shouldThrowAlreadySelectedExceptionForConflictingOptionGroup() {
            OptionGroup group = new OptionGroup();
            group.addOption(Option.builder("a").desc("Option A").build());
            group.addOption(Option.builder("b").desc("Option B").build());
            options.addOptionGroup(group);
            String[] args = {"-a", "-b"};
            
            assertThrows(AlreadySelectedException.class, () -> {
                parser.parse(options, args);
            });
        }
    }

    @Nested
    @DisplayName("Non-Option Arguments Tests")
    class NonOptionArgumentsTests {
        
        @Test
        @DisplayName("Should handle non-option arguments with stopAtNonOption true")
        void shouldHandleNonOptionArgumentsWithStopAtNonOptionTrue() throws ParseException {
            options.addOption("a", "alpha", false, "Alpha option");
            String[] args = {"non-option", "-a"};
            
            CommandLine cmd = parser.parse(options, args, true);
            
            assertEquals(2, cmd.getArgList().size());
            assertEquals("non-option", cmd.getArgList().get(0));
        }
        
        @Test
        @DisplayName("Should handle non-option arguments with stopAtNonOption false")
        void shouldHandleNonOptionArgumentsWithStopAtNonOptionFalse() throws ParseException {
            options.addOption("a", "alpha", false, "Alpha option");
            String[] args = {"non-option", "-a"};
            
            CommandLine cmd = parser.parse(options, args, false);
            
            assertTrue(cmd.hasOption("a"));
            assertEquals(1, cmd.getArgList().size());
            assertEquals("non-option", cmd.getArgList().get(0));
        }
        
        @Test
        @DisplayName("Should stop parsing at non-option with stopAtNonOption true")
        void shouldStopParsingAtNonOptionWithStopAtNonOptionTrue() throws ParseException {
            options.addOption("a", "alpha", false, "Alpha option");
            options.addOption("b", "beta", false, "Beta option");
            String[] args = {"-a", "non-option", "-b"};
            
            CommandLine cmd = parser.parse(options, args, true);
            
            assertTrue(cmd.hasOption("a"));
            assertFalse(cmd.hasOption("b"));
            assertEquals(2, cmd.getArgList().size());
            assertEquals("non-option", cmd.getArgList().get(0));
            assertEquals("-b", cmd.getArgList().get(1));
        }
        
        @Test
        @DisplayName("Should handle double dash as end of options")
        void shouldHandleDoubleDashAsEndOfOptions() throws ParseException {
            options.addOption("a", "alpha", false, "Alpha option");
            String[] args = {"-a", "--", "-b", "--gamma"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertTrue(cmd.hasOption("a"));
            assertEquals(2, cmd.getArgList().size());
            assertEquals("-b", cmd.getArgList().get(0));
            assertEquals("--gamma", cmd.getArgList().get(1));
        }
    }

    @Nested
    @DisplayName("Properties Integration Tests")
    class PropertiesIntegrationTests {
        
        @Test
        @DisplayName("Should parse options from properties")
        void shouldParseOptionsFromProperties() throws ParseException {
            options.addOption("d", "debug", false, "Debug option");
            options.addOption("f", "file", true, "File option");
            
            Properties props = new Properties();
            props.setProperty("debug", "true");
            props.setProperty("file", "config.properties");
            
            String[] args = {};
            CommandLine cmd = parser.parse(options, args, props);
            
            assertTrue(cmd.hasOption("debug"));
            assertEquals("config.properties", cmd.getOptionValue("file"));
        }
        
        @Test
        @DisplayName("Should not override command line options with properties")
        void shouldNotOverrideCommandLineOptionsWithProperties() throws ParseException {
            options.addOption("f", "file", true, "File option");
            
            Properties props = new Properties();
            props.setProperty("file", "default.properties");
            
            String[] args = {"-f", "custom.properties"};
            CommandLine cmd = parser.parse(options, args, props);
            
            assertEquals("custom.properties", cmd.getOptionValue("file"));
        }
        
        @Test
        @DisplayName("Should throw UnrecognizedOptionException for unknown property")
        void shouldThrowUnrecognizedOptionExceptionForUnknownProperty() {
            options.addOption("a", "alpha", false, "Alpha option");
            
            Properties props = new Properties();
            props.setProperty("unknown", "value");
            
            String[] args = {};
            
            assertThrows(UnrecognizedOptionException.class, () -> {
                parser.parse(options, args, props);
            });
        }
    }

    @Nested
    @DisplayName("Quote Stripping Tests")
    class QuoteStrippingTests {
        
        @Test
        @DisplayName("Should strip quotes when configured")
        void shouldStripQuotesWhenConfigured() throws ParseException {
            DefaultParser quoteParser = DefaultParser.builder()
                    .setStripLeadingAndTrailingQuotes(true)
                    .get();
                    
            options.addOption("f", "file", true, "File option");
            String[] args = {"-f", "\"filename.txt\""};
            
            CommandLine cmd = quoteParser.parse(options, args);
            
            assertEquals("filename.txt", cmd.getOptionValue("f"));
        }
        
        @Test
        @DisplayName("Should not strip quotes when disabled")
        void shouldNotStripQuotesWhenDisabled() throws ParseException {
            DefaultParser noQuoteParser = DefaultParser.builder()
                    .setStripLeadingAndTrailingQuotes(false)
                    .get();
                    
            options.addOption("f", "file", true, "File option");
            String[] args = {"-f", "\"filename.txt\""};
            
            CommandLine cmd = noQuoteParser.parse(options, args);
            
            assertEquals("\"filename.txt\"", cmd.getOptionValue("f"));
        }
        
        @Test
        @DisplayName("Should handle quotes with equals sign")
        void shouldHandleQuotesWithEqualsSign() throws ParseException {
            DefaultParser quoteParser = DefaultParser.builder()
                    .setStripLeadingAndTrailingQuotes(true)
                    .get();
                    
            options.addOption("f", "file", true, "File option");
            String[] args = {"--file=\"filename.txt\""};
            
            CommandLine cmd = quoteParser.parse(options, args);
            
            assertEquals("filename.txt", cmd.getOptionValue("file"));
        }
    }

    @Nested
    @DisplayName("Java Property Tests")
    class JavaPropertyTests {
        
        @Test
        @DisplayName("Should parse Java property style options with unlimited args")
        void shouldParseJavaPropertyStyleOptionsWithUnlimitedArgs() throws ParseException {
            options.addOption(Option.builder("D").hasArgs().desc("System property").build());
            String[] args = {"-Dkey=value"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertTrue(cmd.hasOption("D"));
        }
        
        @Test
        @DisplayName("Should parse multiple Java properties with unlimited args")
        void shouldParseMultipleJavaPropertiesWithUnlimitedArgs() throws ParseException {
            options.addOption(Option.builder("D").hasArgs().desc("System property").build());
            String[] args = {"-Dkey1=value1", "-Dkey2=value2"};
            
            CommandLine cmd = parser.parse(options, args);
            
            String[] values = cmd.getOptionValues("D");
            assertNotNull(values);
        }
        
        @Test
        @DisplayName("Should parse Java property without value")
        void shouldParseJavaPropertyWithoutValue() throws ParseException {
            options.addOption(Option.builder("D").hasArgs().desc("System property").build());
            String[] args = {"-Dflag"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertTrue(cmd.hasOption("D"));
            assertEquals("flag", cmd.getOptionValue("D"));
        }
    }

    @Nested
    @DisplayName("Negative Number Tests")
    class NegativeNumberTests {
        
        @Test
        @DisplayName("Should treat negative numbers as arguments")
        void shouldTreatNegativeNumbersAsArguments() throws ParseException {
            options.addOption("n", "number", true, "Number option");
            String[] args = {"-n", "-123"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertEquals("-123", cmd.getOptionValue("n"));
        }
        
        @Test
        @DisplayName("Should treat negative decimal numbers as arguments")
        void shouldTreatNegativeDecimalNumbersAsArguments() throws ParseException {
            options.addOption("n", "number", true, "Number option");
            String[] args = {"-n", "-123.45"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertEquals("-123.45", cmd.getOptionValue("n"));
        }
    }

    @Nested
    @DisplayName("Option Group Tests")
    class OptionGroupTests {
        
        @Test
        @DisplayName("Should parse option from required group")
        void shouldParseOptionFromRequiredGroup() throws ParseException {
            OptionGroup group = new OptionGroup();
            group.setRequired(true);
            group.addOption(Option.builder("a").desc("Option A").build());
            group.addOption(Option.builder("b").desc("Option B").build());
            options.addOptionGroup(group);
            String[] args = {"-a"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertTrue(cmd.hasOption("a"));
            assertFalse(cmd.hasOption("b"));
        }
        
        @Test
        @DisplayName("Should handle required option group selection")
        void shouldHandleRequiredOptionGroupSelection() throws ParseException {
            OptionGroup group = new OptionGroup();
            group.setRequired(true);
            group.addOption(Option.builder("a").desc("Option A").build());
            group.addOption(Option.builder("b").desc("Option B").build());
            options.addOptionGroup(group);
            String[] args = {"-a"};
            
            CommandLine cmd = parser.parse(options, args);
            
            // Should not throw MissingOptionException since group is satisfied
            assertTrue(cmd.hasOption("a"));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle single dash as argument")
        void shouldHandleSingleDashAsArgument() throws ParseException {
            options.addOption("f", "file", true, "File option");
            String[] args = {"-f", "-"};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertEquals("-", cmd.getOptionValue("f"));
        }
        
        @Test
        @DisplayName("Should handle empty argument array")
        void shouldHandleEmptyArgumentArray() throws ParseException {
            options.addOption("a", "alpha", false, "Alpha option");
            String[] args = {};
            
            CommandLine cmd = parser.parse(options, args);
            
            assertFalse(cmd.hasOption("a"));
            assertTrue(cmd.getArgList().isEmpty());
        }
        
        @Test
        @DisplayName("Should handle null argument array")
        void shouldHandleNullArgumentArray() throws ParseException {
            options.addOption("a", "alpha", false, "Alpha option");
            
            CommandLine cmd = parser.parse(options, (String[]) null);
            
            assertFalse(cmd.hasOption("a"));
            assertTrue(cmd.getArgList().isEmpty());
        }
        
        @Test
        @DisplayName("Should handle null properties")
        void shouldHandleNullProperties() throws ParseException {
            options.addOption("a", "alpha", false, "Alpha option");
            String[] args = {"-a"};
            
            CommandLine cmd = parser.parse(options, args, (Properties) null);
            
            assertTrue(cmd.hasOption("a"));
        }
        
        @Test
        @DisplayName("Should throw NullPointerException for null options")
        void shouldThrowNullPointerExceptionForNullOptions() {
            String[] args = {"-a"};
            
            assertThrows(NullPointerException.class, () -> {
                parser.parse(null, args);
            });
        }
    }

    @Nested
    @DisplayName("Deprecated Handler Tests")
    class DeprecatedHandlerTests {
        
        @Test
        @DisplayName("Should call deprecated handler for deprecated options")
        void shouldCallDeprecatedHandlerForDeprecatedOptions() throws ParseException {
            Consumer<Option> handler = mock(Consumer.class);
            DefaultParser customParser = DefaultParser.builder()
                    .setDeprecatedHandler(handler)
                    .get();
                    
            Option deprecatedOption = Option.builder("d")
                    .longOpt("deprecated")
                    .deprecated()
                    .build();
            options.addOption(deprecatedOption);
            String[] args = {"-d"};
            
            CommandLine cmd = customParser.parse(options, args);
            
            assertTrue(cmd.hasOption("d"));
            verify(handler, times(1)).accept(any(Option.class));
        }
    }

    @Nested
    @DisplayName("Backward Compatibility Tests")
    class BackwardCompatibilityTests {
        
        @Test
        @DisplayName("Should parse with stopAtNonOption true")
        void shouldParseWithStopAtNonOptionTrue() throws ParseException {
            options.addOption("a", "alpha", false, "Alpha option");
            String[] args = {"-a", "non-option", "-b"};
            
            CommandLine cmd = parser.parse(options, args, true);
            
            assertTrue(cmd.hasOption("a"));
            assertEquals(2, cmd.getArgList().size());
        }
        
        @Test
        @DisplayName("Should parse with stopAtNonOption false")
        void shouldParseWithStopAtNonOptionFalse() throws ParseException {
            options.addOption("a", "alpha", false, "Alpha option");
            String[] args = {"-a", "non-option"};
            
            CommandLine cmd = parser.parse(options, args, false);
            
            assertTrue(cmd.hasOption("a"));
            assertEquals(1, cmd.getArgList().size());
        }
        
        @Test
        @DisplayName("Should parse with properties and stopAtNonOption")
        void shouldParseWithPropertiesAndStopAtNonOption() throws ParseException {
            options.addOption("d", "debug", false, "Debug option");
            
            Properties props = new Properties();
            props.setProperty("debug", "true");
            
            String[] args = {"non-option"};
            CommandLine cmd = parser.parse(options, args, props, true);
            
            assertTrue(cmd.hasOption("debug"));
            assertEquals(1, cmd.getArgList().size());
        }
    }

    @Test
    @DisplayName("Should handle complex command line")
    void shouldHandleComplexCommandLine() throws ParseException {
        options.addOption("v", "verbose", false, "Verbose output");
        options.addOption("f", "file", true, "Input file");
        options.addOption("o", "output", true, "Output file");
        options.addOption(Option.builder("D").hasArgs().desc("System properties").build());
        
        OptionGroup formatGroup = new OptionGroup();
        formatGroup.addOption(Option.builder("j").longOpt("json").desc("JSON format").build());
        formatGroup.addOption(Option.builder("x").longOpt("xml").desc("XML format").build());
        options.addOptionGroup(formatGroup);
        
        String[] args = {
            "-v",
            "-f", "input.txt",
            "--output=output.json",
            "-D", "jvm.param=value",
            "--json",
            "positional1",
            "positional2"
        };
        
        CommandLine cmd = parser.parse(options, args, true);
        
        assertTrue(cmd.hasOption("verbose"));
        assertEquals("input.txt", cmd.getOptionValue("file"));
        assertEquals("output.json", cmd.getOptionValue("output"));
        assertEquals("jvm.param=value", cmd.getOptionValue("D"));
        assertTrue(cmd.hasOption("json"));
        assertFalse(cmd.hasOption("xml"));
        assertEquals(2, cmd.getArgList().size());
        assertEquals("positional1", cmd.getArgList().get(0));
        assertEquals("positional2", cmd.getArgList().get(1));
    }
}