/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.mixin;

import dzwdz.chat_heads.Compatibility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class ChatHeadsMixinPlugin implements IMixinConfigPlugin {

    private static final Logger LOGGER = LogManager.getLogger("Chat Heads Vintage/Mixin");
    private boolean warned;

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean incompatibleAddonLoaded = Compatibility.isModLoaded(Compatibility.NEO_FONT_RENDER_UI_ENHANCEMENTS);
        if (incompatibleAddonLoaded && !warned) {
            warned = true;
            LOGGER.warn(
                    "Skipping Chat Heads mixins because {} modifies the same chat methods and provides its own chat heads",
                    Compatibility.NEO_FONT_RENDER_UI_ENHANCEMENTS
            );
        }
        if (incompatibleAddonLoaded) {
            return false;
        }

        if (mixinClassName.endsWith(".compat.BrigoSuggestionsListMixin")) {
            return Compatibility.isModLoaded(Compatibility.BRIGO);
        }
        if (mixinClassName.endsWith(".compat.CleanroomSuggestionListMixin")) {
            return Compatibility.hasCleanroomCommandSuggestions();
        }
        if (mixinClassName.endsWith(".compat.SalutationAdvancedTabCompleterMixin")) {
            return Compatibility.isModLoaded(Compatibility.SALUTATION);
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
