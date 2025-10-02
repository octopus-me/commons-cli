package org.apache.commons.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe {@link OptionGroup}.
 */
@DisplayName("Testes da Classe OptionGroup")
class OptionGroupTest {

    private OptionGroup group;
    private Option optionA;
    private Option optionB;
    private Option optionC;

    @BeforeEach
    void setUp() {
        // Inicializa um novo grupo e opções antes de cada teste para garantir o isolamento
        group = new OptionGroup();
        optionA = new Option("a", "alpha", false, "Option A");
        optionB = new Option("b", "beta", false, "Option B");
        optionC = Option.builder("c").longOpt("gamma").desc("Option C").build();
    }

    @Test
    @DisplayName("Um novo OptionGroup deve estar vazio e não ser obrigatório")
    void testNewOptionGroupIsEmptyAndNotRequired() {
        assertAll("Estado inicial do grupo",
            () -> assertTrue(group.getOptions().isEmpty(), "O grupo deve começar sem opções"),
            () -> assertTrue(group.getNames().isEmpty(), "O grupo deve começar sem nomes de opções"),
            () -> assertFalse(group.isRequired(), "O grupo não deve ser obrigatório por padrão"),
            () -> assertNull(group.getSelected(), "Nenhuma opção deve estar selecionada"),
            () -> assertFalse(group.isSelected(), "isSelected() deve retornar falso")
        );
    }

    @Nested
    @DisplayName("Testes de Adição de Opções")
    class AddOptionTests {

        @Test
        @DisplayName("addOption deve adicionar uma opção ao grupo com sucesso")
        void testAddOptionSuccessfully() {
            group.addOption(optionA);

            assertEquals(1, group.getOptions().size(), "O grupo deve conter uma opção");
            assertTrue(group.getOptions().contains(optionA), "A opção A deve estar no grupo");
            assertTrue(group.getNames().contains(optionA.getKey()), "O nome da opção A deve estar no grupo");
        }

        @Test
        @DisplayName("addOption deve permitir adicionar múltiplas opções em cadeia")
        void testAddMultipleOptionsWithChaining() {
            group.addOption(optionA).addOption(optionB).addOption(optionC);

            assertEquals(3, group.getOptions().size(), "O grupo deve conter três opções");
            Collection<Option> options = group.getOptions();
            assertTrue(options.contains(optionA));
            assertTrue(options.contains(optionB));
            assertTrue(options.contains(optionC));
        }
    }

    @Nested
    @DisplayName("Testes da Lógica de Seleção")
    class SelectionLogicTests {

        @BeforeEach
        void addOptionsToGroup() {
            group.addOption(optionA).addOption(optionB);
        }

        @Test
        @DisplayName("setSelected deve definir uma opção como selecionada")
        void testSetSelectedShouldMarkOptionAsSelected() throws AlreadySelectedException {
            group.setSelected(optionA);

            assertTrue(group.isSelected(), "O grupo deve ter uma seleção");
            assertEquals("a", group.getSelected(), "A opção 'a' deve ser a selecionada");
        }

        @Test
        @DisplayName("setSelected deve lançar AlreadySelectedException ao tentar selecionar outra opção")
        void testSetSelectedShouldThrowExceptionWhenAnotherOptionIsAlreadySelected() throws AlreadySelectedException {
            // Primeiro, seleciona uma opção
            group.setSelected(optionA);

            // Tenta selecionar uma opção diferente e espera uma exceção
            AlreadySelectedException exception = assertThrows(
                AlreadySelectedException.class,
                () -> group.setSelected(optionB),
                "Deveria lançar AlreadySelectedException"
            );

            // Verifica a mensagem da exceção e o estado do grupo
            assertEquals("The option 'b' was specified but an option from this group has already been selected: 'a'", exception.getMessage());
            assertEquals("a", group.getSelected(), "A seleção original ('a') deve ser mantida");
        }

        @Test
        @DisplayName("setSelected com a mesma opção não deve lançar exceção")
        void testSetSelectedWithSameOptionShouldNotThrowException() {
            assertDoesNotThrow(() -> {
                group.setSelected(optionA);
                group.setSelected(optionA); // Selecionando a mesma opção novamente
            }, "Selecionar a mesma opção repetidamente não deve causar erro");

            assertEquals("a", group.getSelected(), "A opção 'a' deve permanecer selecionada");
        }

        @Test
        @DisplayName("setSelected com nulo deve limpar a seleção")
        void testSetSelectedWithNullShouldClearSelection() throws AlreadySelectedException {
            // Primeiro, seleciona uma opção
            group.setSelected(optionA);
            assertEquals("a", group.getSelected());

            // Limpa a seleção
            group.setSelected(null);

            assertNull(group.getSelected(), "A seleção deve ser nula após passar null");
            assertFalse(group.isSelected(), "isSelected() deve retornar falso após limpar a seleção");
        }
    }

    @Nested
    @DisplayName("Testes de Propriedades e Estado")
    class PropertiesAndStateTests {

        @Test
        @DisplayName("setRequired deve atualizar o estado de obrigatoriedade do grupo")
        void testSetRequired() {
            assertFalse(group.isRequired(), "O grupo não é obrigatório por padrão");

            group.setRequired(true);
            assertTrue(group.isRequired(), "O grupo deve ser obrigatório após setRequired(true)");

            group.setRequired(false);
            assertFalse(group.isRequired(), "O grupo não deve ser obrigatório após setRequired(false)");
        }

        @Test
        @DisplayName("getNames deve retornar os nomes corretos das opções adicionadas")
        void testGetNames() {
            group.addOption(optionA).addOption(optionB);
            Option longOnlyOption = Option.builder().longOpt("delta").build();
            group.addOption(longOnlyOption);

            Collection<String> names = group.getNames();
            assertEquals(3, names.size());
            assertTrue(names.contains("a"), "Deve conter o nome da opção A");
            assertTrue(names.contains("b"), "Deve conter o nome da opção B");
            assertTrue(names.contains("delta"), "Deve conter o nome da opção apenas longa");
        }
    }

    @Nested
    @DisplayName("Testes do método toString")
    class ToStringTests {

        @Test
        @DisplayName("toString de um grupo vazio deve retornar '[]'")
        void testToStringOnEmptyGroup() {
            assertEquals("[]", group.toString());
        }

        @Test
        @DisplayName("toString deve formatar corretamente uma única opção curta")
        void testToStringWithSingleShortOption() {
            Option opt = new Option("f", "file");
            group.addOption(opt);
            assertEquals("[-f file]", group.toString());
        }

        @Test
        @DisplayName("toString deve formatar corretamente uma única opção longa")
        void testToStringWithSingleLongOption() {
            Option opt = Option.builder().longOpt("filename").desc("the file name").build();
            group.addOption(opt);
            assertEquals("[--filename the file name]", group.toString());
        }
        
        @Test
        @DisplayName("toString deve formatar corretamente múltiplas opções")
        void testToStringWithMultipleOptions() {
            group.addOption(optionA).addOption(optionB);
            String expected = "[-a Option A, -b Option B]";
            assertEquals(expected, group.toString());
        }

        @Test
        @DisplayName("toString deve formatar corretamente opções sem descrição")
        void testToStringWithNoDescription() {
            Option opt1 = new Option("x", null);
            Option opt2 = new Option("y", null);
            group.addOption(opt1).addOption(opt2);
            assertEquals("[-x, -y]", group.toString());
        }
    }
}