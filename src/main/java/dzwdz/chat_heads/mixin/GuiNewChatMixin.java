/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dzwdz.chat_heads.ChatHeadManager;
import dzwdz.chat_heads.ChatHeadRenderer;
import dzwdz.chat_heads.ChatMessageContext;
import dzwdz.chat_heads.FormattedTextLayout;
import dzwdz.chat_heads.HeadData;
import dzwdz.chat_heads.LineBuildContext;
import dzwdz.chat_heads.SenderResolver;
import dzwdz.chat_heads.mixininterface.ChatLineExtension;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GuiNewChat.class, priority = 990)
public abstract class GuiNewChatMixin {

    @Shadow
    @Final
    private Minecraft mc;

    @Unique
    private HeadData chatheads$messageData = HeadData.EMPTY;
    @Unique
    private int chatheads$clickOffset;
    @Unique
    private int chatheads$clickWidthCall;
    @Unique
    private int chatheads$clickOffsetWidthCall = -1;

    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"))
    private void chatheads$detectSender(ITextComponent chatComponent, int chatLineId, CallbackInfo callbackInfo) {
        chatheads$messageData = SenderResolver.resolve(chatComponent, ChatMessageContext.get());
    }

    @Inject(
            method = "refreshChat",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiNewChat;setChatLine(Lnet/minecraft/util/text/ITextComponent;IIZ)V"
            )
    )
    private void chatheads$restoreSenderForRefresh(CallbackInfo callbackInfo, @Local ChatLine chatLine) {
        HeadData previous = ((ChatLineExtension)(Object)chatLine).chatheads$getHeadData();
        chatheads$messageData = SenderResolver.resolveForRefresh(chatLine.getChatComponent(), previous);
    }

    @Inject(method = "setChatLine", at = @At("HEAD"))
    private void chatheads$beginBuildingLines(
            ITextComponent chatComponent,
            int chatLineId,
            int updateCounter,
            boolean displayOnly,
            CallbackInfo callbackInfo
    ) {
        LineBuildContext.begin(chatheads$messageData, chatComponent);
    }

    @Inject(method = "setChatLine", at = @At("RETURN"))
    private void chatheads$finishBuildingLines(
            ITextComponent chatComponent,
            int chatLineId,
            int updateCounter,
            boolean displayOnly,
            CallbackInfo callbackInfo
    ) {
        LineBuildContext.end();
        chatheads$messageData = HeadData.EMPTY;
    }

    @ModifyArg(
            method = "setChatLine",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiUtilRenderComponents;splitText(Lnet/minecraft/util/text/ITextComponent;ILnet/minecraft/client/gui/FontRenderer;ZZ)Ljava/util/List;"
            ),
            index = 1
    )
    private int chatheads$reserveHeadWidth(int originalWidth) {
        return Math.max(1, originalWidth - ChatHeadManager.getTextWidthDifference(chatheads$messageData));
    }

    @WrapOperation(
            method = "drawChat",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"
            )
    )
    private int chatheads$drawHeadAndMoveText(
            FontRenderer fontRenderer,
            String text,
            float x,
            float y,
            int color,
            Operation<Integer> original,
            @Local ChatLine chatLine
    ) {
        HeadData data = ((ChatLineExtension)(Object)chatLine).chatheads$getHeadData();
        int offset = ChatHeadManager.getLineOffset(data);

        if (ChatHeadManager.shouldRenderHead(data)) {
            if (ChatHeadManager.isBeforeName()) {
                FormattedTextLayout.Split split = FormattedTextLayout.split(text, data.getHeadCharacterIndex());
                if (split.isValid()) {
                    String prefix = split.getPrefix();
                    int prefixWidth = fontRenderer.getStringWidth(prefix);
                    int prefixEnd = (int)x;

                    if (!prefix.isEmpty()) {
                        prefixEnd = original.call(fontRenderer, prefix, x, y, color);
                    }

                    ChatHeadRenderer.render(data.getPlayerInfo(), (int)x + prefixWidth, (int)y, color);

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

                    return Math.max(prefixEnd, (int)x + prefixWidth + ChatHeadManager.getHeadWidth());
                }

                // Invalid metadata must not let the head overlap the text.
                ChatHeadRenderer.render(data.getPlayerInfo(), (int)x, (int)y, color);
                return original.call(fontRenderer, text, x + ChatHeadManager.getHeadWidth(), y, color);
            }

            ChatHeadRenderer.render(data.getPlayerInfo(), (int)x, (int)y, color);
        }

        return original.call(fontRenderer, text, x + offset, y, color);
    }

    @Inject(method = "getChatComponent", at = @At("HEAD"))
    private void chatheads$resetClickOffset(int mouseX, int mouseY, CallbackInfoReturnable<ITextComponent> callbackInfo) {
        chatheads$clickOffset = 0;
        chatheads$clickWidthCall = 0;
        chatheads$clickOffsetWidthCall = -1;
    }

    @Inject(
            method = "getChatComponent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/ChatLine;getChatComponent()Lnet/minecraft/util/text/ITextComponent;"
            ),
            cancellable = true
    )
    private void chatheads$prepareClickOffset(
            int mouseX,
            int mouseY,
            CallbackInfoReturnable<ITextComponent> callbackInfo,
            @Local ChatLine chatLine,
            @Local(index = 6) int logicalMouseX
    ) {
        HeadData data = ((ChatLineExtension)(Object)chatLine).chatheads$getHeadData();
        int lineOffset = ChatHeadManager.getLineOffset(data);
        int headPixelX = 0;

        if (lineOffset > 0) {
            chatheads$clickOffset = lineOffset;
            chatheads$clickOffsetWidthCall = 0;
        } else if (ChatHeadManager.isBeforeName() && ChatHeadManager.shouldRenderHead(data)) {
            FormattedTextLayout.Split split = FormattedTextLayout.split(
                    chatLine.getChatComponent().getFormattedText(),
                    data.getHeadCharacterIndex()
            );

            chatheads$clickOffset = ChatHeadManager.getHeadWidth();
            if (split.isValid()) {
                headPixelX = mc.fontRenderer.getStringWidth(split.getPrefix());
                chatheads$clickOffsetWidthCall = chatheads$findWidthCall(
                        chatLine.getChatComponent(),
                        data.getHeadCharacterIndex()
                );
            } else {
                chatheads$clickOffsetWidthCall = 0;
            }
        }

        if (chatheads$clickOffset > 0
                && logicalMouseX >= headPixelX
                && logicalMouseX < headPixelX + chatheads$clickOffset) {
            callbackInfo.setReturnValue(null);
        }
    }

    @WrapOperation(
            method = "getChatComponent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;getStringWidth(Ljava/lang/String;)I"
            )
    )
    private int chatheads$shiftClickBoundaries(
            FontRenderer fontRenderer,
            String text,
            Operation<Integer> original
    ) {
        int width = original.call(fontRenderer, text);
        if (chatheads$clickWidthCall == chatheads$clickOffsetWidthCall) {
            width += chatheads$clickOffset;
        }
        ++chatheads$clickWidthCall;
        return width;
    }

    @Unique
    private int chatheads$findWidthCall(ITextComponent line, int headCharacterIndex) {
        int visibleCharacters = 0;
        int widthCall = 0;
        int lastWidthCall = 0;

        for (ITextComponent component : line) {
            if (!(component instanceof TextComponentString)) {
                continue;
            }

            String text = GuiUtilRenderComponents.removeTextColorsIfConfigured(
                    ((TextComponentString)component).getText(),
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
