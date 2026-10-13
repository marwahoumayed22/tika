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

import org.junit.jupiter.api.Test;

/**
 * Test genere par ChatUniTest (qwen2.5-coder:3b) - IFT3913 Tache 2.
 * Fichier brut : tika-core/chatunitest-tests/.../EndianUtils_getIntBE_22_0_Test.java
 *
 * Corrections manuelles (5) :
 *  C1 - ajout de l'en-tete de licence Apache (exige par checkstyle).
 *  C2 - imports : suppression des imports '*' et des imports inutilises.
 *  C3 - suppression de Mockito : le mock InputStream et les 'when(inputStream.read(..))'
 *       ne servaient a rien (getIntBE lit un tableau, pas un flux). En plus de ne pas
 *       compiler (Mockito absent de tika-core), ces stubs inutiles auraient fait echouer
 *       le test en mode strict (UnnecessaryStubbingException).
 *  C4 - ORACLE FAUX : l'IA attendait 0x04030201 pour getIntBE({1,2,3,4}), soit l'ordre
 *       little-endian. En big-endian la valeur correcte est 0x01020304.
 *  C5 - ORACLE FAUX + DONNEE : getIntBE(data, 1) sur un tableau de 4 octets lit data[4]
 *       et leve ArrayIndexOutOfBoundsException ; l'oracle 0x02030401 etait de plus
 *       incoherent. On garde l'intention (lecture a un offset non nul) avec un tableau
 *       de 5 octets : octets 0x02,0x03,0x04,0x05 -> 0x02030405.
 */
public class EndianUtils_getIntBE_22_0_Test {

    @Test
    public void testGetIntBE() throws IOException {
        // Arrange
        byte[] data = { 0x01, 0x02, 0x03, 0x04 };
        // Act
        int result = EndianUtils.getIntBE(data);
        // Assert
        // CORRECTION C4 : oracle genere = 0x04030201 (faux)
        assertEquals(0x01020304, result);
    }

    @Test
    public void testGetIntBEWithOffset() throws IOException {
        // Arrange
        // CORRECTION C5 : tableau genere = { 0x01, 0x02, 0x03, 0x04 } (trop court pour l'offset 1)
        byte[] data = { 0x01, 0x02, 0x03, 0x04, 0x05 };
        // Act
        int result = EndianUtils.getIntBE(data, 1);
        // Assert
        // CORRECTION C5 : oracle genere = 0x02030401 (faux)
        assertEquals(0x02030405, result);
    }
}
