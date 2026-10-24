/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.slideshow

data class SlideDeck(
    val slides: MutableList<Slide> = mutableListOf(),
    var background: String = "",
    var title: String = "",
    var footerText: String = "",
) {
    fun add(slide: Slide) {
        slides.add(slide)
    }

    fun extractImageUrls(): Set<String> {
        val imageUrls = mutableSetOf<String>()
        if (background.isNotBlank()) {
            imageUrls.add(background)
        }
        slides.map(Slide::extractImageUrls).flatten().forEach(imageUrls::add)
        return imageUrls
    }

    fun shouldSetBackground(): Boolean =
        slides.isEmpty() && background.isBlank()

}