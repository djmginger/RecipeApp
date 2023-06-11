package com.redheaddev.springframework.converters;

import com.redheaddev.springframework.commands.UnitOfMeasureCommand;
import com.redheaddev.springframework.domain.UnitOfMeasure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnitOfMeasureCommandToUnitOfMeasureTest {

    public static final String DESCRIPTION = "description";
    public static final String STRING_VALUE = "1";

    UnitOfMeasureCommandToUnitOfMeasure converter;

    @BeforeEach
    void setUp() {
        converter = new UnitOfMeasureCommandToUnitOfMeasure();
    }

    @Test
    void testNullParameter() {
        assertNull(converter.convert(null));
    }

    @Test
    void testEmptyObject() {
        assertNotNull(converter.convert(new UnitOfMeasureCommand()));
    }

    @Test
    void convert() {

        UnitOfMeasureCommand command = new UnitOfMeasureCommand();
        command.setId(STRING_VALUE);
        command.setDescription(DESCRIPTION);

        UnitOfMeasure uom = converter.convert(command);

        assertNotNull(uom);
        assertEquals(STRING_VALUE, uom.getId());
        assertEquals(DESCRIPTION, uom.getDescription());
    }
}