/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import com.mojang.authlib.GameProfile;
import dzwdz.chat_heads.config.ChatHeadsConfig;
import dzwdz.chat_heads.config.RenderPosition;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.ChatType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatHeadManagerTest {

    @Test
    void keepsWrappingAndRenderingRulesDistinctForBothPositions() {
        RenderPosition previousPosition = ChatHeadsConfig.general.renderPosition;
        boolean previousOffset = ChatHeadsConfig.general.offsetNonPlayerText;
        boolean previousEnabled = ChatHeadsConfig.general.enabled;

        try {
            ChatHeadsConfig.general.enabled = true;
            ChatHeadsConfig.general.offsetNonPlayerText = true;

            NetworkPlayerInfo playerInfo = new NetworkPlayerInfo(
                    new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Player")
            );
            HeadData firstLine = HeadData.forPlayer(playerInfo, ChatType.CHAT, "Player", 1)
                    .asWrappedLine(true, 1);
            HeadData nameOnContinuation = firstLine.asWrappedLine(false, 0);
            HeadData continuationWithoutName = firstLine.asWrappedLine(false, -1);
            HeadData nonPlayer = HeadData.empty(ChatType.CHAT).asWrappedLine(true, -1);

            ChatHeadsConfig.general.renderPosition = RenderPosition.BEFORE_NAME;
            assertEquals(ChatHeadManager.getHeadWidth(), ChatHeadManager.getTextWidthDifference(firstLine));
            assertEquals(0, ChatHeadManager.getLineOffset(firstLine));
            assertTrue(ChatHeadManager.shouldRenderHead(firstLine));
            assertTrue(ChatHeadManager.shouldRenderHead(nameOnContinuation));
            assertFalse(ChatHeadManager.shouldRenderHead(continuationWithoutName));
            assertEquals(0, ChatHeadManager.getTextWidthDifference(nonPlayer));

            ChatHeadsConfig.general.renderPosition = RenderPosition.BEFORE_LINE;
            assertEquals(ChatHeadManager.getHeadWidth(), ChatHeadManager.getLineOffset(firstLine));
            assertEquals(ChatHeadManager.getHeadWidth(), ChatHeadManager.getLineOffset(continuationWithoutName));
            assertTrue(ChatHeadManager.shouldRenderHead(firstLine));
            assertFalse(ChatHeadManager.shouldRenderHead(nameOnContinuation));
            assertEquals(ChatHeadManager.getHeadWidth(), ChatHeadManager.getLineOffset(nonPlayer));
        } finally {
            ChatHeadsConfig.general.renderPosition = previousPosition;
            ChatHeadsConfig.general.offsetNonPlayerText = previousOffset;
            ChatHeadsConfig.general.enabled = previousEnabled;
        }
    }
}
