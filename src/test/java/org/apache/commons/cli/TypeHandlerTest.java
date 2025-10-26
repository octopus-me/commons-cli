package org.apache.commons.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for {@link TypeHandler}.
 * Covers valid, invalid, null, and boundary conditions.
 */
class TypeHandlerTest {

    private TypeHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TypeHandler();
    }

    // ---------------------------------------------------------------------
    // Basic factory methods and getDefault()
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("getDefault should always return the same instance")
    void testGetDefaultReturnsSingleton() {
        TypeHandler first = TypeHandler.getDefault();
        TypeHandler second = TypeHandler.getDefault();
        assertSame(first, second);
    }

    @Test
    @DisplayName("Constructor should throw NullPointerException when given null map")
    void testConstructorWithNullMap() {
        assertThrows(NullPointerException.class, () -> new TypeHandler(null));
    }

    // ---------------------------------------------------------------------
    // createDefaultMap()
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("createDefaultMap should contain expected converter mappings")
    void testCreateDefaultMap() {
        Map<Class<?>, Converter<?, ? extends Throwable>> map = TypeHandler.createDefaultMap();

        assertTrue(map.containsKey(Object.class));
        assertTrue(map.containsKey(Class.class));
        assertTrue(map.containsKey(Date.class));
        assertTrue(map.containsKey(File.class));
        assertTrue(map.containsKey(Path.class));
        assertTrue(map.containsKey(Number.class));
        assertTrue(map.containsKey(URL.class));
        assertTrue(map.containsKey(FileInputStream.class));
        assertTrue(map.containsKey(Long.class));
        assertTrue(map.containsKey(Integer.class));
        assertTrue(map.containsKey(Short.class));
        assertTrue(map.containsKey(Byte.class));
        assertTrue(map.containsKey(Character.class));
        assertTrue(map.containsKey(Double.class));
        assertTrue(map.containsKey(Float.class));
        assertTrue(map.containsKey(BigInteger.class));
        assertTrue(map.containsKey(BigDecimal.class));
    }

    // ---------------------------------------------------------------------
    // getConverter()
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("getConverter should return specific converter for known class")
    void testGetConverterKnownClass() {
        Converter<File, ?> converter = handler.getConverter(File.class);
        assertNotNull(converter);
        assertDoesNotThrow(() -> converter.apply("pom.xml"));
    }

    @Test
    @DisplayName("getConverter should return default converter when class not found")
    void testGetConverterUnknownClass() {
        Converter<Object, ?> converter = handler.getConverter(Object.class);
        assertNotNull(converter);
    }

    // ---------------------------------------------------------------------
    // createValue() success cases
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("createValue should create a Class instance when given valid class name")
    void testCreateClassValid() throws ParseException {
        Class<?> clazz = TypeHandler.createClass("java.lang.String");
        assertEquals(String.class, clazz);
    }

    @Test
    @DisplayName("createValue should create URL when given valid URL string")
    void testCreateURLValid() throws ParseException {
        URL url = TypeHandler.createURL("http://example.com");
        assertEquals("http://example.com", url.toString());
    }

    @Test
    @DisplayName("createValue should create File instance from valid path string")
    void testCreateFileValid() {
        File file = TypeHandler.createFile("pom.xml");
        assertTrue(file.getPath().contains("pom.xml"));
    }

    @Test
    @DisplayName("createValue should create numeric types from string")
    void testCreateNumberValid() throws ParseException {
        Number number = TypeHandler.createNumber("42");
        assertEquals(42L, number.longValue());
    }

    @Test
    @DisplayName("createValue should parse floating numbers correctly")
    void testCreateDoubleValid() throws ParseException {
        Double d = TypeHandler.createValue("3.1415", Double.class);
        assertEquals(3.1415, d);
    }

    @Test
    @DisplayName("createValue should parse BigInteger and BigDecimal correctly")
    void testCreateBigIntegerAndBigDecimal() throws ParseException {
        BigInteger bigInt = TypeHandler.createValue("12345678901234567890", BigInteger.class);
        BigDecimal bigDec = TypeHandler.createValue("12345.6789", BigDecimal.class);
        assertTrue(bigInt.toString().startsWith("1234567890"));
        assertEquals(new BigDecimal("12345.6789"), bigDec);
    }

    @Test
    @DisplayName("createValue should parse unicode escape for Character correctly")
    void testCreateCharacterUnicode() throws ParseException {
        Character c = TypeHandler.createValue("\\u0041", Character.class);
        assertEquals('A', c);
    }

    // ---------------------------------------------------------------------
    // createValue() failure and exception cases
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("createValue should wrap exceptions in ParseException")
    void testCreateValueInvalidType() {
        assertThrows(ParseException.class, () -> TypeHandler.createValue("invalid", URL.class));
    }

    @Test
    @DisplayName("createValue should throw ParseException when class name is invalid")
    void testCreateClassInvalid() {
        assertThrows(ParseException.class, () -> TypeHandler.createClass("non.existent.ClassName"));
    }

    @Test
    @DisplayName("createValue should throw ParseException when number invalid")
    void testCreateNumberInvalid() {
        assertThrows(ParseException.class, () -> TypeHandler.createNumber("notANumber"));
    }

    @Test
    @DisplayName("createURL should throw ParseException for malformed URL")
    void testCreateURLInvalid() {
        assertThrows(ParseException.class, () -> TypeHandler.createURL("htp:/bad_url"));
    }

    // ---------------------------------------------------------------------
    // createValueUnchecked()
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("createDate should throw IllegalArgumentException for invalid value")
    void testCreateDateInvalid() {
        assertThrows(IllegalArgumentException.class, () -> TypeHandler.createDate("not_a_date"));
    }


    // ---------------------------------------------------------------------
    // createFiles()
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("createFiles should throw UnsupportedOperationException")
    void testCreateFilesThrowsUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> TypeHandler.createFiles("someFile.txt"));
    }

    // ---------------------------------------------------------------------
    // Deprecated createObject() and openFile()
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("createObject should return instance of specified class when valid")
    void testCreateObjectValid() throws ParseException {
        Object obj = TypeHandler.createObject("java.lang.String");
        assertEquals(String.class, obj.getClass());
    }

    @Test
    @DisplayName("openFile should throw ParseException for invalid path")
    void testOpenFileInvalidPath() {
        assertThrows(ParseException.class, () -> TypeHandler.openFile("nonexistent.file"));
    }

    // ---------------------------------------------------------------------
    // Integration and boundary behavior
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Handler should correctly map and convert common primitive types")
    void testPrimitiveConversions() throws ParseException {
        assertEquals(123L, TypeHandler.createValue("123", Long.class));
        assertEquals(45, TypeHandler.createValue("45", Integer.class));
        assertEquals((short) 12, TypeHandler.createValue("12", Short.class));
        assertEquals((byte) 5, TypeHandler.createValue("5", Byte.class));
        assertEquals(1.5f, TypeHandler.createValue("1.5", Float.class));
    }

}
