/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chad_heads_cre;

import net.minecraftforge.fml.common.Loader;

public final class Compatibility {

    public static final String BETTER_CHAT = "betterchat";
    public static final String NEO_FONT_RENDER_UI_ENHANCEMENTS = "neofontrender_ui_enhancements";

    private static boolean neoFontRenderUiEnhancementsLoaded;

    private Compatibility() {
    }

    public static void initialize() {
        neoFontRenderUiEnhancementsLoaded = isModLoaded(NEO_FONT_RENDER_UI_ENHANCEMENTS);

        if (neoFontRenderUiEnhancementsLoaded) {
            ChadHeadsCRE.LOGGER.warn(
                    "Disabling Chat Heads integration because {} modifies the same vanilla chat pipeline and provides its own chat heads",
                    NEO_FONT_RENDER_UI_ENHANCEMENTS
            );
        }
    }

    public static boolean isNeoFontRenderUiEnhancementsLoaded() {
        return neoFontRenderUiEnhancementsLoaded;
    }

    public static boolean isModLoaded(String modId) {
        try {
            return Loader.isModLoaded(modId);
        } catch (Throwable ignored) {
            return false;
        }
    }

}
