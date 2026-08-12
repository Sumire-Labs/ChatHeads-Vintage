/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.compat;

import dzwdz.chat_heads.ChatHeadsVintage;
import net.minecraft.client.gui.GuiTextField;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Reflection boundary that keeps Cleanroom's command suggestions an optional integration.
 */
public final class CleanroomSuggestionsAccess {

    private static volatile boolean failed;
    private static Field fieldField;
    private static Field suggestionsField;
    private static Field cachedWidthField;

    private CleanroomSuggestionsAccess() {
    }

    public static View inspect(Object suggestionList) {
        if (suggestionList == null || failed) {
            return null;
        }

        try {
            initialize(suggestionList.getClass());

            Object fieldValue = fieldField.get(suggestionList);
            Object suggestionsValue = suggestionsField.get(suggestionList);
            Object cachedWidthValue = cachedWidthField.get(suggestionList);
            if (!(fieldValue instanceof GuiTextField)
                    || !(suggestionsValue instanceof List)
                    || !(cachedWidthValue instanceof Number)) {
                throw new IllegalStateException("Unexpected Cleanroom suggestion-list fields");
            }

            List<?> rawSuggestions = (List<?>) suggestionsValue;
            for (Object suggestion : rawSuggestions) {
                if (!(suggestion instanceof String)) {
                    throw new IllegalStateException("Unexpected Cleanroom suggestion entry");
                }
            }

            return new View(
                    suggestionList,
                    castSuggestions(rawSuggestions),
                    ((Number) cachedWidthValue).intValue(),
                    ((GuiTextField) fieldValue).width
            );
        } catch (Throwable throwable) {
            fail(throwable);
            return null;
        }
    }

    public static boolean isUsable() {
        return !failed;
    }

    private static void initialize(Class<?> suggestionListClass) throws ReflectiveOperationException {
        if (suggestionsField != null) {
            return;
        }

        synchronized (CleanroomSuggestionsAccess.class) {
            if (suggestionsField != null) {
                return;
            }

            Field resolvedField = findField(suggestionListClass, "field");
            Field resolvedSuggestions = findField(suggestionListClass, "suggestions");
            Field resolvedCachedWidth = findField(suggestionListClass, "cachedWidth");
            resolvedField.setAccessible(true);
            resolvedSuggestions.setAccessible(true);
            resolvedCachedWidth.setAccessible(true);

            fieldField = resolvedField;
            cachedWidthField = resolvedCachedWidth;
            suggestionsField = resolvedSuggestions;
        }
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    @SuppressWarnings("unchecked")
    private static List<String> castSuggestions(List<?> suggestions) {
        return (List<String>) (List<?>) suggestions;
    }

    private static synchronized void fail(Throwable throwable) {
        if (failed) {
            return;
        }

        failed = true;
        ChatHeadsVintage.LOGGER.warn(
                "Disabling the Cleanroom suggestion adapter because its internal layout did not match the supported version",
                throwable
        );
    }

    public static final class View {
        private final Object suggestionList;
        private final List<String> suggestions;
        private final int cachedWidth;
        private final int maximumWidth;

        private View(Object suggestionList, List<String> suggestions, int cachedWidth, int maximumWidth) {
            this.suggestionList = suggestionList;
            this.suggestions = suggestions;
            this.cachedWidth = cachedWidth;
            this.maximumWidth = maximumWidth;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }

        public int getCachedWidth() {
            return cachedWidth;
        }

        public int getMaximumWidth() {
            return maximumWidth;
        }

        public boolean setCachedWidth(int newWidth) {
            if (failed) {
                return false;
            }

            try {
                cachedWidthField.setInt(suggestionList, Math.max(0, newWidth));
                return true;
            } catch (Throwable throwable) {
                fail(throwable);
                return false;
            }
        }
    }
}
