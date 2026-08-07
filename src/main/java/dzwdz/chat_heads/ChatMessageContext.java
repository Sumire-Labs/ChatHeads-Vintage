/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import net.minecraft.util.text.ChatType;

import javax.annotation.Nullable;

/**
 * Carries the ChatType through NormalChatListener into the otherwise untyped GuiNewChat API.
 */
public final class ChatMessageContext {

    private static final ThreadLocal<ChatType> CURRENT_TYPE = new ThreadLocal<ChatType>();

    private ChatMessageContext() {
    }

    public static void set(ChatType chatType) {
        CURRENT_TYPE.set(chatType);
    }

    public static void clear() {
        CURRENT_TYPE.remove();
    }

    @Nullable
    public static ChatType get() {
        return CURRENT_TYPE.get();
    }
}
