/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.compat;

import net.minecraft.client.gui.GuiTextField;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CleanroomSuggestionsAccessTest {

    @Test
    void readsSuggestionsAndUpdatesTheCachedWidth() {
        FakeSuggestionList target = new FakeSuggestionList();
        CleanroomSuggestionsAccess.View view = CleanroomSuggestionsAccess.inspect(target);

        assertNotNull(view);
        assertEquals(Arrays.asList("Alice", "Bob"), view.getSuggestions());
        assertEquals(42, view.getCachedWidth());
        assertEquals(120, view.getMaximumWidth());

        assertTrue(view.setCachedWidth(54));
        assertEquals(54, target.cachedWidth);
    }

    private static final class FakeSuggestionList {
        private final GuiTextField field = new GuiTextField(0, null, 4, 8, 120, 12);
        private final List<String> suggestions = Arrays.asList("Alice", "Bob");
        private int cachedWidth = 42;
    }
}
