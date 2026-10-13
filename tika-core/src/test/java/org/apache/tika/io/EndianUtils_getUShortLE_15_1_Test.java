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

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test genere par ChatUniTest (qwen2.5-coder:3b) - IFT3913 Tache 2.
 * Fichier brut : tika-core/chatunitest-tests/.../EndianUtils_getUShortLE_15_1_Test.java
 *
 * Corrections manuelles (3) :
 *  C1 - ajout de l'en-tete de licence Apache (exige par checkstyle).
 *  C2 - imports : suppression des imports '*' et des imports inutilises.
 *  C3 - suppression de Mockito (@InjectMocks, @Mock InputStream, openMocks et
 *       'when(inputStream.read(data)).thenReturn(2)') : le flux simule n'est jamais
 *       utilise par getUShortLE(byte[], int). Avec MockitoExtension, ce stub inutile
 *       provoquait une UnnecessaryStubbingException (echec a l'execution).
 * Aucun oracle modifie.
 */
public class EndianUtils_getUShortLE_15_1_Test {

    private EndianUtils endianUtils;

    @BeforeEach
    public void setUp() {
        endianUtils = new EndianUtils();
    }

    @Test
    public void testGetUShortLE() throws IOException {
        // Create a byte array with the expected values
        byte[] data = { 0x01, 0x02 };
        // CORRECTION C3 : stub Mockito supprime
        // Call the method under test
        int result = endianUtils.getUShortLE(data, 0);
        // Verify the result
        assertEquals(0x0201, result);
    }
}
