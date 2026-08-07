/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.config;

public enum SenderDetectionMode {
    /** Only trust structured click commands attached to a player name. */
    STRICT,
    /** Use structured evidence first, then scan the rendered message for known names and aliases. */
    HEURISTIC
}
