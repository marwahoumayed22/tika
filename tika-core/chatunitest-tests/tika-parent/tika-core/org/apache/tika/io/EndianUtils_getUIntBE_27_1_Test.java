package org.apache.tika.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.tika.io.EndianUtils;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;

public class EndianUtils_getUIntBE_27_1_Test {

    private EndianUtils endianUtils;

    @BeforeEach
    public void setUp() {
        endianUtils = new EndianUtils();
    }

    @Test
    public void testGetUIntBEWithNegativeOffset() {
        byte[] data = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };
        int offset = -1;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUIntBE(data, offset));
    }

    @Test
    public void testGetUIntBEWithOffsetOutOfRange() {
        byte[] data = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };
        int offset = 8;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUIntBE(data, offset));
    }

    @Test
    public void testGetUIntBEWithZeroLengthData() {
        byte[] data = {};
        int offset = 0;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUIntBE(data, offset));
    }

    @Test
    public void testGetUIntBEWithMultipleBytesData() {
        byte[] data = { 0x01, 0x02, 0x03, 0x04 };
        int offset = 0;
        long expected = 0x01020304L;
        long result = endianUtils.getUIntBE(data, offset);
        assertEquals(expected, result);
    }
}
