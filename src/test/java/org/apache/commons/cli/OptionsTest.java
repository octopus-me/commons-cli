package org.apache.commons.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;

class OptionsTest {

    private Options options;

    @BeforeEach
    void setUp() {
        options = new Options();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create empty Options")
        void shouldCreateEmptyOptions() {
            assertNotNull(options);
            assertTrue(options.getOptions().isEmpty());
            assertTrue(options.getRequiredOptions().isEmpty());
            assertFalse(options.hasOption("a"));
            assertFalse(options.hasShortOption("a"));
            assertFalse(options.hasLongOption("alpha"));
        }
    }

    @Nested
    @DisplayName("Add Option Tests")
    class AddOptionTests {
        
        @Test
        @DisplayName("Should add option with short name only")
        void shouldAddOptionWithShortNameOnly() {
            Option option = Option.builder("a").desc("Alpha option").build();
            
            Options result = options.addOption(option);
            
            assertEquals(options, result);
            assertEquals(1, options.getOptions().size());
            assertTrue(options.hasOption("a"));
            assertTrue(options.hasShortOption("a"));
            assertFalse(options.hasLongOption("a"));
            assertEquals(option, options.getOption("a"));
        }
        
        @Test
        @DisplayName("Should add option with long name only")
        void shouldAddOptionWithLongNameOnly() {
            Option option = Option.builder().longOpt("alpha").desc("Alpha option").build();
            
            options.addOption(option);
            
            assertEquals(1, options.getOptions().size());
            assertTrue(options.hasOption("alpha"));
            assertTrue(options.hasLongOption("alpha"));
            assertTrue(options.hasShortOption("alpha"));
            assertEquals(option, options.getOption("alpha"));
        }
        
        @Test
        @DisplayName("Should add option with both short and long names")
        void shouldAddOptionWithBothShortAndLongNames() {
            Option option = Option.builder("a").longOpt("alpha").desc("Alpha option").build();
            
            options.addOption(option);
            
            assertEquals(1, options.getOptions().size());
            assertTrue(options.hasOption("a"));
            assertTrue(options.hasOption("alpha"));
            assertTrue(options.hasShortOption("a"));
            assertTrue(options.hasLongOption("alpha"));
            assertEquals(option, options.getOption("a"));
            assertEquals(option, options.getOption("alpha"));
        }
        
        @Test
        @DisplayName("Should add required option")
        void shouldAddRequiredOption() {
            Option option = Option.builder("a").required(true).desc("Required option").build();
            
            options.addOption(option);
            
            assertEquals(1, options.getRequiredOptions().size());
            assertTrue(options.getRequiredOptions().contains("a"));
        }
        
        @Test
        @DisplayName("Should update required list when adding duplicate required option")
        void shouldUpdateRequiredListWhenAddingDuplicateRequiredOption() {
            Option option1 = Option.builder("a").required(true).desc("First").build();
            Option option2 = Option.builder("a").required(true).desc("Second").build();
            
            options.addOption(option1);
            options.addOption(option2); // Replace with new option
            
            assertEquals(1, options.getRequiredOptions().size());
            assertEquals(1, options.getOptions().size());
            assertEquals("Second", options.getOption("a").getDescription());
        }
        
        @Test
        @DisplayName("Should add option using convenience method with short name only")
        void shouldAddOptionUsingConvenienceMethodWithShortNameOnly() {
            Options result = options.addOption("a", true, "Alpha option");
            
            assertEquals(options, result);
            assertEquals(1, options.getOptions().size());
            Option option = options.getOption("a");
            assertEquals("a", option.getOpt());
            assertNull(option.getLongOpt());
            assertTrue(option.hasArg());
            assertEquals("Alpha option", option.getDescription());
        }
        
        @Test
        @DisplayName("Should add option using convenience method without argument")
        void shouldAddOptionUsingConvenienceMethodWithoutArgument() {
            options.addOption("a", "Alpha option");
            
            Option option = options.getOption("a");
            assertEquals("a", option.getOpt());
            assertNull(option.getLongOpt());
            assertFalse(option.hasArg());
            assertEquals("Alpha option", option.getDescription());
        }
        
