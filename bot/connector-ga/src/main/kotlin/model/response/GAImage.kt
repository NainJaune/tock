/*
 * Copyright (C) 2017 VSCT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.tock.bot.connector.ga.model.response

import ai.tock.bot.engine.action.SendAttachment.AttachmentType.image
import ai.tock.bot.engine.message.Attachment
import ai.tock.shared.mapNotNullValues

/**
 *
 */
data class GAImage(
        val url: String,
        val accessibilityText: String,
        val height: Int? = null,
        val width: Int? = null
) {

    fun toAttachment(): Attachment
            = Attachment(url, image)

    fun toMetadata(): Map<String, String>
            = mapNotNullValues(
            GAImage::accessibilityText.name to accessibilityText,
            GAImage::height.name to height?.toString(),
            GAImage::width.name to width?.toString()
    )
}