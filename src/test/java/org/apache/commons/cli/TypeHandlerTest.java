package org.apache.commons.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TypeHandlerTest {

    @Nested
    @DisplayName("Testes para createClass")
    class CreateClassTests {

        @Test
        @DisplayName("Deve criar uma classe existente")
        void testCreateClass_Success() throws Exception {
            Class<?> clazz = TypeHandler.createClass("java.lang.String");
            assertEquals(String.class, clazz);
        }

        @Test
        @DisplayName("Deve lançar ParseException para classe inexistente")
        void testCreateClass_NotFound() {
            assertThrows(ParseException.class, () -> TypeHandler.createClass("inexistente.Classe"));
        }
    }

    @Nested
    @DisplayName("Testes para createFile e openFile")
    class FileTests {

        @Test
        @DisplayName("Deve criar File a partir de string válida")
        void testCreateFile_Success() {
            File file = TypeHandler.createFile("pom.xml");
            assertEquals(new File("pom.xml").getPath(), file.getPath());
        }


        @Test
        @DisplayName("Deve abrir FileInputStream em arquivo existente")
        void testOpenFile_Success() throws Exception {
            File temp = File.createTempFile("test", ".txt");
            temp.deleteOnExit();
            FileInputStream fis = TypeHandler.openFile(temp.getAbsolutePath());
            assertNotNull(fis);
            fis.close();
        }

        @Test
        @DisplayName("Deve lançar ParseException quando arquivo não existe em openFile")
        void testOpenFile_NotFound() {
            assertThrows(ParseException.class, () -> TypeHandler.openFile("arquivo_que_nao_existe.txt"));
        }
    }

    @Nested
    @DisplayName("Testes para createFiles (deprecated)")
    class CreateFilesTests {

        @Test
        @DisplayName("Deve lançar UnsupportedOperationException")
        void testCreateFiles() {
            assertThrows(UnsupportedOperationException.class, () -> TypeHandler.createFiles("algum/path"));
        }
    }

    @Nested
    @DisplayName("Testes para createNumber")
    class CreateNumberTests {

        @Test
        @DisplayName("Deve criar Long quando não contém ponto")
        void testCreateNumber_Long() throws Exception {
            Number num = TypeHandler.createNumber("42");
            assertEquals(42L, num.longValue());
        }

        @Test
        @DisplayName("Deve criar Double quando contém ponto")
        void testCreateNumber_Double() throws Exception {
            Number num = TypeHandler.createNumber("3.14");
            assertEquals(3.14, num.doubleValue());
        }

        @Test
        @DisplayName("Deve lançar ParseException quando não é número")
        void testCreateNumber_Invalid() {
            assertThrows(ParseException.class, () -> TypeHandler.createNumber("abc"));
        }
    }

    @Nested
    @DisplayName("Testes para createObject (deprecated)")
    class CreateObjectTests {

        @Test
        @DisplayName("Deve criar objeto de String via reflexão")
        void testCreateObject_String() throws Exception {
            Object obj = TypeHandler.createObject("java.lang.String");
            assertNotNull(obj);
            assertEquals(String.class, obj.getClass());
        }

        @Test
        @DisplayName("Deve lançar ParseException para classe inexistente")
        void testCreateObject_InvalidClass() {
            assertThrows(ParseException.class, () -> TypeHandler.createObject("classe.Inexistente"));
        }
    }

    @Nested
    @DisplayName("Testes para createURL")
    class CreateURLTests {

        @Test
        @DisplayName("Deve criar URL válida")
        void testCreateURL_Success() throws Exception {
            URL url = TypeHandler.createURL("http://example.com");
            assertEquals("http://example.com", url.toString());
        }

        @Test
        @DisplayName("Deve lançar ParseException para URL inválida")
        void testCreateURL_Invalid() {
            assertThrows(ParseException.class, () -> TypeHandler.createURL("://url_invalida"));
        }
    }

    @Nested
    @DisplayName("Testes para createValue")
    class CreateValueTests {

        @Test
        @DisplayName("Deve criar Integer válido")
        void testCreateValue_Integer() throws Exception {
            Integer value = TypeHandler.createValue("123", Integer.class);
            assertEquals(123, value);
        }

        @Test
        @DisplayName("Deve criar BigInteger válido")
        void testCreateValue_BigInteger() throws Exception {
            BigInteger value = TypeHandler.createValue("999999999999", BigInteger.class);
            assertEquals(new BigInteger("999999999999"), value);
        }

        @Test
        @DisplayName("Deve lançar ParseException para valor inválido")
        void testCreateValue_Invalid() {
            assertThrows(ParseException.class, () -> TypeHandler.createValue("abc", Integer.class));
        }


    }

    @Nested
    @DisplayName("Testes para createDate")
    class CreateDateTests {

        @Test
        @DisplayName("Deve lançar IllegalArgumentException pois não há conversor padrão")
        void testCreateDate_Invalid() {
            assertThrows(IllegalArgumentException.class, () -> TypeHandler.createDate("2025-09-07"));
        }
    }

    @Nested
    @DisplayName("Testes para createDefaultMap")
    class CreateDefaultMapTests {

        @Test
        @DisplayName("Mapa padrão deve conter conversores conhecidos")
        void testDefaultMapContents() {
            Map<Class<?>, Converter<?, ? extends Throwable>> map = TypeHandler.createDefaultMap();
            assertTrue(map.containsKey(Object.class));
            assertTrue(map.containsKey(Class.class));
            assertTrue(map.containsKey(File.class));
            assertTrue(map.containsKey(Path.class));
            assertTrue(map.containsKey(Number.class));
            assertTrue(map.containsKey(URL.class));
            assertTrue(map.containsKey(BigInteger.class));
            assertTrue(map.containsKey(BigDecimal.class));
        }
    }

    @Nested
    @DisplayName("Testes para getDefault e getConverter")
    class GetDefaultAndConverterTests {

        @Test
        @DisplayName("getDefault deve sempre retornar a mesma instância")
        void testGetDefaultSingleton() {
            TypeHandler def1 = TypeHandler.getDefault();
            TypeHandler def2 = TypeHandler.getDefault();
            assertSame(def1, def2);
        }

        @Test
        @DisplayName("getConverter deve retornar conversor correto para Integer")
        void testGetConverter_Integer() {
            TypeHandler handler = TypeHandler.getDefault();
            Converter<Integer, ?> converter = handler.getConverter(Integer.class);
            Integer value = assertDoesNotThrow(() -> converter.apply("123"));
            assertEquals(123, value);
        }

        @Test
        @DisplayName("getConverter deve retornar DEFAULT para tipo não registrado")
        void testGetConverter_DefaultFallback() {
            TypeHandler handler = TypeHandler.getDefault();
            Converter<?, ?> converter = handler.getConverter(Object[].class);
            assertNotNull(converter);
            assertEquals(Converter.DEFAULT, converter);
        }
    }
}
