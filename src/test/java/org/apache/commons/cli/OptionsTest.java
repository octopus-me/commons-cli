package org.apache.commons.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe {@link Options}.
 */
@DisplayName("Testes da Classe Options")
class OptionsTest {

    private Options options;
    private Option optionA;
    private Option optionB;

    @BeforeEach
    void setUp() {
        options = new Options();
        optionA = new Option("a", "alpha", true, "Option A");
        optionB = new Option("b", "beta", false, "Option B");
    }

    @Test
    @DisplayName("Um novo objeto Options deve estar vazio")
    void testNewOptionsIsEmpty() {
        assertTrue(options.getOptions().isEmpty(), "A coleção de opções deve estar vazia");
        assertTrue(options.getRequiredOptions().isEmpty(), "A lista de opções obrigatórias deve estar vazia");
    }

    @Nested
    @DisplayName("Testes de Adição de Opções")
    class AddOptionTests {

        @Test
        @DisplayName("addOption deve adicionar uma Option corretamente")
        void testAddOptionObject() {
            options.addOption(optionA);
            assertTrue(options.hasOption("a"), "Deve ter a opção curta 'a'");
            assertTrue(options.hasLongOption("alpha"), "Deve ter a opção longa 'alpha'");
            assertEquals(optionA, options.getOption("a"));
        }

        @Test
        @DisplayName("addOption com parâmetros deve criar e adicionar a opção corretamente")
        void testAddOptionWithParameters() {
            options.addOption("c", "gamma", true, "Option C");
            assertTrue(options.hasOption("c"));
            assertTrue(options.hasLongOption("gamma"));

            Option createdOption = options.getOption("c");
            assertTrue(createdOption.hasArg(), "A opção criada deve aceitar argumento");
            assertEquals("gamma", createdOption.getLongOpt());
        }

        @Test
        @DisplayName("addRequiredOption deve adicionar uma opção como obrigatória")
        void testAddRequiredOption() {
            options.addRequiredOption("r", "required", true, "Required option");
            Option requiredOpt = options.getOption("r");

            assertTrue(requiredOpt.isRequired(), "A opção deve ser marcada como obrigatória");
            assertTrue(options.getRequiredOptions().contains("r"), "A chave da opção deve estar na lista de obrigatórias");
        }
    }

    @Nested
    @DisplayName("Testes de Grupos de Opções (OptionGroup)")
    class OptionGroupTests {
        
        private OptionGroup group;

        @BeforeEach
        void setUpGroup() {
            group = new OptionGroup();
            group.addOption(optionA);
            group.addOption(optionB);
        }

        @Test
        @DisplayName("addOptionGroup deve adicionar todas as opções do grupo")
        void testAddOptionGroup() {
            options.addOptionGroup(group);
            
            assertTrue(options.hasOption("a"), "Deve conter a opção 'a' do grupo");
            assertTrue(options.hasOption("b"), "Deve conter a opção 'b' do grupo");
            assertEquals(group, options.getOptionGroup(optionA), "Deve retornar o grupo correto para a opção 'a'");
        }

        @Test
        @DisplayName("Adicionar um grupo obrigatório deve adicionar o grupo à lista de obrigatórios")
        void testAddRequiredOptionGroup() {
            group.setRequired(true);
            options.addOptionGroup(group);

            assertTrue(options.getRequiredOptions().contains(group), "A lista de obrigatórios deve conter o objeto do grupo");
            assertFalse(options.getRequiredOptions().contains("a"), "A lista de obrigatórios não deve conter a chave da opção individual");
        }

        @Test
        @DisplayName("Opções em um grupo devem ser marcadas como não obrigatórias")
        void testOptionsInGroupAreSetToNotRequired() {
            Option requiredInGroup = new Option("r", "required-in-group", false, "");
            requiredInGroup.setRequired(true);

            // Adiciona a opção obrigatória individualmente primeiro
            options.addOption(requiredInGroup);
            assertTrue(options.getRequiredOptions().contains("r"), "A opção 'r' deve ser obrigatória inicialmente");
            
            // Agora, adiciona um grupo que contém essa mesma opção
            OptionGroup newGroup = new OptionGroup();
            newGroup.addOption(requiredInGroup);
            options.addOptionGroup(newGroup);

            assertFalse(requiredInGroup.isRequired(), "A flag 'required' da opção deve ser desativada");
            assertFalse(options.getRequiredOptions().contains("r"), "A chave da opção não deve mais estar na lista de obrigatórias");
        }

