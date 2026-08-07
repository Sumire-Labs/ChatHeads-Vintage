/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A deterministic, case-insensitive matcher with player-name word boundaries.
 */
public final class NameMatcher<T> {

    private final Map<String, Candidate<T>> candidatesByNormalizedName = new LinkedHashMap<String, Candidate<T>>();

    public void putIfAbsent(String name, T value) {
        if (name == null || value == null) {
            return;
        }

        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            return;
        }

        String normalized = normalize(trimmed);
        if (!candidatesByNormalizedName.containsKey(normalized)) {
            candidatesByNormalizedName.put(normalized, new Candidate<T>(trimmed, value));
        }
    }

    public T getExact(String name) {
        if (name == null) {
            return null;
        }

        Candidate<T> candidate = candidatesByNormalizedName.get(normalize(name.trim()));
        return candidate == null ? null : candidate.value;
    }

    public Match<T> findFirst(String message) {
        return findFirst(message, null, false);
    }

    public Match<T> findFirst(String message, T requiredValue) {
        if (requiredValue == null) {
            return null;
        }
        return findFirst(message, requiredValue, true);
    }

    private Match<T> findFirst(String message, T requiredValue, boolean restrictValue) {
        if (message == null || message.isEmpty()) {
            return null;
        }

        Match<T> best = null;
        for (Candidate<T> candidate : candidatesByNormalizedName.values()) {
            if (restrictValue && !Objects.equals(requiredValue, candidate.value)) {
                continue;
            }

            int index = indexOfCandidate(message, candidate.name);
            if (index < 0) {
                continue;
            }

            if (best == null || index < best.index || index == best.index && candidate.name.length() > best.name.length()) {
                best = new Match<T>(candidate.name, candidate.value, index);
            }
        }
        return best;
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<String>();
        for (Candidate<T> candidate : candidatesByNormalizedName.values()) {
            names.add(candidate.name);
        }
        return names;
    }

    static int indexOfCandidate(String message, String candidate) {
        int maximumStart = message.length() - candidate.length();
        for (int index = 0; index <= maximumStart; index++) {
            if (!message.regionMatches(true, index, candidate, 0, candidate.length())) {
                continue;
            }

            boolean startsWithWord = isWordCharacter(candidate.codePointAt(0));
            boolean precededByWord = index > 0 && isWordCharacter(message.codePointBefore(index));
            if (startsWithWord && precededByWord) {
                continue;
            }

            int candidateEnd = index + candidate.length();
            boolean endsWithWord = isWordCharacter(candidate.codePointBefore(candidate.length()));
            boolean followedByWord = candidateEnd < message.length() && isWordCharacter(message.codePointAt(candidateEnd));
            if (endsWithWord && followedByWord) {
                continue;
            }

            return index;
        }
        return -1;
    }

    static boolean isWordCharacter(int codePoint) {
        return Character.isLetterOrDigit(codePoint)
                || codePoint == '_'
                || Character.getNumericValue(codePoint) != -1;
    }

    static String normalize(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    private static final class Candidate<T> {
        private final String name;
        private final T value;

        private Candidate(String name, T value) {
            this.name = name;
            this.value = value;
        }
    }

    public static final class Match<T> {
        private final String name;
        private final T value;
        private final int index;

        private Match(String name, T value, int index) {
            this.name = name;
            this.value = value;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public T getValue() {
            return value;
        }

        public int getIndex() {
            return index;
        }
    }
}
