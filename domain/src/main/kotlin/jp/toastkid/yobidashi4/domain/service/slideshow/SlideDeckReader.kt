/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.slideshow

import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck
import java.nio.file.Path

interface SlideDeckReader {

    /**
     * Convert to Slides.
     * @return List&lt;Slide&gt;
     */
    operator fun invoke(pathToMarkdown: Path): SlideDeck

}