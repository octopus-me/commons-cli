package org.apache.commons.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Properties;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Testes unitários abrangentes para a classe {@link DefaultParser}.
 */
@DisplayName("Testes do DefaultParser")
class DefaultParserTest {

    private Options options;
    private DefaultParser parser;

    @BeforeEach
    void setUp() {
        options = new Options();
        options.addOption("a", "alpha", false, "toggle alpha");
        options.addOption("b", "beta", true, "set beta");
        options.addOption("c", "gamma", false, "toggle gamma");
        options.addOption(Option.builder("d").longOpt("delta").hasArg().argName("value").build());
        parser = new DefaultParser();
    }

    @Nested
    @DisplayName("Testes do Builder")
    class BuilderTests {

        @Mock
        private Consumer<Option> mockHandler;

        private AutoCloseable closeable;

        @BeforeEach
        void initMocks() {
            closeable = MockitoAnnotations.openMocks(this);
        }

        @AfterEach
        void releaseMocks() throws Exception {
            closeable.close();
        }

        @Test
        @DisplayName("Deve construir um parser com configurações padrão")
        void testBuildWithDefaults() throws ParseException {
            // --ver deve corresponder a --version (partial matching)
            options.addOption(null, "version", false, "version");
            DefaultParser defaultParser = DefaultParser.builder().build();
            CommandLine cmd = defaultParser.parse(options, new String[]{"--ver"});
            assertTrue(cmd.hasOption("version"));
        }

        @Test
        @DisplayName("setAllowPartialMatching(false) deve desabilitar o matching parcial")
        void testSetAllowPartialMatchingFalse() {
            options.addOption(null, "version", false, "version");
            DefaultParser customParser = DefaultParser.builder()
                .setAllowPartialMatching(false)
                .build();
            assertThrows(UnrecognizedOptionException.class, () -> customParser.parse(options, new String[]{"--ver"}));
        }

        @Test
        @DisplayName("setStripLeadingAndTrailingQuotes(true) deve remover as aspas")
        void testSetStripLeadingAndTrailingQuotesTrue() throws ParseException {
            DefaultParser customParser = DefaultParser.builder()
                .setStripLeadingAndTrailingQuotes(true)
                .build();
            CommandLine cmd = customParser.parse(options, new String[]{"-d", "\"quoted value\""});
            assertEquals("quoted value", cmd.getOptionValue("d"));
        }

        @Test
        @DisplayName("setStripLeadingAndTrailingQuotes(false) deve manter as aspas")
        void testSetStripLeadingAndTrailingQuotesFalse() throws ParseException {
            DefaultParser customParser = DefaultParser.builder()
                .setStripLeadingAndTrailingQuotes(false)
                .build();
            CommandLine cmd = customParser.parse(options, new String[]{"-d", "\"quoted value\""});
            assertEquals("\"quoted value\"", cmd.getOptionValue("d"));
        }

    }

    @Nested
    @DisplayName("Cenários de Parsing")
    class ParsingScenarios {

        @Test
        @DisplayName("Deve processar opções curtas e longas simples")
        void testSimpleShortAndLongOptions() throws ParseException {
            CommandLine cmd = parser.parse(options, new String[]{"-a", "--beta", "value"});
            assertTrue(cmd.hasOption("a"));
            assertTrue(cmd.hasOption("beta"));
            assertEquals("value", cmd.getOptionValue("b"));
        }

        @Test
        @DisplayName("Deve processar opções curtas concatenadas")
        void testConcatenatedShortOptions() throws ParseException {
            CommandLine cmd = parser.parse(options, new String[]{"-ac"});
            assertTrue(cmd.hasOption("a"));
            assertTrue(cmd.hasOption("c"));
        }
        
        @Test
        @DisplayName("Deve processar opções curtas concatenadas com argumento no final")
        void testConcatenatedShortOptionsWithArgument() throws ParseException {
            CommandLine cmd = parser.parse(options, new String[]{"-abvalue"});
            assertTrue(cmd.hasOption("a"));
            assertTrue(cmd.hasOption("b"));
            assertEquals("value", cmd.getOptionValue("b"));
        }

        @Test
        @DisplayName("Deve processar argumento de opção longa com '='")
        void testLongOptionWithEqualsArgument() throws ParseException {
            CommandLine cmd = parser.parse(options, new String[]{"--delta=d_value"});
            assertTrue(cmd.hasOption("d"));
            assertEquals("d_value", cmd.getOptionValue("delta"));
        }

        @Test
        @DisplayName("O token '--' deve parar o parsing de opções")
        void testStopParsingToken() throws ParseException {
            CommandLine cmd = parser.parse(options, new String[]{"-a", "--", "-c", "--beta"});
            assertTrue(cmd.hasOption("a"));
            assertFalse(cmd.hasOption("c"));
            assertFalse(cmd.hasOption("beta"));
            assertEquals(2, cmd.getArgList().size());
            assertEquals("-c", cmd.getArgList().get(0));
        }

