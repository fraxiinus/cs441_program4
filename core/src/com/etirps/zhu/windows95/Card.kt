package com.etirps.zhu.windows95

import com.badlogic.gdx.scenes.scene2d.Actor

enum class Suit { CLOVERS, DIAMONDS, HEARTS, SPADES }

class Card (var suit: Suit,         var value: Int,
            posX: Float,            posY: Float,
            width: Float = 69f,     height: Float = 94f): Actor() {

    init {
        x = posX
        y = posY

        setWidth(width)
        setHeight(height)
    }
}