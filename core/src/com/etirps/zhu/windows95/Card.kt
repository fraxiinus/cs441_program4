package com.etirps.zhu.windows95

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

enum class Suit { CLOVERS, DIAMONDS, HEARTS, SPADES }

class Card (var suit: Suit,         var value: Int,
            posX: Float,            posY: Float,
            width: Float = 69f,     height: Float = 94f,
            var cardExtensions: CardExtensions,
            var shapeRenderer: ShapeRenderer,
            var debugFont: BitmapFont? = null): Actor() {

    var touched: Boolean = false
    private var lastX: Float
    private var lastY: Float

    var speedX: Float = 0f
    var speedY: Float = 0f

    private var energyLoss: Float = 10f
    private var textureRegion: TextureRegion

    private var distanceTraveled: Float = 0f

    var bounds: Rectangle
    var polygon: Polygon

    init {
        x = posX
        y = posY

        lastX = x
        lastY = y

        setWidth(width)
        setHeight(height)

        textureRegion = cardExtensions.getFront(suit, value)

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
        when {
            value == 0 -> value = 12
            value > 0 -> value -= 1
            value == 12 -> value = 11
        }

        textureRegion = cardExtensions.getFront(suit, value)
    }

    override fun act(delta: Float) {
        // If is currently being dragged
        if(touched) {
            // Set the speed
            speedX = x - lastX
            speedY = y - lastY

            distanceTraveled += sqrt((x - lastX).pow(2) + (y-lastY).pow(2))

            lastX = x
            lastY = y

            bounds.x = x
            bounds.y = y
            polygon.setPosition(x, y)
        } else {
            distanceTraveled += sqrt((x - lastX).pow(2) + (y-lastY).pow(2))

            lastX = x
            lastY = y

            // Use the speed to move
            x += speedX
            y += speedY

            // Update the speed using the accelerometer
            speedY -= (Gdx.input.accelerometerX * 0.3f)
            speedX += (Gdx.input.accelerometerY * 0.3f)

            bounds.x = x
            bounds.y = y
            polygon.setPosition(x, y)
        }

        // Bounce off the walls
        if(x + width > Gdx.graphics.width) {
            x = Gdx.graphics.width - width

            // Reduce speed
            if(speedX - energyLoss < 0) {
                speedX = 0f
            } else {
                speedX -= energyLoss
                speedX *= -1
            }

            changeSuit()
        } else if(x < 0) {
            x = 0f

            // Reduce speed
            if(abs(speedX) - energyLoss < 0) {
                speedX = 0f
            } else {
                speedX += energyLoss
                speedX *= -1
            }

            changeSuit()
        }

        if(y + height > Gdx.graphics.height) {
            y = Gdx.graphics.height - height

            // Reduce speed
            if(speedY - energyLoss < 0) {
                speedY = 0f
            } else {
                speedY -= energyLoss
                speedY *= -1
            }

            changeSuit()
        } else if (y < 0) {
            y = 0f

            // Reduce speed
            if(abs(speedY) - energyLoss < 0) {
                speedY = 0f
            } else {
                speedY += energyLoss
                speedY *= -1
            }

            changeSuit()
        }

        // Limit the speed of a card
        if(abs(speedX) > 70) {
            speedX = if(speedX < 0) {
                -70f
            } else {
                70f
            }
        }
        if(abs(speedY) > 70) {
            speedY = if(speedY < 0) {
                -70f
            } else {
                70f
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.BLACK
        shapeRenderer.rect(x - 2f, y - 2f, width + 4, height + 4)
        shapeRenderer.end()
        batch.begin()

        batch.draw(textureRegion, x, y, width / 2, height / 2, width, height, 1f, 1f, 0f)
        debugFont?.draw(batch, "pos:$x x $y\nspeed:$speedX x $speedY\ntouched:$touched\ndistance:$distanceTraveled", x, y)
    }
}