package org.apache.commons.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Testes unitários abrangentes para a classe {@link HelpFormatter}.
 */
@DisplayName("Testes da Classe HelpFormatter")
@SuppressWarnings("deprecation") // A classe HelpFormatter está obsoleta
class HelpFormatterTest {

    private StringWriter stringWriter;
    private PrintWriter printWriter;
    private HelpFormatter formatter;
    private Options options;
    private final String EOL = System.lineSeparator();

    @BeforeEach
    void setUp() {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        formatter = new HelpFormatter();
        options = new Options();
    }

    private String getTestOutput() {
        return stringWriter.toString();
    }

    @Nested
    @DisplayName("Testes do Builder")
    class BuilderTests {

        @Test
        @DisplayName("Deve construir um HelpFormatter com PrintWriter customizado")
        void testBuilderWithCustomPrintWriter() {
            HelpFormatter customFormatter = HelpFormatter.builder().setPrintWriter(printWriter).get();
            customFormatter.printHelp("testApp", new Options());
            // Apenas verifica se algo foi escrito, provando que o PrintWriter foi usado.
            assertTrue(getTestOutput().length() > 0);
        }

        @Test
        @DisplayName("setShowDeprecated(true) deve usar o formato padrão para opções obsoletas")
        void testBuilderSetShowDeprecatedTrue() {
            options.addOption(Option.builder("d").deprecated().desc("deprecated option").build());
            HelpFormatter customFormatter = HelpFormatter.builder().setShowDeprecated(true).get();
            customFormatter.printOptions(printWriter, 80, options, 1, 3);

            assertTrue(getTestOutput().contains("[Deprecated] deprecated option"));
        }

        @Test
        @DisplayName("setShowDeprecated(false) não deve formatar opções obsoletas de forma especial")
        void testBuilderSetShowDeprecatedFalse() {
            options.addOption(Option.builder("d").deprecated().desc("deprecated option").build());
            HelpFormatter customFormatter = HelpFormatter.builder().setShowDeprecated(false).get();
            customFormatter.printOptions(printWriter, 80, options, 1, 3);

            // A descrição original é usada, sem o prefixo [Deprecated]
            assertTrue(getTestOutput().contains("deprecated option"));
            assertFalse(getTestOutput().contains("[Deprecated]"));
        }

        @Test
        @DisplayName("setShowSince(true) deve exibir a coluna 'Since'")
        void testBuilderSetShowSinceTrue() {
            // Adiciona uma opção longa para evitar NegativeArraySizeException na SUT
            options.addOption(Option.builder("l").longOpt("long-opt").since("1.2").build());
            options.addOption(Option.builder("s").since("1.1").desc("since option").build());
            HelpFormatter customFormatter = HelpFormatter.builder().setShowSince(true).get();
            customFormatter.printOptions(printWriter, 80, options, 1, 3);
            
            assertTrue(getTestOutput().contains("Since"));
            assertTrue(getTestOutput().contains("1.1"));
        }
    }

    @Nested
    @DisplayName("Testes de Formatação e Impressão")
    class FormattingAndPrintingTests {

