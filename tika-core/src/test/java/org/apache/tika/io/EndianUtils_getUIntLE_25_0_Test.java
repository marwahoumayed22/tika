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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test genere par ChatUniTest (qwen2.5-coder:3b) - IFT3913 Tache 2.
 * Fichier brut : tika-core/chatunitest-tests/.../EndianUtils_getUIntLE_25_0_Test.java
 *
 * Corrections manuelles (3) :
 *  C1 - ajout de l'en-tete de licence Apache (exige par checkstyle).
 *  C2 - imports : suppression des imports '*' et des imports inutilises.
 *  C3 - le dernier test (6 octets) n'avait aucune assertion et son oracle
 *       0x060504030201 etait faux : getUIntLE ne lit que 4 octets. On ajoute
 *       l'assertion avec la valeur correcte 0x04030201 (les octets 5 et 6 sont ignores).
 */
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
        // CORRECTION C3 : oracle genere = 0x060504030201L (faux) et jamais verifie
        long expected = 0x04030201L;
        assertEquals(expected, endianUtils.getUIntLE(data, offset));
    }
}
