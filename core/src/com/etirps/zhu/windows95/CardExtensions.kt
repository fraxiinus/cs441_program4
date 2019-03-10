package com.etirps.zhu.windows95

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion

class CardExtensions(cardFaces: Texture, var cardBack: Texture) {

    var sprites = TextureRegion(cardFaces).split(409, 585).flatten()

    fun getFront(card: Card): TextureRegion {

        when(card.suit) {
            Suit.CLOVERS -> {
                return sprites[card.value]
            }
            Suit.DIAMONDS -> {
                return sprites[card.value + 13]
            }

            Suit.HEARTS -> {
                return sprites[card.value + 26]
            }

            Suit.SPADES -> {
                return sprites[card.value + 39]
            }
        }


    }
}