/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NameMatcherTest {

    @Test
    void exactLookupIsCaseInsensitive() {
        NameMatcher<String> matcher = new NameMatcher<String>();
        matcher.putIfAbsent("PlayerOne", "profile");

        assertEquals("profile", matcher.getExact("playerone"));
        assertEquals("profile", matcher.getExact("PLAYERONE"));
    }

    @Test
    void doesNotMatchPlayerNamesInsideLongerWords() {
        NameMatcher<String> matcher = new NameMatcher<String>();
        matcher.putIfAbsent("tom", "profile");

        assertNull(matcher.findFirst("a custom message"));
        assertNull(matcher.findFirst("tomato joined"));
        assertEquals("profile", matcher.findFirst("<tom> hello").getValue());
    }

    @Test
    void selectsTheEarliestThenLongestCandidate() {
        NameMatcher<String> matcher = new NameMatcher<String>();
        matcher.putIfAbsent("Tom", "short");
        matcher.putIfAbsent("Tommy", "long");
        matcher.putIfAbsent("Alice", "later");

        NameMatcher.Match<String> match = matcher.findFirst("Tommy greeted Alice");

        assertEquals("long", match.getValue());
        assertEquals(0, match.getIndex());
    }

    @Test
    void supportsUnicodeAliasesAndBoundaries() {
        NameMatcher<String> matcher = new NameMatcher<String>();
        matcher.putIfAbsent("すみれ", "profile");

        assertEquals("profile", matcher.findFirst("<すみれ> こんにちは").getValue());
        assertNull(matcher.findFirst("すみれさん"));
    }

    @Test
    void canRestrictANameSearchToTheResolvedPlayer() {
        NameMatcher<String> matcher = new NameMatcher<String>();
        matcher.putIfAbsent("Alice", "alice-profile");
        matcher.putIfAbsent("Bob", "bob-profile");
        matcher.putIfAbsent("BuilderBob", "bob-profile");

        NameMatcher.Match<String> match = matcher.findFirst("Alice greeted BuilderBob", "bob-profile");

        assertEquals("BuilderBob", match.getName());
        assertEquals(14, match.getIndex());
        assertEquals("bob-profile", match.getValue());
    }
}
