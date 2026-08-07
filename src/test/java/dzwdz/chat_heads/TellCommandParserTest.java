/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TellCommandParserTest {

    @Test
    void extractsTargetsFromVanillaAndCommonAliases() {
        assertEquals("Player", TellCommandParser.extractTarget("/msg Player "));
        assertEquals("Player", TellCommandParser.extractTarget("/tell Player hello"));
        assertEquals("Player", TellCommandParser.extractTarget("/w Player"));
        assertEquals("Player", TellCommandParser.extractTarget("/minecraft:msg Player hello"));
    }

    @Test
    void ignoresCommandsThatDoNotIdentifyARecipient() {
        assertNull(TellCommandParser.extractTarget("hello"));
        assertNull(TellCommandParser.extractTarget("/reply hello"));
        assertNull(TellCommandParser.extractTarget("/msg"));
        assertNull(TellCommandParser.extractTarget(null));
    }
}
