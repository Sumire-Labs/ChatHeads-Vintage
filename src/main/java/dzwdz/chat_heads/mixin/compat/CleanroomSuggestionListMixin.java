/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.mixin.compat;

import dzwdz.chat_heads.ChatHeadRenderer;
import dzwdz.chat_heads.SuggestionHeadLookup;
import dzwdz.chat_heads.SuggestionListLayout;
import dzwdz.chat_heads.compat.CleanroomSuggestionsAccess;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

/**
 * Optional adapter for the command-suggestion list built into Cleanroom 0.6.10-alpha and newer.
 */
@Pseudo
@Mixin(targets = "com.cleanroommc.client.chat.suggestion.SuggestionList", remap = false)
public abstract class CleanroomSuggestionListMixin {

    @Unique
    private int chatheads$expandedCachedWidth = -1;
    @Unique
    private boolean chatheads$renderingHeadColumn;
    @Unique
    private SuggestionHeadLookup chatheads$lookup;

    @Inject(method = "setSuggestions(Ljava/util/List;)V", at = @At("RETURN"), require = 0, remap = false)
    private void chatheads$resetAfterSuggestionsChanged(List<String> suggestions, CallbackInfo callbackInfo) {
        chatheads$resetState();
    }

    @Inject(method = "hide()V", at = @At("RETURN"), require = 0, remap = false)
    private void chatheads$resetAfterSuggestionsHidden(CallbackInfo callbackInfo) {
        chatheads$resetState();
    }

    @Inject(method = "render(II)V", at = @At("HEAD"), require = 0, remap = false)
    private void chatheads$prepareHeadColumn(int mouseX, int mouseY, CallbackInfo callbackInfo) {
        chatheads$renderingHeadColumn = false;
        chatheads$lookup = null;

        CleanroomSuggestionsAccess.View view = CleanroomSuggestionsAccess.inspect(this);
        if (view == null) {
            chatheads$expandedCachedWidth = -1;
            return;
        }

        int baseWidth = view.getCachedWidth();
        if (baseWidth == chatheads$expandedCachedWidth) {
            baseWidth = Math.max(0, baseWidth - SuggestionListLayout.HEAD_COLUMN_WIDTH);
            if (!view.setCachedWidth(baseWidth)) {
                chatheads$expandedCachedWidth = -1;
                return;
            }
        }
        chatheads$expandedCachedWidth = -1;

        SuggestionHeadLookup lookup = SuggestionHeadLookup.current();
        if (!lookup.containsAny(view.getSuggestions()) || !CleanroomSuggestionsAccess.isUsable()) {
            return;
        }

        int expandedWidth = baseWidth + SuggestionListLayout.HEAD_COLUMN_WIDTH;
        // Cleanroom clamps its box to the text field. Skip the column instead of clipping long entries.
        if (expandedWidth > view.getMaximumWidth() || !view.setCachedWidth(expandedWidth)) {
            return;
        }

        chatheads$expandedCachedWidth = expandedWidth;
        chatheads$renderingHeadColumn = true;
        chatheads$lookup = lookup;
    }

    @ModifyVariable(
            method = "computeGeometry()Lcom/cleanroommc/client/chat/suggestion/SuggestionList$Geometry;",
            at = @At(value = "STORE", ordinal = 0),
            index = 10,
            require = 0,
            remap = false
    )
    private int chatheads$makeRoomToTheLeft(int originalX) {
        return chatheads$expandedCachedWidth < 0
                ? originalX
                : originalX - SuggestionListLayout.HEAD_COLUMN_WIDTH;
    }

    @ModifyArgs(
            method = "render(II)V",
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
            ChatHeadRenderer.render(playerInfo, (int) x + 1, (int) y, color, false);
        }
        args.set(1, x + SuggestionListLayout.HEAD_COLUMN_WIDTH);
    }

    @Inject(method = "render(II)V", at = @At("RETURN"), require = 0, remap = false)
    private void chatheads$finishHeadColumn(int mouseX, int mouseY, CallbackInfo callbackInfo) {
        chatheads$renderingHeadColumn = false;
        chatheads$lookup = null;
    }

    @Unique
    private void chatheads$resetState() {
        chatheads$expandedCachedWidth = -1;
        chatheads$renderingHeadColumn = false;
        chatheads$lookup = null;
    }
}
