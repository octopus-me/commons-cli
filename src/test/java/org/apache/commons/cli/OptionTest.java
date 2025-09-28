package org.apache.commons.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe {@link Option}.
 */
@DisplayName("Testes da Classe Option")
class OptionTest {

    @Nested
    @DisplayName("Testes de Construtores")
    class ConstructorTests {

        @Test
        @DisplayName("Construtor (String, String) deve definir propriedades corretamente")
        void testConstructorWithShortOptAndDescription() {
            Option option = new Option("a", "Habilita funcionalidade A");
            assertAll("Propriedades da Option",
                () -> assertEquals("a", option.getOpt(), "A opção curta deve ser 'a'"),
                () -> assertNull(option.getLongOpt(), "A opção longa deve ser nula"),
                () -> assertEquals("Habilita funcionalidade A", option.getDescription(), "A descrição deve corresponder"),
                () -> assertFalse(option.hasArg(), "Não deve ter argumento por padrão"),
                () -> assertEquals(-1, option.getArgs(), "O número de argumentos deve ser UNINITIALIZED")
            );
        }

        @Test
        @DisplayName("Construtor (String, boolean, String) deve definir propriedades corretamente")
        void testConstructorWithShortOptHasArgAndDescription() {
            Option optionWithArg = new Option("b", true, "Define o valor para B");
            assertAll("Propriedades da Option com argumento",
                () -> assertEquals("b", optionWithArg.getOpt(), "A opção curta deve ser 'b'"),
                () -> assertTrue(optionWithArg.hasArg(), "Deve ter um argumento"),
                () -> assertEquals(1, optionWithArg.getArgs(), "O número de argumentos deve ser 1"),
                () -> assertEquals("Define o valor para B", optionWithArg.getDescription(), "A descrição deve corresponder")
            );

            Option optionWithoutArg = new Option("c", false, "Habilita funcionalidade C");
            assertAll("Propriedades da Option sem argumento",
                () -> assertEquals("c", optionWithoutArg.getOpt(), "A opção curta deve ser 'c'"),
                () -> assertFalse(optionWithoutArg.hasArg(), "Não deve ter um argumento"),
                () -> assertEquals(-1, optionWithoutArg.getArgs(), "O número de argumentos deve ser UNINITIALIZED")
            );
        }

        @Test
        @DisplayName("Construtor (String, String, boolean, String) deve definir todas as propriedades")
        void testFullConstructor() {
            Option option = new Option("d", "detail", true, "Define o nível de detalhe");
            assertAll("Propriedades completas da Option",
                () -> assertEquals("d", option.getOpt(), "A opção curta deve ser 'd'"),
                () -> assertEquals("detail", option.getLongOpt(), "A opção longa deve ser 'detail'"),
                () -> assertTrue(option.hasArg(), "Deve ter um argumento"),
                () -> assertEquals(1, option.getArgs(), "O número de argumentos deve ser 1"),
                () -> assertEquals("Define o nível de detalhe", option.getDescription(), "A descrição deve corresponder")
            );
        }


    }

    @Nested
    @DisplayName("Testes do Builder")
    class BuilderTests {

        @Test
        @DisplayName("Builder deve construir uma Option completa corretamente")
        void testBuilderBuildsCompleteOption() {
            Option option = Option.builder("o")
                .longOpt("output")
                .argName("file")
                .desc("Arquivo de saída")
                .required()
                .hasArg()
                .type(File.class)
                .valueSeparator('=')
                .build();

            assertAll("Propriedades da Option construída com Builder",
                () -> assertEquals("o", option.getOpt()),
                () -> assertEquals("output", option.getLongOpt()),
                () -> assertEquals("file", option.getArgName()),
                () -> assertEquals("Arquivo de saída", option.getDescription()),
                () -> assertTrue(option.isRequired()),
                () -> assertTrue(option.hasArg()),
                () -> assertEquals(1, option.getArgs()),
                () -> assertEquals(File.class, option.getType()),
                () -> assertEquals('=', option.getValueSeparator()),
                () -> assertTrue(option.hasValueSeparator())
            );
        }

