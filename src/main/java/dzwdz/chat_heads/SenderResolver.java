/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import dzwdz.chat_heads.config.ChatHeadsConfig;
import dzwdz.chat_heads.config.ChatHeadsConfigState;
import dzwdz.chat_heads.config.SenderDetectionMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public final class SenderResolver {

    private SenderResolver() {
    }

    public static HeadData resolve(ITextComponent message, @Nullable ChatType chatType) {
        AutoAliasDetector.observe(message);

        if (!ChatHeadsConfigState.isActive() || !isMessageTypeEnabled(chatType)) {
            return HeadData.empty(chatType);
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        NetHandlerPlayClient connection = minecraft.getConnection();
        if (connection == null) {
            return HeadData.empty(chatType);
        }

        PlayerNameIndex names = new PlayerNameIndex(connection);
        NetworkPlayerInfo structuredSender = names.getExact(StructuredSenderEvidence.findPlayerName(message));
        if (structuredSender != null) {
            return createHeadData(chatType, names.findFirst(message.getUnformattedText(), structuredSender), structuredSender);
        }

        if (ChatHeadsConfig.detection.senderDetection == SenderDetectionMode.HEURISTIC) {
            NameMatcher.Match<NetworkPlayerInfo> match = names.findFirst(message.getUnformattedText());
            if (match != null) {
                return createHeadData(chatType, match, match.getValue());
            }
        }

        return HeadData.empty(chatType);
    }

    public static HeadData resolveForRefresh(ITextComponent message, HeadData previous) {
        if (previous != null && previous.getPlayerInfo() != null) {
            NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
            if (connection != null) {
                NameMatcher.Match<NetworkPlayerInfo> match = new PlayerNameIndex(connection)
                        .findFirst(message.getUnformattedText(), previous.getPlayerInfo());
                if (match != null) {
                    return createHeadData(previous.getChatType(), match, previous.getPlayerInfo());
                }
            }
            return previous.asFirstVisualLine();
        }
        return resolve(message, previous == null ? null : previous.getChatType());
    }

    private static HeadData createHeadData(
            @Nullable ChatType chatType,
            @Nullable NameMatcher.Match<NetworkPlayerInfo> match,
            NetworkPlayerInfo fallbackPlayer
    ) {
        if (match == null) {
            return HeadData.forPlayer(fallbackPlayer, chatType);
        }

        return HeadData.forPlayer(match.getValue(), chatType, match.getName(), match.getIndex());
    }

    public static boolean isMessageTypeEnabled(@Nullable ChatType chatType) {
        return chatType != ChatType.SYSTEM || ChatHeadsConfig.detection.handleSystemMessages;
    }

}
