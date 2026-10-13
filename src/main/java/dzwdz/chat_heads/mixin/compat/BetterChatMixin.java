/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dzwdz.chat_heads.ChatHeadManager;
import dzwdz.chat_heads.ChatLineRenderer;
import dzwdz.chat_heads.ChatMessageContext;
import dzwdz.chat_heads.FormattedTextLayout;
import dzwdz.chat_heads.HeadData;
import dzwdz.chat_heads.LineBuildContext;
import dzwdz.chat_heads.SenderResolver;
import dzwdz.chat_heads.mixininterface.ChatLineExtension;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Better Chat 1.4 (Minecraft 1.12.2) replaces the vanilla chat pipeline, including its private line builder.
 * Override names have both MCP and SRG selectors; Better Chat's private members are not remapped.
 */
@Pseudo
@Mixin(targets = "com.llamalad7.betterchat.gui.GuiBetterChat", remap = false, priority = 990)
public abstract class BetterChatMixin {

    @Shadow
    @Final
    private Minecraft mc;

    @Unique
    private HeadData chatheads$betterChat$messageData = HeadData.EMPTY;
    @Unique
    private int chatheads$betterChat$clickOffset;
    @Unique
    private int chatheads$betterChat$clickWidthCall;
    @Unique
    private int chatheads$betterChat$clickOffsetWidthCall = -1;

    @Inject(method = {"printChatMessageWithOptionalDeletion", "func_146234_a"}, at = @At("HEAD"))
    private void chatheads$betterChat$detectSender(ITextComponent chatComponent, int chatLineId, CallbackInfo callbackInfo) {
        chatheads$betterChat$messageData = SenderResolver.resolve(chatComponent, ChatMessageContext.get());
    }

    @Inject(
            method = {"refreshChat", "func_146245_b"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/llamalad7/betterchat/gui/GuiBetterChat;setChatLine(Lnet/minecraft/util/text/ITextComponent;IIZ)V"
            )
    )
    private void chatheads$betterChat$restoreSenderForRefresh(CallbackInfo callbackInfo, @Local ChatLine chatLine) {
        HeadData previous = ((ChatLineExtension) (Object) chatLine).chatheads$getHeadData();
        chatheads$betterChat$messageData = SenderResolver.resolveForRefresh(chatLine.getChatComponent(), previous);
    }

    @Inject(method = "setChatLine", at = @At("HEAD"))
    private void chatheads$betterChat$beginBuildingLines(
            ITextComponent chatComponent,
            int chatLineId,
            int updateCounter,
            boolean displayOnly,
            CallbackInfo callbackInfo
    ) {
        LineBuildContext.begin(chatheads$betterChat$messageData, chatComponent);
    }

    @Inject(method = "setChatLine", at = @At("RETURN"))
    private void chatheads$betterChat$finishBuildingLines(
            ITextComponent chatComponent,
            int chatLineId,
            int updateCounter,
            boolean displayOnly,
            CallbackInfo callbackInfo
    ) {
        LineBuildContext.end();
        chatheads$betterChat$messageData = HeadData.EMPTY;
    }