        @Test
        @DisplayName("Builder deve tratar hasArgs() corretamente")
        void testBuilderWithHasArgs() {
            Option option = Option.builder("v").hasArgs().build();
            assertTrue(option.hasArgs(), "A opção deve aceitar múltiplos argumentos");
            assertEquals(Option.UNLIMITED_VALUES, option.getArgs(), "A contagem de argumentos deve ser UNLIMITED_VALUES");
        }
        
        @Test
        @DisplayName("Builder deve tratar numberOfArgs() corretamente")
        void testBuilderWithNumberOfArgs() {
            Option option = Option.builder("p").numberOfArgs(3).build();
            assertTrue(option.hasArgs(), "A opção deve ter argumentos");
            assertEquals(3, option.getArgs(), "A contagem de argumentos deve ser 3");
        }

        @Test
        @DisplayName("Builder deve tratar optionalArg() corretamente")
        void testBuilderWithOptionalArg() {
            Option option = Option.builder("o").optionalArg(true).build();
            assertTrue(option.hasOptionalArg(), "O argumento deve ser opcional");
            assertTrue(option.hasArg(), "A opção deve ter um argumento");
            assertEquals(1, option.getArgs(), "A contagem de argumentos deve ser 1 para argumento opcional");
        }
        
        @Test
        @DisplayName("Builder deve lançar IllegalStateException se nem opt nem longOpt forem definidos")
        void testBuilderThrowsExceptionWhenNoOptSet() {
            Option.Builder builder = Option.builder();
            Exception exception = assertThrows(IllegalStateException.class, builder::build);
            assertEquals("Either opt or longOpt must be specified", exception.getMessage());
        }

        @Test
        @DisplayName("Builder não deve lançar exceção se apenas longOpt for definido")
        void testBuilderWithOnlyLongOpt() {
            assertDoesNotThrow(() -> Option.builder().longOpt("file").build());
            Option option = Option.builder().longOpt("file").build();
            assertEquals("file", option.getLongOpt());
            assertNull(option.getOpt());
        }

        
        @Test
        @DisplayName("O método obsoleto build() deve funcionar como get()")
        @SuppressWarnings("deprecation")
        void testDeprecatedBuildMethod() {
            Option option = Option.builder("t").build();
            assertEquals("t", option.getOpt());
        }
        
        @Test
        @DisplayName("Definir o tipo como nulo deve usar String como padrão")
        void testBuilderTypeNullDefaultsToString() {
            Option option = Option.builder("t").type(null).build();
            assertEquals(String.class, option.getType());
        }
    }

    @Nested
    @DisplayName("Testes de Processamento de Valores")
    class ValueProcessingTests {
        private Option option;

        @Test
        @DisplayName("processValue deve adicionar um único valor corretamente")
        void testProcessValueSingle() {
            option = Option.builder("f").hasArg().build();
            option.processValue("value1");
            assertAll("Processamento de valor único",
                () -> assertEquals("value1", option.getValue()),
                () -> assertEquals(1, option.getValuesList().size()),
                () -> assertArrayEquals(new String[]{"value1"}, option.getValues())
            );
        }

        @Test
        @DisplayName("processValue deve lançar IllegalStateException para opção sem argumentos")
        void testProcessValueNoArgsAllowed() {
            option = new Option("a", "description");
            assertThrows(IllegalStateException.class, () -> option.processValue("any"));
        }

        @Test
        @DisplayName("processValue deve lidar com múltiplos valores para hasArgs()")
        void testProcessValueWithHasArgs() {
            option = Option.builder("f").hasArgs().build();
            option.processValue("value1");
            option.processValue("value2");
            assertAll("Processamento de múltiplos valores",
                () -> assertEquals("value1", option.getValue()),
                () -> assertEquals(2, option.getValuesList().size()),
                () -> assertArrayEquals(new String[]{"value1", "value2"}, option.getValues())
            );
        }
        
