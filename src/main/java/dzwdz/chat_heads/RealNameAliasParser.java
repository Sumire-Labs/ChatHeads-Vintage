/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import javax.annotation.Nullable;

/**
 * Parses the standard EssentialsX /realname response: "nickname is profileName".
 */
public final class RealNameAliasParser {

    private static final String SEPARATOR = " is ";

    private RealNameAliasParser() {
    }

    @Nullable
    public static Alias parse(String message) {
        if (message == null) {
            return null;
        }

        int separatorIndex = message.indexOf(SEPARATOR);
        if (separatorIndex <= 0) {
            return null;
        }

        String nickname = message.substring(0, separatorIndex);
        String profileName = message.substring(separatorIndex + SEPARATOR.length());
        if (profileName.isEmpty() || nickname.indexOf(' ') >= 0 || profileName.indexOf(' ') >= 0) {
            return null;
        }

        return new Alias(nickname, profileName);
    }

    public static final class Alias {
        private final String nickname;
        private final String profileName;

        private Alias(String nickname, String profileName) {
            this.nickname = nickname;
            this.profileName = profileName;
        }

        public String getNickname() {
            return nickname;
        }

        public String getProfileName() {
            return profileName;
        }
    }
}
