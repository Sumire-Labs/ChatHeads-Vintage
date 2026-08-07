/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import dzwdz.chat_heads.config.ChatHeadsConfig;
import dzwdz.chat_heads.config.ChatHeadsConfigState;
import dzwdz.chat_heads.config.RenderPosition;

public final class ChatHeadManager {

    public static final int HEAD_SIZE = 8;
    private static final int PADDING = 2;

    private ChatHeadManager() {
    }

    public static int getHeadWidth() {
        return getHeadWidth(ChatHeadsConfig.general.drawShadow);
    }

    public static int getHeadWidth(boolean drawShadow) {
        return HEAD_SIZE + PADDING + (drawShadow ? 1 : 0);
    }

    /**
     * Width removed before vanilla wraps the component.
     */
    public static int getTextWidthDifference(HeadData data) {
        if (!isEligible(data)) {
            return 0;
        }

        if (data.getPlayerInfo() != null) {
            return getHeadWidth();
        }

        return ChatHeadsConfig.general.renderPosition == RenderPosition.BEFORE_LINE
                && ChatHeadsConfig.general.offsetNonPlayerText ? getHeadWidth() : 0;
    }

    /**
     * Horizontal offset applied to whole wrapped lines.
     */
    public static int getLineOffset(HeadData data) {
        return ChatHeadsConfig.general.renderPosition == RenderPosition.BEFORE_LINE
                ? getTextWidthDifference(data)
                : 0;
    }

    public static boolean shouldRenderHead(HeadData data) {
        if (!isEligible(data) || data.getPlayerInfo() == null) {
            return false;
        }

        return ChatHeadsConfig.general.renderPosition == RenderPosition.BEFORE_LINE
                ? data.isFirstVisualLine()
                : data.getHeadCharacterIndex() >= 0;
    }

    public static boolean isBeforeName() {
        return ChatHeadsConfig.general.renderPosition == RenderPosition.BEFORE_NAME;
    }

    private static boolean isEligible(HeadData data) {
        return data != null
                && ChatHeadsConfigState.isActive()
                && SenderResolver.isMessageTypeEnabled(data.getChatType());
    }
}
