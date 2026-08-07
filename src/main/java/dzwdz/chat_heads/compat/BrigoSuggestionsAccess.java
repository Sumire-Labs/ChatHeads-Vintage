/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.compat;

import dzwdz.chat_heads.ChatHeadsVintage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Reflection boundary that keeps Brigo an entirely optional dependency.
 */
public final class BrigoSuggestionsAccess {

    private static volatile boolean failed;
    private static Field suggestionsField;
    private static Field boundsField;
    private static Method boundsXGetter;
    private static Method boundsWidthGetter;
    private static Method boundsXSetter;
    private static Method boundsWidthSetter;
    private static Method suggestionTextGetter;

    private BrigoSuggestionsAccess() {
    }

    public static View inspect(Object suggestionList) {
        if (suggestionList == null || failed) {
            return null;
        }

        try {
            initialize(suggestionList.getClass());

            Object suggestionsValue = suggestionsField.get(suggestionList);
            Object bounds = boundsField.get(suggestionList);
            if (!(suggestionsValue instanceof List) || bounds == null) {
                throw new IllegalStateException("Unexpected Brigo suggestion-list fields");
            }

            int x = ((Number) boundsXGetter.invoke(bounds)).intValue();
            int width = ((Number) boundsWidthGetter.invoke(bounds)).intValue();
            return new View(bounds, (List<?>) suggestionsValue, x, width);
        } catch (Throwable throwable) {
            fail(throwable);
            return null;
        }
    }

    public static String getSuggestionText(Object suggestion) {
        if (suggestion == null || failed) {
            return null;
        }

        try {
            Method getter = suggestionTextGetter;
            if (getter == null) {
                synchronized (BrigoSuggestionsAccess.class) {
                    getter = suggestionTextGetter;
                    if (getter == null) {
                        getter = suggestion.getClass().getMethod("getText");
                        getter.setAccessible(true);
                        suggestionTextGetter = getter;
                    }
                }
            }

            Object value = getter.invoke(suggestion);
            return value instanceof String ? (String) value : null;
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

        synchronized (BrigoSuggestionsAccess.class) {
            if (suggestionsField != null) {
                return;
            }

            Field resolvedSuggestions = findField(suggestionListClass, "suggestions");
            Field resolvedBounds = findField(suggestionListClass, "bounds");
            resolvedSuggestions.setAccessible(true);
            resolvedBounds.setAccessible(true);

            Class<?> boundsClass = resolvedBounds.getType();
            Method resolvedXGetter = boundsClass.getMethod("x");
            Method resolvedWidthGetter = boundsClass.getMethod("width");
            Method resolvedXSetter = boundsClass.getMethod("x", Integer.TYPE);
            Method resolvedWidthSetter = boundsClass.getMethod("width", Integer.TYPE);
            resolvedXGetter.setAccessible(true);
            resolvedWidthGetter.setAccessible(true);
            resolvedXSetter.setAccessible(true);
            resolvedWidthSetter.setAccessible(true);

            boundsField = resolvedBounds;
            boundsXGetter = resolvedXGetter;
            boundsWidthGetter = resolvedWidthGetter;
            boundsXSetter = resolvedXSetter;
            boundsWidthSetter = resolvedWidthSetter;
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

    private static synchronized void fail(Throwable throwable) {
        if (failed) {
            return;
        }

        failed = true;
        ChatHeadsVintage.LOGGER.warn(
                "Disabling the Brigo suggestion adapter because its internal layout did not match the supported version",
                throwable
        );
    }

    public static final class View {
        private final Object bounds;
        private final List<?> suggestions;
        private final int x;
        private final int width;

        private View(Object bounds, List<?> suggestions, int x, int width) {
            this.bounds = bounds;
            this.suggestions = suggestions;
            this.x = x;
            this.width = width;
        }

        public List<?> getSuggestions() {
            return suggestions;
        }

        public int getX() {
            return x;
        }

        public int getWidth() {
            return width;
        }

        public boolean setBounds(int newX, int newWidth) {
            if (failed) {
                return false;
            }

            try {
                boundsXSetter.invoke(bounds, newX);
                boundsWidthSetter.invoke(bounds, newWidth);
                return true;
            } catch (Throwable throwable) {
                fail(throwable);
                return false;
            }
        }
    }
}
