package com.etirps.zhu.windows95

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable

class CardExtensions(var cardFaces: Texture, var cardBack: Texture): Disposable {
    var sprites = TextureRegion(cardFaces).split(409, 585).flatten()

    fun getFront(suit: Suit, value: Int): TextureRegion {
        when (suit) {
            Suit.CLOVERS -> {
                return sprites[value]
            }
            Suit.DIAMONDS -> {
                return sprites[value + 13]
            }

            Suit.HEARTS -> {
                return sprites[value + 26]
            }

            Suit.SPADES -> {
                return sprites[value + 39]
            }
        }
    }

    override fun dispose() {
        cardFaces.dispose()
        cardBack.dispose()
    }
}