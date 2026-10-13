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
 * Fichier brut : tika-core/chatunitest-tests/.../EndianUtils_getShortBE_16_0_Test.java
 *
 * Corrections manuelles (5) :
 *  C1 - ajout de l'en-tete de licence Apache (exige par checkstyle).
 *  C2 - imports : suppression des imports '*' et des imports inutilises.
 *  C3 - suppression de Mockito (@ExtendWith, @InjectMocks, @Mock, openMocks) :
 *       Mockito n'est pas une dependance de tika-core, le test ne compilait pas.
 *       EndianUtils n'a que des methodes statiques : un simple 'new EndianUtils()' suffit.
 *  C4 - ORACLE FAUX : l'IA attendait 0x0201 (ordre little-endian) pour getShortBE({0x01,0x02}).
 *       En big-endian l'octet de poids fort est le premier : la valeur correcte est 0x0102.
 *  C5 - le dernier test (offset 5) n'avait aucune assertion : ajout du assertThrows
 *       manquant, sur le modele des tests voisins.
 */
public class EndianUtils_getShortBE_16_0_Test {

    private EndianUtils endianUtils;

    @BeforeEach
    public void setUp() {
        endianUtils = new EndianUtils();
    }

    @Test
    public void testGetShortBEWithValidData() throws IOException {
        // Arrange
        byte[] data = { 0x01, 0x02 };
        int offset = 0;
        // Act
        short result = endianUtils.getShortBE(data, offset);
        // Assert
        // CORRECTION C4 : oracle genere = 0x0201 (faux)
        assertEquals(0x0102, result);
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
        // CORRECTION C5 : assertion absente dans le test genere
        assertThrows(IndexOutOfBoundsException.class, () -> endianUtils.getShortBE(data, offset));
    }
}
