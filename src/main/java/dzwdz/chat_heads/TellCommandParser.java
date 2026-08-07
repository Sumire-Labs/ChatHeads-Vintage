/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Extracts the recipient from common reply/suggest commands attached to player names.
 */
public final class TellCommandParser {

    private static final Set<String> SUPPORTED_COMMANDS = new HashSet<String>(Arrays.asList(
            "msg", "tell", "w", "whisper", "pm", "message"
    ));

    private TellCommandParser() {
    }

    public static String extractTarget(String command) {
        if (command == null) {
            return null;
        }

        String trimmed = command.trim();
        if (!trimmed.startsWith("/")) {
            return null;
        }

        String[] parts = trimmed.substring(1).split("\\s+");
        if (parts.length < 2) {
            return null;
        }

        String commandName = parts[0].toLowerCase(Locale.ROOT);
        int namespaceSeparator = commandName.lastIndexOf(':');
        if (namespaceSeparator >= 0) {
            commandName = commandName.substring(namespaceSeparator + 1);
        }

        if (!SUPPORTED_COMMANDS.contains(commandName)) {
            return null;
        }

        return parts[1];
    }
}
