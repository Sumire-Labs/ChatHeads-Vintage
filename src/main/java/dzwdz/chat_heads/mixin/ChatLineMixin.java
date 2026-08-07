/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.mixin;

import dzwdz.chat_heads.HeadData;
import dzwdz.chat_heads.LineBuildContext;
import dzwdz.chat_heads.mixininterface.ChatLineExtension;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatLine.class)
public abstract class ChatLineMixin implements ChatLineExtension {

    @Unique
    private HeadData chatheads$headData = HeadData.EMPTY;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void chatheads$attachHeadData(
            int updateCounterCreatedIn,
            ITextComponent lineStringIn,
            int chatLineIdIn,
            CallbackInfo callbackInfo
    ) {
        chatheads$headData = LineBuildContext.dataFor(lineStringIn);
    }

    @Override
    public HeadData chatheads$getHeadData() {
        return chatheads$headData;
    }

    @Override
    public void chatheads$setHeadData(HeadData data) {
        chatheads$headData = data == null ? HeadData.EMPTY : data;
    }
}
