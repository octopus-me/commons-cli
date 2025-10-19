package org.apache.commons.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Testes unitários abrangentes para a classe {@link CommandLine}.
 */
@DisplayName("Testes da Classe CommandLine")
class CommandLineTest {

    private CommandLine.Builder builder;
    private Option optionA;
    private Option optionB;
    private Option optionC;

    @BeforeEach
    void setUp() {
        builder = new CommandLine.Builder();
        optionA = Option.builder("a").longOpt("alpha").desc("toggle alpha").build();
        optionB = Option.builder("b").longOpt("beta").hasArg().argName("value").desc("set beta").build();
        optionC = Option.builder("c").longOpt("gamma").hasArgs().desc("set gamma values").build();
    }

    @Nested
    @DisplayName("Testes do Builder")
    class BuilderTests {
        @Test
        @DisplayName("Deve adicionar argumentos corretamente")
        void testAddArg() {
            builder.addArg("arg1").addArg("arg2").addArg(null);
            CommandLine cmd = builder.build();
            assertEquals(Arrays.asList("arg1", "arg2"), cmd.getArgList());
        }

        @Test
        @DisplayName("Deve adicionar opções corretamente")
        void testAddOption() {
            builder.addOption(optionA).addOption(null);
            CommandLine cmd = builder.build();
            assertTrue(cmd.hasOption('a'));
            assertEquals(1, cmd.getOptions().length);
        }
    }

    @Nested
    @DisplayName("Testes de Acesso a Opções e Valores")
    class AccessorTests {
        private CommandLine cmd;

        @BeforeEach
        void buildCommandLine() {
            optionB.processValue("b_value");
            optionC.processValue("c_val1");
            optionC.processValue("c_val2");
            cmd = builder.addOption(optionA).addOption(optionB).addOption(optionC).addArg("extra").build();
        }

        @Test
        @DisplayName("hasOption deve funcionar com char, String e Option")
        void testHasOption() {
            assertTrue(cmd.hasOption('a'));
            assertTrue(cmd.hasOption("alpha"));
            assertTrue(cmd.hasOption(optionB));
            assertFalse(cmd.hasOption('x'));
            assertFalse(cmd.hasOption("omega"));
        }

        @Test
        @DisplayName("getArgs e getArgList devem retornar os argumentos restantes")
        void testGetArgs() {
            assertArrayEquals(new String[]{"extra"}, cmd.getArgs());
            assertEquals(Arrays.asList("extra"), cmd.getArgList());
        }

        @Test
        @DisplayName("getOptions deve retornar todas as opções processadas")
        void testGetOptions() {
            assertEquals(3, cmd.getOptions().length);
        }

        @Test
        @DisplayName("getOptionValue deve retornar o primeiro valor ou nulo")
        void testGetOptionValue() {
            assertEquals("b_value", cmd.getOptionValue('b'));
            assertEquals("b_value", cmd.getOptionValue("beta"));
            assertEquals("b_value", cmd.getOptionValue(optionB));
            assertEquals("c_val1", cmd.getOptionValue('c')); // Deve retornar o primeiro valor
            assertNull(cmd.getOptionValue('a'), "Opção sem argumento deve retornar nulo");
            assertNull(cmd.getOptionValue('x'), "Opção inexistente deve retornar nulo");
        }

        @Test
        @DisplayName("getOptionValue com valor padrão deve funcionar corretamente")
        void testGetOptionValueWithDefault() {
            assertEquals("b_value", cmd.getOptionValue('b', "default"));
            assertEquals("default", cmd.getOptionValue('x', "default"));
            assertEquals("default", cmd.getOptionValue(Option.builder("x").build(), "default"));
        }

