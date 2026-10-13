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
public class EndianUtils_getIntBE_22_0_Test {

    @Mock
    private InputStream inputStream;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetIntBE() throws IOException {
        // Arrange
        byte[] data = { 0x01, 0x02, 0x03, 0x04 };
        when(inputStream.read(data)).thenReturn(4);
        when(inputStream.read()).thenReturn(-1);
        // Act
        int result = EndianUtils.getIntBE(data);
        // Assert
        assertEquals(0x04030201, result);
    }

    @Test
    public void testGetIntBEWithOffset() throws IOException {
        // Arrange
        byte[] data = { 0x01, 0x02, 0x03, 0x04 };
        when(inputStream.read(data)).thenReturn(4);
        when(inputStream.read()).thenReturn(-1);
        // Act
        int result = EndianUtils.getIntBE(data, 1);
        // Assert
        assertEquals(0x02030401, result);
    }
}
