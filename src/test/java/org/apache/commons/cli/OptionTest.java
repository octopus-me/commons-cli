package org.apache.commons.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for {@link Option}.
 */
class OptionTest {

    private Option simpleOption;
    private Option optionWithLong;

    @BeforeEach
    void setUp() {
        simpleOption = new Option("a", "test option");
        optionWithLong = new Option("b", "beta", true, "long option");
    }

    // ---------- Builder Tests ----------

    @Test
    void testBuilderWithValidOption() {
        Option opt = Option.builder("c")
                .longOpt("config")
                .desc("Config option")
                .hasArg()
                .required()
                .since("1.0")
                .type(Integer.class)
                .valueSeparator('=')
                .argName("file")
                .get();

        assertEquals("c", opt.getOpt());
        assertEquals("config", opt.getLongOpt());
        assertEquals("Config option", opt.getDescription());
        assertTrue(opt.hasArg());
        assertTrue(opt.isRequired());
        assertEquals("1.0", opt.getSince());
        assertEquals(Integer.class, opt.getType());
        assertEquals('=', opt.getValueSeparator());
        assertEquals("file", opt.getArgName());
    }

    @Test
    void testBuilderWithoutOptionAndLongOptThrowsException() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> Option.builder().desc("fail").get());
        assertEquals("Either opt or longOpt must be specified", ex.getMessage());
    }

    @Test
    void testBuilderDeprecatedOption() {
        DeprecatedAttributes attrs = DeprecatedAttributes.DEFAULT;
        Option opt = Option.builder("d")
                .longOpt("deprecated")
                .deprecated(attrs)
                .desc("deprecated option")
                .get();
        assertTrue(opt.isDeprecated());
        assertEquals(attrs, opt.getDeprecated());
    }

    @Test
    void testBuilderHasArgsUnlimited() {
        Option opt = Option.builder("e").hasArgs().get();
        assertTrue(opt.hasArgs());
        assertEquals(Option.UNLIMITED_VALUES, opt.getArgs());
    }

    @Test
    void testBuilderOptionalArg() {
        Option opt = Option.builder("f").optionalArg(true).get();
        assertTrue(opt.hasOptionalArg());
        assertTrue(opt.hasArg());
    }

    // ---------- Constructor Tests ----------

    @Test
    void testConstructorWithDescriptionOnly() {
        Option opt = new Option("x", "desc");
        assertEquals("x", opt.getOpt());
        assertEquals("desc", opt.getDescription());
        assertFalse(opt.hasArg());
    }

    @Test
    void testConstructorWithHasArg() {
        Option opt = new Option("y", "longy", true, "description");
        assertTrue(opt.hasArg());
        assertEquals(1, opt.getArgs());
    }

    // ---------- Equals and HashCode ----------

    @Test
    void testEqualsAndHashCode() {
        Option opt1 = new Option("z", "longz", false, "zzz");
        Option opt2 = new Option("z", "longz", true, "different");
        Option opt3 = new Option("other", "longOther", false, "other");

        assertEquals(opt1, opt2);
        assertEquals(opt1.hashCode(), opt2.hashCode());
        assertNotEquals(opt1, opt3);
    }

    @Test
    void testEqualsWithDifferentClass() {
        assertNotEquals(simpleOption, "string");
    }

    @Test
    void testEqualsWithSameInstance() {
        assertEquals(simpleOption, simpleOption);
    }

    // ---------- Value Handling ----------

    @Test
    void testProcessValueWithoutSeparator() {
        Option opt = Option.builder("k").hasArg().get();
        opt.processValue("value1");
        assertEquals("value1", opt.getValue());
        assertEquals(Arrays.asList("value1"), opt.getValuesList());
    }

    @Test
    void testProcessValueWithSeparator() {
        Option opt = Option.builder("D").hasArgs().valueSeparator('=').get();
        opt.setArgs(2);
        opt.processValue("key=value");
        assertEquals("key", opt.getValue(0));
        assertEquals("value", opt.getValue(1));
    }

    @Test
    void testProcessValueThrowsWhenUninitialized() {
        Option opt = Option.builder("u").get();
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> opt.processValue("something"));
        assertEquals("NO_ARGS_ALLOWED", ex.getMessage());
    }

    @Test
    void testAddValueThrowsWhenListFull() {
        Option opt = Option.builder("l").hasArg().get();
        opt.processValue("first");
        assertThrows(IllegalArgumentException.class, () -> opt.processValue("second"));
    }

    @Test
    void testClearValues() {
        optionWithLong.processValue("abc");
        assertFalse(optionWithLong.getValuesList().isEmpty());
        optionWithLong.clearValues();
        assertTrue(optionWithLong.getValuesList().isEmpty());
    }

    @Test
    void testGetValuesReturnsNullWhenEmpty() {
        assertNull(simpleOption.getValues());
    }

    @Test
    void testGetValueWithDefault() {
        assertEquals("default", simpleOption.getValue("default"));
        optionWithLong.processValue("set");
        assertEquals("set", optionWithLong.getValue("default"));
    }

    // ---------- Clone ----------

    @Test
    void testCloneCreatesNewInstance() {
        optionWithLong.processValue("cloneVal");
        Option clone = (Option) optionWithLong.clone();
        assertEquals(optionWithLong, clone);
        assertNotSame(optionWithLong.getValuesList(), clone.getValuesList());
    }

    // ---------- Other Behavior ----------

    @Test
    void testHasArgAndHasArgs() {
        Option opt = Option.builder("m").hasArg().get();
        assertTrue(opt.hasArg());
        assertFalse(opt.hasArgs());

        Option opt2 = Option.builder("n").hasArgs().get();
        assertTrue(opt2.hasArgs());
    }

    @Test
    void testHasArgName() {
        Option opt = Option.builder("o").argName("filename").get();
        assertTrue(opt.hasArgName());
        opt.setArgName("");
        assertFalse(opt.hasArgName());
    }

    @Test
    void testHasLongOpt() {
        assertTrue(optionWithLong.hasLongOpt());
        assertFalse(simpleOption.hasLongOpt());
    }

    @Test
    void testRequiresArg() {
        Option opt = Option.builder("p").hasArg().get();
        assertTrue(opt.requiresArg());

        Option opt2 = Option.builder("q").optionalArg(true).get();
        assertFalse(opt2.requiresArg());
    }

    @Test
    void testGetIdAndKey() {
        Option opt = new Option("r", "run");
        assertEquals('r', opt.getId());
        assertEquals("r", opt.getKey());

        Option opt2 = Option.builder().longOpt("longOnly").get();
        assertEquals("longOnly", opt2.getKey());
    }

    @Test
    void testSettersAndGetters() {
        simpleOption.setArgName("arg");
        assertEquals("arg", simpleOption.getArgName());

        simpleOption.setArgs(2);
        assertEquals(2, simpleOption.getArgs());

        @SuppressWarnings("unchecked")
        Converter<String, RuntimeException> conv = Mockito.mock(Converter.class);
        simpleOption.setConverter(conv);
        assertEquals(conv, simpleOption.getConverter());

        simpleOption.setDescription("new desc");
        assertEquals("new desc", simpleOption.getDescription());

        simpleOption.setLongOpt("newLong");
        assertEquals("newLong", simpleOption.getLongOpt());

        simpleOption.setOptionalArg(true);
        assertTrue(simpleOption.hasOptionalArg());

        simpleOption.setRequired(true);
        assertTrue(simpleOption.isRequired());

        simpleOption.setType(Integer.class);
        assertEquals(Integer.class, simpleOption.getType());

        simpleOption.setValueSeparator(':');
        assertEquals(':', simpleOption.getValueSeparator());
    }

    @Test
    void testAddValueDeprecatedMethodThrows() {
        assertThrows(UnsupportedOperationException.class, () -> simpleOption.addValue("val"));
    }

    @Test
    void testToDeprecatedStringWhenNotDeprecated() {
        assertEquals("", simpleOption.toDeprecatedString());
    }

    @Test
    void testToStringIncludesDetails() {
        Option opt = Option.builder("s")
                .longOpt("server")
                .desc("Server option")
                .hasArgs()
                .type(String.class)
                .get();

        String repr = opt.toString();
        assertTrue(repr.contains("Option s server"));
        assertTrue(repr.contains("Server option"));
        assertTrue(repr.contains("[ARG...]"));
    }
}
