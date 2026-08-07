/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import net.minecraft.util.text.ITextComponent;

/**
 * Scoped metadata transfer from GuiNewChat#setChatLine to ChatLine constructors.
 */
public final class LineBuildContext {

    private static final ThreadLocal<State> CURRENT = new ThreadLocal<State>();

    private LineBuildContext() {
    }

    public static void begin(HeadData baseData, ITextComponent originalComponent) {
        CURRENT.set(new State(baseData == null ? HeadData.EMPTY : baseData, originalComponent));
    }

    public static void end() {
        CURRENT.remove();
    }

    public static HeadData dataFor(ITextComponent component) {
        State state = CURRENT.get();
        if (state == null) {
            return HeadData.EMPTY;
        }

        if (component == state.originalComponent) {
            return state.baseData.asFirstVisualLine();
        }

        boolean firstLine = !state.firstDrawnLineConsumed;
        state.firstDrawnLineConsumed = true;

        String plainLine = FormattedTextLayout.stripFormatting(component.getUnformattedText());
        int localHeadIndex = state.findHeadIndex(plainLine, firstLine);
        state.sourceCursor += plainLine.length();

        return state.baseData.asWrappedLine(firstLine, localHeadIndex);
    }

    private static int findMatchedName(String line, String matchedName) {
        if (matchedName == null || matchedName.isEmpty()) {
            return -1;
        }
        return NameMatcher.indexOfCandidate(line, matchedName);
    }

    private static final class State {
        private final HeadData baseData;
        private final ITextComponent originalComponent;
        private boolean firstDrawnLineConsumed;
        private boolean headAssigned;
        private int sourceCursor;

        private State(HeadData baseData, ITextComponent originalComponent) {
            this.baseData = baseData;
            this.originalComponent = originalComponent;
        }

        private int findHeadIndex(String plainLine, boolean firstLine) {
            if (headAssigned || baseData.getPlayerInfo() == null) {
                return -1;
            }

            int directIndex = findMatchedName(plainLine, baseData.getMatchedName());
            if (directIndex >= 0) {
                headAssigned = true;
                return directIndex;
            }

            int messageIndex = baseData.getMessageNameIndex();
            if (messageIndex >= sourceCursor && messageIndex < sourceCursor + plainLine.length()) {
                headAssigned = true;
                return messageIndex - sourceCursor;
            }

            if (messageIndex < 0 && firstLine) {
                headAssigned = true;
                return 0;
            }

            return -1;
        }
    }
}
