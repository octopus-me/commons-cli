package org.apache.commons.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class OptionValidatorTest {

    @Nested
    @DisplayName("Null and Empty Input Tests")
    class NullAndEmptyTests {
        
        @Test
        @DisplayName("Should return null for null input")
        void shouldReturnNullForNullInput() {
            String result = OptionValidator.validate(null);
            assertNull(result);
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException for empty string")
        void shouldThrowIllegalArgumentExceptionForEmptyString() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                OptionValidator.validate("");
            });
            assertEquals("Empty option name.", exception.getMessage());
        }
        

    }

    @Nested
    @DisplayName("Single Character Option Tests")
    class SingleCharacterTests {
        
        @ParameterizedTest
        @ValueSource(chars = {'a', 'z', 'A', 'Z', '0', '9', '_', '$'})
        @DisplayName("Should accept valid single character options")
        void shouldAcceptValidSingleCharacterOptions(char optionChar) {
            String option = String.valueOf(optionChar);
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @ParameterizedTest
        @ValueSource(chars = {'?', '@'})
        @DisplayName("Should accept special single character options")
        void shouldAcceptSpecialSingleCharacterOptions(char optionChar) {
            String option = String.valueOf(optionChar);
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @ParameterizedTest
        @ValueSource(chars = {'!', '#', '%', '&', '*', '(', ')', '+', '=', '[', ']', '{', '}', '|', '\\', ':', ';', '"', '\'', '<', '>', ',', '.', '/', ' '})
        @DisplayName("Should reject invalid single character options")
        void shouldRejectInvalidSingleCharacterOptions(char optionChar) {
            String option = String.valueOf(optionChar);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                OptionValidator.validate(option);
            });
            assertTrue(exception.getMessage().contains("Illegal option name"));
        }
        
        @Test
        @DisplayName("Should reject single character hyphen")
        void shouldRejectSingleCharacterHyphen() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                OptionValidator.validate("-");
            });
            assertTrue(exception.getMessage().contains("Illegal option name"));
        }
    }

    @Nested
    @DisplayName("Multi-Character Option Tests")
    class MultiCharacterTests {
        
        @Test
        @DisplayName("Should accept valid multi-character option")
        void shouldAcceptValidMultiCharacterOption() {
            String option = "validOption";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should accept multi-character option with digits")
        void shouldAcceptMultiCharacterOptionWithDigits() {
            String option = "option123";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should accept multi-character option with underscores")
        void shouldAcceptMultiCharacterOptionWithUnderscores() {
            String option = "option_name";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should accept multi-character option with dollar signs")
        void shouldAcceptMultiCharacterOptionWithDollarSigns() {
            String option = "option$name";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should accept multi-character option starting with special character")
        void shouldAcceptMultiCharacterOptionStartingWithSpecialCharacter() {
            String option = "?option";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should accept multi-character option with hyphens in middle")
        void shouldAcceptMultiCharacterOptionWithHyphensInMiddle() {
            String option = "option-name";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"opt!ion", "opt#ion", "opt%ion", "opt&ion", "opt*ion", "opt(ion", "opt)ion", 
                               "opt+ion", "opt=ion", "opt[ion", "opt]ion", "opt{ion", "opt}ion", "opt|ion", 
                               "opt\\ion", "opt:ion", "opt;ion", "opt\"ion", "opt'ion", "opt<ion", "opt>ion", 
                               "opt,ion", "opt.ion", "opt/ion", "opt ion"})
        @DisplayName("Should reject multi-character options with invalid characters")
        void shouldRejectMultiCharacterOptionsWithInvalidCharacters(String option) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                OptionValidator.validate(option);
            });
            assertTrue(exception.getMessage().contains("contains an illegal character"));
        }
        
        @Test
        @DisplayName("Should reject multi-character option starting with hyphen")
        void shouldRejectMultiCharacterOptionStartingWithHyphen() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                OptionValidator.validate("-option");
            });
            assertTrue(exception.getMessage().contains("Illegal option name"));
        }
        
        @Test
        @DisplayName("Should provide detailed error message for invalid character")
        void shouldProvideDetailedErrorMessageForInvalidCharacter() {
            String option = "opt!on";
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                OptionValidator.validate(option);
            });
            String message = exception.getMessage();
            assertTrue(message.contains("opt!on"));
            assertTrue(message.contains("!"));
        }
    }

    @Nested
    @DisplayName("Special Character Tests")
    class SpecialCharacterTests {
        
        @Test
        @DisplayName("Should accept question mark as first character")
        void shouldAcceptQuestionMarkAsFirstCharacter() {
            String option = "?help";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should accept at symbol as first character")
        void shouldAcceptAtSymbolAsFirstCharacter() {
            String option = "@command";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should reject question mark in middle of option")
        void shouldRejectQuestionMarkInMiddleOfOption() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                OptionValidator.validate("opt?ion");
            });
            assertTrue(exception.getMessage().contains("contains an illegal character"));
        }
        
        @Test
        @DisplayName("Should reject at symbol in middle of option")
        void shouldRejectAtSymbolInMiddleOfOption() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                OptionValidator.validate("opt@ion");
            });
            assertTrue(exception.getMessage().contains("contains an illegal character"));
        }
        
        @Test
        @DisplayName("Should accept hyphen in middle of option")
        void shouldAcceptHyphenInMiddleOfOption() {
            String option = "long-option";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should accept multiple hyphens in option")
        void shouldAcceptMultipleHyphensInOption() {
            String option = "very-long-option-name";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
    }

    @Nested
    @DisplayName("Unicode and International Character Tests")
    class UnicodeTests {
        
        @Test
        @DisplayName("Should accept unicode letters as first character")
        void shouldAcceptUnicodeLettersAsFirstCharacter() {
            String option = "ñoption";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should accept unicode letters in option")
        void shouldAcceptUnicodeLettersInOption() {
            String option = "optïon";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should accept unicode currency symbols")
        void shouldAcceptUnicodeCurrencySymbols() {
            String option = "€price";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should accept unicode combining marks")
        void shouldAcceptUnicodeCombiningMarks() {
            String option = "café";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        

        
        @Test
        @DisplayName("Should handle option with mixed valid characters")
        void shouldHandleOptionWithMixedValidCharacters() {
            String option = "a1_b2$c3-d4";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should reject option starting with invalid character followed by valid characters")
        void shouldRejectOptionStartingWithInvalidCharacterFollowedByValidCharacters() {
            String option = "!validRest";
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                OptionValidator.validate(option);
            });
            assertTrue(exception.getMessage().contains("Illegal option name"));
        }
        
        @Test
        @DisplayName("Should reject option with valid start but invalid middle character")
        void shouldRejectOptionWithValidStartButInvalidMiddleCharacter() {
            String option = "valid!middle";
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                OptionValidator.validate(option);
            });
            assertTrue(exception.getMessage().contains("contains an illegal character"));
        }
        
        @Test
        @DisplayName("Should reject option with valid start and middle but invalid end character")
        void shouldRejectOptionWithValidStartAndMiddleButInvalidEndCharacter() {
            String option = "validEnd!";
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                OptionValidator.validate(option);
            });
            assertTrue(exception.getMessage().contains("contains an illegal character"));
        }
    }

    @Nested
    @DisplayName("Character Validation Tests")
    class CharacterValidationTests {
        
        @Test
        @DisplayName("Should validate additional option characters through public API")
        void shouldValidateAdditionalOptionCharactersThroughPublicAPI() {
            assertDoesNotThrow(() -> OptionValidator.validate("?"));
            assertDoesNotThrow(() -> OptionValidator.validate("@"));
            assertDoesNotThrow(() -> OptionValidator.validate("?option"));
            assertDoesNotThrow(() -> OptionValidator.validate("@command"));
        }
        
        @Test
        @DisplayName("Should validate additional long characters through public API")
        void shouldValidateAdditionalLongCharactersThroughPublicAPI() {
            assertDoesNotThrow(() -> OptionValidator.validate("option-with-hyphen"));
            assertDoesNotThrow(() -> OptionValidator.validate("a-b-c"));
            assertDoesNotThrow(() -> OptionValidator.validate("test-option"));
        }
        
        @Test
        @DisplayName("Should test character arrays indirectly through validation")
        void shouldTestCharacterArraysIndirectlyThroughValidation() {
            String optionWithHyphen = "valid-option";
            String result = OptionValidator.validate(optionWithHyphen);
            assertEquals(optionWithHyphen, result);
            
            String optionWithQuestion = "?start";
            result = OptionValidator.validate(optionWithQuestion);
            assertEquals(optionWithQuestion, result);
            
            String optionWithAt = "@begin";
            result = OptionValidator.validate(optionWithAt);
            assertEquals(optionWithAt, result);
        }
        
        @Test
        @DisplayName("Should verify additional option chars are only valid at start")
        void shouldVerifyAdditionalOptionCharsAreOnlyValidAtStart() {
            assertDoesNotThrow(() -> OptionValidator.validate("?valid"));
            assertDoesNotThrow(() -> OptionValidator.validate("@valid"));
            
            assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate("invalid?"));
            assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate("invalid@"));
        }
        
        @Test
        @DisplayName("Should verify additional long chars are valid in middle and end")
        void shouldVerifyAdditionalLongCharsAreValidInMiddleAndEnd() {
            assertDoesNotThrow(() -> OptionValidator.validate("a-b"));
            assertDoesNotThrow(() -> OptionValidator.validate("ab-c"));
            assertDoesNotThrow(() -> OptionValidator.validate("abc-"));
        }
    }

    @Nested
    @DisplayName("Real-world Option Examples")
    class RealWorldExamples {
        
        @ParameterizedTest
        @ValueSource(strings = {"h", "help", "v", "verbose", "f", "file", "D", "version", "?", "@debug"})
        @DisplayName("Should accept common command line options")
        void shouldAcceptCommonCommandLineOptions(String option) {
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should accept options with numbers")
        void shouldAcceptOptionsWithNumbers() {
            String option = "port8080";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should accept options with mixed case")
        void shouldAcceptOptionsWithMixedCase() {
            String option = "JavaHome";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"-help", "--option", "opt ion", "opt+ion", "opt/ion"})
        @DisplayName("Should reject invalid real-world options")
        void shouldRejectInvalidRealWorldOptions(String option) {
            assertThrows(IllegalArgumentException.class, () -> {
                OptionValidator.validate(option);
            });
        }
    }

    @Nested
    @DisplayName("Comprehensive Validation Tests")
    class ComprehensiveTests {
        
        @ParameterizedTest
        @CsvSource({
            "a, true",
            "A, true", 
            "z, true",
            "Z, true",
            "0, true",
            "9, true",
            "_, true",
            "$, true",
            "?, true",
            "@, true",
            "-, false",
            "!, false",
            "#, false",
            "%, false",
            "&, false",
            "*, false",
            "(, false",
            "), false",
            "+, false",
            "=, false",
            "[, false",
            "], false",
            "{, false",
            "}, false",
            "|, false",
            "\\, false",
            ":, false",
            ";, false",
            "\", false",
            "', false",
            "<, false",
            ">, false",
            ",, false",
            "., false",
            "/, false",
            " , false"
        })

        
        @Test
        @DisplayName("Should validate complex valid option")
        void shouldValidateComplexValidOption() {
            String option = "a1_B2$c3-d4";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should validate option with all allowed special first characters")
        void shouldValidateOptionWithAllAllowedSpecialFirstCharacters() {
            String option1 = "?option";
            String option2 = "@option";
            
            assertEquals(option1, OptionValidator.validate(option1));
            assertEquals(option2, OptionValidator.validate(option2));
        }
        
        @Test
        @DisplayName("Should validate option with hyphens in non-first positions")
        void shouldValidateOptionWithHyphensInNonFirstPositions() {
            String option = "long-option-name-with-many-hyphens";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should validate option starting with digit")
        void shouldValidateOptionStartingWithDigit() {
            String option = "1option";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should validate option starting with underscore")
        void shouldValidateOptionStartingWithUnderscore() {
            String option = "_option";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
        
        @Test
        @DisplayName("Should validate option starting with dollar")
        void shouldValidateOptionStartingWithDollar() {
            String option = "$option";
            String result = OptionValidator.validate(option);
            assertEquals(option, result);
        }
    }
}