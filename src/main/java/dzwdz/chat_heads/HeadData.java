/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.ChatType;

import javax.annotation.Nullable;

/**
 * Metadata attached to both saved and visually wrapped vanilla chat lines.
 */
public final class HeadData {

    public static final HeadData EMPTY = new HeadData(null, null, null, -1, true, -1);

    @Nullable
    private final NetworkPlayerInfo playerInfo;
    @Nullable
    private final ChatType chatType;
    @Nullable
    private final String matchedName;
    private final int messageNameIndex;
    private final boolean firstVisualLine;
    private final int headCharacterIndex;

    private HeadData(
            @Nullable NetworkPlayerInfo playerInfo,
            @Nullable ChatType chatType,
            @Nullable String matchedName,
            int messageNameIndex,
            boolean firstVisualLine,
            int headCharacterIndex
    ) {
        this.playerInfo = playerInfo;
        this.chatType = chatType;
        this.matchedName = matchedName;
        this.messageNameIndex = messageNameIndex;
        this.firstVisualLine = firstVisualLine;
        this.headCharacterIndex = headCharacterIndex;
    }

    public static HeadData empty(@Nullable ChatType chatType) {
        return chatType == null ? EMPTY : new HeadData(null, chatType, null, -1, true, -1);
    }

    public static HeadData forPlayer(NetworkPlayerInfo playerInfo, @Nullable ChatType chatType) {
        return forPlayer(playerInfo, chatType, null, -1);
    }

    public static HeadData forPlayer(
            NetworkPlayerInfo playerInfo,
            @Nullable ChatType chatType,
            @Nullable String matchedName,
            int messageNameIndex
    ) {
        if (playerInfo == null) {
            return empty(chatType);
        }
        return new HeadData(playerInfo, chatType, matchedName, messageNameIndex, true, messageNameIndex);
    }

    @Nullable
    public NetworkPlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    @Nullable
    public ChatType getChatType() {
        return chatType;
    }

    @Nullable
    public String getMatchedName() {
        return matchedName;
    }

    public int getMessageNameIndex() {
        return messageNameIndex;
    }

    public boolean isFirstVisualLine() {
        return firstVisualLine;
    }

    /**
     * Visible UTF-16 index on this wrapped line, or -1 when this is not the head line.
     */
    public int getHeadCharacterIndex() {
        return headCharacterIndex;
    }

    public HeadData asFirstVisualLine() {
        if (firstVisualLine && headCharacterIndex == messageNameIndex) {
            return this;
        }
        return new HeadData(playerInfo, chatType, matchedName, messageNameIndex, true, messageNameIndex);
    }

    public HeadData asContinuation() {
        if (!firstVisualLine && headCharacterIndex < 0) {
            return this;
        }
        return asWrappedLine(false, -1);
    }

    public HeadData asWrappedLine(boolean firstLine, int localHeadCharacterIndex) {
        return new HeadData(
                playerInfo,
                chatType,
                matchedName,
                messageNameIndex,
                firstLine,
                localHeadCharacterIndex
        );
    }
}
