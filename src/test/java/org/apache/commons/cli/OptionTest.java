package org.apache.commons.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OptionTest {

    @Test
    @DisplayName("Builder deve lançar IllegalStateException se opt e longOpt não forem definidos")
    void testBuilderWithoutOptOrLongOptThrows() {
        Option.Builder builder = Option.builder(null);
        assertThrows(IllegalStateException.class, builder::get);
    }

    @Test
    @DisplayName("Builder deve construir Option com todos os campos configurados")
    void testBuilderFullConfiguration() {
        Option opt = Option.builder("o")
                .argName("argName")
                .converter(Converter.DEFAULT)
                .deprecated()
                .desc("Descrição de teste")
                .hasArg()
                .longOpt("longo")
                .numberOfArgs(2)
                .optionalArg(true)
                .required(true)
                .since("1.0")
                .type(Integer.class)
                .valueSeparator('=')
                .get();

        assertEquals("o", opt.getOpt());
        assertEquals("argName", opt.getArgName());
        assertEquals("Descrição de teste", opt.getDescription());
        assertEquals("longo", opt.getLongOpt());
        assertEquals(2, opt.getArgs());
        assertTrue(opt.hasOptionalArg());
        assertTrue(opt.isRequired());
        assertEquals("1.0", opt.getSince());
        assertEquals(Integer.class, opt.getType());
        assertEquals('=', opt.getValueSeparator());
        assertTrue(opt.isDeprecated());
        assertNotNull(opt.getConverter());
    }

    @Test
    @DisplayName("Construtor com parâmetros mínimos deve inicializar corretamente")
    void testMinimalConstructor() {
        Option opt = new Option("x", "descrição");
        assertEquals("x", opt.getOpt());
        assertEquals("descrição", opt.getDescription());
        assertFalse(opt.hasArg());
    }

    @Test
    @DisplayName("Construtor com parâmetros completos deve inicializar corretamente")
    void testFullConstructor() {
        Option opt = new Option("y", "longoY", true, "descrição longa");
        assertEquals("y", opt.getOpt());
        assertEquals("longoY", opt.getLongOpt());
        assertTrue(opt.hasArg());
        assertEquals("descrição longa", opt.getDescription());
    }

    @Test
    @DisplayName("equals e hashCode devem funcionar corretamente")
    void testEqualsAndHashCode() {
        Option o1 = new Option("a", "longa", true, "desc");
        Option o2 = new Option("a", "longa", true, "desc diferente");
        Option o3 = new Option("b", "longa", true, "desc");

        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), o2.hashCode());
        assertNotEquals(o1, o3);
        assertNotEquals(o1, null);
        assertNotEquals(o1, "string");
    }

    @Test
    @DisplayName("clone deve retornar cópia independente")
    void testClone() {
        Option opt = new Option("c", "longC", true, "desc");
        opt.setArgs(2);
        opt.setValueSeparator('=');
        opt.processValue("v1");
        opt.processValue("v2");

        Option cloned = (Option) opt.clone();
        assertEquals(opt, cloned);
        assertNotSame(opt, cloned);
        assertEquals(opt.getValuesList(), cloned.getValuesList());
    }

    @Test
    @DisplayName("clearValues deve limpar lista de valores")
    void testClearValues() {
        Option opt = new Option("d", "longD", true, "desc");
        opt.setArgs(2);
        opt.processValue("valor");
        assertFalse(opt.getValuesList().isEmpty());
        opt.clearValues();
        assertTrue(opt.getValuesList().isEmpty());
    }

    @Test
    @DisplayName("processValue deve dividir valores com separador")
    void testProcessValueWithSeparator() {
        Option opt = new Option("e", "longE", true, "desc");
        opt.setArgs(2);
        opt.setValueSeparator('=');
        opt.processValue("k=v");

        List<String> values = opt.getValuesList();
        assertEquals(2, values.size());
        assertEquals("k", values.get(0));
        assertEquals("v", values.get(1));
    }

    @Test
    @DisplayName("processValue deve lançar IllegalStateException se argCount não definido")
    void testProcessValueIllegalState() {
        Option opt = new Option("f", "desc");
        assertThrows(IllegalStateException.class, () -> opt.processValue("v"));
    }

    @Test
    @DisplayName("addValue (deprecated) deve lançar UnsupportedOperationException")
    void testAddValueDeprecated() {
        Option opt = new Option("g", "desc");
        assertThrows(UnsupportedOperationException.class, () -> opt.addValue("x"));
    }

    @Test
    @DisplayName("Métodos de acesso devem retornar valores corretamente")
    void testGettersAndSetters() {
        Option opt = new Option("h", "longH", true, "desc");

        opt.setArgName("argH");
        assertEquals("argH", opt.getArgName());

        opt.setArgs(3);
        assertEquals(3, opt.getArgs());

        opt.setConverter(Converter.DEFAULT);
        assertNotNull(opt.getConverter());

        opt.setDescription("novaDesc");
        assertEquals("novaDesc", opt.getDescription());

        opt.setLongOpt("novoLongo");
        assertEquals("novoLongo", opt.getLongOpt());

        opt.setOptionalArg(true);
        assertTrue(opt.hasOptionalArg());

        opt.setRequired(true);
        assertTrue(opt.isRequired());

        opt.setType(Double.class);
        assertEquals(Double.class, opt.getType());

        opt.setValueSeparator(';');
        assertEquals(';', opt.getValueSeparator());
        assertTrue(opt.hasValueSeparator());
    }

    @Test
    @DisplayName("Métodos de valor devem funcionar corretamente")
    void testValueMethods() {
        Option opt = new Option("i", "longI", true, "desc");
        opt.setArgs(2);

        assertNull(opt.getValue());
        assertEquals("default", opt.getValue("default"));
        assertNull(opt.getValues());

        opt.processValue("val1");
        assertEquals("val1", opt.getValue());
        assertEquals("val1", opt.getValue(0));
        assertThrows(IndexOutOfBoundsException.class, () -> opt.getValue(1));
        assertArrayEquals(new String[]{"val1"}, opt.getValues());
    }

    @Test
    @DisplayName("requiresArg deve respeitar optionalArg e limites")
    void testRequiresArgBehavior() {
        Option opt1 = new Option("j", "desc");
        opt1.setArgs(1);
        assertTrue(opt1.requiresArg());

        Option opt2 = new Option("k", "desc");
        opt2.setArgs(1);
        opt2.setOptionalArg(true);
        assertFalse(opt2.requiresArg());

        Option opt3 = new Option("l", "desc");
        opt3.setArgs(Option.UNLIMITED_VALUES);
        assertTrue(opt3.requiresArg());
        opt3.processValue("v");
        assertFalse(opt3.requiresArg());
    }

    @Test
    @DisplayName("toDeprecatedString deve retornar string vazia se não for deprecated")
    void testToDeprecatedStringNotDeprecated() {
        Option opt = new Option("m", "desc");
        assertEquals("", opt.toDeprecatedString());
    }

    @Test
    @DisplayName("toString deve incluir informações principais")
    void testToString() {
        Option opt = Option.builder("n")
                .longOpt("longN")
                .desc("descN")
                .hasArg()
                .required()
                .get();
        String str = opt.toString();
        assertTrue(str.contains("Option n longN"));
        assertTrue(str.contains("descN"));
        assertTrue(str.contains("String"));
    }
}
