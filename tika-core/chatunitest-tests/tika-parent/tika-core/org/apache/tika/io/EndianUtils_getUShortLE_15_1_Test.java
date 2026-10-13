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
public class EndianUtils_getUShortLE_15_1_Test {

    @InjectMocks
    private EndianUtils endianUtils;

    @Mock
    private InputStream inputStream;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUShortLE() throws IOException {
        // Create a byte array with the expected values
        byte[] data = { 0x01, 0x02 };
        // Set up the mock input stream to return the byte array
        when(inputStream.read(data)).thenReturn(2);
        // Call the method under test
        int result = endianUtils.getUShortLE(data, 0);
        // Verify the result
        assertEquals(0x0201, result);
    }
}
