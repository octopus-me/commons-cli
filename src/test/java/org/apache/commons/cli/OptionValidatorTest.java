package org.apache.commons.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários para a classe {@link OptionValidator}.
 */
@DisplayName("Testes do OptionValidator")
class OptionValidatorTest {

    @ParameterizedTest
    @NullSource
    @DisplayName("validate deve retornar nulo para entrada nula")
    void validateShouldReturnNullForNullInput(String input) {
        assertNull(OptionValidator.validate(input), "A validação de uma string nula deve retornar nulo");
    }

    @ParameterizedTest
    @EmptySource
    @DisplayName("validate deve lançar IllegalArgumentException para entrada vazia")
    void validateShouldThrowExceptionForEmptyInput(String input) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate(input));
        assertEquals("Empty option name.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "Z", "?", "@", "_", "$", "9"})
    @DisplayName("validate deve passar para opções válidas de um único caractere")
    void validateShouldPassForValidSingleCharacterOptions(String option) {
        assertDoesNotThrow(() -> {
            String result = OptionValidator.validate(option);
            assertEquals(option, result, "O resultado da validação deve ser a própria string de entrada");
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"-", " ", "!", "+", "*", "/"})
    @DisplayName("validate deve lançar IllegalArgumentException para opções inválidas de um único caractere")
    void validateShouldThrowExceptionForInvalidSingleCharacterOptions(String option) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate(option));
        String expectedMessage = String.format("Illegal option name '%s'.", option.charAt(0));
        assertEquals(expectedMessage, exception.getMessage());
    }


    @Test
    @DisplayName("validate deve lançar IllegalArgumentException para primeiro caractere inválido em opção de múltiplos caracteres")
    void validateShouldThrowExceptionForInvalidFirstCharInMultiCharOption() {
        String option = "-option";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate(option));
        assertEquals("Illegal option name '-'.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"option!", "my opt", "a+", "b/c", "test*"})
    @DisplayName("validate deve lançar IllegalArgumentException para caractere subsequente inválido")
    void validateShouldThrowExceptionForInvalidSubsequentChar(String option) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate(option));
        // Encontra o primeiro caractere inválido para construir a mensagem esperada
        char invalidChar = ' ';
        for (char c : option.toCharArray()) {
            if (!Character.isJavaIdentifierPart(c) && c != '-') {
                invalidChar = c;
                break;
            }
        }
        String expectedMessage = String.format("The option '%s' contains an illegal character : '%s'.", option, invalidChar);
        assertTrue(exception.getMessage().contains(expectedMessage),
            "A mensagem de exceção deve indicar o caractere ilegal. Esperado: " + expectedMessage + ", Recebido: " + exception.getMessage());
    }
}