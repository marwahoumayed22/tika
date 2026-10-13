package org.apache.tika.io;

import org.apache.tika.io.EndianUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;

@ExtendWith(MockitoExtension.class)
public class EndianUtils_getShortBE_16_0_Test {

    @InjectMocks
    private EndianUtils endianUtils;

    @Mock
    private InputStream inputStream;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetShortBEWithValidData() throws IOException {
        // Arrange
        byte[] data = { 0x01, 0x02 };
        int offset = 0;
        // Act
        short result = endianUtils.getShortBE(data, offset);
        // Assert
        assertEquals(0x0201, result);
    }

    @Test
    public void testGetShortBEWithNegativeOffset() {
        // Arrange
        byte[] data = { 0x01, 0x02 };
        int offset = -1;
        // Act
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getShortBE(data, offset));
    }

    @Test
    public void testGetShortBEWithOffsetOutOfRange() {
        // Arrange
        byte[] data = { 0x01, 0x02 };
        int offset = 2;
        // Act
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getShortBE(data, offset));
    }

    @Test
    public void testGetShortBEWithEmptyData() {
        // Arrange
        byte[] data = {};
        int offset = 0;
        // Act
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getShortBE(data, offset));
    }

    @Test
    public void testGetShortBEWithNullData() {
        // Arrange
        byte[] data = null;
        int offset = 0;
        // Act
        assertThrows(NullPointerException.class, () -> endianUtils.getShortBE(data, offset));
    }

    @Test
    public void testGetShortBEWithInvalidDataLength() {
        // Arrange
        byte[] data = { 0x01 };
        int offset = 0;
        // Act
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getShortBE(data, offset));
    }

    @Test
    public void testGetShortBEWithInvalidOffsetLength() {
        // Arrange
        byte[] data = { 0x01, 0x02 };
        int offset = 1;
        // Act
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getShortBE(data, offset));
    }

    @Test
    public void testGetShortBEWithInvalidDataLengthAndOffset() {
        // Arrange
        byte[] data = { 0x01 };
        int offset = 1;
        // Act
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getShortBE(data, offset));
    }

    @Test
    public void testGetShortBEWithInvalidDataLengthAndOffset2() {
        // Arrange
        byte[] data = { 0x01, 0x02 };
        int offset = 2;
        // Act
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getShortBE(data, offset));
    }

    @Test
    public void testGetShortBEWithInvalidDataLengthAndOffset3() {
        // Arrange
        byte[] data = { 0x01, 0x02 };
        int offset = 3;
        // Act
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getShortBE(data, offset));
    }

    @Test
    public void testGetShortBEWithInvalidDataLengthAndOffset4() {
        // Arrange
        byte[] data = { 0x01, 0x02 };
        int offset = 4;
        // Act
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getShortBE(data, offset));
    }

    @Test
    public void testGetShortBEWithInvalidDataLengthAndOffset5() {
        // Arrange
        byte[] data = { 0x01, 0x02 };
        int offset = 5;
    }
}