        @Test
        @DisplayName("Um hífen solitário '-' deve ser tratado como argumento")
        void testSingleHyphenIsArgument() throws ParseException {
            CommandLine cmd = parser.parse(options, new String[]{"-a", "-", "-b", "value"});
            assertTrue(cmd.getArgList().contains("-"));
            assertEquals(1, cmd.getArgList().size());
        }

        @Test
        @DisplayName("Números negativos devem ser tratados como argumentos, não opções")
        void testNegativeNumberIsArgument() throws ParseException {
            CommandLine cmd = parser.parse(options, new String[]{"-b", "-10"});
            assertEquals("-10", cmd.getOptionValue("b"));
            assertTrue(cmd.getArgList().isEmpty());
        }

        @Test
        @DisplayName("Propriedades Java (-D) devem ser processadas corretamente")
        void testJavaProperties() throws ParseException {
            options.addOption(Option.builder("D").numberOfArgs(2).valueSeparator('=').build());
            CommandLine cmd = parser.parse(options, new String[]{"-Dkey=value"});
            assertArrayEquals(new String[]{"key", "value"}, cmd.getOptionValues("D"));
        }
    }

    @Nested
    @DisplayName("Tratamento de Erros e Exceções")
    class ErrorAndExceptionHandling {

        @Test
        @DisplayName("Deve lançar UnrecognizedOptionException para opção desconhecida")
        void testUnrecognizedOptionException() {
            assertThrows(UnrecognizedOptionException.class, () -> parser.parse(options, new String[]{"-x"}));
        }

        @Test
        @DisplayName("Deve lançar MissingArgumentException se o argumento estiver faltando")
        void testMissingArgumentException() {
            assertThrows(MissingArgumentException.class, () -> parser.parse(options, new String[]{"-b"}));
        }
        
        @Test
        @DisplayName("Deve lançar MissingOptionException se a opção obrigatória estiver faltando")
        void testMissingOptionException() {
            options.addRequiredOption("r", "required", false, "required option");
            assertThrows(MissingOptionException.class, () -> parser.parse(options, new String[]{"-a"}));
        }
        
        @Test
        @DisplayName("Deve lançar AmbiguousOptionException para opções longas ambíguas")
        void testAmbiguousOptionException() {
            options.addOption(null, "version", false, "");
            options.addOption(null, "verbose", false, "");
            assertThrows(AmbiguousOptionException.class, () -> parser.parse(options, new String[]{"--ver"}));
        }
    }
    
    @Nested
    @DisplayName("Grupos de Opções")
    class OptionGroups {

        @BeforeEach
        void setUpGroup() {
            OptionGroup group = new OptionGroup();
            group.addOption(Option.builder("x").build());
            group.addOption(Option.builder("y").build());
            options.addOptionGroup(group);
        }

        @Test
        @DisplayName("Deve permitir uma opção de um grupo")
        void testAllowOneOptionFromGroup() {
            assertDoesNotThrow(() -> parser.parse(options, new String[]{"-x"}));
        }

        @Test
        @DisplayName("Deve lançar AlreadySelectedException para múltiplas opções de um grupo")
        void testThrowForMultipleOptionsFromGroup() {
            // A exceção é lançada pela lógica do OptionGroup, que o parser deve invocar.
            assertThrows(AlreadySelectedException.class, () -> parser.parse(options, new String[]{"-x", "-y"}));
        }

        @Test
        @DisplayName("Deve lançar MissingOptionException se um grupo obrigatório for omitido")
        void testMissingRequiredGroup() {
            options = new Options();
            OptionGroup group = new OptionGroup();
            group.addOption(new Option("a", ""));
            group.setRequired(true);
            options.addOptionGroup(group);

            assertThrows(MissingOptionException.class, () -> parser.parse(options, new String[]{}));
        }
    }

    @Nested
    @DisplayName("Argumento Properties")
    class PropertiesArgument {
        
        @Test
        @DisplayName("Deve definir opções a partir de Properties se não estiverem na linha de comando")
        void testPropertiesAreApplied() throws ParseException {
            Properties props = new Properties();
            props.setProperty("beta", "prop_value");

            CommandLine cmd = parser.parse(options, new String[]{"-a"}, props);
            assertTrue(cmd.hasOption("a"));
            assertTrue(cmd.hasOption("beta"));
            assertEquals("prop_value", cmd.getOptionValue("beta"));
        }

        @Test
        @DisplayName("A linha de comando deve ter precedência sobre Properties")
        void testCommandLinePrecedenceOverProperties() throws ParseException {
            Properties props = new Properties();
            props.setProperty("beta", "prop_value");

            CommandLine cmd = parser.parse(options, new String[]{"--beta", "cli_value"}, props);
            assertEquals("cli_value", cmd.getOptionValue("beta"));
        }
        
        @Test
        @DisplayName("Não deve definir opção booleana se o valor da propriedade não for 'true', 'yes' ou '1'")
        void testBooleanPropertyNotSetForInvalidValue() throws ParseException {
            Properties props = new Properties();
            props.setProperty("alpha", "false");

            CommandLine cmd = parser.parse(options, new String[]{}, props);
            assertFalse(cmd.hasOption("alpha"));
        }
    }
}