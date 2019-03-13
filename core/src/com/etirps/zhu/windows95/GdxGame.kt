package com.etirps.zhu.windows95

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.FitViewport

class GdxGame : ApplicationAdapter(), InputProcessor {
    private lateinit var stage: Stage
    private lateinit var bgGroup: Group
    private lateinit var fgGroup: Group
    private lateinit var camera: OrthographicCamera
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var fpsCounter: FPSCounter
    private lateinit var input: Input

    private var debugFont: BitmapFont? = null

    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f

    private lateinit var cardTextures: CardExtensions
    private lateinit var bootTexture: Texture
    private lateinit var bootSound: Sound

    private lateinit var cards: MutableList<Card>
    private var focusedCard: Card? = null

    private var fpsLimitBegin: Long = 0
    private var fpsLimitEnd: Long = 0

    private var lastTime: Long = 0
    private var soundPlayed: Boolean = false
    private var postBoot: Boolean = false

    override fun create() {
        // Get screen size
        screenWidth = Gdx.graphics.width.toFloat()
        screenHeight = Gdx.graphics.height.toFloat()

        // Create engine objects
        spriteBatch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        //debugFont = BitmapFont()

        // Create camera and set to size of screen
        // this allows play area to be a different "resolution" than the native screen
        camera = OrthographicCamera()
        camera.setToOrtho(false, screenWidth, screenHeight)

        fpsCounter = FPSCounter(debugFont)
        fpsCounter.resize(screenWidth, screenHeight)

        lastTime = TimeUtils.millis()

        // Set viewport size, this is the size of the game area
        stage = Stage(FitViewport(screenWidth, screenHeight, camera), spriteBatch)
        bgGroup = Group()
        fgGroup = Group()

        stage.addActor(bgGroup)
        stage.addActor(fgGroup)

        // Set line width of the border
        Gdx.gl.glLineWidth(2f)

        // Use this class as the input processor
        Gdx.input.inputProcessor = this
        input = Input()

        // Load Textures
        cardTextures = CardExtensions(Texture("cardFaces.png"), Texture("cardBack.png"))
        bootTexture = Texture("loading_win98.gif")
        bootSound = Gdx.audio.newSound(Gdx.files.internal("win98.ogg"))

        // Load game objects
        cards = mutableListOf()

        // Setup game and start
        setupGame()
    }

    override fun render() {
        fpsLimitBegin = TimeUtils.nanoTime()

        // Clear the screen
        Gdx.gl.glClearColor(0f, 128f / 255f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update camera matrices
        camera.update()

        // Tell draw objects to use coordinates provided by camera
        spriteBatch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        actOnInput()

        if((TimeUtils.millis() - lastTime) / 1000f < 1 && (TimeUtils.millis() - lastTime) / 1000f > 0.5 && !soundPlayed) {
            bootSound.play(1f)
            soundPlayed = true
            return
        }
        else if((TimeUtils.millis() - lastTime) / 1000f < 8) {
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

            spriteBatch.begin()
            spriteBatch.draw(bootTexture, screenWidth / 2 - (1305 / 2), 0f, 1305f, 1080f)
            spriteBatch.end()
            return
        } else if(!postBoot) {

            bootTexture.dispose()
            bootSound.dispose()

            postBoot = true
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.circle(input.destX, input.destY, 5f)
        drawHUD()

        shapeRenderer.end()

        // Update FPS
        fpsCounter.update()
        fpsCounter.render()

        stage.act()
        stage.draw()

        fpsLimitEnd = System.nanoTime()
        val timeDiff = fpsLimitEnd - fpsLimitBegin
        val sleepTime = (1000000000f/30 - timeDiff).toInt()
        while(fpsLimitEnd + sleepTime > System.nanoTime()){
            Thread.yield()
        }
    }

    override fun dispose() {
        spriteBatch.dispose()
        cardTextures.dispose()
    }

    /***** GAME LOGIC FUNCTIONS *****/
    private fun setupGame() {
        for(s in 0..3) {
            // Loop for every suit
            val suit = Suit.values()[s]

            // Spawn 4 kings
            val value = 11

            val card = Card(suit, value, 5 + 250f * s, screenHeight - 300f, 69f * 3, 94f * 3, cardTextures, shapeRenderer, bgGroup, debugFont)
            cards.add(card)
            fgGroup.addActor(card)
        }
    }

    private fun actOnInput() {
        // If am not dragging a card
        if(!input.draggingCard) {
            // loop through every card
            for (card in cards) {
                // If the card is at the input point
                if(card.bounds.contains(input.destX, input.destY)) {
                    // Set focused card to the card
                    focusedCard = card
                    // set card as touched
                    card.touched = true
                    // set dragging flag as true
                    input.draggingCard = true
                    // stop looping
                    break
                }
            }
        }else if(input.fingerUp){
            focusedCard?.touched = false
            input.draggingCard = false
            focusedCard = null
        }

        if(input.draggingCard && focusedCard != null) {
            focusedCard?.move(input.destX, input.destY)
        }

    }

    private fun drawHUD() {
        spriteBatch.begin()

        debugFont?.draw(spriteBatch, "fingerDown:${input.fingerDown}\nfingerUp:${input.fingerUp}\ndoubleTap:${input.doubleTap}\ndraggingCard:${input.draggingCard}\npos:${input.destX}x${input.destY}", input.destX + 100, input.destY)

        debugFont?.draw(spriteBatch, "X: ${Gdx.input.accelerometerX}\nY: ${Gdx.input.accelerometerY}\nZ: ${Gdx.input.accelerometerY}", 5f, screenHeight - 40)

        spriteBatch.end()

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

        input.destX = -1f
        input.destY = -1f
        focusedCard?.touched = false

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
