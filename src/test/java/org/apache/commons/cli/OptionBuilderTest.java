package org.apache.commons.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OptionBuilderTest {

    @Test
    @DisplayName("create() sem longOpt deve lançar IllegalStateException")
    void testCreateWithoutLongOptThrows() {
        assertThrows(IllegalStateException.class, OptionBuilder::create);
    }

    @Test
    @DisplayName("create(char) deve criar Option com char como opt")
    void testCreateWithChar() {
        Option opt = OptionBuilder.withLongOpt("long")
                .withDescription("desc")
                .create('x');
        assertEquals("x", opt.getOpt());
        assertEquals("long", opt.getLongOpt());
        assertEquals("desc", opt.getDescription());
    }

    @Test
    @DisplayName("create(String) deve criar Option com string como opt")
    void testCreateWithString() {
        Option opt = OptionBuilder.withLongOpt("long2")
                .withDescription("desc2")
                .create("opt");
        assertEquals("opt", opt.getOpt());
        assertEquals("long2", opt.getLongOpt());
        assertEquals("desc2", opt.getDescription());
    }

    @Test
    @DisplayName("create(null) deve criar Option sem opt mas com longOpt configurado")
    void testCreateWithNullOpt() {
        Option opt = OptionBuilder.withLongOpt("long3")
                .withDescription("desc3")
                .create(null);
        assertNull(opt.getOpt());
        assertEquals("long3", opt.getLongOpt());
        assertEquals("desc3", opt.getDescription());
    }

    @Test
    @DisplayName("hasArg deve configurar argCount = 1")
    void testHasArg() {
        Option opt = OptionBuilder.withLongOpt("longA")
                .hasArg()
                .create('a');
        assertTrue(opt.hasArg());
        assertEquals(1, opt.getArgs());
    }

    @Test
    @DisplayName("hasArg(false) deve configurar argCount como UNINITIALIZED")
    void testHasArgFalse() {
        Option opt = OptionBuilder.withLongOpt("longB")
                .hasArg(false)
                .create('b');
        assertEquals(Option.UNINITIALIZED, opt.getArgs());
    }

    @Test
    @DisplayName("hasArgs() deve configurar argCount como UNLIMITED_VALUES")
    void testHasArgsUnlimited() {
        Option opt = OptionBuilder.withLongOpt("longC")
                .hasArgs()
                .create('c');
        assertEquals(Option.UNLIMITED_VALUES, opt.getArgs());
    }

    @Test
    @DisplayName("hasArgs(int) deve configurar argCount como valor específico")
    void testHasArgsInt() {
        Option opt = OptionBuilder.withLongOpt("longD")
                .hasArgs(3)
                .create('d');
        assertEquals(3, opt.getArgs());
    }

    @Test
    @DisplayName("hasOptionalArg deve configurar optionalArg=true e argCount=1")
    void testHasOptionalArg() {
        Option opt = OptionBuilder.withLongOpt("longE")
                .hasOptionalArg()
                .create('e');
        assertTrue(opt.hasOptionalArg());
        assertEquals(1, opt.getArgs());
    }

    @Test
    @DisplayName("hasOptionalArgs() deve configurar optionalArg=true e argCount=UNLIMITED_VALUES")
    void testHasOptionalArgsUnlimited() {
        Option opt = OptionBuilder.withLongOpt("longF")
                .hasOptionalArgs()
                .create('f');
        assertTrue(opt.hasOptionalArg());
        assertEquals(Option.UNLIMITED_VALUES, opt.getArgs());
    }

    @Test
    @DisplayName("hasOptionalArgs(int) deve configurar optionalArg=true e argCount=numArgs")
    void testHasOptionalArgsInt() {
        Option opt = OptionBuilder.withLongOpt("longG")
                .hasOptionalArgs(2)
                .create('g');
        assertTrue(opt.hasOptionalArg());
        assertEquals(2, opt.getArgs());
    }

    @Test
    @DisplayName("isRequired() deve configurar required=true")
    void testIsRequired() {
        Option opt = OptionBuilder.withLongOpt("longH")
                .isRequired()
                .create('h');
        assertTrue(opt.isRequired());
    }

    @Test
    @DisplayName("isRequired(false) deve configurar required=false")
    void testIsRequiredFalse() {
        Option opt = OptionBuilder.withLongOpt("longI")
                .isRequired(false)
                .create('i');
        assertFalse(opt.isRequired());
    }

    @Test
    @DisplayName("withArgName deve configurar argName corretamente")
    void testWithArgName() {
        Option opt = OptionBuilder.withLongOpt("longJ")
                .withArgName("argNameJ")
                .create('j');
        assertEquals("argNameJ", opt.getArgName());
    }

    @Test
    @DisplayName("withDescription deve configurar descrição corretamente")
    void testWithDescription() {
        Option opt = OptionBuilder.withLongOpt("longK")
                .withDescription("descK")
                .create('k');
        assertEquals("descK", opt.getDescription());
    }

    @Test
    @DisplayName("withType(Class) deve configurar tipo corretamente")
    void testWithTypeClass() {
        Option opt = OptionBuilder.withLongOpt("longL")
                .withType(Integer.class)
                .create('l');
        assertEquals(Integer.class, opt.getType());
    }

    @Test
    @DisplayName("withType(Object) deve configurar tipo corretamente")
    void testWithTypeObject() {
        Option opt = OptionBuilder.withLongOpt("longM")
                .withType((Object) Double.class)
                .create('m');
        assertEquals(Double.class, opt.getType());
    }

    @Test
    @DisplayName("withValueSeparator() deve configurar '=' como separador")
    void testWithValueSeparatorDefault() {
        Option opt = OptionBuilder.withLongOpt("longN")
                .withValueSeparator()
                .create('n');
        assertEquals('=', opt.getValueSeparator());
    }

    @Test
    @DisplayName("withValueSeparator(char) deve configurar separador customizado")
    void testWithValueSeparatorChar() {
        Option opt = OptionBuilder.withLongOpt("longO")
                .withValueSeparator(';')
                .create('o');
        assertEquals(';', opt.getValueSeparator());
    }

    @Test
    @DisplayName("create deve resetar os valores estáticos após criação")
    void testCreateResetsValues() {
        Option opt1 = OptionBuilder.withLongOpt("longP")
                .withDescription("descP")
                .isRequired()
                .withArgName("argP")
                .withType(String.class)
                .withValueSeparator(':')
                .hasArgs(2)
                .create('p');

        assertEquals("longP", opt1.getLongOpt());
        assertEquals("descP", opt1.getDescription());
        assertTrue(opt1.isRequired());
        assertEquals("argP", opt1.getArgName());
        assertEquals(String.class, opt1.getType());
        assertEquals(':', opt1.getValueSeparator());
        assertEquals(2, opt1.getArgs());

        // Cria nova option sem definir nada para validar reset
        assertThrows(IllegalStateException.class, OptionBuilder::create);
    }
}
