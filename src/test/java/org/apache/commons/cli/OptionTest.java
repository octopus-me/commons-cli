package org.apache.commons.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class OptionTest {

    private Option option;

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create Option with short name only")
        void shouldCreateOptionWithShortNameOnly() {
            option = new Option("a", "description");
            
            assertEquals("a", option.getOpt());
            assertNull(option.getLongOpt());
            assertFalse(option.hasArg());
            assertEquals("description", option.getDescription());
        }
        
        @Test
        @DisplayName("Should create Option with short name, long name, hasArg and description")
        void shouldCreateOptionWithAllParameters() {
            option = new Option("a", "arg", true, "description");
            
            assertEquals("a", option.getOpt());
            assertEquals("arg", option.getLongOpt());
            assertTrue(option.hasArg());
            assertEquals("description", option.getDescription());
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException for invalid short option")
        void shouldThrowExceptionForInvalidShortOption() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Option("invalid!", "description");
            });
        }
        
        @Test
        @DisplayName("Should create Option using Builder pattern")
        void shouldCreateOptionUsingBuilder() {
            option = Option.builder("a")
                    .longOpt("arg")
                    .required(true)
                    .hasArg()
                    .desc("description")
                    .build();
            
            assertEquals("a", option.getOpt());
            assertEquals("arg", option.getLongOpt());
            assertTrue(option.hasArg());
            assertTrue(option.isRequired());
            assertEquals("description", option.getDescription());
        }
        
        @Test
        @DisplayName("Should throw IllegalStateException when neither opt nor longOpt is specified in Builder")
        void shouldThrowExceptionWhenNoOptionInBuilder() {
            Option.Builder builder = Option.builder();
            
            assertThrows(IllegalStateException.class, builder::build);
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {
        
        @BeforeEach
        void setUp() {
            option = new Option("a", "arg", true, "description");
        }
        
        @Test
        @DisplayName("Should get and set arg name")
        void shouldGetAndSetArgName() {
            assertFalse(option.hasArgName());
            assertNull(option.getArgName());
            
            option.setArgName("argument");
            
            assertTrue(option.hasArgName());
            assertEquals("argument", option.getArgName());
        }
        
        @Test
        @DisplayName("Should get and set description")
        void shouldGetAndSetDescription() {
            assertEquals("description", option.getDescription());
            
            option.setDescription("new description");
            
            assertEquals("new description", option.getDescription());
        }
        
        @Test
        @DisplayName("Should get and set long option")
        void shouldGetAndSetLongOpt() {
            assertEquals("arg", option.getLongOpt());
            assertTrue(option.hasLongOpt());
            
            option.setLongOpt("newarg");
            
            assertEquals("newarg", option.getLongOpt());
        }
        
        @Test
        @DisplayName("Should handle null long option")
        void shouldHandleNullLongOpt() {
            option = new Option("a", false, "description");
            
            assertNull(option.getLongOpt());
            assertFalse(option.hasLongOpt());
        }
        
        @Test
        @DisplayName("Should get and set required flag")
        void shouldGetAndSetRequired() {
            assertFalse(option.isRequired());
            
            option.setRequired(true);
            
            assertTrue(option.isRequired());
        }
        
        @Test
        @DisplayName("Should get and set optional argument flag")
        void shouldGetAndSetOptionalArg() {
            assertFalse(option.hasOptionalArg());
            
            option.setOptionalArg(true);
            
            assertTrue(option.hasOptionalArg());
        }
        
        @Test
        @DisplayName("Should get and set value separator")
        void shouldGetAndSetValueSeparator() {
            assertFalse(option.hasValueSeparator());
            assertEquals(0, option.getValueSeparator());
            
            option.setValueSeparator('=');
            
            assertTrue(option.hasValueSeparator());
            assertEquals('=', option.getValueSeparator());
        }
        
        @Test
        @DisplayName("Should get and set type using Class")
        void shouldGetAndSetTypeWithClass() {
            assertEquals(String.class, option.getType());
            
            option.setType(Integer.class);
            
            assertEquals(Integer.class, option.getType());
        }
        
        @Test
        @DisplayName("Should get and set args count")
        void shouldGetAndSetArgsCount() {
            assertEquals(1, option.getArgs());
            
            option.setArgs(3);
            
            assertEquals(3, option.getArgs());
        }
        
        @Test
        @DisplayName("Should get key based on available options")
        void shouldGetKey() {
            assertEquals("a", option.getKey());
            
            Option longOnlyOption = Option.builder()
                    .longOpt("longonly")
                    .build();
            
            assertEquals("longonly", longOnlyOption.getKey());
        }
        
        @Test
        @DisplayName("Should get ID for single character option")
        void shouldGetId() {
            assertEquals('a', option.getId());
        }
    }

    @Nested
    @DisplayName("Argument Handling Tests")
    class ArgumentHandlingTests {
        
        @BeforeEach
        void setUp() {
            option = new Option("a", true, "description");
        }
        
        @Test
        @DisplayName("Should accept argument when hasArg is true")
        void shouldAcceptArgumentWhenHasArg() {
            assertTrue(option.hasArg());
            assertTrue(option.acceptsArg());
        }
        
        @Test
        @DisplayName("Should not accept argument when hasArg is false")
        void shouldNotAcceptArgumentWhenHasNoArg() {
            option = new Option("a", false, "description");
            
            assertFalse(option.hasArg());
            assertFalse(option.acceptsArg());
        }
        
        @Test
        @DisplayName("Should accept multiple arguments when hasArgs is true")
        void shouldAcceptMultipleArguments() {
            option = Option.builder("a").hasArgs().build();
            
            assertTrue(option.hasArgs());
            assertTrue(option.acceptsArg());
            assertEquals(Option.UNLIMITED_VALUES, option.getArgs());
        }
        
        @Test
        @DisplayName("Should process single value correctly")
        void shouldProcessSingleValue() {
            option.processValue("test");
            
            assertEquals("test", option.getValue());
            assertEquals(1, option.getValuesList().size());
        }
        
        @Test
        @DisplayName("Should process multiple values with separator")
        void shouldProcessMultipleValuesWithSeparator() {
            option.setValueSeparator(',');
            option.setArgs(3);
            
            option.processValue("val1,val2,val3");
            
            assertEquals("val1", option.getValue(0));
            assertEquals("val2", option.getValue(1));
            assertEquals("val3", option.getValue(2));
            assertEquals(3, option.getValuesList().size());
        }
        
        @Test
        @DisplayName("Should throw IllegalStateException when processing value for no-args option")
        void shouldThrowExceptionWhenProcessingValueForNoArgsOption() {
            option = new Option("a", false, "description");
            
            assertThrows(IllegalStateException.class, () -> {
                option.processValue("test");
            });
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException when adding value to full list")
        void shouldThrowExceptionWhenAddingValueToFullList() {
            option.setArgs(1);
            option.processValue("first");
            
            assertThrows(IllegalArgumentException.class, () -> {
                option.processValue("second");
            });
        }
        
        @Test
        @DisplayName("Should handle unlimited values")
        void shouldHandleUnlimitedValues() {
            option = Option.builder("a").hasArgs().build();
            
            option.processValue("val1");
            option.processValue("val2");
            option.processValue("val3");
            
            assertEquals(3, option.getValuesList().size());
            assertTrue(option.acceptsArg()); // Should still accept more
        }
        
        @Test
        @DisplayName("Should clear values correctly")
        void shouldClearValues() {
            option.processValue("test");
            assertEquals(1, option.getValuesList().size());
            
            option.clearValues();
            
            assertTrue(option.getValuesList().isEmpty());
            assertNull(option.getValue());
        }
    }

    @Nested
    @DisplayName("Value Retrieval Tests")
    class ValueRetrievalTests {
        
        @BeforeEach
        void setUp() {
            option = Option.builder("a").hasArgs().build();
            option.processValue("value1");
            option.processValue("value2");
            option.processValue("value3");
        }
        
        @Test
        @DisplayName("Should get first value")
        void shouldGetFirstValue() {
            assertEquals("value1", option.getValue());
        }
        
        @Test
        @DisplayName("Should get value by index")
        void shouldGetValueByIndex() {
            assertEquals("value2", option.getValue(1));
            assertEquals("value3", option.getValue(2));
        }
        
        @Test
        @DisplayName("Should throw IndexOutOfBoundsException for invalid index")
        void shouldThrowExceptionForInvalidIndex() {
            assertThrows(IndexOutOfBoundsException.class, () -> {
                option.getValue(5);
            });
        }
        
        @Test
        @DisplayName("Should get value with default")
        void shouldGetValueWithDefault() {
            Option emptyOption = new Option("b", true, "description");
            
            assertNull(emptyOption.getValue());
            assertEquals("default", emptyOption.getValue("default"));
        }
        
        @Test
        @DisplayName("Should get values as array")
        void shouldGetValuesAsArray() {
            String[] values = option.getValues();
            
            assertNotNull(values);
            assertEquals(3, values.length);
            assertEquals("value1", values[0]);
            assertEquals("value2", values[1]);
            assertEquals("value3", values[2]);
        }
        
        @Test
        @DisplayName("Should get values as list")
        void shouldGetValuesAsList() {
            List<String> values = option.getValuesList();
            
            assertEquals(3, values.size());
            assertEquals("value1", values.get(0));
            assertEquals("value2", values.get(1));
            assertEquals("value3", values.get(2));
        }
        
        @Test
        @DisplayName("Should return null for empty values array")
        void shouldReturnNullForEmptyValuesArray() {
            Option emptyOption = new Option("b", true, "description");
            
            assertNull(emptyOption.getValues());
        }
    }

    @Nested
    @DisplayName("Deprecated Feature Tests")
    class DeprecatedTests {
        
        @Test
        @DisplayName("Should handle deprecated option")
        void shouldHandleDeprecatedOption() {
            option = Option.builder("a")
                    .deprecated()
                    .build();
            
            assertTrue(option.isDeprecated());
            assertNotNull(option.getDeprecated());
        }
        
        @Test
        @DisplayName("Should handle custom deprecated attributes")
        void shouldHandleCustomDeprecatedAttributes() {
            DeprecatedAttributes deprecated = DeprecatedAttributes.builder()
                    .setDescription("Custom deprecated message")
                    .setSince("2.0")
                    .setForRemoval(true)
                    .get();
            
            option = Option.builder("a")
                    .deprecated(deprecated)
                    .build();
            
            assertTrue(option.isDeprecated());
            assertEquals(deprecated, option.getDeprecated());
            assertEquals("Custom deprecated message", option.getDeprecated().getDescription());
            assertEquals("2.0", option.getDeprecated().getSince());
            assertTrue(option.getDeprecated().isForRemoval());
        }
        
        @Test
        @DisplayName("Should generate deprecated string representation")
        void shouldGenerateDeprecatedString() {
            option = Option.builder("a")
                    .longOpt("arg")
                    .deprecated()
                    .build();
            
            String deprecatedString = option.toDeprecatedString();
            assertNotNull(deprecatedString);
            assertTrue(deprecatedString.contains("Option 'a'"));
        }
        
        @Test
        @DisplayName("Should return empty string for non-deprecated option")
        void shouldReturnEmptyStringForNonDeprecatedOption() {
            option = new Option("a", "description");
            
            assertEquals("", option.toDeprecatedString());
        }
        
        @Test
        @DisplayName("Should handle deprecated option with custom attributes in toString")
        void shouldHandleDeprecatedWithCustomAttributesInToString() {
            DeprecatedAttributes deprecated = DeprecatedAttributes.builder()
                    .setDescription("Will be removed")
                    .setForRemoval(true)
                    .get();
            
            option = Option.builder("a")
                    .deprecated(deprecated)
                    .build();
            
            String toString = option.toString();
            assertNotNull(toString);
            assertFalse(toString.isEmpty());
        }
    }

    @Nested
    @DisplayName("Converter Tests")
    class ConverterTests {
        
        @Test
        @DisplayName("Should get and set converter")
        void shouldGetAndSetConverter() {
            // Fix: Use proper Exception type and correct method name
            Converter<Integer, NumberFormatException> converter = new Converter<Integer, NumberFormatException>() {
                @Override
                public Integer apply(String value) throws NumberFormatException {
                    return Integer.parseInt(value);
                }
            };
            
            option = Option.builder("a")
                    .converter(converter)
                    .build();
            
            assertEquals(converter, option.getConverter());
        }
        
        @Test
        @DisplayName("Should use default converter when not set")
        void shouldUseDefaultConverterWhenNotSet() {
            option = Option.builder("a")
                    .type(Integer.class)
                    .build();
            
            assertNotNull(option.getConverter());
        }
    }

    @Nested
    @DisplayName("Boundary and Edge Case Tests")
    class BoundaryTests {
        
        @Test
        @DisplayName("Should handle UNINITIALIZED arg count")
        void shouldHandleUninitializedArgCount() {
            option = new Option("a", false, "description");
            
            assertEquals(Option.UNINITIALIZED, option.getArgs());
            assertFalse(option.hasArg());
            assertFalse(option.hasArgs());
        }
        
        @Test
        @DisplayName("Should handle UNLIMITED_VALUES arg count")
        void shouldHandleUnlimitedValues() {
            option = Option.builder("a").hasArgs().build();
            
            assertEquals(Option.UNLIMITED_VALUES, option.getArgs());
            assertTrue(option.hasArg());
            assertTrue(option.hasArgs());
        }
        
        @Test
        @DisplayName("Should handle optional argument with uninitialized arg count")
        void shouldHandleOptionalArgWithUninitialized() {
            option = Option.builder("a")
                    .optionalArg(true)
                    .build();
            
            assertTrue(option.hasOptionalArg());
            assertEquals(1, option.getArgs()); // Should be set to 1 when optionalArg is true
        }
        
        @Test
        @DisplayName("Should determine if argument is required")
        void shouldDetermineIfArgumentIsRequired() {
            option = new Option("a", true, "description");
            assertTrue(option.requiresArg());
            
            option.setOptionalArg(true);
            assertFalse(option.requiresArg());
            
            Option unlimitedOption = Option.builder("b").hasArgs().build();
            assertTrue(unlimitedOption.requiresArg());
            
            unlimitedOption.processValue("test");
            assertFalse(unlimitedOption.requiresArg());
        }
    }

    @Nested
    @DisplayName("Object Method Tests")
    class ObjectMethodTests {
        
        @Test
        @DisplayName("Should implement equals correctly")
        void shouldImplementEqualsCorrectly() {
            Option option1 = new Option("a", "arg", true, "description");
            Option option2 = new Option("a", "arg", true, "different description");
            Option option3 = new Option("b", "arg", true, "description");
            Option option4 = new Option("a", "different", true, "description");
            
            assertEquals(option1, option2);
            assertNotEquals(option1, option3);
            assertNotEquals(option1, option4);
            assertNotEquals(option1, null);
            assertNotEquals(option1, "not an option");
        }
        
        @Test
        @DisplayName("Should implement hashCode correctly")
        void shouldImplementHashCodeCorrectly() {
            Option option1 = new Option("a", "arg", true, "description");
            Option option2 = new Option("a", "arg", true, "different description");
            
            assertEquals(option1.hashCode(), option2.hashCode());
        }
        
        @Test
        @DisplayName("Should clone option correctly")
        void shouldCloneOptionCorrectly() {
            option = Option.builder("a")
                    .longOpt("arg")
                    .hasArg()
                    .desc("description")
                    .build();
            
            option.processValue("test");
            
            Option cloned = (Option) option.clone();
            
            assertEquals(option.getOpt(), cloned.getOpt());
            assertEquals(option.getLongOpt(), cloned.getLongOpt());
            assertEquals(option.getDescription(), cloned.getDescription());
            
            // Clone should have separate values list
            cloned.clearValues();
            assertEquals(0, cloned.getValuesList().size());
            assertEquals(1, option.getValuesList().size());
        }
        
        @Test
        @DisplayName("Should generate meaningful toString representation")
        void shouldGenerateMeaningfulToString() {
            option = Option.builder("a")
                    .longOpt("arg")
                    .hasArg()
                    .desc("description")
                    .type(Integer.class)
                    .build();
            
            String toString = option.toString();
            
            assertTrue(toString.contains("Option a"));
            assertTrue(toString.contains("arg"));
            assertTrue(toString.contains("description"));
            assertTrue(toString.contains("Integer"));
        }
        
        @Test
        @DisplayName("Should handle deprecated option in toString")
        void shouldHandleDeprecatedInToString() {
            option = Option.builder("a")
                    .deprecated()
                    .build();
            
            String toString = option.toString();
            assertNotNull(toString);
            assertFalse(toString.isEmpty());
        }
    }

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderTests {
        
        @Test
        @DisplayName("Should build option with all builder methods")
        void shouldBuildOptionWithAllBuilderMethods() {
            option = Option.builder("a")
                    .argName("arg")
                    .required(true)
                    .hasArg()
                    .longOpt("argument")
                    .desc("description")
                    .type(Integer.class)
                    .valueSeparator('=')
                    .since("1.0")
                    .build();
            
            assertEquals("a", option.getOpt());
            assertEquals("argument", option.getLongOpt());
            assertEquals("arg", option.getArgName());
            assertTrue(option.isRequired());
            assertTrue(option.hasArg());
            assertEquals("description", option.getDescription());
            assertEquals(Integer.class, option.getType());
            assertEquals('=', option.getValueSeparator());
            assertEquals("1.0", option.getSince());
        }
        
        @Test
        @DisplayName("Should handle builder with long option only")
        void shouldHandleBuilderWithLongOptionOnly() {
            option = Option.builder()
                    .longOpt("longonly")
                    .build();
            
            assertNull(option.getOpt());
            assertEquals("longonly", option.getLongOpt());
            assertEquals("longonly", option.getKey());
        }
        
        @Test
        @DisplayName("Should handle type conversion in builder")
        void shouldHandleTypeConversionInBuilder() {
            option = Option.builder("a")
                    .type(null)  // Should default to String.class
                    .build();
            
            assertEquals(String.class, option.getType());
        }
        
        @Test
        @DisplayName("Should mark as required using convenience method")
        void shouldMarkAsRequiredUsingConvenienceMethod() {
            option = Option.builder("a")
                    .required()
                    .build();
            
            assertTrue(option.isRequired());
        }
        
        @Test
        @DisplayName("Should mark as deprecated using convenience method")
        void shouldMarkAsDeprecatedUsingConvenienceMethod() {
            option = Option.builder("a")
                    .deprecated()
                    .build();
            
            assertTrue(option.isDeprecated());
        }
        
        @Test
        @DisplayName("Should use valueSeparator convenience method")
        void shouldUseValueSeparatorConvenienceMethod() {
            option = Option.builder("a")
                    .valueSeparator()
                    .build();
            
            assertTrue(option.hasValueSeparator());
        }
        
        @Test
        @DisplayName("Should build with specific number of arguments")
        void shouldBuildWithSpecificNumberOfArgs() {
            option = Option.builder("a")
                    .numberOfArgs(3)
                    .build();
            
            assertEquals(3, option.getArgs());
        }
        
        @Test
        @DisplayName("Should build with optional argument")
        void shouldBuildWithOptionalArgument() {
            option = Option.builder("a")
                    .optionalArg(true)
                    .build();
            
            assertTrue(option.hasOptionalArg());
        }
    }

    @Test
    @DisplayName("Should test addValue deprecated method")
    void shouldTestAddValueDeprecatedMethod() {
        option = new Option("a", true, "description");
        
        assertThrows(UnsupportedOperationException.class, () -> {
            option.addValue("test");
        });
    }

    @Test
    @DisplayName("Should handle since version")
    void shouldHandleSinceVersion() {
        option = Option.builder("a")
                .since("2.0")
                .build();
        
        assertEquals("2.0", option.getSince());
    }
    
    @Test
    @DisplayName("Should handle empty string values")
    void shouldHandleEmptyStringValues() {
        option = Option.builder("a").hasArg().build();
        option.processValue("");
        
        assertEquals("", option.getValue());
        assertEquals(1, option.getValuesList().size());
    }
    
    @Test
    @DisplayName("Should handle null value in processValue")
    void shouldHandleNullValueInProcessValue() {
        option = Option.builder("a").hasArg().build();
        
        assertThrows(NullPointerException.class, () -> {
            option.processValue(null);
        });
    }
}