        @Test
        @DisplayName("Should add option using convenience method with both names")
        void shouldAddOptionUsingConvenienceMethodWithBothNames() {
            options.addOption("a", "alpha", true, "Alpha option");
            
            Option option = options.getOption("a");
            assertEquals("a", option.getOpt());
            assertEquals("alpha", option.getLongOpt());
            assertTrue(option.hasArg());
            assertEquals("Alpha option", option.getDescription());
        }
        
        @Test
        @DisplayName("Should add required option using convenience method")
        void shouldAddRequiredOptionUsingConvenienceMethod() {
            options.addRequiredOption("a", "alpha", true, "Required option");
            
            Option option = options.getOption("a");
            assertEquals("a", option.getOpt());
            assertEquals("alpha", option.getLongOpt());
            assertTrue(option.hasArg());
            assertTrue(option.isRequired());
            assertEquals("Required option", option.getDescription());
            assertTrue(options.getRequiredOptions().contains("a"));
        }
    }

    @Nested
    @DisplayName("Option Group Tests")
    class OptionGroupTests {
        
        @Test
        @DisplayName("Should add option group")
        void shouldAddOptionGroup() {
            OptionGroup group = new OptionGroup();
            Option optionA = Option.builder("a").desc("Option A").build();
            Option optionB = Option.builder("b").desc("Option B").build();
            group.addOption(optionA).addOption(optionB);
            
            Options result = options.addOptionGroup(group);
            
            assertEquals(options, result);
            assertEquals(2, options.getOptions().size());
            assertTrue(options.hasOption("a"));
            assertTrue(options.hasOption("b"));
            assertEquals(group, options.getOptionGroup(optionA));
            assertEquals(group, options.getOptionGroup(optionB));
        }
        
        @Test
        @DisplayName("Should add required option group")
        void shouldAddRequiredOptionGroup() {
            OptionGroup group = new OptionGroup();
            group.setRequired(true);
            Option optionA = Option.builder("a").desc("Option A").build();
            Option optionB = Option.builder("b").desc("Option B").build();
            group.addOption(optionA).addOption(optionB);
            
            options.addOptionGroup(group);
            
            assertEquals(1, options.getRequiredOptions().size());
            assertTrue(options.getRequiredOptions().contains(group));
            // Options in group should not be individually required
            assertFalse(optionA.isRequired());
            assertFalse(optionB.isRequired());
        }
        
        @Test
        @DisplayName("Should remove individual requirement when adding to option group")
        void shouldRemoveIndividualRequirementWhenAddingToOptionGroup() {
            OptionGroup group = new OptionGroup();
            Option optionA = Option.builder("a").required(true).desc("Option A").build();
            group.addOption(optionA);
            
            options.addOptionGroup(group);
            
            assertFalse(optionA.isRequired());
            assertEquals(0, options.getRequiredOptions().stream()
                .filter(obj -> obj instanceof String && "a".equals(obj))
                .count()); // Should not have "a" in required list
        }
        
        @Test
        @DisplayName("Should get option groups")
        void shouldGetOptionGroups() {
            OptionGroup group1 = new OptionGroup();
            Option optionA = Option.builder("a").desc("A").build();
            Option optionB = Option.builder("b").desc("B").build();
            group1.addOption(optionA).addOption(optionB);
            
            OptionGroup group2 = new OptionGroup();
            Option optionC = Option.builder("c").desc("C").build();
            group2.addOption(optionC);
            
            options.addOptionGroup(group1);
            options.addOptionGroup(group2);
            
            Collection<OptionGroup> groups = options.getOptionGroups();
            
            assertNotNull(groups);
            assertEquals(2, groups.size());
            assertTrue(groups.contains(group1));
            assertTrue(groups.contains(group2));
        }
        
        @Test
        @DisplayName("Should return null for option not in any group")
        void shouldReturnNullForOptionNotInAnyGroup() {
            Option option = Option.builder("a").desc("Option").build();
            options.addOption(option);
            
            assertNull(options.getOptionGroup(option));
        }
    }