        @Test
        @DisplayName("getOptionValue com Supplier de valor padrão deve funcionar")
        void testGetOptionValueWithDefaultSupplier() {
            Supplier<String> defaultSupplier = () -> "supplied_default";
            assertEquals("b_value", cmd.getOptionValue('b', defaultSupplier));
            assertEquals("supplied_default", cmd.getOptionValue('x', defaultSupplier));
            assertEquals("supplied_default", cmd.getOptionValue(Option.builder("x").build(), defaultSupplier));
        }

        @Test
        @DisplayName("getOptionValues deve retornar todos os valores de uma opção")
        void testGetOptionValues() {
            assertArrayEquals(new String[]{"b_value"}, cmd.getOptionValues('b'));
            assertArrayEquals(new String[]{"c_val1", "c_val2"}, cmd.getOptionValues("gamma"));
            assertArrayEquals(new String[]{"c_val1", "c_val2"}, cmd.getOptionValues(optionC));
            assertNull(cmd.getOptionValues('a'), "Opção sem argumento deve retornar nulo");
            assertNull(cmd.getOptionValues('x'), "Opção inexistente deve retornar nulo");
        }
    }

    @Nested
    @DisplayName("Testes de Valores Tipados (getParsedOptionValue)")
    class ParsedValueTests {
        private CommandLine cmd;
        private Option intOption;
        private Option doubleOption;

        @BeforeEach
        void setUp() {
            intOption = Option.builder("i").hasArg().type(Integer.class).build();
            doubleOption = Option.builder("d").hasArgs().type(Double.class).build();

            // Simula o parser adicionando valores
            intOption.processValue("123");
            doubleOption.processValue("45.6");
            doubleOption.processValue("78.9");

            cmd = builder.addOption(intOption).addOption(doubleOption).build();
        }

        @Test
        @DisplayName("Deve retornar o valor convertido para o tipo correto")
        void testGetParsedOptionValue() throws ParseException {
            assertEquals(123, (Integer) cmd.getParsedOptionValue("i"));
            assertEquals(45.6, (Double) cmd.getParsedOptionValue(doubleOption));
        }

        @Test
        @DisplayName("Deve lançar ParseException para valor inválido")
        void testGetParsedOptionValueWithInvalidValue() {
            Option badOption = Option.builder("bad").hasArg().type(Integer.class).build();
            badOption.processValue("not-a-number");
            CommandLine badCmd = new CommandLine.Builder().addOption(badOption).build();

            assertThrows(ParseException.class, () -> badCmd.getParsedOptionValue("bad"));
        }

        @Test
        @DisplayName("Deve retornar valor padrão se a opção não existir")
        void testGetParsedOptionValueWithDefault() throws ParseException {
            assertEquals(999, cmd.getParsedOptionValue("missing", 999));
            Supplier<Integer> supplier = () -> 888;
            assertEquals(888, cmd.getParsedOptionValue("missing", supplier));
        }

        @Test
        @DisplayName("Deve retornar array de valores convertidos")
        void testGetParsedOptionValues() throws ParseException {
            Double[] expected = {45.6, 78.9};
            assertArrayEquals(expected, cmd.getParsedOptionValues(doubleOption));
        }
    }

    @Nested
    @DisplayName("Testes com OptionGroup")
    class OptionGroupTests {
        private OptionGroup group;
        private Option groupOpt1;
        private Option groupOpt2;

        @BeforeEach
        void setUp() {
            group = new OptionGroup();
            groupOpt1 = Option.builder("g1").hasArg().build();
            groupOpt2 = Option.builder("g2").hasArgs().type(Integer.class).build();
            group.addOption(groupOpt1);
            group.addOption(groupOpt2);
        }

        @Test
        @DisplayName("Deve retornar o valor da opção selecionada no grupo")
        void testGetOptionValueFromGroup() throws AlreadySelectedException {
            groupOpt1.processValue("groupValue");
            group.setSelected(groupOpt1); // Simula seleção pelo parser

            CommandLine cmd = builder.addOption(groupOpt1).build();
            assertEquals("groupValue", cmd.getOptionValue(group));
            assertArrayEquals(new String[]{"groupValue"}, cmd.getOptionValues(group));
        }

