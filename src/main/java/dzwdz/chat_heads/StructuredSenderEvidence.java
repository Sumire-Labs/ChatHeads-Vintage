/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;

import javax.annotation.Nullable;

/** Reads the reply command attached to vanilla and server-provided player name components. */
public final class StructuredSenderEvidence {

    private StructuredSenderEvidence() {
    }

    @Nullable
    public static String findPlayerName(ITextComponent message) {
        for (ITextComponent component : message) {
            ClickEvent clickEvent = component.getStyle().getClickEvent();
            if (clickEvent == null
                    || clickEvent.getAction() != ClickEvent.Action.SUGGEST_COMMAND
                    && clickEvent.getAction() != ClickEvent.Action.RUN_COMMAND) {
                continue;
            }

            String target = TellCommandParser.extractTarget(clickEvent.getValue());
            if (target != null) {
                return target;
            }
        }
        return null;
    }
}
