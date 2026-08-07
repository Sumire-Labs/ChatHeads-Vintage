/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.compat;

import dzwdz.chat_heads.ChatHeadsVintage;
import net.minecraft.client.gui.GuiTextField;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/** Reflection boundary that keeps Salutation an entirely optional dependency. */
public final class SalutationSuggestionsAccess {

    private static volatile boolean failed;
    private static Field offsetField;
    private static Field boxField;
    private static Field textFieldField;
    private static Field completionsField;
    private static Field completionIndexField;
    private static Method boxSetMethod;

    private SalutationSuggestionsAccess() {
    }

    public static View inspect(Object completer) {
        if (completer == null || failed) {
            return null;
        }

        try {
            initialize(completer.getClass());
            Object box = boxField.get(completer);
            Object textField = textFieldField.get(completer);
            Object completions = completionsField.get(completer);
            if (box == null || !(textField instanceof GuiTextField) || !(completions instanceof List)) {
                throw new IllegalStateException("Unexpected Salutation completion fields");
            }
            return new View(
                    ((Number)offsetField.get(completer)).intValue(),
                    ((Number)completionIndexField.get(completer)).intValue(),
                    box,
                    (GuiTextField)textField,
                    castCompletions((List<?>)completions)
            );
        } catch (Throwable throwable) {
            fail(throwable);
            return null;
        }
    }

    private static void initialize(Class<?> completerClass) throws ReflectiveOperationException {
        if (offsetField != null) {
            return;
        }

        synchronized (SalutationSuggestionsAccess.class) {
            if (offsetField != null) {
                return;
            }

            Field resolvedOffset = findField(completerClass, "offset");
            Field resolvedBox = findField(completerClass, "box");
            Field resolvedTextField = findField(completerClass, "textField", "field_186844_a");
            Field resolvedCompletions = findField(completerClass, "completions", "field_186849_f");
            Field resolvedCompletionIndex = findField(completerClass, "completionIdx", "field_186848_e");
            resolvedOffset.setAccessible(true);
            resolvedBox.setAccessible(true);
            resolvedTextField.setAccessible(true);
            resolvedCompletions.setAccessible(true);
            resolvedCompletionIndex.setAccessible(true);

            Method resolvedBoxSet = resolvedBox.getType().getMethod(
                    "set",
                    Integer.TYPE,
                    Integer.TYPE,
                    Integer.TYPE,
                    Integer.TYPE
            );
            resolvedBoxSet.setAccessible(true);

            boxField = resolvedBox;
            textFieldField = resolvedTextField;
            completionsField = resolvedCompletions;
            completionIndexField = resolvedCompletionIndex;
            boxSetMethod = resolvedBoxSet;
            offsetField = resolvedOffset;
        }
    }

    private static Field findField(Class<?> type, String... names) throws NoSuchFieldException {
        for (String name : names) {
            Class<?> current = type;
            while (current != null) {
                try {
                    return current.getDeclaredField(name);
                } catch (NoSuchFieldException ignored) {
                    current = current.getSuperclass();
                }
            }
        }
        throw new NoSuchFieldException(names[0]);
    }

    @SuppressWarnings("unchecked")
    private static List<String> castCompletions(List<?> completions) {
        return (List<String>)(List<?>)completions;
    }

    private static synchronized void fail(Throwable throwable) {
        if (failed) {
            return;
        }

        failed = true;
        ChatHeadsVintage.LOGGER.warn(
                "Disabling the Salutation suggestion adapter because its internal layout did not match the supported version",
                throwable
        );
    }

    public static final class View {
        private final int offset;
        private final int completionIndex;
        private final Object box;
        private final GuiTextField textField;
        private final List<String> completions;

        private View(
                int offset,
                int completionIndex,
                Object box,
                GuiTextField textField,
                List<String> completions
        ) {
            this.offset = offset;
            this.completionIndex = completionIndex;
            this.box = box;
            this.textField = textField;
            this.completions = completions;
        }

        public int getOffset() {
            return offset;
        }

        public int getCompletionIndex() {
            return completionIndex;
        }

        public GuiTextField getTextField() {
            return textField;
        }

        public List<String> getCompletions() {
            return completions;
        }

        public boolean setBox(int x, int y, int width, int height) {
            if (failed) {
                return false;
            }

            try {
                boxSetMethod.invoke(box, x, y, width, height);
                return true;
            } catch (Throwable throwable) {
                fail(throwable);
                return false;
            }
        }
    }
}
