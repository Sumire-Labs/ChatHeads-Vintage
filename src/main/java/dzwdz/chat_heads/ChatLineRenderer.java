/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import dzwdz.chat_heads.mixininterface.ChatLineExtension;

/** Shared rendering for vanilla chat and replacement chat GUIs. */
public final class ChatLineRenderer {
    private ChatLineRenderer() {}

    public static int draw(FontRenderer fontRenderer, String text, float x, float y,
                           int color, Operation<Integer> original, ChatLine chatLine) {
        HeadData data = ((ChatLineExtension) (Object) chatLine).chatheads$getHeadData();
        int offset = ChatHeadManager.getLineOffset(data);

        if (ChatHeadManager.shouldRenderHead(data)) {
            if (ChatHeadManager.isBeforeName()) {
                FormattedTextLayout.Split split = FormattedTextLayout.split(text, data.getHeadCharacterIndex());
                if (split.isValid()) {
                    String prefix = split.getPrefix();
                    int prefixWidth = fontRenderer.getStringWidth(prefix);
                    int prefixEnd = (int) x;

                    if (!prefix.isEmpty()) {
                        prefixEnd = original.call(fontRenderer, prefix, x, y, color);
                    }

                    ChatHeadRenderer.render(data.getPlayerInfo(), (int) x + prefixWidth, (int) y, color);

                    String suffix = FontRenderer.getFormatFromString(prefix) + split.getSuffix();
                    if (!suffix.isEmpty()) {
                        return original.call(
                                fontRenderer,
                                suffix,
                                x + prefixWidth + ChatHeadManager.getHeadWidth(),
                                y,
                                color
                        );
                    }

                    return Math.max(prefixEnd, (int) x + prefixWidth + ChatHeadManager.getHeadWidth());
                }

                // Invalid metadata must not let the head overlap the text.
                ChatHeadRenderer.render(data.getPlayerInfo(), (int) x, (int) y, color);
                return original.call(fontRenderer, text, x + ChatHeadManager.getHeadWidth(), y, color);
            }

            ChatHeadRenderer.render(data.getPlayerInfo(), (int) x, (int) y, color);
        }

        return original.call(fontRenderer, text, x + offset, y, color);
    }
}