    @Nested
    @DisplayName("Add Options Tests")
    class AddOptionsTests {
        
        @Test
        @DisplayName("Should add all options from another Options instance")
        void shouldAddAllOptionsFromAnotherOptionsInstance() {
            Options other = new Options();
            other.addOption("a", "Alpha option");
            other.addOption("b", "Beta option");
            
            Options result = options.addOptions(other);
            
            assertEquals(options, result);
            assertEquals(2, options.getOptions().size());
            assertTrue(options.hasOption("a"));
            assertTrue(options.hasOption("b"));
        }
        
        @Test
        @DisplayName("Should add option groups from another Options instance")
        void shouldAddOptionGroupsFromAnotherOptionsInstance() {
            Options other = new Options();
            OptionGroup group = new OptionGroup();
            group.addOption(Option.builder("a").desc("A").build());
            other.addOptionGroup(group);
            
            options.addOptions(other);
            
            assertEquals(1, options.getOptionGroups().size());
            assertNotNull(options.getOptionGroup(Option.builder("a").build()));
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException for duplicate keys")
        void shouldThrowIllegalArgumentExceptionForDuplicateKeys() {
            Options other = new Options();
            other.addOption("a", "Alpha option");
            options.addOption("a", "Existing option");
            
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                options.addOptions(other);
            });
            
