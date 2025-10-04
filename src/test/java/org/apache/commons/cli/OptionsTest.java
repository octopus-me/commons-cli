package org.apache.commons.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for {@link Options}.
 */
class OptionsTest {

    private Options options;

    @BeforeEach
    void setUp() {
        options = new Options();
    }

    // ---------- addOption ----------

    @Test
    void testAddOptionStoresShortAndLongOptions() {
        Option opt = new Option("a", "alpha", true, "description");
        options.addOption(opt);

        assertTrue(options.hasOption("a"));
        assertTrue(options.hasOption("alpha"));
        assertSame(opt, options.getOption("a"));
        assertSame(opt, options.getOption("alpha"));
        assertTrue(options.getOptions().contains(opt));
    }

    @Test
    void testAddOptionMarksRequiredOptions() {
        Option opt = new Option("r", "required", false, "required");
        opt.setRequired(true);
        options.addOption(opt);

        assertTrue(options.getRequiredOptions().contains("r"));
    }

    @Test
    void testAddOptionReplacesDuplicateRequiredKey() {
        Option opt1 = new Option("r", "first", false, "desc1");
        opt1.setRequired(true);
        Option opt2 = new Option("r", "second", false, "desc2");
        opt2.setRequired(true);

        options.addOption(opt1);
        options.addOption(opt2);

        assertEquals(1, options.getRequiredOptions().size());
        assertTrue(options.getRequiredOptions().contains("r"));
        assertSame(opt2, options.getOption("r"));
    }

    @Test
    void testAddOptionShortOnly() {
        options.addOption("x", true, "short only");
        assertTrue(options.hasOption("x"));
        assertFalse(options.hasLongOption("x"));
    }

    @Test
    void testAddOptionWithDescriptionOnly() {
        options.addOption("y", "short only no arg");
        assertTrue(options.hasOption("y"));
        Option retrieved = options.getOption("y");
        assertNotNull(retrieved);
        assertEquals("short only no arg", retrieved.getDescription());
        assertFalse(retrieved.hasArg());
    }

    @Test
    void testAddOptionWithShortAndLong() {
        options.addOption("z", "zulu", true, "desc");
        assertTrue(options.hasShortOption("z"));
        assertTrue(options.hasLongOption("zulu"));
        Option o = options.getOption("zulu");
        assertEquals("zulu", o.getLongOpt());
    }

    // ---------- addRequiredOption ----------

    @Test
    void testAddRequiredOptionAddsToRequiredList() {
        options.addRequiredOption("a", "alpha", false, "desc");
        assertTrue(options.hasOption("a"));
        assertTrue(options.getRequiredOptions().contains("a"));
        Option retrieved = options.getOption("a");
        assertTrue(retrieved.isRequired());
    }

    // ---------- addOptionGroup ----------

    @Test
    void testAddOptionGroupAddsAllOptions() {
        OptionGroup group = new OptionGroup();
        Option o1 = new Option("a", "alpha");
        Option o2 = new Option("b", "beta");
        group.addOption(o1).addOption(o2);
        group.setRequired(true);

        options.addOptionGroup(group);

        assertTrue(options.hasOption("a"));
        assertTrue(options.hasOption("b"));
        assertEquals(1, options.getOptionGroups().size());
        assertSame(group, options.getOptionGroup(o1));
        assertSame(group, options.getOptionGroup(o2));
        assertTrue(options.getRequiredOptions().contains(group));
    }

    @Test
    void testAddOptionGroupMakesOptionsNonRequired() {
        OptionGroup group = new OptionGroup();
        Option o = new Option("r", "required", false, "desc");
        o.setRequired(true);
        group.addOption(o);
        options.addOptionGroup(group);

        Option stored = options.getOption("r");
        assertFalse(stored.isRequired(), "Options in group must not be required individually");
    }

    // ---------- addOptions ----------

    @Test
    void testAddOptionsMergesWithoutConflict() {
        Options source = new Options();
        source.addOption("a", "alpha", false, "descA");
        OptionGroup group = new OptionGroup();
        group.addOption(new Option("g", "grouped"));
        source.addOptionGroup(group);

        options.addOptions(source);

        assertTrue(options.hasOption("a"));
        assertTrue(options.hasOption("g"));
        assertEquals(1, options.getOptionGroups().size());
    }

    @Test
    void testAddOptionsThrowsOnDuplicateKey() {
        Options source = new Options();
        source.addOption("a", "alpha", false, "desc");
        options.addOption("a", "alpha", false, "existing");

        assertThrows(IllegalArgumentException.class, () -> options.addOptions(source));
    }

