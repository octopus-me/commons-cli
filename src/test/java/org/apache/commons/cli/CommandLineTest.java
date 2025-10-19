package org.apache.commons.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

class CommandLineTest {

    private CommandLine commandLine;
    private Option optionA;
    private Option optionB;
    private Option optionWithArgs;

    @BeforeEach
    void setUp() {
        commandLine = new CommandLine();
        optionA = Option.builder("a").longOpt("alpha").desc("Alpha option").build();
        optionB = Option.builder("b").longOpt("beta").desc("Beta option").build();
        optionWithArgs = Option.builder("f").longOpt("file").hasArg().desc("File option").build();
    }

    @Nested
    @DisplayName("Constructor and Builder Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create empty CommandLine with default constructor")
        void shouldCreateEmptyCommandLineWithDefaultConstructor() {
            assertNotNull(commandLine);
            assertTrue(commandLine.getArgList().isEmpty());
            assertEquals(0, commandLine.getArgs().length);
            assertEquals(0, commandLine.getOptions().length);
        }
        
        @Test
        @DisplayName("Should create CommandLine using Builder pattern")
        void shouldCreateCommandLineUsingBuilderPattern() {
            CommandLine built = CommandLine.builder()
                    .addArg("arg1")
                    .addArg("arg2")
                    .addOption(optionA)
                    .build();
            
            assertNotNull(built);
            assertEquals(2, built.getArgList().size());
            assertEquals(1, built.getOptions().length);
        }
        
        @Test
        @DisplayName("Should create CommandLine with deprecated handler")
        void shouldCreateCommandLineWithDeprecatedHandler() {
            CommandLine.Builder builder = CommandLine.builder();
            CommandLine cmd = builder.build();
            
            assertNotNull(cmd);
        }
        
