/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.config;

import dzwdz.chat_heads.ChatHeadsVintage;
import dzwdz.chat_heads.Compatibility;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ChatHeadsVintage.MOD_ID, value = Side.CLIENT)
public final class ChatHeadsConfigState {

    private static volatile Map<String, String> aliases = Collections.emptyMap();

    private ChatHeadsConfigState() {
    }

    public static boolean isActive() {
        if (!ChatHeadsConfig.general.enabled) {
            return false;
        }

        return !Compatibility.isNeoFontRenderUiEnhancementsLoaded();
    }

    public static Map<String, String> getAliases() {
        return aliases;
    }

    public static void reloadAliases() {
        Map<String, String> parsed = new LinkedHashMap<String, String>();
        String[] configuredAliases = ChatHeadsConfig.detection.nameAliases;

        if (configuredAliases != null) {
            for (String entry : configuredAliases) {
                if (entry == null) {
                    continue;
                }

                int separator = entry.indexOf('=');
                if (separator <= 0 || separator == entry.length() - 1) {
                    if (!entry.trim().isEmpty()) {
                        ChatHeadsVintage.LOGGER.warn("Ignoring invalid name alias '{}'; expected nickname=profileName", entry);
                    }
                    continue;
                }

                String nickname = entry.substring(0, separator).trim();
                String profileName = entry.substring(separator + 1).trim();
                if (nickname.isEmpty() || profileName.isEmpty()) {
                    ChatHeadsVintage.LOGGER.warn("Ignoring invalid name alias '{}'; neither side may be empty", entry);
                    continue;
                }

                String previous = parsed.put(normalize(nickname), profileName);
                if (previous != null) {
                    ChatHeadsVintage.LOGGER.warn("Name alias '{}' is configured more than once; the last value wins", nickname);
                }
            }
        }

        aliases = Collections.unmodifiableMap(parsed);
    }

    public static synchronized boolean addNameAlias(String nickname, String profileName) {
        String cleanNickname = nickname == null ? "" : nickname.trim();
        String cleanProfileName = profileName == null ? "" : profileName.trim();
        if (cleanNickname.isEmpty() || cleanProfileName.isEmpty()) {
            return false;
        }

        String normalizedNickname = normalize(cleanNickname);
        String existingTarget = aliases.get(normalizedNickname);
        if (existingTarget != null && existingTarget.equalsIgnoreCase(cleanProfileName)) {
            return false;
        }

        String[] configured = ChatHeadsConfig.detection.nameAliases;
        List<String> updated = new ArrayList<String>(
                configured == null ? 1 : configured.length + 1
        );

        if (configured != null) {
            for (String entry : configured) {
                int separator = entry == null ? -1 : entry.indexOf('=');
                String existingNickname = separator <= 0 ? "" : entry.substring(0, separator).trim();
                if (!normalize(existingNickname).equals(normalizedNickname)) {
                    updated.add(entry);
                }
            }
        }
        updated.add(cleanNickname + "=" + cleanProfileName);

        ChatHeadsConfig.detection.nameAliases = updated.toArray(new String[updated.size()]);
        reloadAliases();

        try {
            ConfigManager.sync(ChatHeadsVintage.MOD_ID, Config.Type.INSTANCE);
            ChatHeadsVintage.LOGGER.info(
                    "Learned chat name alias '{}' -> '{}' from a /realname response",
                    cleanNickname,
                    cleanProfileName
            );
        } catch (Throwable throwable) {
            ChatHeadsVintage.LOGGER.error(
                    "Learned chat name alias '{}' -> '{}' for this session, but could not save it",
                    cleanNickname,
                    cleanProfileName,
                    throwable
            );
        }

        return true;
    }

    public static String normalize(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (!ChatHeadsVintage.MOD_ID.equals(event.getModID())) {
            return;
        }

        ConfigManager.sync(ChatHeadsVintage.MOD_ID, Config.Type.INSTANCE);
        reloadAliases();

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.ingameGUI != null) {
            minecraft.ingameGUI.getChatGUI().refreshChat();
        }
    }
}
