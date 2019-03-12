package com.etirps.zhu.windows95

import com.badlogic.gdx.utils.TimeUtils

class Input {

    var fingerDown: Boolean = false
    var fingerUp: Boolean = false
    var doubleTap: Boolean = false
    var draggingCard: Boolean = false
    var doubleTapSpeed: Float = 2f

    var origX: Float = 0f
    var origY: Float = 0f

    var destX: Float = 0f
    var destY: Float = 0f

    private var lastTapTime: Long = 0

    fun tappedDown(x: Float, y: Float) {
        if(!fingerDown) {
            origX = x
            origY = y

            fingerDown = true
            fingerUp = false
        }
    }

    fun dragging(x: Float, y: Float) {
        if(fingerDown) {
            destX = x
            destY = y
        }
    }

    fun tappedUp(x: Float, y: Float) {
        if(!fingerUp) {
            val currTime = TimeUtils.millis()
            doubleTap = currTime - lastTapTime < doubleTapSpeed

            lastTapTime = TimeUtils.millis()

            destX = x
            destY = y
            fingerDown = false
            fingerUp = true
            draggingCard = false
        }
    }
}