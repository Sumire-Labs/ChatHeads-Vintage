/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.compat;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrigoSuggestionsAccessTest {

    @Test
    void readsSuggestionsAndMutatesTheExistingBoundsObject() {
        FakeSuggestionsList target = new FakeSuggestionsList();
        BrigoSuggestionsAccess.View view = BrigoSuggestionsAccess.inspect(target);

        assertNotNull(view);
        assertEquals(7, view.getX());
        assertEquals(40, view.getWidth());
        assertEquals("Player", BrigoSuggestionsAccess.getSuggestionText(view.getSuggestions().get(0)));

        assertTrue(view.setBounds(3, 52));
        assertEquals(3, target.bounds.x());
        assertEquals(52, target.bounds.width());
    }

    private static final class FakeSuggestionsList {
        private final FakeBounds bounds = new FakeBounds(7, 40);
        private final List<FakeSuggestion> suggestions = Arrays.asList(new FakeSuggestion("Player"));
    }

    private static final class FakeSuggestion {
        private final String text;

        private FakeSuggestion(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private static final class FakeBounds {
        private int x;
        private int width;

        private FakeBounds(int x, int width) {
            this.x = x;
            this.width = width;
        }

        public int x() {
            return x;
        }

        public int width() {
            return width;
        }

        public FakeBounds x(int newX) {
            x = newX;
            return this;
        }

        public FakeBounds width(int newWidth) {
            width = newWidth;
            return this;
        }
    }
}
