/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LineBuildContextTest {

    @Test
    void marksOnlyTheFirstWrappedLineAsTheHeadLineAndKeepsTheSavedLine() {
        TextComponentString original = new TextComponentString("whole message");
        HeadData base = HeadData.empty(ChatType.CHAT);

        LineBuildContext.begin(base, original);
        try {
            HeadData first = LineBuildContext.dataFor(new TextComponentString("first"));
            HeadData continuation = LineBuildContext.dataFor(new TextComponentString("continuation"));
            HeadData saved = LineBuildContext.dataFor(original);

            assertTrue(first.isFirstVisualLine());
            assertFalse(continuation.isFirstVisualLine());
            assertTrue(saved.isFirstVisualLine());
            assertSame(ChatType.CHAT, saved.getChatType());
        } finally {
            LineBuildContext.end();
        }
    }

    @Test
    void relocatesTheHeadWhenAnotherModAddsAChatPrefixBeforeWrapping() {
        TextComponentString original = new TextComponentString("<Player> hello");
        NetworkPlayerInfo playerInfo = new NetworkPlayerInfo(
                new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Player")
        );
        HeadData base = HeadData.forPlayer(playerInfo, ChatType.CHAT, "Player", 1);

        LineBuildContext.begin(base, original);
        try {
            HeadData wrapped = LineBuildContext.dataFor(new TextComponentString("[12:00] <Player> hello"));
            HeadData saved = LineBuildContext.dataFor(original);

            assertEquals(9, wrapped.getHeadCharacterIndex());
            assertEquals(1, saved.getMessageNameIndex());
        } finally {
            LineBuildContext.end();
        }
    }

    @Test
    void assignsBeforeNameHeadToTheWrappedLineContainingTheName() {
        TextComponentString original = new TextComponentString("prefix Player says hello");
        NetworkPlayerInfo playerInfo = new NetworkPlayerInfo(
                new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Player")
        );
        HeadData base = HeadData.forPlayer(playerInfo, ChatType.CHAT, "Player", 7);

        LineBuildContext.begin(base, original);
        try {
            HeadData first = LineBuildContext.dataFor(new TextComponentString("prefix "));
            HeadData second = LineBuildContext.dataFor(new TextComponentString("Player says hello"));

            assertEquals(-1, first.getHeadCharacterIndex());
            assertEquals(0, second.getHeadCharacterIndex());
            assertTrue(first.isFirstVisualLine());
            assertFalse(second.isFirstVisualLine());
        } finally {
            LineBuildContext.end();
        }
    }
}
