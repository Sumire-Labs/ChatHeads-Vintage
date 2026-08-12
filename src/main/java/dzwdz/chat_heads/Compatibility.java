/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import net.minecraftforge.fml.common.Loader;

public final class Compatibility {

    public static final String BRIGO = "brigo";
    public static final String CLEANROOM = "cleanroom";
    public static final String CLEANROOM_SUGGESTION_LIST =
            "com.cleanroommc.client.chat.suggestion.SuggestionList";
    public static final String NEO_FONT_RENDER_UI_ENHANCEMENTS = "neofontrender_ui_enhancements";
    public static final String SALUTATION = "salutation";

    private static boolean neoFontRenderUiEnhancementsLoaded;

    private Compatibility() {
    }

    public static void initialize() {
        neoFontRenderUiEnhancementsLoaded = isModLoaded(NEO_FONT_RENDER_UI_ENHANCEMENTS);

        if (neoFontRenderUiEnhancementsLoaded) {
            ChatHeadsVintage.LOGGER.warn(
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

    public static boolean hasCleanroomCommandSuggestions() {
        return isModLoaded(CLEANROOM) && isClassPresent(CLEANROOM_SUGGESTION_LIST);
    }

    static boolean isClassPresent(String className) {
        String resourceName = className.replace('.', '/') + ".class";
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null && contextClassLoader.getResource(resourceName) != null) {
                return true;
            }

            ClassLoader ownClassLoader = Compatibility.class.getClassLoader();
            return ownClassLoader != null
                    && ownClassLoader != contextClassLoader
                    && ownClassLoader.getResource(resourceName) != null;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
