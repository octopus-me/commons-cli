package org.apache.commons.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários para a classe {@link TypeHandler}.
 */
@DisplayName("Testes do TypeHandler")
@SuppressWarnings("deprecation") // A classe TypeHandler e muitos de seus métodos são obsoletos.
class TypeHandlerTest {

    @Test
    @DisplayName("Deve criar uma instância de Classe a partir de um nome de classe válido")
    void createClass_withValidClassName_shouldReturnClassInstance() throws ParseException {
        assertEquals(String.class, TypeHandler.createClass("java.lang.String"));
    }

    @Test
    @DisplayName("Deve lançar ParseException para nome de classe inválido")
    void createClass_withInvalidClassName_shouldThrowParseException() {
        assertThrows(ParseException.class, () -> TypeHandler.createClass("non.existent.ClassName"));
    }

    @Test
    @DisplayName("Deve criar um objeto File a partir de um caminho")
    void createFile_shouldReturnFileObject() {
        File file = TypeHandler.createFile("/path/to/file.txt");
        assertNotNull(file);
        assertEquals("file.txt", file.getName());
    }

    @Test
    @DisplayName("Deve criar uma instância de objeto a partir de um nome de classe válido")
    void createObject_withValidClassName_shouldReturnNewInstance() throws ParseException {
        Object obj = TypeHandler.createObject("java.util.ArrayList");
        assertInstanceOf(java.util.ArrayList.class, obj);
    }

    @Test
    @DisplayName("Deve criar uma URL a partir de uma string de URL válida")
    void createURL_withValidURL_shouldReturnURL() throws ParseException {
        URL url = TypeHandler.createURL("https://commons.apache.org");
        assertEquals("https", url.getProtocol());
        assertEquals("commons.apache.org", url.getHost());
    }

    @Test
    @DisplayName("Deve lançar ParseException para uma string de URL malformada")
    void createURL_withMalformedURL_shouldThrowParseException() {
        assertThrows(ParseException.class, () -> TypeHandler.createURL("invalid-url"));
    }

    @Nested
    @DisplayName("Testes para createNumber")
    class CreateNumberTests {
        @Test
        @DisplayName("Deve criar um Long para uma string de número inteiro")
        void createNumber_withIntegerString_shouldReturnLong() throws ParseException {
            Number num = TypeHandler.createNumber("123");
            assertInstanceOf(Long.class, num);
            assertEquals(123L, num.longValue());
        }

        @Test
        @DisplayName("Deve criar um Double para uma string de número decimal")
        void createNumber_withDecimalString_shouldReturnDouble() throws ParseException {
            Number num = TypeHandler.createNumber("123.45");
            assertInstanceOf(Double.class, num);
            assertEquals(123.45, num.doubleValue());
        }

        @Test
        @DisplayName("Deve lançar ParseException para uma string que não é um número")
        void createNumber_withInvalidString_shouldThrowParseException() {
            assertThrows(ParseException.class, () -> TypeHandler.createNumber("not-a-number"));
        }
    }

    @Nested
    @DisplayName("Testes para createValue genérico")
    class CreateValueTests {
        @Test
        @DisplayName("Deve criar o tipo correto para várias classes")
        void createValue_forVariousTypes_shouldSucceed() throws ParseException {
            assertEquals(Integer.valueOf(123), TypeHandler.createValue("123", Integer.class));
            assertEquals(Long.valueOf(456L), TypeHandler.createValue("456", Long.class));
            assertEquals(Double.valueOf(78.9), TypeHandler.createValue("78.9", Double.class));
            assertEquals(new BigInteger("12345"), TypeHandler.createValue("12345", BigInteger.class));
            assertEquals(new BigDecimal("12.345"), TypeHandler.createValue("12.345", BigDecimal.class));
        }

        @Test
        @DisplayName("Deve criar um Character a partir de uma string de caractere")
        void createValue_forCharacter_shouldSucceed() throws ParseException {
            assertEquals('c', TypeHandler.createValue("c", Character.class));
        }
        
        @Test
        @DisplayName("Deve criar um Character a partir de um escape unicode")
        void createValue_forUnicodeCharacter_shouldSucceed() throws ParseException {
            assertEquals('A', TypeHandler.createValue("\\u0041", Character.class));
        }

