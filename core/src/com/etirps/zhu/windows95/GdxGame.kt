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

    private lateinit var cardTextures: CardExtensions
    private lateinit var img: Texture

    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f

    override fun create() {
        // Get screen size
        screenWidth = Gdx.graphics.width.toFloat()
        screenHeight = Gdx.graphics.height.toFloat()

        // Create camera and set to size of screen
        // this allows play area to be a different "resolution" than the native screen
        camera = OrthographicCamera()
        camera.setToOrtho(false, screenWidth, screenHeight)

        spriteBatch = SpriteBatch()

        // Set viewport size, this is the size of the game area
        stage = Stage(FitViewport(screenWidth, screenHeight, camera), spriteBatch)
        shapeRenderer = ShapeRenderer()

        // Use this class as the input processor
        Gdx.input.inputProcessor = this

        // Load game stuff
        cardTextures = CardExtensions(Texture("cardFaces.png"), Texture("cardBack.png"))
        img = Texture("badlogic.jpg")
    }

    override fun render() {
        // Clear the screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update camera matrices
        camera.update()

        // Tell draw objects to use coordinates provided by camera
        spriteBatch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        /////// Test
        var card = Card(Suit.HEARTS, 0, 0f, 0f)
        ///////

        spriteBatch.begin()

        for(i in 0..12) {
            card.value = i
            spriteBatch.draw(cardTextures.getFront(card), 150f * i, 100f, card.width * 2, card.height * 2)
        }

        spriteBatch.end()
    }

    override fun dispose() {
        spriteBatch.dispose()
        img.dispose()
    }

    /***** INPUT PROCESSOR FUNCTIONS *****/
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return true
    }

    override fun keyTyped(character: Char): Boolean {
        return true
    }

    override fun scrolled(amount: Int): Boolean {
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }
}
