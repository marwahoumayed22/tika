package org.apache.tika.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import org.apache.tika.io.EndianUtils;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;

public class EndianUtils_getShortLE_13_1_Test {

    private EndianUtils endianUtils;

    @BeforeEach
    public void setUp() {
        endianUtils = new EndianUtils();
    }

    @Test
    public void testGetShortLEWithValidInput() throws IOException {
        byte[] data = { 0x01, 0x02 };
        int offset = 0;
        short expected = 0x0201;
        short result = endianUtils.getShortLE(data, offset);
        assertEquals(expected, result);
    }

    @Test
    public void testGetShortLEWithNegativeOffset() {
        byte[] data = { 0x01, 0x02 };
        int offset = -1;
        assertThrows(IndexOutOfBoundsException.class, () -> {
            endianUtils.getShortLE(data, offset);
        });
    }

    @Test
    public void testGetShortLEWithOffsetOutOfBounds() {
        byte[] data = { 0x01, 0x02 };
        int offset = 2;
        assertThrows(IndexOutOfBoundsException.class, () -> {
            endianUtils.getShortLE(data, offset);
        });
    }

    @Test
    public void testGetShortLEWithNullData() {
        byte[] data = null;
        int offset = 0;
        assertThrows(NullPointerException.class, () -> {
            endianUtils.getShortLE(data, offset);
        });
    }

    @Test
    public void testGetShortLEWithEmptyData() throws IOException {
        byte[] data = {};
        int offset = 0;
        assertThrows(IndexOutOfBoundsException.class, () -> {
            endianUtils.getShortLE(data, offset);
        });
    }

    @Test
    public void testGetShortLEWithSingleByteData() throws IOException {
        byte[] data = { 0x01 };
        int offset = 0;
        assertThrows(IndexOutOfBoundsException.class, () -> {
            endianUtils.getShortLE(data, offset);
        });
    }

    @Test
    public void testGetShortLEWithTwoBytesData() throws IOException {
        byte[] data = { 0x01, 0x02 };
        int offset = 1;
        assertThrows(IndexOutOfBoundsException.class, () -> {
            endianUtils.getShortLE(data, offset);
        });
    }

    @Test
    public void testGetShortLEWithTwoBytesDataAtOffsetZero() throws IOException {
        byte[] data = { 0x01, 0x02 };
        int offset = 0;
        short expected = 0x0201;
        short result = endianUtils.getShortLE(data, offset);
        assertEquals(expected, result);
    }

    @Test
    public void testGetShortLEWithTwoBytesDataAtOffsetTwo() throws IOException {
        byte[] data = { 0x01, 0x02 };
        int offset = 2;
        assertThrows(IndexOutOfBoundsException.class, () -> {
            endianUtils.getShortLE(data, offset);
        });
    }

    @Test
    public void testGetShortLEWithTwoBytesDataAtOffsetThree() throws IOException {
        byte[] data = { 0x01, 0x02 };
        int offset = 3;
        assertThrows(IndexOutOfBoundsException.class, () -> {
            endianUtils.getShortLE(data, offset);
        });
    }

    @Test
    public void testGetShortLEWithTwoBytesDataAtOffsetFour() throws IOException {
        byte[] data = { 0x01, 0x02 };
        int offset = 4;
        assertThrows(IndexOutOfBoundsException.class, () -> {
            endianUtils.getShortLE(data, offset);
        });
    }
}