        @Test
        @DisplayName("Deve imprimir a ajuda completa com cabeçalho, rodapé e auto-uso")
        void testPrintHelpFull() {
            options.addOption("a", "alpha", false, "toggle alpha");
            options.addOption("b", "beta", true, "beta value");
            options.addOption(Option.builder().longOpt("gamma").required().desc("gamma toggle").build());

            String header = "Header" + EOL + "More header.";
            String footer = "Footer" + EOL + "More footer.";
            String cmdLineSyntax = "testApp";

            formatter.printHelp(printWriter, 80, cmdLineSyntax, header, options, 1, 3, footer, true);

            String expected = "usage: testApp [-a] [-b <arg>] --gamma" + EOL +
                              "Header" + EOL +
                              "More header." + EOL +
                              " -a,--alpha        toggle alpha" + EOL +
                              " -b,--beta <arg>   beta value" + EOL +
                              "    --gamma        gamma toggle" + EOL +
                              "Footer" + EOL +
                              "More footer." + EOL;
            assertEquals(expected, getTestOutput());
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException se a sintaxe do comando for nula ou vazia")
        void testPrintHelpWithEmptyCmdLineSyntax() {
            assertThrows(IllegalArgumentException.class,
                () -> formatter.printHelp(printWriter, 80, null, "header", options, 1, 3, "footer"));
            assertThrows(IllegalArgumentException.class,
                () -> formatter.printHelp(printWriter, 80, "", "header", options, 1, 3, "footer"));
        }
        
        @Test
        @DisplayName("Deve formatar OptionGroups corretamente na linha de uso")
        void testPrintHelpWithOptionGroups() {
            OptionGroup group = new OptionGroup();
            group.addOption(new Option("a", ""));
            group.addOption(new Option("b", ""));
            options.addOptionGroup(group);

            formatter.printUsage(printWriter, 80, "testApp", options);
            assertEquals("usage: testApp [-a | -b]" + EOL, getTestOutput());
        }

        @Test
        @DisplayName("Deve respeitar um comparador de opções customizado")
        void testPrintHelpWithCustomComparator() {
            options.addOption("c", "c option");
            options.addOption("a", "a option");
            options.addOption("b", "b option");
            
            // Ordenação reversa
            formatter.setOptionComparator(Comparator.comparing(Option::getOpt).reversed());
            formatter.printOptions(printWriter, 80, options, 1, 3);
            
            String output = getTestOutput();
            int posC = output.indexOf("-c");
            int posB = output.indexOf("-b");
            int posA = output.indexOf("-a");

            assertTrue(posC < posB, "Opção 'c' deve vir antes de 'b'");
            assertTrue(posB < posA, "Opção 'b' deve vir antes de 'a'");
        }

        @Test
        @DisplayName("Deve usar o formato de depreciação customizado")
        @SuppressWarnings("unchecked")
        void testPrintHelpWithCustomDeprecatedFormatter() {
            Function<Option, String> mockFormatter = Mockito.mock(Function.class);
            Mockito.when(mockFormatter.apply(any(Option.class))).thenReturn("IS DEPRECATED");
            
            Option deprecatedOpt = Option.builder("d").deprecated().desc("deprecated option").build();
            options.addOption(deprecatedOpt);
            options.addOption("a", "active option");

            HelpFormatter customFormatter = HelpFormatter.builder().setShowDeprecated(mockFormatter).get();
            customFormatter.printOptions(printWriter, 80, options, 1, 3);
            
            // Verifica que o mock foi chamado apenas para a opção obsoleta
            verify(mockFormatter, times(1)).apply(deprecatedOpt);
            verify(mockFormatter, never()).apply(options.getOption("a"));
            
            // Verifica se a saída customizada está presente
            assertTrue(getTestOutput().contains("IS DEPRECATED"));
        }
    }

    @Nested
    @DisplayName("Testes de Quebra de Linha (Wrapping)")
    class WrappingTests {
        @Test
        @DisplayName("findWrapPos deve encontrar a posição correta para quebra de linha")
        void testFindWrapPos() {
            // Teste com texto longo sem espaços
            String text = "thisisareallylongwordthatshouldbebroken";
            assertEquals(10, formatter.findWrapPos(text, 10, 0));

            // Teste com espaço antes do limite
            text = "word1 word2 word3";
            assertEquals(5, formatter.findWrapPos(text, 10, 0));
            
            // Teste sem necessidade de quebra
            text = "short text";
            assertEquals(-1, formatter.findWrapPos(text, 20, 0));
        }

        @Test
        @DisplayName("Deve quebrar a descrição da opção em múltiplas linhas")
        void testPrintOptionsWithLongDescription() {
            String longDesc = "This is a very long description that will certainly need to be wrapped into multiple lines to fit the screen width.";
            options.addOption("a", "alpha", false, longDesc);

            formatter.printOptions(printWriter, 40, options, 1, 3);
            
            String expected = " -a,--alpha   This is a very long" + EOL +
                              "              description that will" + EOL +
                              "              certainly need to be" + EOL +
                              "              wrapped into multiple" + EOL +
                              "              lines to fit the screen" + EOL +
                              "              width." + EOL;
            
            assertEquals(expected, getTestOutput());
        }
    }

    @Nested
    @DisplayName("Testes de Getters e Setters")
    class AccessorTests {
        @Test
        @DisplayName("Setters devem alterar as propriedades de formatação")
        void testSetters() {
            formatter.setWidth(80);
            formatter.setLeftPadding(5);
            formatter.setDescPadding(10);
            formatter.setSyntaxPrefix("how to use: ");
            formatter.setNewLine("\n");
            formatter.setOptPrefix("/");
            formatter.setLongOptPrefix("//");
            formatter.setArgName("argument");
            formatter.setLongOptSeparator("=");

            assertEquals(80, formatter.getWidth());
            assertEquals(5, formatter.getLeftPadding());
            assertEquals(10, formatter.getDescPadding());
            assertEquals("how to use: ", formatter.getSyntaxPrefix());
            assertEquals("\n", formatter.getNewLine());
            assertEquals("/", formatter.getOptPrefix());
            assertEquals("//", formatter.getLongOptPrefix());
            assertEquals("argument", formatter.getArgName());
            assertEquals("=", formatter.getLongOptSeparator());
        }
    }
}