        @Test
        @DisplayName("processValue deve lidar com um número limitado de argumentos")
        void testProcessValueWithLimitedArgs() {
            option = Option.builder("f").numberOfArgs(2).build();
            option.processValue("val1");

            assertTrue(option.requiresArg(), "Teste alterado manualmente por um erro de entendimento da LLM");
            assertTrue(option.acceptsArg(), "Ainda deve aceitar outro argumento");

            option.processValue("val2");
            assertFalse(option.acceptsArg(), "Não deve aceitar mais argumentos");

            assertThrows(IllegalArgumentException.class, () -> option.processValue("val3"), "Deve lançar exceção quando estiver cheio");
        }

        @Test
        @DisplayName("processValue deve dividir valores usando o separador de valor")
        void testProcessValueWithValueSeparator() {
            option = Option.builder("D").hasArgs().valueSeparator('=').build();
            option.processValue("key=value");
            assertArrayEquals(new String[]{"key", "value"}, option.getValues());
        }

        @Test
        @DisplayName("processValue não deve dividir o último valor com separador se o limite de argumentos for atingido")
        void testProcessValueSeparatorLimit() {
            option = Option.builder("D").numberOfArgs(2).valueSeparator('=').build();
            option.processValue("key=value=more");
            assertArrayEquals(new String[]{"key", "value=more"}, option.getValues());
        }

        @Test
        @DisplayName("getValue(index) deve retornar o valor correto ou lançar exceção")
        void testGetValueByIndex() {
            option = Option.builder("f").hasArgs().build();
            option.processValue("v1");
            option.processValue("v2");
            
            assertEquals("v1", option.getValue(0));
            assertEquals("v2", option.getValue(1));
            assertThrows(IndexOutOfBoundsException.class, () -> option.getValue(2));
            assertThrows(IndexOutOfBoundsException.class, () -> option.getValue(-1));
        }

        @Test
        @DisplayName("getValue(defaultValue) deve funcionar corretamente")
        void testGetValueWithDefault() {
            option = Option.builder("f").hasArg().build();
            assertEquals("default", option.getValue("default"));
            option.processValue("actual");
            assertEquals("actual", option.getValue("default"));
        }

        @Test
        @DisplayName("clearValues deve remover todos os valores processados")
        void testClearValues() {
            option = Option.builder("f").hasArgs().build();
            option.processValue("v1");

            assertFalse(option.getValuesList().isEmpty());
            option.clearValues();
            assertTrue(option.getValuesList().isEmpty());
            assertNull(option.getValues());
            assertNull(option.getValue());
        }

        @Test
        @DisplayName("addValue deve lançar UnsupportedOperationException")
        @SuppressWarnings("deprecation")
        void testAddValue() {
            option = new Option("a", "desc");
            assertThrows(UnsupportedOperationException.class, () -> option.addValue("some-value"));
        }
    }
    
    @Nested
    @DisplayName("Testes de Métodos de Estado e Propriedades")
    class StateAndPropertyMethodsTests {

        @Test
        @DisplayName("getKey deve retornar a opção curta se presente, senão a longa")
        void testGetKey() {
            Option shortOnly = new Option("a", "desc");
            assertEquals("a", shortOnly.getKey());

            Option longOnly = Option.builder().longOpt("alpha").build();
            assertEquals("alpha", longOnly.getKey());
            
            Option both = new Option("a", "alpha", false, "desc");
            assertEquals("a", both.getKey());
        }
        
        @Test
        @DisplayName("getId deve retornar o primeiro caractere da chave")
        void testGetId() {
            Option shortOpt = new Option("t", "desc");
            assertEquals('t', shortOpt.getId());

            Option longOptOnly = Option.builder().longOpt("test").build();
            assertEquals('t', longOptOnly.getId());
        }
        