        @Test
        @DisplayName("getOptionGroups deve retornar uma coleção de grupos únicos")
        void testGetOptionGroupsReturnsUniqueGroups() {
            OptionGroup group1 = new OptionGroup().addOption(optionA);
            OptionGroup group2 = new OptionGroup().addOption(optionB);

            options.addOptionGroup(group1);
            options.addOptionGroup(group2);
            
            Collection<OptionGroup> groups = options.getOptionGroups();
            assertEquals(2, groups.size());
            assertTrue(groups.contains(group1));
            assertTrue(groups.contains(group2));
        }
    }

    @Nested
    @DisplayName("Testes de Consulta e Verificação")
    class QueryingAndCheckingTests {

        @BeforeEach
        void addSampleOptions() {
            options.addOption(optionA);
            options.addOption(optionB);
        }

        @Test
        @DisplayName("hasOption deve funcionar para nomes curtos e longos com ou sem hifens")
        void testHasOption() {
            assertTrue(options.hasOption("a"));
            assertTrue(options.hasOption("-a"));
            assertTrue(options.hasOption("beta"));
            assertTrue(options.hasOption("--beta"));
            assertFalse(options.hasOption("c"));
        }

        @Test
        @DisplayName("getOption deve retornar a opção correta para nomes curtos e longos")
        void testGetOption() {
            assertEquals(optionA, options.getOption("a"));
            assertEquals(optionA, options.getOption("alpha"));
            assertNull(options.getOption("c"), "Deve retornar nulo para opção inexistente");
        }

        @Test
        @DisplayName("getMatchingOptions deve encontrar correspondências parciais e exatas")
        void testGetMatchingOptions() {
            options.addOption("f", "file", false, "");
            options.addOption("fi", "filter", false, "");
            
            List<String> matchesForF = options.getMatchingOptions("f");
            assertEquals(2, matchesForF.size());
            assertTrue(matchesForF.contains("file"));
            assertTrue(matchesForF.contains("filter"));

            List<String> matchesForFile = options.getMatchingOptions("file");
            assertEquals(1, matchesForFile.size());
            assertTrue(matchesForFile.contains("file"), "Uma correspondência exata deve retornar apenas ela mesma");

            assertTrue(options.getMatchingOptions("z").isEmpty(), "Não deve encontrar correspondências");
        }
    }

    @Nested
    @DisplayName("Testes de Coleções Retornadas")
    class ReturnedCollectionsTests {
        
        @Test
        @DisplayName("getOptions deve retornar uma coleção não modificável")
        void testGetOptionsIsUnmodifiable() {
            options.addOption(optionA);
            Collection<Option> retrievedOptions = options.getOptions();
            
            assertThrows(UnsupportedOperationException.class, () -> retrievedOptions.add(optionB));
        }

        @Test
        @DisplayName("getRequiredOptions deve retornar uma lista não modificável")
        @SuppressWarnings({"rawtypes", "unchecked"}) // Necessário para testar o contrato de imutabilidade em uma List<?>
        void testGetRequiredOptionsIsUnmodifiable() {
            options.addRequiredOption("r", "req", false, "");
            // Usa um tipo raw 'List' para contornar a verificação de tipo em tempo de compilação do método add.
            List required = options.getRequiredOptions();

            // A chamada 'add' agora compila e lançará a exceção em tempo de execução, que é o que queremos testar.
            assertThrows(UnsupportedOperationException.class, () -> required.add("another"));
        }
    }

    @Nested
    @DisplayName("Testes de Cenários de Erro")
    class ErrorScenarioTests {

        @Test
        @DisplayName("addOptions deve lançar exceção ao adicionar chave duplicada")
        void testAddOptionsThrowsOnDuplicate() {
            options.addOption(optionA);
            
            Options otherOptions = new Options();
            otherOptions.addOption(optionA); // Adiciona a mesma opção

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> options.addOptions(otherOptions)
            );
            
            assertEquals("Duplicate key: a", exception.getMessage());
        }
    }

    @Test
    @DisplayName("toString deve gerar uma representação de string correta")
    void testToString() {
        options.addOption(optionA).addOption(optionB);
        String str = options.toString();
        
        assertTrue(str.contains("short"), "Deve conter a seção de opções curtas");
        assertTrue(str.contains("long"), "Deve conter a seção de opções longas");
        assertTrue(str.contains(optionA.getOpt() + "="), "Deve conter a opção A");
        assertTrue(str.contains(optionB.getLongOpt() + "="), "Deve conter a opção B");
    }
}