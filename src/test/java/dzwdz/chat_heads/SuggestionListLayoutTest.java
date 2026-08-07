/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SuggestionListLayoutTest {

    @Test
    void addsAHeadColumnWithoutMovingTextWhenThereIsRoomOnTheLeft() {
        SuggestionListLayout layout = SuggestionListLayout.withHeadColumn(30, 80);

        assertEquals(18, layout.getLeft());
        assertEquals(92, layout.getWidth());
        assertEquals(30, layout.getTextLeft());
        assertEquals(110, layout.getLeft() + layout.getWidth());
    }

    @Test
    void movesTheWholeListRightWhenTheHeadWouldLeaveTheScreen() {
        SuggestionListLayout layout = SuggestionListLayout.withHeadColumn(4, 80);

        assertEquals(SuggestionListLayout.SCREEN_MARGIN, layout.getLeft());
        assertEquals(15, layout.getTextLeft());
        assertEquals(92, layout.getWidth());
    }
}
