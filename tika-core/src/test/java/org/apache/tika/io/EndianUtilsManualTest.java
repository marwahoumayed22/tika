/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Tests ecrits a la main - IFT3913 Tache 2 (Houmayed / El Fakir).
 * <p>
 * Objectif : tuer les mutants PIT qui survivent aux tests originaux et aux tests
 * generes par ChatUniTest. Chaque test est documente dans le README.md (section 7).
 * <p>
 * Conventions des donnees :
 * <ul>
 *   <li>octets tous differents : une permutation ou un mauvais decalage change le resultat ;</li>
 *   <li>octets &gt;= 0x80 : un mutant qui retire ou remplace le masque {@code & 0xFF}
 *       propage le signe et change le resultat ;</li>
 *   <li>octets nuls : seule facon de distinguer {@code < 0} de {@code <= 0}.</li>
 * </ul>
 */
public class EndianUtilsManualTest {

    /** Octets 0x81..0x88 : tous differents et tous avec le bit de poids fort a 1. */
    private static final byte[] HIGH = {
            (byte) 0x81, (byte) 0x82, (byte) 0x83, (byte) 0x84,
            (byte) 0x85, (byte) 0x86, (byte) 0x87, (byte) 0x88
    };

    private static InputStream stream(byte[] data) {
        return new ByteArrayInputStream(data);
    }

    /**
     * Flux qui renvoie exactement la suite d'entiers donnee (y compris -1 au milieu).
     * Sert a placer la fin de flux a une position precise.
     */
    private static InputStream sequence(int... values) {
        return new InputStream() {
            private int pos = 0;

            @Override
            public int read() {
                return pos < values.length ? values[pos++] : -1;
            }
        };
    }

    /** Lecteur generique pour factoriser les tests de fin de flux. */
    private interface Reader {
        void read(InputStream in) throws Exception;
    }

    /**
     * Verifie que la lecture echoue des qu'UN SEUL des {@code width} octets vaut -1,
     * quelle que soit sa position.
     */
    private static void assertUnderrunAtEveryPosition(int width, Reader reader) {
        for (int missing = 0; missing < width; missing++) {
            int[] values = new int[width];
            for (int k = 0; k < width; k++) {
                values[k] = (k == missing) ? -1 : k + 1;
            }
            assertThrows(EndianUtils.BufferUnderrunException.class,
                    () -> reader.read(sequence(values)), "octet manquant en position " + missing);
        }
    }

    // ------------------------------------------------------------------
    // 1. Lecture de flux sur 16 bits
    // ------------------------------------------------------------------

    @Test
    public void testReadUShortLEAndBEWithHighBytes() throws Exception {
        assertEquals(0x8281, EndianUtils.readUShortLE(stream(HIGH)));
        assertEquals(0x8182, EndianUtils.readUShortBE(stream(HIGH)));
    }

    @Test
    public void testReadShortLEAndBEAreSigned() throws Exception {
        assertEquals((short) 0x8281, EndianUtils.readShortLE(stream(HIGH)));
        assertEquals((short) 0x8182, EndianUtils.readShortBE(stream(HIGH)));
        assertEquals(-2, EndianUtils.readShortLE(stream(new byte[]{(byte) 0xFE, (byte) 0xFF})));
    }

    @Test
    public void testReadShortsWithZeroBytesDoNotThrow() throws Exception {
        byte[] zeros = new byte[2];
        assertEquals(0, EndianUtils.readUShortLE(stream(zeros)));
        assertEquals(0, EndianUtils.readUShortBE(stream(zeros)));
    }

    @Test
    public void testReadShortsUnderrun() {
        assertUnderrunAtEveryPosition(2, EndianUtils::readUShortLE);
        assertUnderrunAtEveryPosition(2, EndianUtils::readUShortBE);
    }

    // ------------------------------------------------------------------
    // 2. Lecture de flux sur 32 bits
    // ------------------------------------------------------------------

    @Test
    public void testReadIntLEAndBEWithHighBytes() throws Exception {
        assertEquals(0x84838281, EndianUtils.readIntLE(stream(HIGH)));
        assertEquals(0x81828384, EndianUtils.readIntBE(stream(HIGH)));
    }

    @Test
    public void testReadUIntLEAndBEWithHighBytes() throws Exception {
        assertEquals(0x84838281L, EndianUtils.readUIntLE(stream(HIGH)));
        assertEquals(0x81828384L, EndianUtils.readUIntBE(stream(HIGH)));
    }

    @Test
    public void testReadIntsWithZeroBytesDoNotThrow() throws Exception {
        byte[] zeros = new byte[4];
        assertEquals(0, EndianUtils.readIntLE(stream(zeros)));
        assertEquals(0, EndianUtils.readIntBE(stream(zeros)));
        assertEquals(0, EndianUtils.readIntME(stream(zeros)));
        assertEquals(0L, EndianUtils.readUIntLE(stream(zeros)));
        assertEquals(0L, EndianUtils.readUIntBE(stream(zeros)));
    }

