/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import dzwdz.chat_heads.config.ChatHeadsConfig;
import dzwdz.chat_heads.config.ChatHeadsConfigState;
import net.minecraft.util.text.ITextComponent;

public final class AutoAliasDetector {

    private AutoAliasDetector() {
    }

    public static void observe(ITextComponent message) {
        if (!ChatHeadsConfig.detection.detectNameAliases || message == null) {
            return;
        }

        RealNameAliasParser.Alias alias = RealNameAliasParser.parse(
                FormattedTextLayout.stripFormatting(message.getUnformattedText())
        );
        if (alias != null) {
            ChatHeadsConfigState.addNameAlias(alias.getNickname(), alias.getProfileName());
        }
    }
}
