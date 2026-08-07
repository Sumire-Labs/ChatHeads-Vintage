/*
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.mixininterface;

import dzwdz.chat_heads.HeadData;

public interface ChatLineExtension {

    HeadData chatheads$getHeadData();

    void chatheads$setHeadData(HeadData data);
}