        @Test
        @DisplayName("Should handle null arguments in builder")
        void shouldHandleNullArgumentsInBuilder() {
            CommandLine built = CommandLine.builder()
                    .addArg(null)
                    .addOption(null)
                    .build();
            
            assertNotNull(built);
            assertTrue(built.getArgList().isEmpty());
            assertEquals(0, built.getOptions().length);
        }
    }

    @Nested
    @DisplayName("Add Methods Tests")
    class AddMethodsTests {
        
        @Test
        @DisplayName("Should add argument to command line")
        void shouldAddArgumentToCommandLine() {
            commandLine.addArg("test");
            
            assertEquals(1, commandLine.getArgList().size());
            assertEquals("test", commandLine.getArgList().get(0));
            assertEquals("test", commandLine.getArgs()[0]);
        }
        
        @Test
        @DisplayName("Should add option to command line")
        void shouldAddOptionToCommandLine() {
            commandLine.addOption(optionA);
            
            assertEquals(1, commandLine.getOptions().length);
            assertEquals(optionA, commandLine.getOptions()[0]);
            assertTrue(commandLine.hasOption("a"));
            assertTrue(commandLine.hasOption("alpha"));
        }
        
        @Test
        @DisplayName("Should handle null argument in addArg")
        void shouldHandleNullArgumentInAddArg() {
            commandLine.addArg(null);
            
            assertTrue(commandLine.getArgList().isEmpty());
        }
        
        @Test
        @DisplayName("Should handle null option in addOption")
        void shouldHandleNullOptionInAddOption() {
            commandLine.addOption(null);
            
            assertEquals(0, commandLine.getOptions().length);
        }
    }

    @Nested
    @DisplayName("Option Presence Tests")
    class OptionPresenceTests {
        
        @BeforeEach
        void setUp() {
            commandLine.addOption(optionA);
            commandLine.addOption(optionWithArgs);
        }
        
        @Test
        @DisplayName("Should check option presence by char")
        void shouldCheckOptionPresenceByChar() {
            assertTrue(commandLine.hasOption('a'));
            assertFalse(commandLine.hasOption('x'));
        }
        
        @Test
        @DisplayName("Should check option presence by string")
        void shouldCheckOptionPresenceByString() {
            assertTrue(commandLine.hasOption("a"));
            assertTrue(commandLine.hasOption("alpha"));
            assertFalse(commandLine.hasOption("nonexistent"));
        }
        
        @Test
        @DisplayName("Should check option presence by Option object")
        void shouldCheckOptionPresenceByOptionObject() {
            assertTrue(commandLine.hasOption(optionA));
            assertFalse(commandLine.hasOption(optionB));
        }
        
        @Test
        @DisplayName("Should handle null option name")
        void shouldHandleNullOptionName() {
            assertFalse(commandLine.hasOption((String) null));
        }
        
        @Test
        @DisplayName("Should handle null option object")
        void shouldHandleNullOptionObject() {
            assertFalse(commandLine.hasOption((Option) null));
        }
    }

    @Nested
    @DisplayName("Option Value Retrieval Tests")
    class OptionValueRetrievalTests {
        
        @BeforeEach
        void setUp() {
            Option fileOption = Option.builder("f").longOpt("file").hasArgs().desc("File option").build();
            fileOption.processValue("file1.txt");
            fileOption.processValue("file2.txt");
            
            commandLine.addOption(fileOption);
            
            Option singleOption = Option.builder("s").longOpt("single").hasArg().desc("Single option").build();
            singleOption.processValue("value");
            commandLine.addOption(singleOption);
        }
        
        @Test
        @DisplayName("Should get single option value by char")
        void shouldGetSingleOptionValueByChar() {
            assertEquals("value", commandLine.getOptionValue('s'));
        }
        
        @Test
        @DisplayName("Should get single option value by string")
        void shouldGetSingleOptionValueByString() {
            assertEquals("value", commandLine.getOptionValue("s"));
            assertEquals("value", commandLine.getOptionValue("single"));
        }
        
        @Test
        @DisplayName("Should return null for non-existent option value")
        void shouldReturnNullForNonExistentOptionValue() {
            assertNull(commandLine.getOptionValue("nonexistent"));
        }
        
        @Test
        @DisplayName("Should get option values array by char")
        void shouldGetOptionValuesArrayByChar() {
            String[] values = commandLine.getOptionValues('f');
            
            assertNotNull(values);
            assertEquals(2, values.length);
            assertEquals("file1.txt", values[0]);
            assertEquals("file2.txt", values[1]);
        }
        
        @Test
        @DisplayName("Should get option values array by string")
        void shouldGetOptionValuesArrayByString() {
            String[] values = commandLine.getOptionValues("file");
            
            assertNotNull(values);
            assertEquals(2, values.length);
            assertEquals("file1.txt", values[0]);
            assertEquals("file2.txt", values[1]);
        }
        
        @Test
        @DisplayName("Should return null for non-existent option values")
        void shouldReturnNullForNonExistentOptionValues() {
            assertNull(commandLine.getOptionValues("nonexistent"));
        }
        
        @Test
        @DisplayName("Should return null for null option values")
        void shouldReturnNullForNullOptionValues() {
            assertNull(commandLine.getOptionValues((Option) null));
        }
    }

    @Nested
    @DisplayName("Default Value Tests")
    class DefaultValueTests {
        
        @Test
        @DisplayName("Should return default value for non-existent option")
        void shouldReturnDefaultValueForNonExistentOption() {
            assertEquals("default", commandLine.getOptionValue('x', "default"));
            assertEquals("default", commandLine.getOptionValue("nonexistent", "default"));
        }
        
        @Test
        @DisplayName("Should return default value from supplier")
        void shouldReturnDefaultValueFromSupplier() {
            Supplier<String> supplier = () -> "supplied";
            assertEquals("supplied", commandLine.getOptionValue('x', supplier));
            assertEquals("supplied", commandLine.getOptionValue("nonexistent", supplier));
        }
        
        @Test
        @DisplayName("Should return option value over default value")
        void shouldReturnOptionValueOverDefaultValue() {
            Option option = Option.builder("o").hasArg().desc("Option").build();
            option.processValue("actual");
            commandLine.addOption(option);
            
            assertEquals("actual", commandLine.getOptionValue('o', "default"));
            assertEquals("actual", commandLine.getOptionValue("o", "default"));
        }
        
        @Test
        @DisplayName("Should handle null default value supplier")
        void shouldHandleNullDefaultValueSupplier() {
            assertNull(commandLine.getOptionValue('x', (Supplier<String>) null));
        }
    }

    @Nested
    @DisplayName("Option Group Tests")
    class OptionGroupTests {
        
        private OptionGroup group;
        
        @BeforeEach
        void setUp() {
            group = new OptionGroup();
            Option option1 = Option.builder("1").hasArg().desc("Option 1").build();
            Option option2 = Option.builder("2").hasArg().desc("Option 2").build();
            group.addOption(option1).addOption(option2);
            
            option1.processValue("value1");
            commandLine.addOption(option1);
            try {
                group.setSelected(option1);
            } catch (AlreadySelectedException e) {
                // Should not happen in test setup
            }
        }
        
        @Test
        @DisplayName("Should check option group presence")
        void shouldCheckOptionGroupPresence() {
            assertTrue(commandLine.hasOption(group));
        }
        
        @Test
        @DisplayName("Should get option value from group")
        void shouldGetOptionValueFromGroup() {
            assertEquals("value1", commandLine.getOptionValue(group));
        }
        
        @Test
        @DisplayName("Should get option values from group")
        void shouldGetOptionValuesFromGroup() {
            String[] values = commandLine.getOptionValues(group);
            
            assertNotNull(values);
            assertEquals(1, values.length);
            assertEquals("value1", values[0]);
        }
        
        @Test
        @DisplayName("Should return default value for non-selected group")
        void shouldReturnDefaultValueForNonSelectedGroup() {
            OptionGroup emptyGroup = new OptionGroup();
            assertEquals("default", commandLine.getOptionValue(emptyGroup, "default"));
        }
        
        @Test
        @DisplayName("Should return null for null option group")
        void shouldReturnNullForNullOptionGroup() {
            assertNull(commandLine.getOptionValue((OptionGroup) null));
            assertNull(commandLine.getOptionValues((OptionGroup) null));
            assertFalse(commandLine.hasOption((OptionGroup) null));
        }
    }

    @Nested
    @DisplayName("Properties Tests")
    class PropertiesTests {
        
        @Test
        @DisplayName("Should get properties from option with key-value pairs")
        void shouldGetPropertiesFromOptionWithKeyValuePairs() {
            Option propOption = Option.builder("D").hasArgs().desc("Properties").build();
            propOption.processValue("key1");
            propOption.processValue("value1");
            propOption.processValue("key2");
            propOption.processValue("value2");
            commandLine.addOption(propOption);
            
            Properties props = commandLine.getOptionProperties("D");
            
            assertNotNull(props);
            assertEquals("value1", props.getProperty("key1"));
            assertEquals("value2", props.getProperty("key2"));
        }
        
        @Test
        @DisplayName("Should get properties from option with boolean flag")
        void shouldGetPropertiesFromOptionWithBooleanFlag() {
            Option propOption = Option.builder("D").hasArgs().desc("Properties").build();
            propOption.processValue("flag");
            commandLine.addOption(propOption);
            
            Properties props = commandLine.getOptionProperties("D");
            
            assertNotNull(props);
            assertEquals("true", props.getProperty("flag"));
        }
        
        @Test
        @DisplayName("Should get properties from Option object")
        void shouldGetPropertiesFromOptionObject() {
            Option propOption = Option.builder("D").hasArgs().desc("Properties").build();
            propOption.processValue("key");
            propOption.processValue("value");
            commandLine.addOption(propOption);
            
            Properties props = commandLine.getOptionProperties(propOption);
            
            assertNotNull(props);
            assertEquals("value", props.getProperty("key"));
        }
        
        @Test
        @DisplayName("Should return empty properties for non-existent option")
        void shouldReturnEmptyPropertiesForNonExistentOption() {
            Properties props = commandLine.getOptionProperties("nonexistent");
            
            assertNotNull(props);
            assertTrue(props.isEmpty());
        }
        
        @Test
        @DisplayName("Should return empty properties for null option")
        void shouldReturnEmptyPropertiesForNullOption() {
            Properties props = commandLine.getOptionProperties((Option) null);
            
            assertNotNull(props);
            assertTrue(props.isEmpty());
        }
    }

    @Nested
    @DisplayName("Parsed Value Tests")
    class ParsedValueTests {
        
        @Test
        @DisplayName("Should get parsed option value with converter")
        void shouldGetParsedOptionValueWithConverter() throws ParseException {
            Option intOption = Option.builder("n").hasArg().type(Integer.class).desc("Number").build();
            intOption.processValue("42");
            commandLine.addOption(intOption);
            
            Integer result = commandLine.getParsedOptionValue("n");
            
            assertNotNull(result);
            assertEquals(Integer.valueOf(42), result);
        }
        
        @Test
        @DisplayName("Should get parsed option value with default")
        void shouldGetParsedOptionValueWithDefault() throws ParseException {
            Integer result = commandLine.getParsedOptionValue("nonexistent", 100);
            
            assertNotNull(result);
            assertEquals(Integer.valueOf(100), result);
        }
        
        @Test
        @DisplayName("Should get parsed option value with supplier")
        void shouldGetParsedOptionValueWithSupplier() throws ParseException {
            Supplier<Integer> supplier = () -> 200;
            Integer result = commandLine.getParsedOptionValue("nonexistent", supplier);
            
            assertNotNull(result);
            assertEquals(Integer.valueOf(200), result);
        }
        
        @Test
        @DisplayName("Should throw ParseException for conversion error")
        void shouldThrowParseExceptionForConversionError() {
            Option intOption = Option.builder("n").hasArg().type(Integer.class).desc("Number").build();
            intOption.processValue("not-a-number");
            commandLine.addOption(intOption);
            
            assertThrows(ParseException.class, () -> {
                commandLine.getParsedOptionValue("n");
            });
        }
        
        @Test
        @DisplayName("Should get parsed option values array")
        void shouldGetParsedOptionValuesArray() throws ParseException {
            Option intOption = Option.builder("n").hasArgs().type(Integer.class).desc("Numbers").build();
            intOption.processValue("1");
            intOption.processValue("2");
            intOption.processValue("3");
            commandLine.addOption(intOption);
            
            Integer[] result = commandLine.getParsedOptionValues("n");
            
            assertNotNull(result);
            assertEquals(3, result.length);
            assertEquals(Integer.valueOf(1), result[0]);
            assertEquals(Integer.valueOf(2), result[1]);
            assertEquals(Integer.valueOf(3), result[2]);
        }
        
        @Test
        @DisplayName("Should get parsed option values array with default")
        void shouldGetParsedOptionValuesArrayWithDefault() throws ParseException {
            Integer[] defaultValue = {10, 20};
            Integer[] result = commandLine.getParsedOptionValues("nonexistent", defaultValue);
            
            assertNotNull(result);
            assertSame(defaultValue, result);
        }
    }

    @Nested
    @DisplayName("Deprecated Option Tests")
    class DeprecatedOptionTests {
        
        @Test
        @DisplayName("Should handle deprecated option with default handler")
        void shouldHandleDeprecatedOptionWithDefaultHandler() {
            Option deprecatedOption = Option.builder("d").longOpt("deprecated").deprecated().desc("Deprecated option").build();
            commandLine.addOption(deprecatedOption);
            
            // Should not throw exception
            assertTrue(commandLine.hasOption("d"));
        }
        
        @Test
        @DisplayName("Should call custom deprecated handler")
        void shouldCallCustomDeprecatedHandler() {
            @SuppressWarnings("unchecked")
            Consumer<Option> handler = mock(Consumer.class);
            CommandLine customCmd = CommandLine.builder()
                    .setDeprecatedHandler(handler)
                    .addOption(Option.builder("d").deprecated().build())
                    .build();
            
            customCmd.hasOption("d");
            
            verify(handler, times(1)).accept(any(Option.class));
        }
    }

    @Nested
    @DisplayName("Iterator Tests")
    class IteratorTests {
        
        @Test
        @DisplayName("Should iterate over options")
        void shouldIterateOverOptions() {
            commandLine.addOption(optionA);
            commandLine.addOption(optionB);
            
            Iterator<Option> iterator = commandLine.iterator();
            assertNotNull(iterator);
            
            int count = 0;
            while (iterator.hasNext()) {
                iterator.next();
                count++;
            }
            
            assertEquals(2, count);
        }
        
        @Test
        @DisplayName("Should handle empty iterator")
        void shouldHandleEmptyIterator() {
            Iterator<Option> iterator = commandLine.iterator();
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());
        }
    }

    @Nested
    @DisplayName("Argument List Tests")
    class ArgumentListTests {
        
        @Test
        @DisplayName("Should get argument list")
        void shouldGetArgumentList() {
            commandLine.addArg("arg1");
            commandLine.addArg("arg2");
            commandLine.addArg("arg3");
            
            List<String> args = commandLine.getArgList();
            assertNotNull(args);
            assertEquals(3, args.size());
            assertEquals("arg1", args.get(0));
            assertEquals("arg2", args.get(1));
            assertEquals("arg3", args.get(2));
        }
        
        @Test
        @DisplayName("Should get arguments as array")
        void shouldGetArgumentsAsArray() {
            commandLine.addArg("arg1");
            commandLine.addArg("arg2");
            
            String[] args = commandLine.getArgs();
            assertNotNull(args);
            assertEquals(2, args.length);
            assertEquals("arg1", args[0]);
            assertEquals("arg2", args[1]);
        }
        
        @Test
        @DisplayName("Should return empty array for no arguments")
        void shouldReturnEmptyArrayForNoArguments() {
            String[] args = commandLine.getArgs();
            assertNotNull(args);
            assertEquals(0, args.length);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle deprecated getOptionObject methods")
        void shouldHandleDeprecatedGetOptionObjectMethods() {
            Option intOption = Option.builder("n").hasArg().type(Integer.class).desc("Number").build();
            intOption.processValue("42");
            commandLine.addOption(intOption);
            
            // These methods are deprecated but should still work
            Object result1 = commandLine.getOptionObject('n');
            Object result2 = commandLine.getOptionObject("n");
            
            assertNotNull(result1);
            assertNotNull(result2);
            assertEquals(42, result1);
            assertEquals(42, result2);
        }
        
        @Test
        @DisplayName("Should handle conversion error in getOptionObject")
        void shouldHandleConversionErrorInGetOptionObject() {
            Option intOption = Option.builder("n").hasArg().type(Integer.class).desc("Number").build();
            intOption.processValue("not-a-number");
            commandLine.addOption(intOption);
            
            // Should return null and print error message
            Object result = commandLine.getOptionObject("n");
            assertNull(result);
        }
        
        @Test
        @DisplayName("Should resolve option with hyphens")
        void shouldResolveOptionWithHyphens() {
            commandLine.addOption(optionA);
            
            assertTrue(commandLine.hasOption("-a"));
            assertTrue(commandLine.hasOption("--alpha"));
        }
        
        @Test
        @DisplayName("Should handle empty option name")
        void shouldHandleEmptyOptionName() {
            assertFalse(commandLine.hasOption(""));
        }
        
        @Test
        @DisplayName("Should handle option with same short and long name")
        void shouldHandleOptionWithSameShortAndLongName() {
            Option sameOption = Option.builder("a").longOpt("a").desc("Same name").build();
            commandLine.addOption(sameOption);
            
            assertTrue(commandLine.hasOption("a"));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should handle complex command line scenario")
        void shouldHandleComplexCommandLineScenario() throws ParseException {
            // Setup options with various types
            Option stringOption = Option.builder("s").hasArg().desc("String option").build();
            stringOption.processValue("text");
            
            Option intOption = Option.builder("i").hasArg().type(Integer.class).desc("Integer option").build();
            intOption.processValue("123");
            
            Option multiOption = Option.builder("m").hasArgs().desc("Multi-value option").build();
            multiOption.processValue("val1");
            multiOption.processValue("val2");
            
            Option propOption = Option.builder("D").hasArgs().desc("Properties").build();
            propOption.processValue("key1");
            propOption.processValue("value1");
            propOption.processValue("key2");
            propOption.processValue("value2");
            
            // Add options to command line
            commandLine.addOption(stringOption);
            commandLine.addOption(intOption);
            commandLine.addOption(multiOption);
            commandLine.addOption(propOption);
            
            // Add arguments
            commandLine.addArg("arg1");
            commandLine.addArg("arg2");
            
            // Verify all functionality
            assertTrue(commandLine.hasOption("s"));
            assertEquals("text", commandLine.getOptionValue("s"));
            
            assertTrue(commandLine.hasOption("i"));
            assertEquals(Integer.valueOf(123), commandLine.getParsedOptionValue("i"));
            
            assertTrue(commandLine.hasOption("m"));
            String[] multiValues = commandLine.getOptionValues("m");
            assertNotNull(multiValues);
            assertEquals(2, multiValues.length);
            
            assertTrue(commandLine.hasOption("D"));
            Properties props = commandLine.getOptionProperties("D");
            assertEquals("value1", props.getProperty("key1"));
            assertEquals("value2", props.getProperty("key2"));
            
            assertEquals(2, commandLine.getArgList().size());
            assertEquals("arg1", commandLine.getArgList().get(0));
            assertEquals("arg2", commandLine.getArgList().get(1));
            
            assertEquals(4, commandLine.getOptions().length);
        }
        
        @Test
        @DisplayName("Should handle option group with parsed values")
        void shouldHandleOptionGroupWithParsedValues() throws ParseException {
            OptionGroup group = new OptionGroup();
            Option intOption = Option.builder("1").hasArg().type(Integer.class).desc("Option 1").build();
            Option stringOption = Option.builder("2").hasArg().desc("Option 2").build();
            group.addOption(intOption).addOption(stringOption);
            
            intOption.processValue("999");
            commandLine.addOption(intOption);
            try {
                group.setSelected(intOption);
            } catch (AlreadySelectedException e) {
                // Should not happen in test setup
            }
            
            assertTrue(commandLine.hasOption(group));
            assertEquals(Integer.valueOf(999), commandLine.getParsedOptionValue(group));
            
            Integer[] values = commandLine.getParsedOptionValues(group);
            assertNotNull(values);
            assertEquals(1, values.length);
            assertEquals(Integer.valueOf(999), values[0]);
        }
    }
}