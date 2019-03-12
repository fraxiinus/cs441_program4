package com.etirps.zhu.windows95

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor

enum class Suit { CLOVERS, DIAMONDS, HEARTS, SPADES }

class Card (var suit: Suit,         var value: Int,
            posX: Float,            posY: Float,
            width: Float = 69f,     height: Float = 94f,
            var textureRegion: TextureRegion,
            var debugFont: BitmapFont? = null): Actor() {

    var touched: Boolean = false
    private var lastX: Float
    private var lastY: Float

    private var speedX: Float = 0f
    private var speedY: Float = 0f

    private var energyLoss: Float = 1.5f

    var bounds: Rectangle
    var polygon: Polygon

    init {
        x = posX
        y = posY

        lastX = x
        lastY = y

        setWidth(width)
        setHeight(height)

        bounds = Rectangle(x, y, width, height)
        polygon = Polygon(floatArrayOf( 0f,             0f,
                bounds.width,   0f,
                bounds.width,   bounds.height,
                0f,             bounds.height))
        polygon.setOrigin(bounds.width / 2, bounds.height / 2)
        polygon.setPosition(x, y)
    }

    fun move(destX: Float, destY: Float) {
        x = destX - width / 2
        y = destY - height / 2

        bounds.x = x
        bounds.y = y
        polygon.setPosition(x, y)
    }

    private fun changeSuit() {

    }

    override fun act(delta: Float) {
        if(touched) {
            speedX = x - lastX
            speedY = y - lastY

            lastX = x
            lastY = y

            bounds.x = x
            bounds.y = y
            polygon.setPosition(x, y)
        } else {
            lastX = x
            lastY = y

            x += speedX
            y += speedY

            bounds.x = x
            bounds.y = y
            polygon.setPosition(x, y)
        }

        if(x + width > Gdx.graphics.width) {
            x = Gdx.graphics.width - width
            speedX = -(speedX / energyLoss)
        } else if(x < 0) {
            x = 0f
            speedX = -(speedX / energyLoss)
        }

        if(y + height > Gdx.graphics.height) {
            y = Gdx.graphics.height - height
            speedY = -(speedY / energyLoss)
        } else if (y < 0) {
            y = 0f
            speedY = -(speedY / energyLoss)
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(textureRegion, x, y, width / 2, height / 2, width, height, 1f, 1f, 0f)
        debugFont?.draw(batch, "pos:$x x $y\nspeed:$speedX x $speedY\ntouched:$touched", x, y)
    }
}