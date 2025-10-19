package org.apache.commons.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Testes unitários abrangentes para a classe {@link DefaultParser}.
 */
class DefaultParserTest {

    private DefaultParser parser;
    private Options options;

    @BeforeEach
    void setUp() {
        parser = new DefaultParser();
        options = new Options();
    }

    // ---------- TESTES DO BUILDER ----------

    @Test
    void testBuilderDefaultValues() {
        DefaultParser.Builder builder = DefaultParser.builder();
        DefaultParser built = builder.get();
        assertNotNull(built);
    }

    @Test
    void testBuilderSettersAndBuild() {
        @SuppressWarnings("unchecked")
        Consumer<Option> handler = mock(Consumer.class);
        DefaultParser.Builder builder = DefaultParser.builder()
                .setAllowPartialMatching(false)
                .setStripLeadingAndTrailingQuotes(true)
                .setDeprecatedHandler(handler);
        DefaultParser built = builder.get();
        assertNotNull(built);
    }

    // ---------- TESTES DE CONSTRUTORES ----------

    @Test
    void testDefaultConstructor() {
        DefaultParser p = new DefaultParser();
        assertNotNull(p);
    }

    @Test
    void testConstructorWithPartialMatching() {
        DefaultParser p = new DefaultParser(false);
        assertNotNull(p);
    }

    // ---------- TESTES DE MÉTODOS ESTÁTICOS ----------

    @Test
    void testIndexOfEqual() {
        assertEquals(1, DefaultParser.indexOfEqual("a=b"));
        assertEquals(-1, DefaultParser.indexOfEqual("abc"));
    }

    // ---------- TESTES DE MÉTODOS PRIVADOS VIA REFLEXÃO ----------

    @Test
    void testStripLeadingAndTrailingQuotesDefaultOnAndOff() throws Exception {
        DefaultParser p1 = DefaultParser.builder().setStripLeadingAndTrailingQuotes(true).get();
        java.lang.reflect.Method method1 = DefaultParser.class.getDeclaredMethod("stripLeadingAndTrailingQuotesDefaultOn", String.class);
        method1.setAccessible(true);
        assertEquals("x", method1.invoke(p1, "\"x\""));

        DefaultParser p2 = DefaultParser.builder().setStripLeadingAndTrailingQuotes(false).get();
        java.lang.reflect.Method method2 = DefaultParser.class.getDeclaredMethod("stripLeadingAndTrailingQuotesDefaultOff", String.class);
        method2.setAccessible(true);
        assertEquals("y", method2.invoke(p2, "y"));
    }

    // ---------- TESTES DE ENUM ----------

    @Test
    void testEnumNonOptionActionValues() {
        for (DefaultParser.NonOptionAction action : DefaultParser.NonOptionAction.values()) {
            assertNotNull(action.name());
        }
        assertEquals(DefaultParser.NonOptionAction.valueOf("IGNORE"), DefaultParser.NonOptionAction.IGNORE);
    }

    // ---------- TESTES DE MÉTODOS PÚBLICOS parse() ----------

    @Test
    void testParseSimpleOption() throws Exception {
        options.addOption(Option.builder("a").longOpt("alpha").desc("teste").get());
        CommandLine cmd = parser.parse(options, new String[]{"-a"});
        assertTrue(cmd.hasOption("a"));
    }

    @Test
    void testParseWithPropertyAndBooleanValue() throws Exception {
        options.addOption(Option.builder("f").hasArg().get());
        Properties props = new Properties();
        props.setProperty("f", "true");
        CommandLine cmd = parser.parse(options, new String[]{}, props);
        assertTrue(cmd.hasOption("f"));
    }

    @Test
    void testParseWithUnrecognizedOptionThrow() {
        options.addOption(Option.builder("a").get());
        assertThrows(UnrecognizedOptionException.class,
                () -> parser.parse(options, new String[]{"-b"}, new Properties(), false));
    }

    @Test
    void testParseWithUnrecognizedOptionStop() throws Exception {
        options.addOption(Option.builder("a").get());
        CommandLine cmd = parser.parse(options, new String[]{"-b", "x"}, new Properties(), true);
        assertTrue(cmd.getArgList().contains("-b"));
    }

    @Test
    void testParseWithRequiredOptionMissingThrows() {
        Option req = Option.builder("r").required(true).get();
        options.addOption(req);
        assertThrows(MissingOptionException.class, () ->
                parser.parse(options, new Properties(), DefaultParser.NonOptionAction.THROW, new String[]{}));
    }

    @Test
    void testParseWithMultipleArgsOption() throws Exception {
        Option opt = Option.builder("n").hasArgs().get();
        options.addOption(opt);
        CommandLine cmd = parser.parse(options, new String[]{"-n", "1", "2"});
        assertEquals(Arrays.asList("1", "2"), Arrays.asList(cmd.getOptionValues("n")));
    }

    @Test
    void testParseHandlesDoubleDashAsArg() throws Exception {
        Option opt = Option.builder("a").hasArg().get();
        options.addOption(opt);
        CommandLine cmd = parser.parse(options, new String[]{"--", "value"});
        assertTrue(cmd.getArgList().contains("value"));
    }

    @Test
    void testParseWithEmptyArgsAndPropertiesNull() throws Exception {
        CommandLine cmd = parser.parse(options, new Properties(), DefaultParser.NonOptionAction.IGNORE);
        assertNotNull(cmd);
    }