        @Test
        @DisplayName("O método obsoleto createValue(String, Object) deve delegar corretamente")
        void createValue_withObjectParameter_shouldDelegate() throws ParseException {
            Object type = Integer.class;
            Object value = TypeHandler.createValue("42", type);
            assertInstanceOf(Integer.class, value);
            assertEquals(42, value);
        }

        @Test
        @DisplayName("Deve lançar ParseException para formato de valor inválido")
        void createValue_withInvalidFormat_shouldThrowParseException() {
            assertThrows(ParseException.class, () -> TypeHandler.createValue("abc", Integer.class));
        }
    }
    
    @Nested
    @DisplayName("Testes com Sistema de Arquivos")
    class FileSystemTests {
        @TempDir
        Path tempDir;
        private Path testFile;
        
        @BeforeEach
        void setUp() throws IOException {
            testFile = tempDir.resolve("test.txt");
            Files.createFile(testFile);
        }
        
        @Test
        @DisplayName("openFile deve retornar FileInputStream para um arquivo existente")
        void openFile_withExistingFile_shouldReturnFileInputStream() throws ParseException, IOException {
            try (FileInputStream fis = TypeHandler.openFile(testFile.toString())) {
                assertNotNull(fis);
            }
        }
        
        @Test
        @DisplayName("openFile deve lançar ParseException para um arquivo inexistente")
        void openFile_withNonExistentFile_shouldThrowParseException() {
            String nonExistentFilePath = tempDir.resolve("nonexistent.txt").toString();
            assertThrows(ParseException.class, () -> TypeHandler.openFile(nonExistentFilePath));
        }

        @Test
        @DisplayName("createValue deve criar um Path")
        void createValue_forPath_shouldSucceed() throws ParseException {
            Object path = TypeHandler.createValue(testFile.toString(), Path.class);
            assertInstanceOf(Path.class, path);
            assertEquals(testFile, path);
        }
    }

    @Nested
    @DisplayName("Testes de Operações Não Suportadas")
    class UnsupportedOperationsTests {
        @Test
        @DisplayName("createFiles deve lançar UnsupportedOperationException")
        void createFiles_shouldThrowUnsupportedOperationException() {
            assertThrows(UnsupportedOperationException.class, () -> TypeHandler.createFiles("file1.txt,file2.txt"));
        }
    }

    @Nested
    @DisplayName("Testes de Instância e Conversores Customizados")
    class InstanceTests {
        
        @Test
        @DisplayName("getConverter deve retornar o conversor correto para tipos padrão")
        void getConverter_forStandardTypes_shouldReturnCorrectConverter() {
            TypeHandler handler = new TypeHandler();
            // Verifica se os conversores retornados não são o padrão genérico
            assertTrue(handler.getConverter(Integer.class) != Converter.DEFAULT);
            assertTrue(handler.getConverter(URL.class) != Converter.DEFAULT);
        }
        
        @Test
        @DisplayName("getConverter deve retornar o conversor padrão para um tipo não registrado")
        void getConverter_forUnregisteredType_shouldReturnDefaultConverter() {
            TypeHandler handler = new TypeHandler();
            // Uma classe qualquer que não está no mapa padrão
            class MyCustomType {}
            assertEquals(Converter.DEFAULT, handler.getConverter(MyCustomType.class));
        }

        @Test
        @DisplayName("TypeHandler deve usar o mapa de conversores customizado fornecido")
        void typeHandler_withCustomMap_shouldUseCustomConverter() {
            // Classe de teste simples
            class Person {
                final String name;
                Person(String name) { this.name = name; }
            }
            // Conversor customizado
            Converter<Person, RuntimeException> personConverter = Person::new;
            Map<Class<?>, Converter<?, ? extends Throwable>> customMap = new HashMap<>();
            customMap.put(Person.class, personConverter);

            TypeHandler customHandler = new TypeHandler(customMap);

            assertEquals(personConverter, customHandler.getConverter(Person.class));
        }
        
        @Test
        @DisplayName("O construtor deve lançar NullPointerException se o mapa for nulo")
        void constructor_withNullMap_shouldThrowNullPointerException() {
            assertThrows(NullPointerException.class, () -> new TypeHandler(null));
        }
    }
}