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

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test genere par ChatUniTest (qwen2.5-coder:3b) - IFT3913 Tache 2.
 * Fichier brut : tika-core/chatunitest-tests/.../EndianUtils_getShortLE_13_1_Test.java
 *
 * Corrections manuelles (2) :
 *  C1 - ajout de l'en-tete de licence Apache (exige par checkstyle).
 *  C2 - imports : suppression des imports '*' et des imports inutilises
 *       (Mockito, TikaException, Method, InputStream...), interdits par checkstyle.
 * Aucun oracle modifie.
 */
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
