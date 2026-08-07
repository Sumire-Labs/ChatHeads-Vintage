/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.TextComponentString;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SuggestionHeadLookupTest {

    @Test
    void matchesOnlyTheWholeCaseSensitiveProfileName() {
        NetworkPlayerInfo playerInfo = player("PlayerOne");
        SuggestionHeadLookup lookup = SuggestionHeadLookup.from(Arrays.asList(playerInfo));

        assertSame(playerInfo, lookup.find("PlayerOne"));
        assertNull(lookup.find("playerone"));
        assertNull(lookup.find("PlayerOne extra"));
        assertNull(lookup.find("/tell PlayerOne"));
    }

    @Test
    void doesNotUseTabDisplayNamesForCommandSuggestions() {
        NetworkPlayerInfo playerInfo = player("ProfileName");
        playerInfo.setDisplayName(new TextComponentString("ServerNickname"));
        SuggestionHeadLookup lookup = SuggestionHeadLookup.from(Arrays.asList(playerInfo));

        assertNull(lookup.find("ServerNickname"));
        assertFalse(lookup.containsAny(Arrays.asList("help", "ServerNickname")));
        assertTrue(lookup.containsAny(Arrays.asList("help", "ProfileName")));
    }

    private static NetworkPlayerInfo player(String name) {
        return new NetworkPlayerInfo(new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name));
    }
}
