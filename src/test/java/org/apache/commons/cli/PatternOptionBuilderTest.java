package org.apache.commons.cli;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PatternOptionBuilderTest {

    // --- getValueType tests ---

    @Test
    void testGetValueTypeObject() {
        assertEquals(Object.class, PatternOptionBuilder.getValueType('@'));
    }

    @Test
    void testGetValueTypeString() {
        assertEquals(String.class, PatternOptionBuilder.getValueType(':'));
    }

    @Test
    void testGetValueTypeNumber() {
        assertEquals(Number.class, PatternOptionBuilder.getValueType('%'));
    }

    @Test
    void testGetValueTypeClass() {
        assertEquals(Class.class, PatternOptionBuilder.getValueType('+'));
    }

    @Test
    void testGetValueTypeDate() {
        assertEquals(Date.class, PatternOptionBuilder.getValueType('#'));
    }

    @Test
    void testGetValueTypeExistingFile() {
        assertEquals(FileInputStream.class, PatternOptionBuilder.getValueType('<'));
    }

    @Test
    void testGetValueTypeFile() {
        assertEquals(File.class, PatternOptionBuilder.getValueType('>'));
    }

    @Test
    void testGetValueTypeFiles() {
        assertEquals(File[].class, PatternOptionBuilder.getValueType('*'));
    }

    @Test
    void testGetValueTypeUrl() {
        assertEquals(URL.class, PatternOptionBuilder.getValueType('/'));
    }

    @Test
    void testGetValueTypeUnknownReturnsNull() {
        assertNull(PatternOptionBuilder.getValueType('X'));
    }

    // --- getValueClass (deprecated) ---

    @Test
    void testGetValueClassDelegatesToGetValueType() {
        assertEquals(String.class, PatternOptionBuilder.getValueClass(':'));
    }

    // --- isValueCode tests ---

    @Test
    void testIsValueCodeRecognizedChars() {
        char[] codes = {'@', ':', '%', '+', '#', '<', '>', '*', '/', '!'};
        for (char c : codes) {
            assertTrue(PatternOptionBuilder.isValueCode(c), "Should recognize value code: " + c);
        }
    }

    @Test
    void testIsValueCodeUnrecognizedChar() {
        assertFalse(PatternOptionBuilder.isValueCode('X'));
    }

    // --- parsePattern tests ---

    @Test
    void testParsePatternSimpleFlag() {
        Options options = PatternOptionBuilder.parsePattern("a");
        Option opt = options.getOption("a");
        assertNotNull(opt);
        assertFalse(opt.hasArg());
        assertFalse(opt.isRequired());
        assertNull(opt.getType());
    }

    @Test
    void testParsePatternFlagWithStringArg() {
        Options options = PatternOptionBuilder.parsePattern("b:");
        Option opt = options.getOption("b");
        assertNotNull(opt);
        assertTrue(opt.hasArg());
        assertEquals(String.class, opt.getType());
    }

    @Test
    void testParsePatternRequiredFlag() {
        Options options = PatternOptionBuilder.parsePattern("!c:");
        Option opt = options.getOption("c");
        assertNotNull(opt);
        assertTrue(opt.hasArg());
        assertTrue(opt.isRequired());
    }

    @Test
    void testParsePatternMultipleOptions() {
        Options options = PatternOptionBuilder.parsePattern("a:b:c");
        assertEquals(3, options.getOptions().size());

        Option b = options.getOption("b");
        assertTrue(b.hasArg());
        assertEquals(String.class, b.getType());

        Option c = options.getOption("c");
        assertFalse(c.hasArg());
    }

    @Test
    void testParsePatternWithDifferentValueTypes() {
        Options options = PatternOptionBuilder.parsePattern("d%f/");
        Option d = options.getOption("d");
        assertNotNull(d);
        assertTrue(d.hasArg());
        assertEquals(Number.class, d.getType());

        Option f = options.getOption("f");
        assertTrue(f.hasArg());
        assertEquals(URL.class, f.getType());
    }

    @Test
    void testParsePatternEndsWithValueTypeOption() {
        Options options = PatternOptionBuilder.parsePattern("g>");
        Option g = options.getOption("g");
        assertNotNull(g);
        assertTrue(g.hasArg());
        assertEquals(File.class, g.getType());
    }


    // --- constructor (deprecated) ---

    @Test
    void testConstructorDoesNotThrow() {
        assertDoesNotThrow(PatternOptionBuilder::new);
    }
}
