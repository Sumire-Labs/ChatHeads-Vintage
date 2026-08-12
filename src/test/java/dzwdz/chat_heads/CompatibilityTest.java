/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompatibilityTest {

    @Test
    void checksClassResourcesWithoutLoadingTheOptionalTarget() {
        assertTrue(Compatibility.isClassPresent("dzwdz.chat_heads.Compatibility"));
        assertFalse(Compatibility.isClassPresent("missing.optional.CommandSuggestionList"));
    }
}
