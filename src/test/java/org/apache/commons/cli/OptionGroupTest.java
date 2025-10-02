package org.apache.commons.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OptionGroup}.
 */
class OptionGroupTest {

    private OptionGroup group;
    private Option optA;
    private Option optB;
    private Option optLong;

    @BeforeEach
    void setUp() {
        group = new OptionGroup();
        optA = new Option("a", "alpha");
        optA.setDescription("Alpha option");
        optB = new Option("b", "beta");
        optB.setDescription("Beta option");
        optLong = Option.builder().longOpt("longOnly").desc("Long option").get();
    }

    // ---------- addOption / getOptions / getNames ----------

    @Test
    void testAddOptionAndRetrieveByNamesAndOptions() {
        group.addOption(optA).addOption(optB).addOption(optLong);

        Collection<String> names = group.getNames();
        Collection<Option> options = group.getOptions();

        assertEquals(3, names.size());
        assertEquals(3, options.size());
        assertTrue(names.contains("a"));
        assertTrue(names.contains("b"));
        assertTrue(names.contains("longOnly"));
        assertTrue(options.contains(optA));
        assertTrue(options.contains(optB));
        assertTrue(options.contains(optLong));
    }

    @Test
    void testAddOptionOverridesExistingKey() {
        group.addOption(optA);
        Option newA = new Option("a", "newAlpha");
        group.addOption(newA);

        assertEquals(1, group.getOptions().size());
        assertTrue(group.getOptions().contains(newA));
        assertTrue(group.getOptions().contains(optA));
    }

    // ---------- required flag ----------

    @Test
    void testRequiredFlagSetAndGet() {
        assertFalse(group.isRequired());
        group.setRequired(true);
        assertTrue(group.isRequired());
    }

    // ---------- selection ----------

    @Test
    void testSelectionSetAndGet() throws Exception {
        assertFalse(group.isSelected());
        assertNull(group.getSelected());

        group.addOption(optA);
        group.setSelected(optA);

        assertTrue(group.isSelected());
        assertEquals("a", group.getSelected());
    }

    @Test
    void testSelectionResetToNull() throws Exception {
        group.addOption(optA).setSelected(optA);
        assertTrue(group.isSelected());

        group.setSelected(null);
        assertFalse(group.isSelected());
        assertNull(group.getSelected());
    }

    @Test
    void testReselectSameOptionAllowed() throws Exception {
        group.addOption(optA).setSelected(optA);
        assertDoesNotThrow(() -> group.setSelected(optA));
    }

    @Test
    void testSelectingDifferentOptionThrowsAlreadySelectedException() throws Exception {
        group.addOption(optA).addOption(optB).setSelected(optA);
        AlreadySelectedException ex = assertThrows(AlreadySelectedException.class,
                () -> group.setSelected(optB));
    }

    // ---------- toString ----------

    @Test
    void testToStringWithShortAndLongOptions() {
        group.addOption(optA).addOption(optB).addOption(optLong);

        String repr = group.toString();
        assertTrue(repr.startsWith("["));
        assertTrue(repr.endsWith("]"));
        assertTrue(repr.contains("-a Alpha option"));
        assertTrue(repr.contains("-b Beta option"));
        assertTrue(repr.contains("--longOnly Long option"));
    }

    @Test
    void testToStringWithOptionWithoutDescription() {
        Option noDesc = new Option("x", "noDesc");
        group.addOption(noDesc);

        String repr = group.toString();
        assertTrue(repr.contains("-x"));
        assertFalse(repr.contains("null"));
    }

    @Test
    void testToStringEmptyGroup() {
        assertEquals("[]", group.toString());
    }
}