        @Test
        @DisplayName("requiresArg deve se comportar corretamente")
        void testRequiresArg() {
            // Argumento opcional nunca é requerido
            Option opt = Option.builder("o").optionalArg(true).build();
            assertFalse(opt.requiresArg());
            
            // Argumentos ilimitados requerem um se vazio
            opt = Option.builder("u").hasArgs().build();
            assertTrue(opt.requiresArg());
            opt.processValue("v1");
            assertFalse(opt.requiresArg());
            
            // Argumento padrão requer um se vazio
            opt = Option.builder("s").hasArg().build();
            assertTrue(opt.requiresArg());
            opt.processValue("v1");
            assertFalse(opt.requiresArg());
        }

        @Test
        @DisplayName("O método obsoleto setType deve funcionar")
        @SuppressWarnings("deprecation")
        void testSetTypeDeprecated() {
            Option option = new Option("t", "test");
            option.setType(Integer.class);
            assertEquals(Integer.class, option.getType());
        }
    }

    @Nested
    @DisplayName("Testes de Métodos de Objeto (equals, hashCode, clone, toString)")
    class ObjectMethodsTests {

        private final Option optionA1 = new Option("a", "alpha", false, "desc");
        private final Option optionA2 = new Option("a", "alpha", true, "different desc");
        private final Option optionB = new Option("b", "beta", false, "desc");
        private final Option optionAlpha = Option.builder().longOpt("alpha").build();
        private final Option optionAOnly = new Option("a", "desc");

        @Test
        @DisplayName("equals deve ser reflexivo, simétrico e consistente")
        void testEquals() {
            // Reflexivo
            assertEquals(optionA1, optionA1);
            
            // Simétrico
            assertEquals(optionA1, optionA2);
            assertEquals(optionA2, optionA1);
            
            // Inconsistente
            assertNotEquals(optionA1, optionB);
            assertNotEquals(optionA1, null);
            assertNotEquals(optionA1, new Object());
            
            // Curta vs. Longa
            assertNotEquals(optionA1, optionAlpha, "Não devem ser iguais se uma tem opção curta e a outra não");
            
        }
        
        @Test
        @DisplayName("hashCode deve ser consistente com equals")
        void testHashCode() {
            assertEquals(optionA1.hashCode(), optionA2.hashCode());
            assertNotEquals(optionA1.hashCode(), optionB.hashCode());
        }
        
        @Test
        @DisplayName("clone deve criar uma cópia profunda dos valores")
        void testClone() {
            Option original = Option.builder("c").hasArgs().build();
            original.processValue("v1");
            
            Option cloned = (Option) original.clone();
            
            assertNotSame(original, cloned, "O clone deve ser um objeto diferente");
            assertEquals(original, cloned, "O clone deve ser igual ao original");
            
            // Verifica se a lista de valores é uma instância separada
            assertNotSame(original.getValuesList(), cloned.getValuesList(), "A lista de valores deve ser uma instância diferente");
            assertEquals(original.getValuesList(), cloned.getValuesList(), "A lista de valores deve ter o mesmo conteúdo");
            
            // Modifica o original e verifica se o clone não é afetado
            original.processValue("v2");

            assertEquals(2, original.getValuesList().size());
            assertEquals(1, cloned.getValuesList().size());
        }

        @Test
        @DisplayName("toString deve conter os principais detalhes da opção")
        void testToString() {
            Option option = new Option("a", "arg-name", true, "description");
            String str = option.toString();
            
            assertTrue(str.contains("[ Option a arg-name"));
            assertTrue(str.contains(" [ARG]"));
            assertTrue(str.contains(" :: description"));
            assertTrue(str.contains(" :: class java.lang.String ]"));
        }
        
        @Test
        @DisplayName("toString para opção com hasArgs")
        void testToStringHasArgs() {
            Option option = Option.builder("b").hasArgs().desc("files").build();
            String str = option.toString();
            assertTrue(str.contains("[ARG...]"));
        }
    }
}