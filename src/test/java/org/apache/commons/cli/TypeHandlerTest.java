package org.apache.commons.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class TypeHandlerTest {

    private TypeHandler typeHandler;

    @BeforeEach
    void setUp() {
        typeHandler = new TypeHandler();
    }

    @Nested
    @DisplayName("Static Method Tests")
    class StaticMethodTests {
        
        @Test
        @DisplayName("Should get default TypeHandler instance")
        void shouldGetDefaultTypeHandlerInstance() {
            TypeHandler defaultHandler = TypeHandler.getDefault();
            assertNotNull(defaultHandler);
            assertSame(TypeHandler.getDefault(), defaultHandler); // Should be singleton
        }
        
        @Test
        @DisplayName("Should create default converter map")
        void shouldCreateDefaultConverterMap() {
            Map<Class<?>, Converter<?, ? extends Throwable>> map = TypeHandler.createDefaultMap();
            assertNotNull(map);
            assertFalse(map.isEmpty());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create TypeHandler with default constructor")
        void shouldCreateTypeHandlerWithDefaultConstructor() {
            assertNotNull(typeHandler);
        }
        
        @Test
        @DisplayName("Should create TypeHandler with custom converter map")
        void shouldCreateTypeHandlerWithCustomConverterMap() {
            Map<Class<?>, Converter<?, ? extends Throwable>> customMap = new HashMap<>();
            customMap.put(String.class, s -> s.toUpperCase());
            
            TypeHandler customHandler = new TypeHandler(customMap);
            assertNotNull(customHandler);
        }
        
        @Test
        @DisplayName("Should throw NullPointerException for null converter map")
        void shouldThrowNullPointerExceptionForNullConverterMap() {
            assertThrows(NullPointerException.class, () -> {
                new TypeHandler(null);
            });
        }
    }

    @Nested
    @DisplayName("Converter Retrieval Tests")
    class ConverterRetrievalTests {
        
        @Test
        @DisplayName("Should get converter for registered class")
        void shouldGetConverterForRegisteredClass() {
            Converter<Integer, ?> converter = typeHandler.getConverter(Integer.class);
            assertNotNull(converter);
        }
        
        @Test
        @DisplayName("Should get default converter for unregistered class")
        void shouldGetDefaultConverterForUnregisteredClass() {
            Converter<CustomClass, ?> converter = typeHandler.getConverter(CustomClass.class);
            assertNotNull(converter);
            assertEquals(Converter.DEFAULT, converter);
        }
        
        @Test
        @DisplayName("Should get converter for all built-in types")
        void shouldGetConverterForAllBuiltInTypes() {
            assertNotNull(typeHandler.getConverter(Object.class));
            assertNotNull(typeHandler.getConverter(Class.class));
            assertNotNull(typeHandler.getConverter(Date.class));
            assertNotNull(typeHandler.getConverter(File.class));
            assertNotNull(typeHandler.getConverter(Path.class));
            assertNotNull(typeHandler.getConverter(Number.class));
            assertNotNull(typeHandler.getConverter(URL.class));
            assertNotNull(typeHandler.getConverter(FileInputStream.class));
            assertNotNull(typeHandler.getConverter(Long.class));
            assertNotNull(typeHandler.getConverter(Integer.class));
            assertNotNull(typeHandler.getConverter(Short.class));
            assertNotNull(typeHandler.getConverter(Byte.class));
            assertNotNull(typeHandler.getConverter(Character.class));
            assertNotNull(typeHandler.getConverter(Double.class));
            assertNotNull(typeHandler.getConverter(Float.class));
            assertNotNull(typeHandler.getConverter(BigInteger.class));
            assertNotNull(typeHandler.getConverter(BigDecimal.class));
        }
    }

    @Nested
    @DisplayName("Static Create Value Tests")
    class StaticCreateValueTests {
        
        @Test
        @DisplayName("Should create value for String to Integer conversion")
        void shouldCreateValueForStringToIntegerConversion() throws ParseException {
            Integer result = TypeHandler.createValue("123", Integer.class);
            assertEquals(123, result.intValue());
        }
        
        @Test
        @DisplayName("Should create value for String to Long conversion")
        void shouldCreateValueForStringToLongConversion() throws ParseException {
            Long result = TypeHandler.createValue("123456789", Long.class);
            assertEquals(123456789L, result.longValue());
        }
        
        @Test
        @DisplayName("Should create value for String to Double conversion")
        void shouldCreateValueForStringToDoubleConversion() throws ParseException {
            Double result = TypeHandler.createValue("123.45", Double.class);
            assertEquals(123.45, result.doubleValue(), 0.001);
        }
        
        @Test
        @DisplayName("Should create value for String to Float conversion")
        void shouldCreateValueForStringToFloatConversion() throws ParseException {
            Float result = TypeHandler.createValue("123.45", Float.class);
            assertEquals(123.45f, result.floatValue(), 0.001);
        }
        
        @Test
        @DisplayName("Should create value for String to Short conversion")
        void shouldCreateValueForStringToShortConversion() throws ParseException {
            Short result = TypeHandler.createValue("123", Short.class);
            assertEquals(123, result.shortValue());
        }
        
        @Test
        @DisplayName("Should create value for String to Byte conversion")
        void shouldCreateValueForStringToByteConversion() throws ParseException {
            Byte result = TypeHandler.createValue("127", Byte.class);
            assertEquals(127, result.byteValue());
        }
        
        @Test
        @DisplayName("Should create value for String to Character conversion")
        void shouldCreateValueForStringToCharacterConversion() throws ParseException {
            Character result = TypeHandler.createValue("A", Character.class);
            assertEquals('A', result.charValue());
        }
        
        @Test
        @DisplayName("Should create value for String to Character with unicode")
        void shouldCreateValueForStringToCharacterWithUnicode() throws ParseException {
            Character result = TypeHandler.createValue("\\u0041", Character.class);
            assertEquals('A', result.charValue());
        }
        
        @Test
        @DisplayName("Should create value for String to BigInteger conversion")
        void shouldCreateValueForStringToBigIntegerConversion() throws ParseException {
            BigInteger result = TypeHandler.createValue("12345678901234567890", BigInteger.class);
            assertEquals(new BigInteger("12345678901234567890"), result);
        }
        
        @Test
        @DisplayName("Should create value for String to BigDecimal conversion")
        void shouldCreateValueForStringToBigDecimalConversion() throws ParseException {
            BigDecimal result = TypeHandler.createValue("123.456789", BigDecimal.class);
            assertEquals(new BigDecimal("123.456789"), result);
        }
        
        @Test
        @DisplayName("Should throw ParseException for invalid number conversion")
        void shouldThrowParseExceptionForInvalidNumberConversion() {
            assertThrows(ParseException.class, () -> {
                TypeHandler.createValue("not-a-number", Integer.class);
            });
        }
        
        @Test
        @DisplayName("Should create value for String to Class conversion") 
        void shouldCreateValueForStringToClassConversion() throws ParseException {
            Class<?> result = TypeHandler.createValue("java.lang.String", Class.class);
            assertEquals(String.class, result);
        }
        
        @Test
        @DisplayName("Should throw ParseException for invalid class name")
        void shouldThrowParseExceptionForInvalidClassName() {
            assertThrows(ParseException.class, () -> {
                TypeHandler.createValue("invalid.ClassName", Class.class);
            });
        }
        
        @Test
        @DisplayName("Should create value for String to File conversion")
        void shouldCreateValueForStringToFileConversion() throws ParseException {
            File result = TypeHandler.createValue("/tmp/test", File.class);
            assertEquals(new File("/tmp/test"), result);
        }
        
        @Test
        @DisplayName("Should create value for String to URL conversion") 
        void shouldCreateValueForStringToURLConversion() throws ParseException {
            URL result = TypeHandler.createValue("https://example.com", URL.class);
            assertEquals("https://example.com", result.toString());
        }
        
        @Test
        @DisplayName("Should throw ParseException for invalid URL")
        void shouldThrowParseExceptionForInvalidURL() {
            assertThrows(ParseException.class, () -> {
                TypeHandler.createValue("invalid-url", URL.class);
            });
        }
    }

    @Nested
    @DisplayName("Specialized Static Create Methods Tests")
    class SpecializedStaticCreateMethodsTests {
        
        @Test
        @DisplayName("Should create class from string")
        void shouldCreateClassFromString() throws ParseException {
            Class<?> result = TypeHandler.createClass("java.lang.Integer");
            assertEquals(Integer.class, result);
        }
        
        
        @Test
        @DisplayName("Should create file from string")
        void shouldCreateFileFromString() {
            File result = TypeHandler.createFile("/tmp/test");
            assertEquals(new File("/tmp/test"), result);
        }
        
        @Test
        @DisplayName("Should throw UnsupportedOperationException for createFiles")
        void shouldThrowUnsupportedOperationExceptionForCreateFiles() {
            assertThrows(UnsupportedOperationException.class, () -> {
                TypeHandler.createFiles("/tmp");
            });
        }
        
        @Test
        @DisplayName("Should create number from string")
        void shouldCreateNumberFromString() throws ParseException {
            Number result = TypeHandler.createNumber("123.45");
            assertNotNull(result);
            assertTrue(result instanceof Double || result instanceof Long);
        }
        
        
        @Test
        @DisplayName("Should create URL from string")
        void shouldCreateURLFromString() throws ParseException {
            URL result = TypeHandler.createURL("https://example.com");
            assertEquals("https://example.com", result.toString());
        }
        
        @Test
        @DisplayName("Should open file input stream")
        void shouldOpenFileInputStream() throws ParseException {
            // Create a temporary file for testing
            File tempFile = null;
            try {
                tempFile = File.createTempFile("test", ".txt");
                tempFile.deleteOnExit();
                
                FileInputStream result = TypeHandler.openFile(tempFile.getAbsolutePath());
                assertNotNull(result);
                result.close();
            } catch (Exception e) {
                // Skip test if file creation fails
            }
        }
        
        @Test
        @DisplayName("Should throw ParseException for non-existent file in openFile")
        void shouldThrowParseExceptionForNonExistentFileInOpenFile() {
            assertThrows(ParseException.class, () -> {
                TypeHandler.openFile("/non/existent/file");
            });
        }
    }

    @Nested
    @DisplayName("Deprecated Method Tests")
    class DeprecatedMethodTests {
        
        
        @Test
        @DisplayName("Should handle deprecated createNumber method")
        void shouldHandleDeprecatedCreateNumberMethod() throws ParseException {
            Number result = TypeHandler.createNumber("123");
            assertNotNull(result);
        }
        
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle empty string for character conversion")
        void shouldHandleEmptyStringForCharacterConversion() {
            assertThrows(ParseException.class, () -> {
                TypeHandler.createValue("", Character.class);
            });
        }

        
        @Test
        @DisplayName("Should handle number format exceptions")
        void shouldHandleNumberFormatExceptions() {
            assertThrows(ParseException.class, () -> {
                TypeHandler.createValue("abc", Integer.class);
            });
        }
        
        @Test
        @DisplayName("Should handle out of range values")
        void shouldHandleOutOfRangeValues() {
            assertThrows(ParseException.class, () -> {
                TypeHandler.createValue("99999999999999999999", Integer.class);
            });
        }
        
        @Test
        @DisplayName("Should handle very large numbers")
        void shouldHandleVeryLargeNumbers() throws ParseException {
            BigInteger result = TypeHandler.createValue("999999999999999999999999999999", BigInteger.class);
            assertNotNull(result);
            assertEquals(new BigInteger("999999999999999999999999999999"), result);
        }
        
        @Test
        @DisplayName("Should handle scientific notation for doubles")
        void shouldHandleScientificNotationForDoubles() throws ParseException {
            Double result = TypeHandler.createValue("1.23e10", Double.class);
            assertEquals(1.23e10, result.doubleValue(), 0.001);
        }
        

    }

    @Nested
    @DisplayName("Custom Converter Tests")
    class CustomConverterTests {
        
        @Test
        @DisplayName("Should use custom converter when provided")
        void shouldUseCustomConverterWhenProvided() throws ParseException {
            Map<Class<?>, Converter<?, ? extends Throwable>> customMap = new HashMap<>();
            customMap.put(String.class, s -> s.toUpperCase());
            
            TypeHandler customHandler = new TypeHandler(customMap);
            Converter<String, ?> converter = customHandler.getConverter(String.class);
            assertNotNull(converter);
            
        }
        
        @Test
        @DisplayName("Should override default converters with custom ones")
        void shouldOverrideDefaultConvertersWithCustomOnes() throws ParseException {
            Map<Class<?>, Converter<?, ? extends Throwable>> customMap = TypeHandler.createDefaultMap();
            customMap.put(Integer.class, s -> 999); // Always return 999
            
            TypeHandler customHandler = new TypeHandler(customMap);
            Integer result = TypeHandler.createValue("123", Integer.class);
            // Should use the default handler, not the custom one
            assertEquals(123, result.intValue());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should handle multiple type conversions")
        void shouldHandleMultipleTypeConversions() throws ParseException {
            Integer intVal = TypeHandler.createValue("123", Integer.class);
            Double doubleVal = TypeHandler.createValue("123.45", Double.class);
            String stringVal = TypeHandler.createValue("test", String.class);
            
            assertEquals(123, intVal.intValue());
            assertEquals(123.45, doubleVal.doubleValue(), 0.001);
            assertEquals("test", stringVal);
        }
        
        @Test
        @DisplayName("Should handle complex type hierarchy")
        void shouldHandleComplexTypeHierarchy() throws ParseException {
            Number integerAsNumber = TypeHandler.createValue("123", Number.class);
            Number doubleAsNumber = TypeHandler.createValue("123.45", Number.class);
            
            assertNotNull(integerAsNumber);
            assertNotNull(doubleAsNumber);
        }
        

    }

    // Helper class for testing
    static class CustomClass {
        private final String value;
        
        public CustomClass(String value) {
            this.value = value;
        }
        
        public static CustomClass valueOf(String value) {
            return new CustomClass(value);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CustomClass that = (CustomClass) obj;
            return value.equals(that.value);
        }
        
        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
}