    @Test
    public void testReadIntsUnderrun() {
        assertUnderrunAtEveryPosition(4, EndianUtils::readIntLE);
        assertUnderrunAtEveryPosition(4, EndianUtils::readIntBE);
        assertUnderrunAtEveryPosition(4, EndianUtils::readIntME);
        assertUnderrunAtEveryPosition(4, EndianUtils::readUIntLE);
        assertUnderrunAtEveryPosition(4, EndianUtils::readUIntBE);
    }

    // ------------------------------------------------------------------
    // 3. Lecture de flux sur 64 bits
    // ------------------------------------------------------------------

    @Test
    public void testReadLongLEAndBEWithHighBytes() throws Exception {
        assertEquals(0x8887868584838281L, EndianUtils.readLongLE(stream(HIGH)));
        assertEquals(0x8182838485868788L, EndianUtils.readLongBE(stream(HIGH)));
    }

    @Test
    public void testReadLongsWithZeroBytesDoNotThrow() throws Exception {
        byte[] zeros = new byte[8];
        assertEquals(0L, EndianUtils.readLongLE(stream(zeros)));
        assertEquals(0L, EndianUtils.readLongBE(stream(zeros)));
    }

    @Test
    public void testReadLongsUnderrun() {
        assertUnderrunAtEveryPosition(8, EndianUtils::readLongLE);
        assertUnderrunAtEveryPosition(8, EndianUtils::readLongBE);
    }

    // ------------------------------------------------------------------
    // 4. readUE7 : entier a longueur variable (7 bits par octet)
    // ------------------------------------------------------------------

    @Test
    public void testReadUE7ZeroByte() throws Exception {
        assertEquals(0L, EndianUtils.readUE7(stream(new byte[]{0x00})));
    }

    @Test
    public void testReadUE7ContinuationThenZero() throws Exception {
        // 0x81 = continuation, valeur 1 ; 0x00 = dernier octet, valeur 0 -> (1 << 7) + 0
        assertEquals(128L, EndianUtils.readUE7(stream(new byte[]{(byte) 0x81, 0x00})));
    }

    @Test
    public void testReadUE7StopsAfterSixBytes() throws Exception {
        // 7 octets de continuation : seuls les 6 premiers sont lus (max = 6)
        byte[] data = new byte[7];
        Arrays.fill(data, (byte) 0x81);
        long expected = 0;
        for (int k = 0; k < 6; k++) {
            expected = (expected << 7) + 1;
        }
        assertEquals(expected, EndianUtils.readUE7(stream(data)));
    }

    @Test
    public void testReadUE7Underrun() {
        assertThrows(IOException.class, () -> EndianUtils.readUE7(stream(new byte[]{(byte) 0x81})));
        assertThrows(IOException.class, () -> EndianUtils.readUE7(stream(new byte[0])));
    }

    // ------------------------------------------------------------------
    // 5. Lecture dans un tableau d'octets
    // ------------------------------------------------------------------

    @Test
    public void testGetShortsWithoutOffset() {
        assertEquals(0x8281, EndianUtils.getUShortLE(HIGH));
        assertEquals(0x8182, EndianUtils.getUShortBE(HIGH));
        assertEquals((short) 0x8281, EndianUtils.getShortLE(HIGH));
        assertEquals((short) 0x8182, EndianUtils.getShortBE(HIGH));
    }

    @Test
    public void testGetShortsWithOffset() {
        assertEquals(0x8483, EndianUtils.getUShortLE(HIGH, 2));
        assertEquals(0x8384, EndianUtils.getUShortBE(HIGH, 2));
    }

    @Test
    public void testGetIntLE() {
        assertEquals(0x84838281, EndianUtils.getIntLE(HIGH));
        assertEquals(0x86858483, EndianUtils.getIntLE(HIGH, 2));
    }

    @Test
    public void testGetIntBEWithHighBytes() {
        assertEquals(0x81828384, EndianUtils.getIntBE(HIGH));
        assertEquals(0x83848586, EndianUtils.getIntBE(HIGH, 2));
    }

    @Test
    public void testGetUIntWithHighBytes() {
        assertEquals(0x84838281L, EndianUtils.getUIntLE(HIGH));
        assertEquals(0x81828384L, EndianUtils.getUIntBE(HIGH));
        assertEquals(0x86858483L, EndianUtils.getUIntLE(HIGH, 2));
        assertEquals(0x83848586L, EndianUtils.getUIntBE(HIGH, 2));
    }

    @Test
    public void testGetLongLE() {
        assertEquals(0x8887868584838281L, EndianUtils.getLongLE(HIGH, 0));
        byte[] shifted = new byte[10];
        System.arraycopy(HIGH, 0, shifted, 2, 8);
        assertEquals(0x8887868584838281L, EndianUtils.getLongLE(shifted, 2));
    }

    @Test
    public void testUnsignedByteHelpers() {
        assertEquals(0x80, EndianUtils.ubyteToInt((byte) 0x80));
        assertEquals(0xFF, EndianUtils.ubyteToInt((byte) 0xFF));
        assertEquals(0x83, EndianUtils.getUByte(HIGH, 2));
    }
}
