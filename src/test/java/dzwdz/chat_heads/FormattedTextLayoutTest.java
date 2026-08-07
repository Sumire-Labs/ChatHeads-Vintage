/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormattedTextLayoutTest {

    @Test
    void splitsImmediatelyBeforeTheVisibleNameAndKeepsItsFormatting() {
        String formatted = "\u00a77[12:00] \u00a7r\u00a7aPlayer\u00a7r: hello";
        String plain = FormattedTextLayout.stripFormatting(formatted);

        FormattedTextLayout.Split split = FormattedTextLayout.split(formatted, plain.indexOf("Player"));

        assertTrue(split.isValid());
        assertEquals("[12:00] ", FormattedTextLayout.stripFormatting(split.getPrefix()));
        assertTrue(split.getSuffix().startsWith("\u00a7r\u00a7aPlayer"));
    }

    @Test
    void usesUtf16PositionsConsistentlyWhenThePrefixContainsAFormattingCodeAndEmoji() {
        String formatted = "\u00a7b\ud83d\udcac \u00a7fPlayer";
        String plain = FormattedTextLayout.stripFormatting(formatted);

        FormattedTextLayout.Split split = FormattedTextLayout.split(formatted, plain.indexOf("Player"));

        assertTrue(split.isValid());
        assertEquals("\ud83d\udcac ", FormattedTextLayout.stripFormatting(split.getPrefix()));
        assertEquals("Player", FormattedTextLayout.stripFormatting(split.getSuffix()));
        assertEquals(plain.length(), FormattedTextLayout.plainLength(formatted));
    }

    @Test
    void rejectsAnIndexPastTheEndOfTheVisibleString() {
        FormattedTextLayout.Split split = FormattedTextLayout.split("\u00a7aPlayer", 7);

        assertFalse(split.isValid());
    }
}
