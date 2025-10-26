package org.apache.commons.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Extensive unit tests for {@link OptionValidator}.
 * Covers valid, invalid, null, and edge cases to ensure validation logic correctness.
 */
class OptionValidatorTest {

    // ---------------------------------------------------------
    // Tests for validate()
    // ---------------------------------------------------------

    @Test
    @DisplayName("validate should return null when input is null")
    void testValidateWithNullOption() {
        assertNull(OptionValidator.validate(null));
    }

    @Test
    @DisplayName("validate should throw exception for empty string")
    void testValidateWithEmptyString() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate(""));
        assertEquals("Empty option name.", ex.getMessage());
    }

    @Test
    @DisplayName("validate should accept single valid letter")
    void testValidateWithSingleLetter() {
        assertEquals("a", OptionValidator.validate("a"));
        assertEquals("Z", OptionValidator.validate("Z"));
    }

    @Test
    @DisplayName("validate should accept single valid special characters '?' and '@'")
    void testValidateWithSingleSpecialCharacters() {
        assertEquals("?", OptionValidator.validate("?"));
        assertEquals("@", OptionValidator.validate("@"));
    }

    @Test
    @DisplayName("validate should throw exception for single invalid character like '!'")
    void testValidateWithSingleInvalidCharacter() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate("!"));
        assertTrue(ex.getMessage().contains("Illegal option name"));
    }

    @Test
    @DisplayName("validate should accept multi-character string with valid characters")
    void testValidateWithMultiCharacterValidString() {
        assertEquals("file1", OptionValidator.validate("file1"));
        assertEquals("f-i_l3", OptionValidator.validate("f-i_l3"));
        assertEquals("f$ile", OptionValidator.validate("f$ile"));
    }

    @Test
    @DisplayName("validate should throw exception for invalid starting character")
    void testValidateWithInvalidStartingCharacter() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate("!bad"));
        assertTrue(ex.getMessage().contains("Illegal option name"));
    }

    @Test
    @DisplayName("validate should throw exception when second character is invalid")
    void testValidateWithInvalidSecondCharacter() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate("a!c"));
        assertTrue(ex.getMessage().contains("contains an illegal character"));
        assertTrue(ex.getMessage().contains("!"));
    }

    @Test
    @DisplayName("validate should accept single digit as valid option")
    void testValidateWithSingleDigit() {
        assertEquals("5", OptionValidator.validate("5"));
    }

    @Test
    @DisplayName("validate should accept underscore as valid character")
    void testValidateWithUnderscore() {
        assertEquals("_", OptionValidator.validate("_"));
        assertEquals("a_b_c", OptionValidator.validate("a_b_c"));
    }

    @Test
    @DisplayName("validate should accept hyphen as valid character after first position")
    void testValidateWithHyphenInMiddle() {
        assertEquals("x-y", OptionValidator.validate("x-y"));
    }

    @Test
    @DisplayName("validate should reject hyphen as first character since it's not allowed as option start")
    void testValidateWithHyphenAsFirstCharacter() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate("-option"));
        assertTrue(ex.getMessage().contains("Illegal option name"));
    }

    @Test
    @DisplayName("validate should accept Unicode identifier characters like letters with accents")
    void testValidateWithUnicodeCharacters() {
        assertEquals("áéíóú", OptionValidator.validate("áéíóú"));
        assertEquals("ção", OptionValidator.validate("ção"));
    }

    @Test
    @DisplayName("validate should throw exception for illegal symbol in middle like '#'")
    void testValidateWithIllegalSymbolInMiddle() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate("f#ile"));
        assertTrue(ex.getMessage().contains("illegal character"));
        assertTrue(ex.getMessage().contains("#"));
    }

    @Test
    @DisplayName("validate should handle long valid identifier containing digits and letters")
    void testValidateWithLongValidIdentifier() {
        String longOpt = "option123_valid";
        assertEquals(longOpt, OptionValidator.validate(longOpt));
    }

    @Test
    @DisplayName("validate should handle single currency symbol as valid")
    void testValidateWithCurrencySymbol() {
        assertEquals("$", OptionValidator.validate("$"));
        assertEquals("f$", OptionValidator.validate("f$"));
    }

    // ---------------------------------------------------------
    // Internal behavior testing via reflection (indirectly tested)
    // ---------------------------------------------------------

    @Test
    @DisplayName("validate should allow Java identifier ignorable characters")
    void testValidateWithIdentifierIgnorableChar() {
        char ignorable = '\u0000'; // Null char isIdentifierIgnorable returns true
        String option = "a" + ignorable + "b";
        assertEquals(option, OptionValidator.validate(option));
    }

    // ---------------------------------------------------------
    // Robustness and edge cases
    // ---------------------------------------------------------

    @Test
    @DisplayName("validate should handle multi-character input with only valid chars")
    void testValidateComplexValidString() {
        String complex = "a_1-@b?c"; // note: '@' and '?' only allowed in first position, so invalid if not first
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate(complex));
        assertTrue(ex.getMessage().contains("illegal character"));
    }

    @Test
    @DisplayName("validate should accept single '@' or '?' but reject when used after first character")
    void testValidateAtSymbolAfterFirstChar() {
        assertEquals("@", OptionValidator.validate("@"));
        assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate("a@b"));
    }

    @Test
    @DisplayName("validate should handle upper and lower case equivalently for valid letters")
    void testValidateCaseSensitivity() {
        assertEquals("X", OptionValidator.validate("X"));
        assertEquals("x", OptionValidator.validate("x"));
    }

    @Test
    @DisplayName("validate should throw when option contains space")
    void testValidateWithSpaceCharacter() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate("a b"));
        assertTrue(ex.getMessage().contains("illegal character"));
    }

}
