/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import dzwdz.chat_heads.config.ChatHeadsConfigState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Exact profile-name lookup used only by command-suggestion adapters. */
public final class SuggestionHeadLookup {

    private static final SuggestionHeadLookup EMPTY = new SuggestionHeadLookup(
            Collections.<String, NetworkPlayerInfo>emptyMap()
    );

    private final Map<String, NetworkPlayerInfo> profilesByName;

    private SuggestionHeadLookup(Map<String, NetworkPlayerInfo> profilesByName) {
        this.profilesByName = profilesByName;
    }

    public static SuggestionHeadLookup current() {
        if (!ChatHeadsConfigState.isActive()) {
            return EMPTY;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        NetHandlerPlayClient connection = minecraft.getConnection();
        return connection == null ? EMPTY : from(connection.getPlayerInfoMap());
    }

    static SuggestionHeadLookup from(Collection<NetworkPlayerInfo> playerInfos) {
        if (playerInfos == null || playerInfos.isEmpty()) {
            return EMPTY;
        }

        Map<String, NetworkPlayerInfo> profiles = new LinkedHashMap<String, NetworkPlayerInfo>();
        for (NetworkPlayerInfo playerInfo : playerInfos) {
            if (playerInfo == null || playerInfo.getGameProfile() == null) {
                continue;
            }

            String profileName = playerInfo.getGameProfile().getName();
            if (profileName != null && !profileName.isEmpty() && !profiles.containsKey(profileName)) {
                profiles.put(profileName, playerInfo);
            }
        }

        return profiles.isEmpty()
                ? EMPTY
                : new SuggestionHeadLookup(Collections.unmodifiableMap(profiles));
    }

    public NetworkPlayerInfo find(String wholeSuggestion) {
        return wholeSuggestion == null ? null : profilesByName.get(wholeSuggestion);
    }

    public boolean containsAny(Collection<String> suggestions) {
        if (suggestions == null || suggestions.isEmpty() || profilesByName.isEmpty()) {
            return false;
        }

        for (String suggestion : suggestions) {
            if (find(suggestion) != null) {
                return true;
            }
        }
        return false;
    }
}
