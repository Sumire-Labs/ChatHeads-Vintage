/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import com.mojang.authlib.GameProfile;
import dzwdz.chat_heads.config.ChatHeadsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public final class ChatHeadRenderer {

    private ChatHeadRenderer() {
    }

    public static void render(NetworkPlayerInfo playerInfo, int x, int y, int textColor) {
        render(playerInfo, x, y, textColor, ChatHeadsConfig.general.drawShadow);
    }

    public static void render(NetworkPlayerInfo playerInfo, int x, int y, int textColor, boolean drawShadow) {
        Minecraft minecraft = Minecraft.getMinecraft();
        ResourceLocation skin = playerInfo.getLocationSkin();
        float opacity = getOpacity(textColor);

        EntityPlayer player = getPlayer(minecraft, playerInfo.getGameProfile());
        boolean upsideDown = isUpsideDown(playerInfo.getGameProfile(), player);
        boolean showHat = shouldShowHat(minecraft, playerInfo.getGameProfile(), player);
        float hatScale = 1.0F + (float)ChatHeadsConfig.general.threeDeeNess * 0.25F;

        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        minecraft.getTextureManager().bindTexture(skin);

        int textureY = upsideDown ? 16 : 8;
        int textureHeight = upsideDown ? -8 : 8;
        int normalY = drawShadow ? y - 1 : y;

        if (drawShadow) {
            GlStateManager.color(0.25F, 0.25F, 0.25F, opacity);
            drawSkinLayer(x + 1, y, 8, textureY, textureHeight, 1.0F);
            if (showHat) {
                drawSkinLayer(x + 1, y, 40, textureY, textureHeight, hatScale);
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, opacity);
        drawSkinLayer(x, normalY, 8, textureY, textureHeight, 1.0F);
        if (showHat) {
            drawSkinLayer(x, normalY, 40, textureY, textureHeight, hatScale);
        }

        // The active FontRenderer (vanilla, Smooth Font, NeoFontRender, etc.) owns all state after this point.
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void drawSkinLayer(int x, int y, int textureX, int textureY, int textureHeight, float scale) {
        if (scale != 1.0F) {
            float centerX = x + ChatHeadManager.HEAD_SIZE / 2.0F;
            float centerY = y + ChatHeadManager.HEAD_SIZE / 2.0F;
            GlStateManager.pushMatrix();
            GlStateManager.translate(centerX, centerY, 0.0F);
            GlStateManager.scale(scale, scale, 1.0F);
            GlStateManager.translate(-centerX, -centerY, 0.0F);
        }

        Gui.drawScaledCustomSizeModalRect(
                x,
                y,
                textureX,
                textureY,
                8,
                textureHeight,
                ChatHeadManager.HEAD_SIZE,
                ChatHeadManager.HEAD_SIZE,
                64.0F,
                64.0F
        );

        if (scale != 1.0F) {
            GlStateManager.popMatrix();
        }
    }

    private static float getOpacity(int color) {
        if ((color & 0xFC000000) == 0) {
            return 1.0F;
        }
        return (float)(color >>> 24 & 255) / 255.0F;
    }

    private static EntityPlayer getPlayer(Minecraft minecraft, GameProfile profile) {
        UUID uuid = profile.getId();
        if (minecraft.world == null || uuid == null) {
            return null;
        }
        return minecraft.world.getPlayerEntityByUUID(uuid);
    }

    private static boolean isUpsideDown(GameProfile profile, EntityPlayer player) {
        if (player == null || !player.isWearing(EnumPlayerModelParts.CAPE)) {
            return false;
        }
        String name = profile.getName();
        return "Dinnerbone".equals(name) || "Grumm".equals(name);
    }

    private static boolean shouldShowHat(Minecraft minecraft, GameProfile profile, EntityPlayer player) {
        if (player != null) {
            return player.isWearing(EnumPlayerModelParts.HAT);
        }

        UUID uuid = profile.getId();
        UUID localUuid = minecraft.getSession().getProfile().getId();
        return uuid != null
                && uuid.equals(localUuid)
                && minecraft.gameSettings.getModelParts().contains(EnumPlayerModelParts.HAT);
    }
}
