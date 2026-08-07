/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.compat;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.TabCompleter;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalutationSuggestionsAccessTest {

    @Test
    void readsCustomAndInheritedCompletionFieldsAndUpdatesTheClickBox() {
        GuiTextField textField = new GuiTextField(0, null, 4, 8, 120, 12);
        FakeCompleter target = new FakeCompleter(textField);
        SalutationSuggestionsAccess.View view = SalutationSuggestionsAccess.inspect(target);

        assertNotNull(view);
        assertEquals(2, view.getOffset());
        assertEquals(3, view.getCompletionIndex());
        assertSame(textField, view.getTextField());
        assertEquals(Arrays.asList("Alice", "Bob"), view.getCompletions());

        assertTrue(view.setBox(3, 5, 52, 24));
        assertEquals(3, target.box.x);
        assertEquals(5, target.box.y);
        assertEquals(52, target.box.width);
        assertEquals(24, target.box.height);
    }

    private static final class FakeCompleter extends TabCompleter {
        private final FakeClickBox box = new FakeClickBox();
        private int offset = 2;

        private FakeCompleter(GuiTextField textField) {
            super(textField, false);
            completions.addAll(Arrays.asList("Alice", "Bob"));
            completionIdx = 3;
        }

        @Override
        public BlockPos getTargetBlockPos() {
            return null;
        }
    }

    private static final class FakeClickBox {
        private int x;
        private int y;
        private int width;
        private int height;

        public void set(int newX, int newY, int newWidth, int newHeight) {
            x = newX;
            y = newY;
            width = newWidth;
            height = newHeight;
        }
    }
}
