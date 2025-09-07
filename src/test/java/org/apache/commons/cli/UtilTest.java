package org.apache.commons.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Nested
    @DisplayName("Testes para isEmpty(Object[])")
    class IsEmptyObjectArrayTests {

        @Test
        @DisplayName("Deve retornar true quando array é null")
        void testIsEmptyObjectArray_Null() {
            assertTrue(Util.isEmpty((Object[]) null));
        }

        @Test
        @DisplayName("Deve retornar true quando array é vazio")
        void testIsEmptyObjectArray_Empty() {
            assertTrue(Util.isEmpty(new Object[]{}));
        }

        @Test
        @DisplayName("Deve retornar false quando array possui elementos")
        void testIsEmptyObjectArray_WithElements() {
            assertFalse(Util.isEmpty(new Object[]{"elemento"}));
        }
    }

    @Nested
    @DisplayName("Testes para isEmpty(String)")
    class IsEmptyStringTests {

        @Test
        @DisplayName("Deve retornar true quando string é null")
        void testIsEmptyString_Null() {
            assertTrue(Util.isEmpty((String) null));
        }

        @Test
        @DisplayName("Deve retornar true quando string é vazia")
        void testIsEmptyString_Empty() {
            assertTrue(Util.isEmpty(""));
        }

        @Test
        @DisplayName("Deve retornar false quando string contém espaços")
        void testIsEmptyString_Spaces() {
            assertFalse(Util.isEmpty("   "));
        }

        @Test
        @DisplayName("Deve retornar false quando string contém caracteres")
        void testIsEmptyString_WithContent() {
            assertFalse(Util.isEmpty("abc"));
        }
    }

    @Nested
    @DisplayName("Testes para stripLeadingAndTrailingQuotes")
    class StripLeadingAndTrailingQuotesTests {

        @Test
        @DisplayName("Deve retornar null quando entrada é null")
        void testStripLeadingAndTrailingQuotes_Null() {
            assertNull(Util.stripLeadingAndTrailingQuotes(null));
        }

        @Test
        @DisplayName("Deve retornar string vazia quando entrada é vazia")
        void testStripLeadingAndTrailingQuotes_Empty() {
            assertEquals("", Util.stripLeadingAndTrailingQuotes(""));
        }

        @Test
        @DisplayName("Deve remover aspas no início e no fim quando não há aspas internas")
        void testStripLeadingAndTrailingQuotes_WithQuotes() {
            assertEquals("conteudo", Util.stripLeadingAndTrailingQuotes("\"conteudo\""));
        }

        @Test
        @DisplayName("Não deve remover aspas quando apenas começa com aspas")
        void testStripLeadingAndTrailingQuotes_StartsWithQuoteOnly() {
            assertEquals("\"conteudo", Util.stripLeadingAndTrailingQuotes("\"conteudo"));
        }

        @Test
        @DisplayName("Não deve remover aspas quando apenas termina com aspas")
        void testStripLeadingAndTrailingQuotes_EndsWithQuoteOnly() {
            assertEquals("conteudo\"", Util.stripLeadingAndTrailingQuotes("conteudo\""));
        }

        @Test
        @DisplayName("Não deve remover aspas quando há aspas internas")
        void testStripLeadingAndTrailingQuotes_WithInternalQuotes() {
            assertEquals("\"abc\"def\"", Util.stripLeadingAndTrailingQuotes("\"abc\"def\""));
        }

        @Test
        @DisplayName("String sem aspas deve ser retornada inalterada")
        void testStripLeadingAndTrailingQuotes_NoQuotes() {
            assertEquals("abc", Util.stripLeadingAndTrailingQuotes("abc"));
        }
    }

    @Nested
    @DisplayName("Testes para stripLeadingHyphens")
    class StripLeadingHyphensTests {

        @Test
        @DisplayName("Deve retornar null quando entrada é null")
        void testStripLeadingHyphens_Null() {
            assertNull(Util.stripLeadingHyphens(null));
        }

        @Test
        @DisplayName("Deve retornar string vazia quando entrada é vazia")
        void testStripLeadingHyphens_Empty() {
            assertEquals("", Util.stripLeadingHyphens(""));
        }

        @Test
        @DisplayName("Deve remover dois hifens do início")
        void testStripLeadingHyphens_DoubleHyphen() {
            assertEquals("option", Util.stripLeadingHyphens("--option"));
        }

        @Test
        @DisplayName("Deve remover um hifen do início")
        void testStripLeadingHyphens_SingleHyphen() {
            assertEquals("option", Util.stripLeadingHyphens("-option"));
        }

        @Test
        @DisplayName("Não deve alterar string sem hifen inicial")
        void testStripLeadingHyphens_NoHyphen() {
            assertEquals("option", Util.stripLeadingHyphens("option"));
        }

        @Test
        @DisplayName("Deve remover apenas o primeiro hifen se houver três hifens")
        void testStripLeadingHyphens_TripleHyphen() {
            assertEquals("-option", Util.stripLeadingHyphens("---option"));
        }
    }

    @Test
    @DisplayName("Constante EMPTY_STRING_ARRAY deve ser imutável e vazia")
    void testEmptyStringArrayConstant() {
        assertNotNull(Util.EMPTY_STRING_ARRAY);
        assertEquals(0, Util.EMPTY_STRING_ARRAY.length);

        // Verifica que é realmente um array vazio imutável em uso normal
        String[] arr = Util.EMPTY_STRING_ARRAY;
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            String s = arr[0];
        });
    }
}