    @ModifyArg(
            method = "setChatLine",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiUtilRenderComponents;splitText(Lnet/minecraft/util/text/ITextComponent;ILnet/minecraft/client/gui/FontRenderer;ZZ)Ljava/util/List;",
                    remap = true
            ),
            index = 1
    )
    private int chatheads$betterChat$reserveHeadWidth(int originalWidth) {
        return Math.max(1, originalWidth - ChatHeadManager.getTextWidthDifference(chatheads$betterChat$messageData));
    }

    @WrapOperation(
            method = {"drawChat", "func_146230_a"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I",
                    remap = true
            )
    )
    private int chatheads$betterChat$drawHeadAndMoveText(
            FontRenderer fontRenderer,
            String text,
            float x,
            float y,
            int color,
            Operation<Integer> original,
            @Local ChatLine chatLine
    ) {
        return ChatLineRenderer.draw(fontRenderer, text, x, y, color, original, chatLine);
    }

    @Inject(method = {"getChatComponent", "func_146236_a"}, at = @At("HEAD"))
    private void chatheads$betterChat$resetClickOffset(int mouseX, int mouseY, CallbackInfoReturnable<ITextComponent> callbackInfo) {
        chatheads$betterChat$clickOffset = 0;
        chatheads$betterChat$clickWidthCall = 0;
        chatheads$betterChat$clickOffsetWidthCall = -1;
    }

    @Inject(
            method = {"getChatComponent", "func_146236_a"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/ChatLine;getChatComponent()Lnet/minecraft/util/text/ITextComponent;",
                    remap = true
            ),
            cancellable = true
    )
    private void chatheads$betterChat$prepareClickOffset(
            int mouseX,
            int mouseY,
            CallbackInfoReturnable<ITextComponent> callbackInfo,
            @Local ChatLine chatLine,
            @Local(index = 6) int logicalMouseX
    ) {
        HeadData data = ((ChatLineExtension) (Object) chatLine).chatheads$getHeadData();
        int lineOffset = ChatHeadManager.getLineOffset(data);
        int headPixelX = 0;

        if (lineOffset > 0) {
            chatheads$betterChat$clickOffset = lineOffset;
            chatheads$betterChat$clickOffsetWidthCall = 0;
        } else if (ChatHeadManager.isBeforeName() && ChatHeadManager.shouldRenderHead(data)) {
            FormattedTextLayout.Split split = FormattedTextLayout.split(
                    chatLine.getChatComponent().getFormattedText(),
                    data.getHeadCharacterIndex()
            );

            chatheads$betterChat$clickOffset = ChatHeadManager.getHeadWidth();
            if (split.isValid()) {
                headPixelX = mc.fontRenderer.getStringWidth(split.getPrefix());
                chatheads$betterChat$clickOffsetWidthCall = chatheads$betterChat$findWidthCall(
                        chatLine.getChatComponent(),
                        data.getHeadCharacterIndex()
                );
            } else {
                chatheads$betterChat$clickOffsetWidthCall = 0;
            }
        }

        if (chatheads$betterChat$clickOffset > 0
                && logicalMouseX >= headPixelX
                && logicalMouseX < headPixelX + chatheads$betterChat$clickOffset) {
            callbackInfo.setReturnValue(null);
        }
    }

    @WrapOperation(
            method = {"getChatComponent", "func_146236_a"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;getStringWidth(Ljava/lang/String;)I",
                    remap = true
            )
    )
    private int chatheads$betterChat$shiftClickBoundaries(
            FontRenderer fontRenderer,
            String text,
            Operation<Integer> original
    ) {
        int width = original.call(fontRenderer, text);
        if (chatheads$betterChat$clickWidthCall == chatheads$betterChat$clickOffsetWidthCall) {
            width += chatheads$betterChat$clickOffset;
        }
        ++chatheads$betterChat$clickWidthCall;
        return width;
    }

    @Unique
    private int chatheads$betterChat$findWidthCall(ITextComponent line, int headCharacterIndex) {
        int visibleCharacters = 0;
        int widthCall = 0;
        int lastWidthCall = 0;

        for (ITextComponent component : line) {
            if (!(component instanceof TextComponentString)) {
                continue;
            }

            String text = GuiUtilRenderComponents.removeTextColorsIfConfigured(
                    ((TextComponentString) component).getText(),
                    false
            );
            int componentLength = FormattedTextLayout.plainLength(text);
            lastWidthCall = widthCall;

            if (componentLength > 0
                    && headCharacterIndex >= visibleCharacters
                    && headCharacterIndex < visibleCharacters + componentLength) {
                return widthCall;
            }

            visibleCharacters += componentLength;
            ++widthCall;
        }

        return lastWidthCall;
    }
}
