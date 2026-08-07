/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RealNameAliasParserTest {

    @Test
    void parsesAnEssentialsRealNameResponse() {
        RealNameAliasParser.Alias alias = RealNameAliasParser.parse("ServerNick is RealPlayer");

        assertEquals("ServerNick", alias.getNickname());
        assertEquals("RealPlayer", alias.getProfileName());
    }

    @Test
    void rejectsOrdinaryChatAndNamesContainingSpaces() {
        assertNull(RealNameAliasParser.parse("ServerNick: hello"));
        assertNull(RealNameAliasParser.parse("Server Nick is RealPlayer"));
        assertNull(RealNameAliasParser.parse("ServerNick is Real Player"));
        assertNull(RealNameAliasParser.parse(" is RealPlayer"));
        assertNull(RealNameAliasParser.parse(null));
    }
}
