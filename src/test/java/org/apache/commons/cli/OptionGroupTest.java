package org.apache.commons.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Iterator;

class OptionGroupTest {

    private OptionGroup optionGroup;
    private Option optionA;
    private Option optionB;
    private Option optionC;

    @BeforeEach
    void setUp() {
        optionGroup = new OptionGroup();
        optionA = Option.builder("a").longOpt("alpha").desc("Alpha option").build();
        optionB = Option.builder("b").longOpt("beta").desc("Beta option").build();
        optionC = Option.builder().longOpt("gamma").desc("Gamma option").build();
    }

    @Nested
    @DisplayName("Constructor and Basic Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create empty OptionGroup")
        void shouldCreateEmptyOptionGroup() {
            assertNotNull(optionGroup);
            assertTrue(optionGroup.getNames().isEmpty());
            assertTrue(optionGroup.getOptions().isEmpty());
            assertFalse(optionGroup.isRequired());
            assertFalse(optionGroup.isSelected());
            assertNull(optionGroup.getSelected());
        }
    }

    @Nested
    @DisplayName("Add Option Tests")
    class AddOptionTests {
        
        @Test
        @DisplayName("Should add single option to group")
        void shouldAddSingleOptionToGroup() {
            OptionGroup result = optionGroup.addOption(optionA);
            
            assertEquals(optionGroup, result);
            assertEquals(1, optionGroup.getNames().size());
            assertEquals(1, optionGroup.getOptions().size());
            assertTrue(optionGroup.getNames().contains("a"));
            assertTrue(optionGroup.getOptions().contains(optionA));
        }
        
        @Test
        @DisplayName("Should add multiple options to group")
        void shouldAddMultipleOptionsToGroup() {
            optionGroup.addOption(optionA)
                      .addOption(optionB)
                      .addOption(optionC);
            
            assertEquals(3, optionGroup.getNames().size());
            assertEquals(3, optionGroup.getOptions().size());
            assertTrue(optionGroup.getNames().contains("a"));
            assertTrue(optionGroup.getNames().contains("b"));
            assertTrue(optionGroup.getNames().contains("gamma"));
        }
        
        @Test
        @DisplayName("Should add option with only long name")
        void shouldAddOptionWithOnlyLongName() {
            Option longOnlyOption = Option.builder().longOpt("longonly").build();
            optionGroup.addOption(longOnlyOption);
            
            assertEquals(1, optionGroup.getNames().size());
            assertEquals("longonly", optionGroup.getNames().iterator().next());
        }
        
        @Test
        @DisplayName("Should handle adding duplicate option")
        void shouldHandleAddingDuplicateOption() {
            Option duplicateOption = Option.builder("a").longOpt("alpha").desc("Duplicate").build();
            
            optionGroup.addOption(optionA);
            optionGroup.addOption(duplicateOption);
            
            // Should replace the existing option with same key
            assertEquals(1, optionGroup.getNames().size());
            assertEquals(1, optionGroup.getOptions().size());
        }
        
        @Test
        @DisplayName("Should add options in insertion order")
        void shouldAddOptionsInInsertionOrder() {
            optionGroup.addOption(optionA)
                      .addOption(optionB)
                      .addOption(optionC);
            
            Iterator<String> nameIterator = optionGroup.getNames().iterator();
            Iterator<Option> optionIterator = optionGroup.getOptions().iterator();
            
            assertEquals("a", nameIterator.next());
            assertEquals("b", nameIterator.next());
            assertEquals("gamma", nameIterator.next());
            
            assertEquals(optionA, optionIterator.next());
            assertEquals(optionB, optionIterator.next());
            assertEquals(optionC, optionIterator.next());
        }
    }

    @Nested
    @DisplayName("Required Flag Tests")
    class RequiredFlagTests {
        
        @Test
        @DisplayName("Should set and get required flag")
        void shouldSetAndGetRequiredFlag() {
            assertFalse(optionGroup.isRequired());
            
            optionGroup.setRequired(true);
            
            assertTrue(optionGroup.isRequired());
            
            optionGroup.setRequired(false);
            
            assertFalse(optionGroup.isRequired());
        }
    }

    @Nested
    @DisplayName("Selection Tests")
    class SelectionTests {
        
        @BeforeEach
        void setUp() {
            optionGroup.addOption(optionA).addOption(optionB).addOption(optionC);
        }
        
        @Test
        @DisplayName("Should set selected option")
        void shouldSetSelectedOption() throws AlreadySelectedException {
            optionGroup.setSelected(optionA);
            
            assertTrue(optionGroup.isSelected());
            assertEquals("a", optionGroup.getSelected());
        }
        
        @Test
        @DisplayName("Should set selected option with long name only")
        void shouldSetSelectedOptionWithLongNameOnly() throws AlreadySelectedException {
            optionGroup.setSelected(optionC);
            
            assertTrue(optionGroup.isSelected());
            assertEquals("gamma", optionGroup.getSelected());
        }
        
        @Test
        @DisplayName("Should clear selection with null option")
        void shouldClearSelectionWithNullOption() throws AlreadySelectedException {
            optionGroup.setSelected(optionA);
            assertTrue(optionGroup.isSelected());
            
            optionGroup.setSelected(null);
            
            assertFalse(optionGroup.isSelected());
            assertNull(optionGroup.getSelected());
        }
        
        @Test
        @DisplayName("Should reselect same option without exception")
        void shouldReselectSameOptionWithoutException() throws AlreadySelectedException {
            optionGroup.setSelected(optionA);
            optionGroup.setSelected(optionA); // Reselect same option
            
            assertTrue(optionGroup.isSelected());
            assertEquals("a", optionGroup.getSelected());
        }
        
        @Test
        @DisplayName("Should throw AlreadySelectedException when selecting different option")
        void shouldThrowAlreadySelectedExceptionWhenSelectingDifferentOption() throws AlreadySelectedException {
            optionGroup.setSelected(optionA);
            
            AlreadySelectedException exception = assertThrows(AlreadySelectedException.class, () -> {
                optionGroup.setSelected(optionB);
            });
            
            assertTrue(optionGroup.isSelected());
            assertEquals("a", optionGroup.getSelected());
            assertNotNull(exception);
        }
        
        @Test
        @DisplayName("Should allow selecting different option after clearing")
        void shouldAllowSelectingDifferentOptionAfterClearing() throws AlreadySelectedException {
            optionGroup.setSelected(optionA);
            optionGroup.setSelected(null); // Clear selection
            optionGroup.setSelected(optionB); // Select different option
            
            assertTrue(optionGroup.isSelected());
            assertEquals("b", optionGroup.getSelected());
        }
        
        @Test
        @DisplayName("Should throw AlreadySelectedException for option not in group")
        void shouldThrowAlreadySelectedExceptionForOptionNotInGroup() throws AlreadySelectedException {
            Option externalOption = Option.builder("x").longOpt("external").build();
            
            optionGroup.setSelected(optionA);
            
            AlreadySelectedException exception = assertThrows(AlreadySelectedException.class, () -> {
                optionGroup.setSelected(externalOption);
            });
            
            assertTrue(optionGroup.isSelected());
            assertEquals("a", optionGroup.getSelected());
            assertNotNull(exception);
        }
    }

    @Nested
    @DisplayName("Collection Access Tests")
    class CollectionAccessTests {
        
        @Test
        @DisplayName("Should get names collection")
        void shouldGetNamesCollection() {
            optionGroup.addOption(optionA).addOption(optionB);
            
            Collection<String> names = optionGroup.getNames();
            
            assertNotNull(names);
            assertEquals(2, names.size());
            assertTrue(names.contains("a"));
            assertTrue(names.contains("b"));
            assertFalse(names.contains("c"));
        }
        
        @Test
        @DisplayName("Should get options collection")
        void shouldGetOptionsCollection() {
            optionGroup.addOption(optionA).addOption(optionB);
            
            Collection<Option> options = optionGroup.getOptions();
            
            assertNotNull(options);
            assertEquals(2, options.size());
            assertTrue(options.contains(optionA));
            assertTrue(options.contains(optionB));
        }
        
        @Test
        @DisplayName("Should return unmodifiable names collection")
        void shouldReturnUnmodifiableNamesCollection() {
            optionGroup.addOption(optionA);
            Collection<String> names = optionGroup.getNames();
            
            assertThrows(UnsupportedOperationException.class, () -> {
                names.add("newOption");
            });
        }
        
        @Test
        @DisplayName("Should return unmodifiable options collection")
        void shouldReturnUnmodifiableOptionsCollection() {
            optionGroup.addOption(optionA);
            Collection<Option> options = optionGroup.getOptions();
            
            assertThrows(UnsupportedOperationException.class, () -> {
                options.add(optionB);
            });
        }
        
        @Test
        @DisplayName("Should handle empty collections")
        void shouldHandleEmptyCollections() {
            Collection<String> names = optionGroup.getNames();
            Collection<Option> options = optionGroup.getOptions();
            
            assertNotNull(names);
            assertNotNull(options);
            assertTrue(names.isEmpty());
            assertTrue(options.isEmpty());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle selection with deprecated options")
        void shouldHandleSelectionWithDeprecatedOptions() throws AlreadySelectedException {
            Option deprecatedOption = Option.builder("d")
                    .longOpt("deprecated")
                    .deprecated()
                    .build();
            
            optionGroup.addOption(deprecatedOption);
            optionGroup.setSelected(deprecatedOption);
            
            assertTrue(optionGroup.isSelected());
            assertEquals("d", optionGroup.getSelected());
            // No exception should be thrown for deprecated options
        }
        
        @Test
        @DisplayName("Should handle options with same key but different properties")
        void shouldHandleOptionsWithSameKeyButDifferentProperties() {
            Option option1 = Option.builder("a").longOpt("alpha").desc("First").build();
            Option option2 = Option.builder("a").longOpt("alpha").desc("Second").build();
            
            optionGroup.addOption(option1);
            optionGroup.addOption(option2); // Should replace the first one
            
            assertEquals(1, optionGroup.getNames().size());
            assertEquals(1, optionGroup.getOptions().size());
            // The last added option should be the one in the group
            assertEquals("Second", optionGroup.getOptions().iterator().next().getDescription());
        }
        
        @Test
        @DisplayName("Should handle mixed short and long options")
        void shouldHandleMixedShortAndLongOptions() {
            Option shortOnly = Option.builder("s").desc("Short only").build();
            Option longOnly = Option.builder().longOpt("longonly").desc("Long only").build();
            Option both = Option.builder("b").longOpt("both").desc("Both").build();
            
            optionGroup.addOption(shortOnly)
                      .addOption(longOnly)
                      .addOption(both);
            
            assertEquals(3, optionGroup.getNames().size());
            assertTrue(optionGroup.getNames().contains("s"));
            assertTrue(optionGroup.getNames().contains("longonly"));
            assertTrue(optionGroup.getNames().contains("b"));
        }
        
        @Test
        @DisplayName("Should handle selection state transitions")
        void shouldHandleSelectionStateTransitions() throws AlreadySelectedException {
            // Initial state
            assertFalse(optionGroup.isSelected());
            assertNull(optionGroup.getSelected());
            
            // Select option
            optionGroup.addOption(optionA);
            optionGroup.setSelected(optionA);
            assertTrue(optionGroup.isSelected());
            assertEquals("a", optionGroup.getSelected());
            
            // Clear selection
            optionGroup.setSelected(null);
            assertFalse(optionGroup.isSelected());
            assertNull(optionGroup.getSelected());
            
            // Select again
            optionGroup.setSelected(optionA);
            assertTrue(optionGroup.isSelected());
            assertEquals("a", optionGroup.getSelected());
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("Should generate string representation for empty group")
        void shouldGenerateStringRepresentationForEmptyGroup() {
            String result = optionGroup.toString();
            
            assertEquals("[]", result);
        }
        
        @Test
        @DisplayName("Should generate string representation for single short option")
        void shouldGenerateStringRepresentationForSingleShortOption() {
            optionGroup.addOption(optionA);
            
            String result = optionGroup.toString();
            
            assertEquals("[-a Alpha option]", result);
        }
        
        @Test
        @DisplayName("Should generate string representation for single long option")
        void shouldGenerateStringRepresentationForSingleLongOption() {
            optionGroup.addOption(optionC);
            
            String result = optionGroup.toString();
            
            assertEquals("[--gamma Gamma option]", result);
        }
        
        @Test
        @DisplayName("Should generate string representation for multiple options")
        void shouldGenerateStringRepresentationForMultipleOptions() {
            optionGroup.addOption(optionA).addOption(optionB).addOption(optionC);
            
            String result = optionGroup.toString();
            
            assertTrue(result.startsWith("["));
            assertTrue(result.endsWith("]"));
            assertTrue(result.contains("-a Alpha option"));
            assertTrue(result.contains("-b Beta option"));
            assertTrue(result.contains("--gamma Gamma option"));
            assertTrue(result.contains(", ")); // Should have separators
        }
        
        @Test
        @DisplayName("Should generate string representation for option without description")
        void shouldGenerateStringRepresentationForOptionWithoutDescription() {
            Option noDescOption = Option.builder("n").build();
            optionGroup.addOption(noDescOption);
            
            String result = optionGroup.toString();
            
            assertEquals("[-n]", result);
        }
        
        @Test
        @DisplayName("Should generate string representation for mixed options")
        void shouldGenerateStringRepresentationForMixedOptions() {
            Option shortOnly = Option.builder("s").desc("Short").build();
            Option longOnly = Option.builder().longOpt("long").desc("Long").build();
            
            optionGroup.addOption(shortOnly).addOption(longOnly);
            
            String result = optionGroup.toString();
            
            assertEquals("[-s Short, --long Long]", result);
        }
        
        @Test
        @DisplayName("Should handle options with special characters in description")
        void shouldHandleOptionsWithSpecialCharactersInDescription() {
            Option specialOption = Option.builder("x").desc("Special: chars, here!").build();
            optionGroup.addOption(specialOption);
            
            String result = optionGroup.toString();
            
            assertEquals("[-x Special: chars, here!]", result);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should maintain state after multiple operations")
        void shouldMaintainStateAfterMultipleOperations() throws AlreadySelectedException {
            // Initial setup
            optionGroup.setRequired(true);
            optionGroup.addOption(optionA).addOption(optionB);
            
            // Verify initial state
            assertTrue(optionGroup.isRequired());
            assertEquals(2, optionGroup.getNames().size());
            assertFalse(optionGroup.isSelected());
            
            // Select option
            optionGroup.setSelected(optionA);
            assertTrue(optionGroup.isSelected());
            assertEquals("a", optionGroup.getSelected());
            
            // Add more options
            optionGroup.addOption(optionC);
            assertEquals(3, optionGroup.getNames().size());
            
            // Selection should persist
            assertTrue(optionGroup.isSelected());
            assertEquals("a", optionGroup.getSelected());
            
            // Clear selection and select different option
            optionGroup.setSelected(null);
            optionGroup.setSelected(optionC);
            assertEquals("gamma", optionGroup.getSelected());
        }
        
        @Test
        @DisplayName("Should handle complex selection scenarios")
        void shouldHandleComplexSelectionScenarios() throws AlreadySelectedException {
            Option option1 = Option.builder("1").desc("One").build();
            Option option2 = Option.builder("2").desc("Two").build();
            Option option3 = Option.builder("3").desc("Three").build();
            
            optionGroup.addOption(option1).addOption(option2).addOption(option3);
            
            // Select first option
            optionGroup.setSelected(option1);
            assertEquals("1", optionGroup.getSelected());
            
            // Try to select second option - should fail
            assertThrows(AlreadySelectedException.class, () -> {
                optionGroup.setSelected(option2);
            });
            
            // Reselect first option - should succeed
            optionGroup.setSelected(option1);
            assertEquals("1", optionGroup.getSelected());
            
            // Clear and select third option
            optionGroup.setSelected(null);
            optionGroup.setSelected(option3);
            assertEquals("3", optionGroup.getSelected());
        }
    }

    @Test
    @DisplayName("Should handle null option in addOption")
    void shouldHandleNullOptionInAddOption() {
        assertThrows(NullPointerException.class, () -> {
            optionGroup.addOption(null);
        });
    }
    
    @Test
    @DisplayName("Should handle large number of options")
    void shouldHandleLargeNumberOfOptions() {
        for (int i = 0; i < 100; i++) {
            Option option = Option.builder(String.valueOf((char) ('a' + i % 26)))
                                .longOpt("option" + i)
                                .desc("Option " + i)
                                .build();
            optionGroup.addOption(option);
        }
        
        assertEquals(26, optionGroup.getNames().size());
        assertEquals(26, optionGroup.getOptions().size());
    }
    
    @Test
    @DisplayName("Should maintain insertion order for large number of options")
    void shouldMaintainInsertionOrderForLargeNumberOfOptions() {
        for (int i = 0; i < 10; i++) {
            Option option = Option.builder(String.valueOf(i))
                                .desc("Option " + i)
                                .build();
            optionGroup.addOption(option);
        }
        
        Iterator<String> nameIterator = optionGroup.getNames().iterator();
        for (int i = 0; i < 10; i++) {
            assertEquals(String.valueOf(i), nameIterator.next());
        }
    }
}