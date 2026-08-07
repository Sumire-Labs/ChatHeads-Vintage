/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.mixin.compat;

import dzwdz.chat_heads.ChatHeadRenderer;
import dzwdz.chat_heads.SuggestionHeadLookup;
import dzwdz.chat_heads.SuggestionListLayout;
import dzwdz.chat_heads.compat.BrigoSuggestionsAccess;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/** Optional adapter for Brigo 1.1.1's command-suggestion list. */
@Pseudo
@Mixin(targets = "dev.xhyrom.brigo.client.gui.CommandSuggestions$SuggestionsList", remap = false)
public abstract class BrigoSuggestionsListMixin {

    @Unique
    private boolean chatheads$capturedOriginalBounds;
    @Unique
    private int chatheads$originalX;
    @Unique
    private int chatheads$originalWidth;
    @Unique
    private boolean chatheads$renderingHeadColumn;
    @Unique
    private SuggestionHeadLookup chatheads$lookup;

    @Inject(method = "render(II)V", at = @At("HEAD"), require = 0, remap = false)
    private void chatheads$prepareHeadColumn(int mouseX, int mouseY, CallbackInfo callbackInfo) {
        BrigoSuggestionsAccess.View view = BrigoSuggestionsAccess.inspect(this);
        if (view == null) {
            chatheads$renderingHeadColumn = false;
            chatheads$lookup = null;
            return;
        }

        if (!chatheads$capturedOriginalBounds) {
            chatheads$capturedOriginalBounds = true;
            chatheads$originalX = view.getX();
            chatheads$originalWidth = view.getWidth();
        }

        SuggestionHeadLookup lookup = SuggestionHeadLookup.current();
        boolean hasPlayerSuggestion = false;
        for (Object suggestion : view.getSuggestions()) {
            String text = BrigoSuggestionsAccess.getSuggestionText(suggestion);
            if (!BrigoSuggestionsAccess.isUsable()) {
                break;
            }
            if (lookup.find(text) != null) {
                hasPlayerSuggestion = true;
                break;
            }
        }

        if (!hasPlayerSuggestion || !BrigoSuggestionsAccess.isUsable()) {
            view.setBounds(chatheads$originalX, chatheads$originalWidth);
            chatheads$renderingHeadColumn = false;
            chatheads$lookup = null;
            return;
        }

        SuggestionListLayout layout = SuggestionListLayout.withHeadColumn(
                chatheads$originalX,
                chatheads$originalWidth
        );
        if (view.setBounds(layout.getLeft(), layout.getWidth())) {
            chatheads$renderingHeadColumn = true;
            chatheads$lookup = lookup;
        } else {
            chatheads$renderingHeadColumn = false;
            chatheads$lookup = null;
        }
    }

    @ModifyArgs(
            method = "renderSuggestions(IIIZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I",
                    remap = true
            ),
            require = 0,
            remap = false
    )
    private void chatheads$renderHeadAndMoveText(Args args) {
        if (!chatheads$renderingHeadColumn || chatheads$lookup == null) {
            return;
        }

        String suggestion = args.get(0);
        float x = args.get(1);
        float y = args.get(2);
        int color = args.get(3);
        NetworkPlayerInfo playerInfo = chatheads$lookup.find(suggestion);

        if (playerInfo != null) {
            ChatHeadRenderer.render(playerInfo, (int)x + 1, (int)y, color, false);
        }
        args.set(1, x + SuggestionListLayout.HEAD_COLUMN_WIDTH);
    }

    @Inject(method = "render(II)V", at = @At("RETURN"), require = 0, remap = false)
    private void chatheads$finishHeadColumn(int mouseX, int mouseY, CallbackInfo callbackInfo) {
        chatheads$renderingHeadColumn = false;
        chatheads$lookup = null;
    }
}
