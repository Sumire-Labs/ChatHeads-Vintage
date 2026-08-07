/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

/**
 * Utilities for mapping visible UTF-16 positions to legacy formatted chat strings.
 */
public final class FormattedTextLayout {

    private static final char FORMAT_MARKER = '\u00a7';

    private FormattedTextLayout() {
    }

    public static String stripFormatting(String text) {
        if (text == null || text.isEmpty()) {
            return text == null ? "" : text;
        }

        StringBuilder plain = new StringBuilder(text.length());
        for (int index = 0; index < text.length(); ++index) {
            char character = text.charAt(index);
            if (character == FORMAT_MARKER && index + 1 < text.length()) {
                ++index;
                continue;
            }
            plain.append(character);
        }
        return plain.toString();
    }

    public static int plainLength(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int length = 0;
        for (int index = 0; index < text.length(); ++index) {
            if (text.charAt(index) == FORMAT_MARKER && index + 1 < text.length()) {
                ++index;
            } else {
                ++length;
            }
        }
        return length;
    }

    public static Split split(String formattedText, int plainIndex) {
        String text = formattedText == null ? "" : formattedText;
        if (plainIndex < 0) {
            return Split.invalid(text);
        }

        int visibleIndex = 0;
        int formattedIndex = 0;
        while (formattedIndex < text.length() && visibleIndex < plainIndex) {
            if (text.charAt(formattedIndex) == FORMAT_MARKER && formattedIndex + 1 < text.length()) {
                formattedIndex += 2;
            } else {
                ++formattedIndex;
                ++visibleIndex;
            }
        }

        if (visibleIndex != plainIndex) {
            return Split.invalid(text);
        }

        return new Split(
                text.substring(0, formattedIndex),
                text.substring(formattedIndex),
                true
        );
    }

    public static final class Split {
        private final String prefix;
        private final String suffix;
        private final boolean valid;

        private Split(String prefix, String suffix, boolean valid) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.valid = valid;
        }

        private static Split invalid(String text) {
            return new Split(text, "", false);
        }

        public String getPrefix() {
            return prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public boolean isValid() {
            return valid;
        }
    }
}
