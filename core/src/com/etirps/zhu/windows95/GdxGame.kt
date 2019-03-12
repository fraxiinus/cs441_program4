package com.etirps.zhu.windows95

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport

class GdxGame : ApplicationAdapter(), InputProcessor {
    private lateinit var stage: Stage
    private lateinit var camera: OrthographicCamera
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var input: Input

    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f

    private lateinit var cardTextures: CardExtensions

    private lateinit var cards: MutableList<Card>
    private var focusedCard: Card? = null

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
        input = Input()

        // Load Textures
        cardTextures = CardExtensions(Texture("cardFaces.png"), Texture("cardBack.png"))

        // Load game objects
        cards = mutableListOf()

        // Setup game and start
        setupGame()
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

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.WHITE

        shapeRenderer.circle(input.destX, input.destY, 5f)

        shapeRenderer.end()

        spriteBatch.begin()

        actOnInput()

        for(card in cards) {
            spriteBatch.draw(card.textureRegion, card.x, card.y, card.width, card.height)
        }

        spriteBatch.end()
    }

    override fun dispose() {
        spriteBatch.dispose()
        cardTextures.dispose()
    }

    /***** GAME LOGIC FUNCTIONS *****/
    private fun setupGame() {
        for(s in 0..0) {
            // Loop for every suit
            val suit = Suit.values()[s]

            for(value in 0..12) {
                // Loop for every value
                cards.add(Card(suit, value, 150f * value, 100f, 69f * 2, 94f * 2, cardTextures.getFront(suit, value)))
            }
        }
    }

    private fun actOnInput() {

        if(input.fingerUp) {
            focusedCard = null
        }

        if(input.draggingCard && focusedCard != null) {
            focusedCard?.move(input.destX, input.destY)
        }

        if(!input.draggingCard && focusedCard == null) {
            for (card in cards) {
                if(card.bounds.contains(input.destX, input.destY)) {
                    focusedCard = card
                    input.draggingCard = true
                    break
                }
            }
        }
        
    }

    /***** INPUT PROCESSOR FUNCTIONS *****/
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val actualxy = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        input.tappedDown(actualxy.x, actualxy.y)

        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val actualxy = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        input.dragging(actualxy.x, actualxy.y)

        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val actualxy = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        input.tappedUp(actualxy.x, actualxy.y)
        focusedCard = null

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

    override fun keyDown(keycode: Int): Boolean {
        return true
    }


}
