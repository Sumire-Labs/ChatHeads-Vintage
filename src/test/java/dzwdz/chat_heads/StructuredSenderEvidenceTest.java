/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StructuredSenderEvidenceTest {

    @Test
    void findsTheVanillaReplyCommandOnANameComponent() {
        TextComponentString message = new TextComponentString("<");
        TextComponentString name = new TextComponentString("Player");
        name.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg Player "));
        message.appendSibling(name).appendText("> hello");

        assertEquals("Player", StructuredSenderEvidence.findPlayerName(message));
    }

    @Test
    void ignoresUnrelatedClickableComponents() {
        TextComponentString message = new TextComponentString("website");
        message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://example.invalid"));

        assertNull(StructuredSenderEvidence.findPlayerName(message));
    }
}