            assertTrue(exception.getMessage().contains("Duplicate key: a"));
        }
    }

    @Nested
    @DisplayName("Query Methods Tests")
    class QueryMethodsTests {
        
        @BeforeEach
        void setUp() {
            options.addOption("a", "alpha", false, "Alpha option");
            options.addOption("b", "beta", true, "Beta option");
            options.addOption("c", false, "Gamma option");
            options.addOption(Option.builder().longOpt("delta").desc("Delta option").build());
        }
        
        @Test
        @DisplayName("Should get option by short name")
        void shouldGetOptionByShortName() {
            Option option = options.getOption("a");
            
            assertNotNull(option);
            assertEquals("a", option.getOpt());
            assertEquals("alpha", option.getLongOpt());
        }
        
        @Test
        @DisplayName("Should get option by long name")
        void shouldGetOptionByLongName() {
            Option option = options.getOption("alpha");
            
            assertNotNull(option);
            assertEquals("a", option.getOpt());
            assertEquals("alpha", option.getLongOpt());
        }
        
        @Test
        @DisplayName("Should return null for non-existent option")
        void shouldReturnNullForNonExistentOption() {
            assertNull(options.getOption("nonexistent"));
        }
        
        @Test
        @DisplayName("Should strip leading hyphens when getting option")
        void shouldStripLeadingHyphensWhenGettingOption() {
            Option option1 = options.getOption("-a");
            Option option2 = options.getOption("--alpha");
            
            assertNotNull(option1);
            assertNotNull(option2);
            assertEquals(option1, option2);
        }
        
        @Test
        @DisplayName("Should check if option exists by short name")
        void shouldCheckIfOptionExistsByShortName() {
            assertTrue(options.hasOption("a"));
            assertTrue(options.hasShortOption("a"));
            assertFalse(options.hasOption("x"));
            assertFalse(options.hasShortOption("x"));
        }
        
        @Test
        @DisplayName("Should check if option exists by long name")
        void shouldCheckIfOptionExistsByLongName() {
            assertTrue(options.hasOption("alpha"));
            assertTrue(options.hasLongOption("alpha"));
            assertFalse(options.hasOption("nonexistent"));
            assertFalse(options.hasLongOption("nonexistent"));
        }
        
        @Test
        @DisplayName("Should strip leading hyphens when checking option existence")
        void shouldStripLeadingHyphensWhenCheckingOptionExistence() {
            assertTrue(options.hasOption("-a"));
            assertTrue(options.hasOption("--alpha"));
            assertTrue(options.hasShortOption("-a"));
            assertTrue(options.hasLongOption("--alpha"));
        }
        
        @Test
        @DisplayName("Should get all options as unmodifiable collection")
        void shouldGetAllOptionsAsUnmodifiableCollection() {
            Collection<Option> allOptions = options.getOptions();
            
            assertNotNull(allOptions);
            assertEquals(4, allOptions.size());
            
            assertThrows(UnsupportedOperationException.class, () -> {
                allOptions.add(Option.builder("x").build());
            });
        }
        
        @Test
        @DisplayName("Should get required options as unmodifiable list")
        void shouldGetRequiredOptionsAsUnmodifiableList() {
            options.addOption(Option.builder("r").required(true).desc("Required").build());
            
            List<?> requiredOptions = options.getRequiredOptions();
            
            assertNotNull(requiredOptions);
            assertEquals(1, requiredOptions.size());
            
        }
    }

    @Nested
    @DisplayName("Matching Options Tests")
    class MatchingOptionsTests {
        
        @BeforeEach
        void setUp() {
            options.addOption("a", "alpha", false, "Alpha option");
            options.addOption("b", "beta", true, "Beta option");
            options.addOption("c", "charlie", false, "Charlie option");
            options.addOption("d", "delta", false, "Delta option");
            options.addOption(Option.builder().longOpt("epsilon").desc("Epsilon option").build());
        }
        
        @Test
        @DisplayName("Should get perfect match for long option")
        void shouldGetPerfectMatchForLongOption() {
            List<String> matches = options.getMatchingOptions("alpha");
            
            assertNotNull(matches);
            assertEquals(1, matches.size());
            assertEquals("alpha", matches.get(0));
        }
        
        @Test
        @DisplayName("Should get partial matches for long options")
        void shouldGetPartialMatchesForLongOptions() {
            List<String> matches = options.getMatchingOptions("be");
            
            assertNotNull(matches);
            assertEquals(1, matches.size());
            assertEquals("beta", matches.get(0));
        }
        
        @Test
        @DisplayName("Should get multiple partial matches")
        void shouldGetMultiplePartialMatches() {
            options.addOption("e", "echo", false, "Echo option");
            options.addOption("f", "echofox", false, "Echofox option");
            
            List<String> matches = options.getMatchingOptions("ech");
            
            assertNotNull(matches);
            assertEquals(2, matches.size());
            assertTrue(matches.contains("echo"));
            assertTrue(matches.contains("echofox"));
        }
        
        @Test
        @DisplayName("Should return empty list for no matches")
        void shouldReturnEmptyListForNoMatches() {
            List<String> matches = options.getMatchingOptions("xyz");
            
            assertNotNull(matches);
            assertTrue(matches.isEmpty());
        }
        
        @Test
        @DisplayName("Should strip leading hyphens when matching")
        void shouldStripLeadingHyphensWhenMatching() {
            List<String> matches = options.getMatchingOptions("--alp");
            
            assertNotNull(matches);
            assertEquals(1, matches.size());
            assertEquals("alpha", matches.get(0));
        }
        
        @Test
        @DisplayName("Should handle empty string when matching")
        void shouldHandleEmptyStringWhenMatching() {
            List<String> matches = options.getMatchingOptions("");
            
            assertNotNull(matches);
            // Should match all long options
            assertTrue(matches.size() >= 4);
        }
    }

    @Nested
    @DisplayName("Required Options Tests")
    class RequiredOptionsTests {
        
        @Test
        @DisplayName("Should track required individual options")
        void shouldTrackRequiredIndividualOptions() {
            options.addOption(Option.builder("a").required(true).desc("A").build());
            options.addOption(Option.builder("b").required(true).desc("B").build());
            options.addOption(Option.builder("c").desc("C").build());
            
            List<?> required = options.getRequiredOptions();
            
            assertEquals(2, required.size());
            assertTrue(required.stream().anyMatch(obj -> "a".equals(obj)));
            assertTrue(required.stream().anyMatch(obj -> "b".equals(obj)));
            assertFalse(required.stream().anyMatch(obj -> "c".equals(obj)));
        }
        
        @Test
        @DisplayName("Should track required option groups")
        void shouldTrackRequiredOptionGroups() {
            OptionGroup group = new OptionGroup();
            group.setRequired(true);
            group.addOption(Option.builder("a").desc("A").build());
            group.addOption(Option.builder("b").desc("B").build());
            
            options.addOptionGroup(group);
            
            List<?> required = options.getRequiredOptions();
            
            assertEquals(1, required.size());
            assertTrue(required.contains(group));
        }
        
        @Test
        @DisplayName("Should handle mixed required options and groups")
        void shouldHandleMixedRequiredOptionsAndGroups() {
            options.addOption(Option.builder("x").required(true).desc("X").build());
            
            OptionGroup group = new OptionGroup();
            group.setRequired(true);
            group.addOption(Option.builder("a").desc("A").build());
            options.addOptionGroup(group);
            
            List<?> required = options.getRequiredOptions();
            
            assertEquals(2, required.size());
            assertTrue(required.stream().anyMatch(obj -> "x".equals(obj)));
            assertTrue(required.contains(group));
        }
        
        @Test
        @DisplayName("Should remove individual requirement when option added to group")
        void shouldRemoveIndividualRequirementWhenOptionAddedToGroup() {
            Option option = Option.builder("a").required(true).desc("A").build();
            options.addOption(option);
            
            OptionGroup group = new OptionGroup();
            group.addOption(option);
            options.addOptionGroup(group);
            
            List<?> required = options.getRequiredOptions();
            
            assertFalse(required.stream().anyMatch(obj -> "a".equals(obj)));
            assertFalse(option.isRequired());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle options with same short and long names")
        void shouldHandleOptionsWithSameShortAndLongNames() {
            Option option1 = Option.builder("a").desc("First A").build();
            Option option2 = Option.builder().longOpt("a").desc("Long A").build();
            
            options.addOption(option1);
            options.addOption(option2);
            
            assertEquals(1, options.getOptions().size());
            assertTrue(options.hasShortOption("a"));
            assertTrue(options.hasLongOption("a"));
        }
        
        @Test
        @DisplayName("Should handle null option in addOption")
        void shouldHandleNullOptionInAddOption() {
            assertThrows(NullPointerException.class, () -> {
                options.addOption(null);
            });
        }
        
        @Test
        @DisplayName("Should handle null option group in addOptionGroup")
        void shouldHandleNullOptionGroupInAddOptionGroup() {
            assertThrows(NullPointerException.class, () -> {
                options.addOptionGroup(null);
            });
        }
        
        @Test
        @DisplayName("Should handle null options in addOptions")
        void shouldHandleNullOptionsInAddOptions() {
            assertThrows(NullPointerException.class, () -> {
                options.addOptions(null);
            });
        }
        
        @Test
        @DisplayName("Should handle empty option names")
        void shouldHandleEmptyOptionNames() {
            // This should throw IllegalArgumentException in Option constructor
            assertThrows(IllegalArgumentException.class, () -> {
                options.addOption("", "Invalid option");
            });
        }
        
        @Test
        @DisplayName("Should maintain insertion order for options")
        void shouldMaintainInsertionOrderForOptions() {
            options.addOption("a", "First");
            options.addOption("b", "Second");
            options.addOption("c", "Third");
            
            Collection<Option> allOptions = options.getOptions();
            java.util.Iterator<Option> iterator = allOptions.iterator();
            
            assertEquals("a", iterator.next().getOpt());
            assertEquals("b", iterator.next().getOpt());
            assertEquals("c", iterator.next().getOpt());
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("Should generate string representation for empty options")
        void shouldGenerateStringRepresentationForEmptyOptions() {
            String result = options.toString();
            
            assertTrue(result.contains("Options"));
            assertTrue(result.contains("short {}"));
            assertTrue(result.contains("long {}"));
        }
        
        @Test
        @DisplayName("Should generate string representation with options")
        void shouldGenerateStringRepresentationWithOptions() {
            options.addOption("a", "Alpha");
            options.addOption("b", "beta", true, "Beta");
            
            String result = options.toString();
            
            assertTrue(result.contains("Options"));
            assertTrue(result.contains("short"));
            assertTrue(result.contains("long"));
            assertTrue(result.contains("a"));
            assertTrue(result.contains("b"));
            assertTrue(result.contains("beta"));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should handle complex option configuration")
        void shouldHandleComplexOptionConfiguration() {
            // Add individual options
            options.addOption("v", "verbose", false, "Verbose output");
            options.addOption("f", "file", true, "Input file");
            options.addOption("o", "output", true, "Output file");
            
            // Add required option
            options.addRequiredOption("r", "required", true, "Required option");
            
            // Add option group
            OptionGroup formatGroup = new OptionGroup();
            formatGroup.addOption(Option.builder("j").longOpt("json").desc("JSON format").build());
            formatGroup.addOption(Option.builder("x").longOpt("xml").desc("XML format").build());
            formatGroup.setRequired(true);
            options.addOptionGroup(formatGroup);
            
            // Verify configuration
            assertEquals(6, options.getOptions().size());
            // Check required options count by iterating
            int requiredCount = 0;
            for (Object req : options.getRequiredOptions()) {
                if (req instanceof String && "r".equals(req)) {
                    requiredCount++;
                } else if (req instanceof OptionGroup && req == formatGroup) {
                    requiredCount++;
                }
            }
            assertEquals(2, requiredCount);
            assertTrue(options.hasOption("v"));
            assertTrue(options.hasOption("verbose"));
            assertTrue(options.hasOption("r"));
            assertTrue(options.hasOption("json"));
            assertTrue(options.hasOption("xml"));
            
            // Verify option group
            Option jsonOption = options.getOption("json");
            assertNotNull(options.getOptionGroup(jsonOption));
            assertEquals(formatGroup, options.getOptionGroup(jsonOption));
        }
        
        @Test
        @DisplayName("Should combine multiple Options instances")
        void shouldCombineMultipleOptionsInstances() {
            Options config1 = new Options();
            config1.addOption("a", "Alpha");
            config1.addOption("b", "Beta");
            
            Options config2 = new Options();
            config2.addOption("c", "Charlie");
            config2.addOption("d", "Delta");
            
            OptionGroup group = new OptionGroup();
            group.addOption(Option.builder("x").desc("X").build());
            config2.addOptionGroup(group);
            
            options.addOptions(config1).addOptions(config2);
            
            assertEquals(5, options.getOptions().size());
            assertTrue(options.hasOption("a"));
            assertTrue(options.hasOption("b"));
            assertTrue(options.hasOption("c"));
            assertTrue(options.hasOption("d"));
            assertEquals(1, options.getOptionGroups().size());
        }
    }

    @Test
    @DisplayName("Should handle large number of options")
    void shouldHandleLargeNumberOfOptions() {
        for (int i = 0; i < 100; i++) {
            String opt = String.valueOf((char) ('a' + i % 26));
            options.addOption(opt, "option" + i, false, "Option " + i);
        }
        
        assertEquals(26, options.getOptions().size());
        assertTrue(options.hasOption("a"));
        assertTrue(options.hasOption("z"));
    }
    
    @Test
    @DisplayName("Should handle options with special characters")
    void shouldHandleOptionsWithSpecialCharacters() {
        // Options with special characters in description
        options.addOption("s", "special", false, "Option with: special, characters!");
        
        Option option = options.getOption("s");
        assertNotNull(option);
        assertEquals("Option with: special, characters!", option.getDescription());
    }
    
    @Test
    @DisplayName("Should test unmodifiable required options list properly")
    void shouldTestUnmodifiableRequiredOptionsListProperly() {
        options.addOption(Option.builder("r").required(true).desc("Required").build());
        
        List<?> requiredOptions = options.getRequiredOptions();
        
        assertNotNull(requiredOptions);
        assertEquals(1, requiredOptions.size());
        
        // Test that the list is unmodifiable by trying to clear it
        assertThrows(UnsupportedOperationException.class, () -> {
            requiredOptions.clear();
        });
    }
}