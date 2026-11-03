/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chad_heads_cre.mixin;

import dzwdz.chad_heads_cre.ChatMessageContext;
import net.minecraft.client.gui.chat.NormalChatListener;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NormalChatListener.class)
public abstract class NormalChatListenerMixin {

    @Inject(method = "say", at = @At("HEAD"))
    private void chatheads$rememberChatType(ChatType chatTypeIn, ITextComponent message, CallbackInfo callbackInfo) {
        ChatMessageContext.set(chatTypeIn);
    }

    @Inject(method = "say", at = @At("RETURN"))
    private void chatheads$forgetChatType(ChatType chatTypeIn, ITextComponent message, CallbackInfo callbackInfo) {
        ChatMessageContext.clear();
    }
}
