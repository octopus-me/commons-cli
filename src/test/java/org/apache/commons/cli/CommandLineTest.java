package org.apache.commons.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineTest {

    private Options options;
    private DefaultParser parser;

    @BeforeEach
    void setUp() {
        options = new Options();
        parser = new DefaultParser();
    }

    @Test
    void testBuilderAddsArgsAndOptionsAndBuilds() {
        CommandLine.Builder builder = CommandLine.builder();
        Option opt = Option.builder("a").longOpt("alpha").hasArg().desc("desc").build();
        builder.addArg("leftover1").addArg("leftover2");
        builder.addOption(opt);
        CommandLine cmd = builder.get();

        List<String> argList = cmd.getArgList();
        assertEquals(2, argList.size());
        assertTrue(argList.contains("leftover1"));
        assertTrue(argList.contains("leftover2"));

        Option[] opts = cmd.getOptions();
        assertEquals(1, opts.length);
        assertEquals(opt.getOpt(), opts[0].getOpt());
    }

    @Test
    void testHasOptionByOptAndLongOptAndOptionObject() {
        Option optShort = Option.builder("b").longOpt("beta").hasArg().desc("desc").build();
        options.addOption(optShort);
        try {
            CommandLine cmd = parser.parse(options, new String[]{"-b", "valueB"});
            assertTrue(cmd.hasOption("b"));
            assertTrue(cmd.hasOption("beta"));
            assertEquals("valueB", cmd.getOptionValue("b"));
            assertEquals("valueB", cmd.getOptionValue("beta"));
            assertTrue(cmd.hasOption(optShort));
        } catch (ParseException e) {
            fail("ParseException thrown unexpectedly: " + e.getMessage());
        }
    }

    @Test
    void testGetOptionValueWithDefaultWhenNotPresent() {
        Option opt = Option.builder("c").longOpt("charlie").hasArg().desc("desc").build();
        options.addOption(opt);
        try {
            CommandLine cmd = parser.parse(options, new String[]{});
            assertNull(cmd.getOptionValue("c"));
            assertEquals("def", cmd.getOptionValue("c", "def"));
            assertEquals("def2", cmd.getOptionValue("c", (Supplier<String>) () -> "def2"));
        } catch (ParseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testGetOptionValuesMultipleAndProperties() {
        Option optProps = Option.builder("D").hasArgs().valueSeparator().longOpt("define").desc("define properties").build();
        options.addOption(optProps);
        try {
            CommandLine cmd = parser.parse(options, new String[]{"-Dkey1=value1", "-Dkey2=value2"});
            String[] values = cmd.getOptionValues("D");
            assertNotNull(values);
            assertEquals(4, values.length);
            assertEquals("key1", values[0]);
            assertEquals("value1", values[1]);
            assertEquals("key2", values[2]);
            assertEquals("value2", values[3]);

            Properties props = cmd.getOptionProperties("D");
            assertEquals("value1", props.getProperty("key1"));
            assertEquals("value2", props.getProperty("key2"));
        } catch (ParseException e) {
            fail("Unexpected parse exception: " + e.getMessage());
        }
    }

    @Test
    void testGetOptionObjectDeprecatedUsage() {
        Option opt = Option.builder("e").hasArg().longOpt("echo").desc("echo").build();
        options.addOption(opt);
        try {
            CommandLine cmd = parser.parse(options, new String[]{"-e", "valueE"});
            Object obj = cmd.getOptionObject("e");
            assertEquals("valueE", obj);
            Object obj2 = cmd.getOptionObject('x');
            assertNull(obj2);
        } catch (ParseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testGetArgsAndArgListLeftover() {
        Option opt = Option.builder("f").hasArg().longOpt("flag").desc("flag").build();
        options.addOption(opt);
        try {
            CommandLine cmd = parser.parse(options, new String[]{"-f", "valF", "left1", "left2"});
            assertTrue(cmd.hasOption("f"));
            List<String> leftovers = cmd.getArgList();
            assertEquals(2, leftovers.size());
            assertTrue(leftovers.contains("left1"));
            assertTrue(leftovers.contains("left2"));
            String[] argsArray = cmd.getArgs();
            assertArrayEquals(leftovers.toArray(new String[0]), argsArray);
        } catch (ParseException e) {
            fail("ParseException: " + e.getMessage());
        }
    }

    @Test
    void testIteratorOverOptions() {
        Option opt1 = Option.builder("g").longOpt("gamma").build();
        Option opt2 = Option.builder("h").longOpt("hotel").build();
        options.addOption(opt1);
        options.addOption(opt2);
        try {
            CommandLine cmd = parser.parse(options, new String[]{"-g", "-h"});
            int count = 0;
            for (Option o : cmd.getOptions()) {
                assertFalse(o == opt1 || o == opt2);
                count++;
            }
            assertEquals(2, count);
        } catch (ParseException e) {
            fail("Unexpected: " + e.getMessage());
        }
    }

    @Test
    void testHasOptionWithOptionGroup() {
        OptionGroup group = new OptionGroup();
        Option a = new Option("a", "alpha");
        Option b = new Option("b", "beta");
        group.addOption(a).addOption(b);
        options.addOptionGroup(group);
        try {
            CommandLine cmd = parser.parse(options, new String[]{"-a"});
            assertTrue(cmd.hasOption(group));
            assertEquals(cmd.getOptionValue(group), cmd.getOptionValue(a));
        } catch (ParseException e) {
            fail("Exception: " + e.getMessage());
        }
    }

    @Test
    void testHasOptionNullGroupOrNoSelection() {
        OptionGroup group = new OptionGroup();
        Option c = new Option("c", "charlie");
        group.addOption(c);
        options.addOptionGroup(group);
        CommandLine cmd = CommandLine.builder().get();
        assertFalse(cmd.hasOption((OptionGroup) null));
        assertFalse(cmd.hasOption(group));
    }

    @Test
    void testGetParsedOptionValueSuccessfulConversion() throws Exception {
        Option opt = Option.builder("i").longOpt("int").hasArg().type(Integer.class).desc("integer").build();
        options.addOption(opt);
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, new String[]{"-i", "42"});
        } catch (ParseException e) {
            fail("Parse failed: " + e.getMessage());
        }
        Integer parsed = cmd.getParsedOptionValue("i");
        assertEquals(42, parsed.intValue());
        Integer parsedDefault = cmd.getParsedOptionValue("j", () -> 99);
        assertEquals(99, parsedDefault.intValue());
    }

    @Test
    void testGetParsedOptionValuesArrayConversion() throws Exception {
        Option opt = Option.builder("s").longOpt("str").hasArgs().type(String.class).desc("strings").build();
        options.addOption(opt);
        CommandLine cmd = parser.parse(options, new String[]{"-s", "one", "two"});
        String[] parsed = cmd.getParsedOptionValues("s");
        assertArrayEquals(new String[]{"one", "two"}, parsed);
        String[] defaultArr = cmd.getParsedOptionValues("x", new String[]{"none"});
        assertArrayEquals(new String[]{"none"}, defaultArr);
    }

    @Test
    void testGetOptionPropertiesWhenOddNumberOfValues() throws Exception {
        Option opt = Option.builder("D").hasArgs().valueSeparator().build();
        options.addOption(opt);
        CommandLine cmd = parser.parse(options, new String[]{"-DkeyOnly"});
        Properties props = cmd.getOptionProperties("D");
        assertEquals("true", props.getProperty("keyOnly"));
    }

    @Test
    void testProcessPropertiesFromValuesInternallyHandlesNullOption() {
        CommandLine.Builder builder = CommandLine.builder();
        CommandLine cmd = builder.get();
        Properties props = cmd.getOptionProperties("doesNotExist");
        assertNotNull(props);
        assertTrue(props.isEmpty());
    }

    @Test
    void testGetOptionsReturnsAllProcessed() {
        Option o1 = Option.builder("m").longOpt("male").build();
        Option o2 = Option.builder("f").longOpt("female").build();
        options.addOption(o1);
        options.addOption(o2);
        try {
            CommandLine cmd = parser.parse(options, new String[]{"-m", "-f"});
            Option[] arr = cmd.getOptions();
            assertEquals(2, arr.length);
            assertTrue(Arrays.stream(arr).anyMatch(o -> o.getOpt().equals("m")));
            assertTrue(Arrays.stream(arr).anyMatch(o -> o.getOpt().equals("f")));
        } catch (ParseException e) {
            fail("ParseException: " + e.getMessage());
        }
    }
}
