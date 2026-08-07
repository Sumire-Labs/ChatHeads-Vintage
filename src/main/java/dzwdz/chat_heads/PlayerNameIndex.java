/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import dzwdz.chat_heads.config.ChatHeadsConfigState;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.ITextComponent;

import java.util.LinkedHashMap;
import java.util.Map;

final class PlayerNameIndex {

    private final NameMatcher<NetworkPlayerInfo> matcher = new NameMatcher<NetworkPlayerInfo>();

    PlayerNameIndex(NetHandlerPlayClient connection) {
        Map<String, NetworkPlayerInfo> profilesByName = new LinkedHashMap<String, NetworkPlayerInfo>();

        for (NetworkPlayerInfo playerInfo : connection.getPlayerInfoMap()) {
            String profileName = cleanName(playerInfo.getGameProfile().getName());
            if (profileName.isEmpty()) {
                continue;
            }

            profilesByName.put(NameMatcher.normalize(profileName), playerInfo);
            matcher.putIfAbsent(profileName, playerInfo);
        }

        for (NetworkPlayerInfo playerInfo : connection.getPlayerInfoMap()) {
            ITextComponent displayName = playerInfo.getDisplayName();
            if (displayName != null) {
                matcher.putIfAbsent(cleanName(displayName.getUnformattedText()), playerInfo);
            }
        }

        for (Map.Entry<String, String> alias : ChatHeadsConfigState.getAliases().entrySet()) {
            NetworkPlayerInfo target = profilesByName.get(NameMatcher.normalize(cleanName(alias.getValue())));
            if (target != null) {
                matcher.putIfAbsent(alias.getKey(), target);
            }
        }
    }

    NetworkPlayerInfo getExact(String name) {
        return matcher.getExact(cleanName(name));
    }

    NameMatcher.Match<NetworkPlayerInfo> findFirst(String message) {
        return matcher.findFirst(cleanMessage(message));
    }

    NameMatcher.Match<NetworkPlayerInfo> findFirst(String message, NetworkPlayerInfo playerInfo) {
        return matcher.findFirst(cleanMessage(message), playerInfo);
    }

    static String cleanName(String value) {
        return cleanMessage(value).trim();
    }

    static String cleanMessage(String value) {
        if (value == null) {
            return "";
        }
        return FormattedTextLayout.stripFormatting(value);
    }
}
