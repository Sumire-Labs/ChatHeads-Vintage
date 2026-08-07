/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

/**
 * Geometry shared by optional command-suggestion adapters.
 */
public final class SuggestionListLayout {

    public static final int SCREEN_MARGIN = 3;
    public static final int HEAD_COLUMN_WIDTH = ChatHeadManager.getHeadWidth(false) + 2;

    private final int left;
    private final int width;

    private SuggestionListLayout(int left, int width) {
        this.left = left;
        this.width = width;
    }

    public static SuggestionListLayout withHeadColumn(int originalLeft, int originalWidth) {
        int preferredLeft = originalLeft - HEAD_COLUMN_WIDTH;
        return new SuggestionListLayout(
                Math.max(SCREEN_MARGIN, preferredLeft),
                Math.max(0, originalWidth) + HEAD_COLUMN_WIDTH
        );
    }

    public int getLeft() {
        return left;
    }

    public int getWidth() {
        return width;
    }

    public int getTextLeft() {
        return left + HEAD_COLUMN_WIDTH;
    }
}
