package org.apache.tika.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.io.EndianUtils;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.apache.tika.exception.TikaException;

public class EndianUtils_getUIntLE_25_0_Test {

    private EndianUtils endianUtils;

    @BeforeEach
    public void setUp() {
        endianUtils = new EndianUtils();
    }

    @Test
    public void testGetUIntLE_WithNegativeOffset() {
        byte[] data = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };
        int offset = -1;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUIntLE(data, offset));
    }

    @Test
    public void testGetUIntLE_WithOffsetOutOfRange() {
        byte[] data = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };
        int offset = 8;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUIntLE(data, offset));
    }

    @Test
    public void testGetUIntLE_WithZeroLengthData() {
        byte[] data = {};
        int offset = 0;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUIntLE(data, offset));
    }

    @Test
    public void testGetUIntLE_WithFourByteData() {
        byte[] data = { 0x01, 0x02, 0x03, 0x04 };
        int offset = 0;
        long expected = 0x04030201L;
        long result = endianUtils.getUIntLE(data, offset);
        assertEquals(expected, result);
    }

    @Test
    public void testGetUIntLE_WithSixByteData() {
        byte[] data = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
        int offset = 0;
        long expected = 0x060504030201L;
    }
}
