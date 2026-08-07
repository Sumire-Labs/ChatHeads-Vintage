/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.mixin.compat;

import dzwdz.chat_heads.ChatHeadRenderer;
import dzwdz.chat_heads.SuggestionHeadLookup;
import dzwdz.chat_heads.SuggestionListLayout;
import dzwdz.chat_heads.compat.SalutationSuggestionsAccess;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Optional adapter for Salutation 1.0.2's advanced tab-completion list.
 */
@Pseudo
@Mixin(targets = "speiger.src.salutation.client.gui.chat.AdvancedTabCompleter", remap = false)
public abstract class SalutationAdvancedTabCompleterMixin {

    @Inject(
            method = "render(IILnet/minecraft/client/gui/FontRenderer;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            remap = false
    )
    private void chatheads$renderSuggestionHeads(
            int mouseX,
            int mouseY,
            FontRenderer fontRenderer,
            CallbackInfo callbackInfo
    ) {
        SalutationSuggestionsAccess.View view = SalutationSuggestionsAccess.inspect(this);
        if (view == null) {
            return;
        }

        List<String> completions = view.getCompletions();
        int offset = view.getOffset();
        int visibleCount = Math.min(completions.size() - offset, 10);
        if (visibleCount <= 0) {
            return;
        }

        SuggestionHeadLookup lookup = SuggestionHeadLookup.current();
        if (!lookup.containsAny(completions)) {
            return;
        }

        GuiTextField textField = view.getTextField();
        String input = textField.getText();
        int prefixWidth = input.isEmpty()
                ? 0
                : fontRenderer.getStringWidth(
                input.substring(0, Math.max(0, input.lastIndexOf(" ")) + 1)
        );
        int originalX = Math.min(prefixWidth, textField.getWidth()) + textField.x;

        int originalWidth = 0;
        for (String completion : completions) {
            originalWidth = Math.max(originalWidth, fontRenderer.getStringWidth(completion));
        }

        int baseY = textField.y - 12 * visibleCount - 3;
        int height = textField.y - 3 - baseY;
        SuggestionListLayout layout = SuggestionListLayout.withHeadColumn(originalX, originalWidth);
        if (!view.setBox(layout.getLeft(), baseY, layout.getWidth(), height)) {
            return;
        }

        int hoveredIndex = mouseX >= layout.getLeft()
                && mouseX <= layout.getLeft() + layout.getWidth()
                && mouseY >= baseY
                && mouseY < baseY + height
                ? (mouseY - baseY) / 12
                : -1;

        Gui.drawRect(
                layout.getLeft(),
                baseY,
                layout.getLeft() + layout.getWidth() + 5,
                textField.y - 3,
                -805306368
        );

        for (int row = 0; row < visibleCount; ++row) {
            int completionIndex = row + offset;
            String completion = completions.get(completionIndex);
            int color = row == hoveredIndex || completionIndex == view.getCompletionIndex()
                    ? -256
                    : -5592406;
            int y = baseY + row * 12 + 2;
            NetworkPlayerInfo playerInfo = lookup.find(completion);

            if (playerInfo != null) {
                ChatHeadRenderer.render(playerInfo, layout.getLeft() + 2, y, color, false);
            }
            fontRenderer.drawStringWithShadow(completion, layout.getTextLeft() + 2, y, color);
        }

        callbackInfo.cancel();
    }
}