    // ---------- getMatchingOptions ----------

    @Test
    void testGetMatchingOptionsPerfectMatch() {
        options.addOption("a", "alpha", false, "desc");
        List<String> result = options.getMatchingOptions("alpha");
        assertEquals(Collections.singletonList("alpha"), result);
    }

    @Test
    void testGetMatchingOptionsPartialMatch() {
        options.addOption("a", "alpha", false, "desc");
        options.addOption("b", "alphabet", false, "desc");
        List<String> result = options.getMatchingOptions("alph");
        Set<String> expected = new HashSet<>(Arrays.asList("alpha", "alphabet"));
        assertEquals(expected, new HashSet<>(result));
    }


    @Test
    void testGetMatchingOptionsNoMatch() {
        options.addOption("x", "xyz", false, "desc");
        assertTrue(options.getMatchingOptions("nope").isEmpty());
    }

    // ---------- getOption ----------

    @Test
    void testGetOptionByShortOrLongName() {
        options.addOption("a", "alpha", false, "desc");
        assertNotNull(options.getOption("a"));
        assertNotNull(options.getOption("alpha"));
    }

    @Test
    void testGetOptionIgnoresHyphens() {
        options.addOption("a", "alpha", false, "desc");
        assertNotNull(options.getOption("--alpha"));
        assertNotNull(options.getOption("-a"));
    }

    @Test
    void testGetOptionReturnsNullIfNotExists() {
        assertNull(options.getOption("notExisting"));
    }

    // ---------- hasOption / hasShortOption / hasLongOption ----------

    @Test
    void testHasOptionVariants() {
        options.addOption("a", "alpha", false, "desc");
        assertTrue(options.hasShortOption("a"));
        assertTrue(options.hasLongOption("alpha"));
        assertTrue(options.hasOption("alpha"));
        assertFalse(options.hasOption("beta"));
    }

    // ---------- getOptionGroup ----------

    @Test
    void testGetOptionGroupReturnsNullIfNotGrouped() {
        Option opt = new Option("a", "alpha");
        options.addOption(opt);
        assertNull(options.getOptionGroup(opt));
    }

    // ---------- getOptions ----------

    @Test
    void testGetOptionsReturnsUnmodifiableCollection() {
        Option opt = new Option("a", "alpha");
        options.addOption(opt);
        Collection<Option> retrieved = options.getOptions();

        assertTrue(retrieved.contains(opt));
        assertThrows(UnsupportedOperationException.class, () -> retrieved.clear());
    }

    // ---------- getRequiredOptions ----------

    @Test
    void testGetRequiredOptionsReturnsUnmodifiableList() {
        Option opt = new Option("a", "alpha");
        opt.setRequired(true);
        options.addOption(opt);

        List<?> required = options.getRequiredOptions();
        assertTrue(required.contains("a"));
        assertThrows(UnsupportedOperationException.class, () -> required.clear());
    }

    // ---------- getOptionGroups ----------

    @Test
    void testGetOptionGroupsFiltersDuplicates() {
        OptionGroup group = new OptionGroup();
        Option o1 = new Option("a", "alpha");
        group.addOption(o1);
        options.addOptionGroup(group);
        options.addOptionGroup(group); // add twice intentionally

        Collection<OptionGroup> groups = options.getOptionGroups();
        assertEquals(1, groups.size());
        assertTrue(groups.contains(group));
    }

    // ---------- helpOptions ----------

    @Test
    void testHelpOptionsReturnsCopyOfShortOpts() {
        Option o1 = new Option("a", "alpha");
        Option o2 = new Option("b", "beta");
        options.addOption(o1).addOption(o2);

        List<Option> list = options.helpOptions();
        assertEquals(2, list.size());
        assertTrue(list.contains(o1));
        assertTrue(list.contains(o2));
    }

    // ---------- toString ----------

    @Test
    void testToStringIncludesShortAndLongMaps() {
        options.addOption("a", "alpha", false, "desc");
        String text = options.toString();
        assertTrue(text.contains("short"));
        assertTrue(text.contains("long"));
        assertTrue(text.contains("a"));
        assertTrue(text.contains("alpha"));
    }

    @Test
    void testToStringEmptyOptions() {
        String text = options.toString();
        assertTrue(text.startsWith("[ Options:"));
        assertTrue(text.contains("short"));
        assertTrue(text.contains("long"));
    }
}