        @Test
        @DisplayName("Deve retornar nulo se nenhuma opção do grupo for selecionada")
        void testGetOptionValueFromUnselectedGroup() {
            CommandLine cmd = builder.build(); // Nenhum opção do grupo adicionada
            assertNull(cmd.getOptionValue(group));
            assertEquals("default", cmd.getOptionValue(group, "default"));
        }

        @Test
        @DisplayName("Deve retornar o valor parseado da opção selecionada no grupo")
        void testGetParsedOptionValueFromGroup() throws ParseException, AlreadySelectedException {
            groupOpt2.processValue("123");
            group.setSelected(groupOpt2);
            CommandLine cmd = builder.addOption(groupOpt2).build();

            assertEquals(123, (Integer) cmd.getParsedOptionValue(group));
        }
    }

    @Nested
    @DisplayName("Testes de Comportamento de Depreciação")
    class DeprecatedBehaviorTests {
        @Mock
        private Consumer<Option> mockHandler;
        private AutoCloseable closeable;
        private final PrintStream originalErr = System.err;
        private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

        @BeforeEach
        void setUp() {
            closeable = MockitoAnnotations.openMocks(this);
            System.setErr(new PrintStream(errContent));
        }

        @AfterEach
        void tearDown() throws Exception {
            closeable.close();
            System.setErr(originalErr);
        }

        @Test
        @DisplayName("O handler de depreciação deve ser chamado ao acessar uma opção obsoleta")
        void testDeprecatedHandlerIsCalled() {
            Option deprecatedOpt = Option.builder("dep").deprecated().build();
            CommandLine cmd = builder.addOption(deprecatedOpt).setDeprecatedHandler(mockHandler).build();

            // Acessa a opção para acionar o handler
            cmd.getOptionValues(deprecatedOpt);

            ArgumentCaptor<Option> optionCaptor = ArgumentCaptor.forClass(Option.class);
            verify(mockHandler).accept(optionCaptor.capture());
            assertEquals("dep", optionCaptor.getValue().getOpt());
        }

        @Test
        @DisplayName("O handler de depreciação não deve ser chamado para opção não obsoleta")
        void testDeprecatedHandlerNotCalled() {
            CommandLine cmd = builder.addOption(optionA).setDeprecatedHandler(mockHandler).build();
            cmd.getOptionValues(optionA);
            verify(mockHandler, never()).accept(optionA);
        }
        
        @Test
        @SuppressWarnings("deprecation")
        @DisplayName("getOptionObject deve retornar o valor e não imprimir erro em caso de sucesso")
        void testGetOptionObjectSuccess() {
            Option intOption = Option.builder("i").hasArg().type(Integer.class).build();
            intOption.processValue("42");
            CommandLine cmd = builder.addOption(intOption).build();
            
            Object value = cmd.getOptionObject('i');
            
            assertEquals(42, value);
            assertTrue(errContent.toString().isEmpty());
        }
        
        @Test
        @SuppressWarnings("deprecation")
        @DisplayName("getOptionObject deve retornar nulo e imprimir erro no System.err em caso de falha de parse")
        void testGetOptionObjectParseException() {
            Option intOption = Option.builder("i").hasArg().type(Integer.class).build();
            intOption.processValue("abc");
            CommandLine cmd = builder.addOption(intOption).build();
            
            assertNull(cmd.getOptionObject('i'));
            assertTrue(errContent.toString().contains("Exception found converting i to desired type"));
        }
    }

    @Test
    @DisplayName("O iterador deve percorrer todas as opções")
    void testIterator() {
        CommandLine cmd = builder.addOption(optionA).addOption(optionB).build();
        int count = 0;
        for (Option opt : cmd.getOptions()) {
            assertNotNull(opt);
            count++;
        }
        assertEquals(2, count);
    }
}