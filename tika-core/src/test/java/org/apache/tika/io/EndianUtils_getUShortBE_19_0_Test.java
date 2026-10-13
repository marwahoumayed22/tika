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
 * Fichier brut : tika-core/chatunitest-tests/.../EndianUtils_getUShortBE_19_0_Test.java
 *
 * Corrections manuelles (3) :
 *  C1 - ajout de l'en-tete de licence Apache (exige par checkstyle).
 *  C2 - imports : suppression des imports '*' et des imports inutilises.
 *  C3 - suppression de Mockito (@ExtendWith(MockitoExtension.class), openMocks) :
 *       absent de tika-core et inutile ici.
 * Aucun oracle modifie. Remarque : plusieurs tests sont des doublons exacts
 * (offset -1 et -2 testes deux fois chacun, offset 1 teste deux fois) - conserves tels quels.
 */
public class EndianUtils_getUShortBE_19_0_Test {

    private EndianUtils endianUtils;

    @BeforeEach
    public void setUp() {
        endianUtils = new EndianUtils();
    }

    @Test
    public void testGetUShortBEWithValidInput() throws IOException {
        byte[] data = { 0x12, 0x34 };
        int offset = 0;
        int expected = 0x1234;
        int result = endianUtils.getUShortBE(data, offset);
        assertEquals(expected, result);
    }

    @Test
    public void testGetUShortBEWithNegativeOffset() {
        byte[] data = { 0x12, 0x34 };
        int offset = -1;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUShortBE(data, offset));
    }

    @Test
    public void testGetUShortBEWithOffsetOutOfBounds() {
        byte[] data = { 0x12, 0x34 };
        int offset = 2;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUShortBE(data, offset));
    }

    @Test
    public void testGetUShortBEWithEmptyArray() {
        byte[] data = {};
        int offset = 0;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUShortBE(data, offset));
    }

    @Test
    public void testGetUShortBEWithSingleByteArray() {
        byte[] data = { 0x12 };
        int offset = 0;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUShortBE(data, offset));
    }

    @Test
    public void testGetUShortBEWithSingleByteArrayWithOffset() {
        byte[] data = { 0x12 };
        int offset = 1;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUShortBE(data, offset));
    }

    @Test
    public void testGetUShortBEWithSingleByteArrayWithNegativeOffset() {
        byte[] data = { 0x12 };
        int offset = -1;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUShortBE(data, offset));
    }

    @Test
    public void testGetUShortBEWithSingleByteArrayWithOffsetOutOfBounds() {
        byte[] data = { 0x12 };
        int offset = 1;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUShortBE(data, offset));
    }

    @Test
    public void testGetUShortBEWithSingleByteArrayWithOffsetOutOfBoundsNegative() {
        byte[] data = { 0x12 };
        int offset = -2;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUShortBE(data, offset));
    }

    @Test
    public void testGetUShortBEWithSingleByteArrayWithOffsetOutOfBoundsPositive() {
        byte[] data = { 0x12 };
        int offset = 2;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUShortBE(data, offset));
    }

    @Test
    public void testGetUShortBEWithSingleByteArrayWithOffsetOutOfBoundsNegativeAndPositive() {
        byte[] data = { 0x12 };
        int offset = -1;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUShortBE(data, offset));
    }

    @Test
    public void testGetUShortBEWithSingleByteArrayWithOffsetOutOfBoundsNegativeAndPositiveAndNegative() {
        byte[] data = { 0x12 };
        int offset = -2;
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getUShortBE(data, offset));
    }
}