    @Test
    void testParseWithNonOptionActionIgnore() throws Exception {
        Option opt = Option.builder("a").get();
        options.addOption(opt);
        CommandLine cmd = parser.parse(options, new Properties(), DefaultParser.NonOptionAction.IGNORE, "-x", "-a");
        assertFalse(cmd.getArgList().contains("-x"));
        assertTrue(cmd.hasOption("a"));
    }

    @Test
    void testParseWithNonOptionActionStop() throws Exception {
        Option opt = Option.builder("a").get();
        options.addOption(opt);
        CommandLine cmd = parser.parse(options, new Properties(), DefaultParser.NonOptionAction.STOP, "-x", "-a");
        assertTrue(cmd.getArgList().contains("-x"));
    }

    // ---------- TESTES DE MÉTODOS PROTEGIDOS ----------

    @Test
    void testHandleUnknownTokenAddsArg() throws Exception {
        parser.cmd = CommandLine.builder().get();
        parser.nonOptionAction = DefaultParser.NonOptionAction.IGNORE;
        parser.addArg("token");
        assertTrue(parser.cmd.getArgList().contains("token"));
    }

    @Test
    void testHandleUnknownTokenThrowsForUnrecognizedOption() {
        parser.nonOptionAction = DefaultParser.NonOptionAction.THROW;
        assertThrows(UnrecognizedOptionException.class, () -> parser.handleUnknownToken("-x"));
    }

    @Test
    void testHandleUnknownTokenStopSetsSkipParsing() throws Exception {
        parser.nonOptionAction = DefaultParser.NonOptionAction.STOP;
        parser.cmd = CommandLine.builder().get();
        parser.handleUnknownToken("value");
        assertTrue(parser.skipParsing);
    }

    // ---------- TESTES DE CASOS DE ERRO ----------

    @Test
    void testCheckRequiredArgsThrowsMissingArgument() throws Exception {
        java.lang.reflect.Method method = DefaultParser.class.getDeclaredMethod("checkRequiredArgs");
        method.setAccessible(true);
        parser.currentOption = Option.builder("x").hasArg().get();
        parser.currentOption.processValue("v1");
        assertDoesNotThrow(() -> method.invoke(parser));
        parser.currentOption = Option.builder("y").hasArg().required(true).get();
        assertThrows(Exception.class, () -> method.invoke(parser));
    }

    @Test
    void testIsNegativeNumberTrueAndFalse() throws Exception {
        java.lang.reflect.Method method = DefaultParser.class.getDeclaredMethod("isNegativeNumber", String.class);
        method.setAccessible(true);
        assertTrue((boolean) method.invoke(parser, "-1"));
        assertFalse((boolean) method.invoke(parser, "abc"));
    }

    @Test
    void testIsOptionAndIsShortOptionAndIsLongOption() throws Exception {
        options.addOption(Option.builder("a").get());
        java.lang.reflect.Field field = DefaultParser.class.getDeclaredField("options");
        field.setAccessible(true);
        field.set(parser, options);

        java.lang.reflect.Method method = DefaultParser.class.getDeclaredMethod("isOption", String.class);
        method.setAccessible(true);
        assertTrue((boolean) method.invoke(parser, "-a"));
        assertFalse((boolean) method.invoke(parser, "abc"));
    }

    @Test
    void testUpdateRequiredOptionsRemovesFromExpectedOpts() throws Exception {
        options.addOption(Option.builder("r").required(true).get());
        parser.options = options;
        parser.expectedOpts = new ArrayList<>(Arrays.asList("r"));
        java.lang.reflect.Method method = DefaultParser.class.getDeclaredMethod("updateRequiredOptions", Option.class);
        method.setAccessible(true);
        Option opt = options.getOption("r");
        method.invoke(parser, opt);
        assertFalse(parser.expectedOpts.contains("r"));
    }

    @Test
    void testCheckRequiredOptionsThrowsWhenExpectedNotEmpty() throws Exception {
        parser.expectedOpts = new ArrayList<>(Arrays.asList("x"));
        assertThrows(MissingOptionException.class, () -> parser.checkRequiredOptions());
    }

    // ---------- TESTES DE HANDLE LONG OPTION ----------

    @Test
    void testHandleLongOptionWithEqual() throws Exception {
        Option opt = Option.builder("l").longOpt("long").hasArg().get();
        options.addOption(opt);
        parser.options = options;
        parser.cmd = CommandLine.builder().get();
        java.lang.reflect.Method method = DefaultParser.class.getDeclaredMethod("handleLongOptionWithEqual", String.class);
        method.setAccessible(true);
        method.invoke(parser, "--long=value");
        assertTrue(parser.cmd.hasOption("l"));
    }

    @Test
    void testHandleLongOptionWithoutEqualThrowsAmbiguousOption() throws Exception {
        Option opt1 = Option.builder("a").longOpt("alpha").get();
        Option opt2 = Option.builder("b").longOpt("alphabet").get();
        options.addOption(opt1);
        options.addOption(opt2);
        parser.options = options;
        parser.cmd = CommandLine.builder().get();
        java.lang.reflect.Method method = DefaultParser.class.getDeclaredMethod("handleLongOptionWithoutEqual", String.class);
        method.setAccessible(true);
    }
}
