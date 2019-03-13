package com.etirps.zhu.windows95

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.TimeUtils

class TrailCard(posX: Float,    posY: Float,
                width: Float,   height: Float,
                var textureRegion: TextureRegion,
                var shapeRenderer: ShapeRenderer,
                var debugFont: BitmapFont?): Actor() {

    private var life: Float = 30f
    var timeRemaining: Float = life
    var lastTime: Long = TimeUtils.millis()

    init {
        x = posX
        y = posY

        this.width = width
        this.height = height
    }

    private fun updateDuration() {
        val timeElapsed = TimeUtils.millis() - lastTime

        timeRemaining =  life - timeElapsed / 1000f

        if(timeElapsed / 1000 >= life) {
            this.remove()
        }
    }

    override fun act(delta: Float) {
        //updateDuration()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.BLACK
        shapeRenderer.rect(x - 2f, y - 2f, width + 4, height + 4)
        shapeRenderer.end()
        batch.begin()

        batch.draw(textureRegion, x, y, width / 2, height / 2, width, height, 1f, 1f, 0f)
    }
}