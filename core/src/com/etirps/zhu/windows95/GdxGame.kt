package com.etirps.zhu.windows95

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport

class GdxGame : ApplicationAdapter(), InputProcessor {
    private lateinit var stage: Stage
    private lateinit var camera: OrthographicCamera
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var shapeRenderer: ShapeRenderer

    private lateinit var img: Texture

    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f

    override fun create() {
        // Get screen size
        screenWidth = Gdx.graphics.width.toFloat()
        screenHeight = Gdx.graphics.width.toFloat()

        // Create camera and set to size of screen
        // this allows play area to be a different "resolution" than the native screen
        camera = OrthographicCamera()
        camera.setToOrtho(false, screenWidth, screenHeight)

        spriteBatch = SpriteBatch()
        stage = Stage(FitViewport(screenWidth, screenHeight, camera), spriteBatch)
        shapeRenderer = ShapeRenderer()

        Gdx.input.inputProcessor = this

        img = Texture("badlogic.jpg")
    }

    override fun render() {
        // Clear the screen
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update camera matrices
        camera.update()

        // Tell draw objects to use coordinates provided by camera
        spriteBatch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        spriteBatch.begin()
        spriteBatch.draw(img, 0f, 0f)
        spriteBatch.end()
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyTyped(character: Char): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun scrolled(amount: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyUp(keycode: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyDown(keycode: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dispose() {
        spriteBatch.dispose()
        img.dispose()
    }
}
