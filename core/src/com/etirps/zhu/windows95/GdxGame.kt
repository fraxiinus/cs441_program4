package com.etirps.zhu.windows95

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
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
    private lateinit var frameBuffer: FrameBuffer
    private lateinit var fpsCounter: FPSCounter
    private lateinit var input: Input

    private var debugFont: BitmapFont? = null

    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f

    private lateinit var cardTextures: CardExtensions
    private lateinit var bsodTexture: Texture
    private lateinit var windowTexture: Texture
    private lateinit var lastTexture: TextureRegion
    private lateinit var bootSound: Sound

    private lateinit var cards: MutableList<Card>
    private var focusedCard: Card? = null

    private var lastTime: Long = 0
    private var soundPlayed: Boolean = false
    private var bsodShown: Boolean = false

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

        // Create framebuffer, used for trail effect
        frameBuffer = FrameBuffer(Pixmap.Format.RGB888, camera.viewportWidth.toInt(), camera.viewportHeight.toInt(), false)

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
        Gdx.input.isCatchBackKey = true

        // Load Textures
        cardTextures = CardExtensions(Texture("cardFaces.png"), Texture("cardBack.png"))
        bsodTexture = Texture("WindowsBSOD.png")
        windowTexture = Texture("solitaire.png")
        bootSound = Gdx.audio.newSound(Gdx.files.internal("win98.ogg"))

        // Load game objects
        cards = mutableListOf()

        // Setup game and start
        //setupGame()

        frameBuffer.begin()

        Gdx.gl.glClearColor(85f / 255f, 170f / 255f, 170f / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        frameBuffer.end()
        lastTexture = TextureRegion(frameBuffer.colorBufferTexture)
        lastTexture.flip(false, true)

    }

    override fun render() {
        frameBuffer.begin()

        // Clear the screen
        Gdx.gl.glClearColor(85f / 255f, 170f / 255f, 170f / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update camera matrices
        camera.update()

        // Tell draw objects to use coordinates provided by camera
        spriteBatch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        // Handle user Input
        actOnInput()

        // Draw the last frame as the background
        spriteBatch.begin()
        spriteBatch.draw(lastTexture, 0f, 0f)
        spriteBatch.end()


        if((TimeUtils.millis() - lastTime) / 1000f < 1.5 && (TimeUtils.millis() - lastTime) / 1000f > 1 && !soundPlayed) {
            bootSound.play(1f)
            soundPlayed = true
            frameBuffer.end()

            drawFrame()
            return
        }
        else if((TimeUtils.millis() - lastTime) / 1000f < 2) {
            Gdx.gl.glClearColor(85f / 255f, 170f / 255f, 170f / 255f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

            //spriteBatch.begin()
            //spriteBatch.draw(bootTexture, screenWidth / 2 - (1305 / 2), 0f, 1305f, 1080f)
            //spriteBatch.end()
            // Draw solitaire texture

            frameBuffer.end()

            drawFrame()
            return
        } else if(soundPlayed) {
            bootSound.dispose()

            Gdx.gl.glClearColor(85f / 255f, 170f / 255f, 170f / 255f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)


            spriteBatch.begin()
            val bsodWidth = 628f * 2
            val bsodHeight = 442f * 2
            spriteBatch.draw(windowTexture, screenWidth / 2 - bsodWidth / 2, screenHeight / 2 - bsodHeight / 2, bsodWidth, bsodHeight)
            spriteBatch.end()

            soundPlayed = false
        }

        drawHUD()

        // Update FPS
        fpsCounter.update()
        fpsCounter.render()

        stage.act()
        stage.draw()

        frameBuffer.end()

        drawFrame()
    }

    override fun dispose() {
        spriteBatch.dispose()
        cardTextures.dispose()
        frameBuffer.dispose()
        bootSound.dispose()
        bsodTexture.dispose()
        windowTexture.dispose()
    }

    /***** GAME LOGIC FUNCTIONS *****/
    private fun setupGame() {
        for(s in 0..3) {
            // Loop for every suit
            val suit = Suit.values()[s]

            // Spawn 4 kings
            val value = 11

            val card = Card(suit, value, 5 + 250f * s, screenHeight - 400f, 69f * 3, 94f * 3, cardTextures, shapeRenderer, debugFont)
            card.speedX = (0..3).random() * 15f
            card.speedY = (0..3).random() * 15f
            cards.add(card)
            fgGroup.addActor(card)
        }
    }

    private fun addCard(x: Float, y: Float) {
        val suit = Suit.values()[(0..3).random()]
        val value = (0..11).random()

        val cardWidth = 69f * 2
        val cardHeight = 94f * 2
        val newCard = Card(suit, value, x - cardWidth / 2, y - cardHeight / 2, cardWidth, cardHeight, cardTextures, shapeRenderer, debugFont)
        newCard.speedX = 5f + (5f * (0..3).random())
        //newCard.speedY = 5f * (1..2).random()
        cards.add(newCard)
        fgGroup.addActor(newCard)
    }

    private fun drawFrame() {
        spriteBatch.begin()
        val texture = TextureRegion(frameBuffer.colorBufferTexture)
        texture.flip(false, true)
        spriteBatch.draw(texture, 0f, 0f)
        spriteBatch.end()
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

        if(input.doubleTap) {
            addCard(input.origX, input.origY)
            input.doubleTap = false
        }

    }

    private fun drawHUD() {
        spriteBatch.begin()

        debugFont?.draw(spriteBatch, "fingerDown:${input.fingerDown}\nfingerUp:${input.fingerUp}\ndoubleTap:${input.doubleTap}\ndraggingCard:${input.draggingCard}\npos:${input.destX}x${input.destY}", input.destX + 100, input.destY)

        debugFont?.draw(spriteBatch, "X: ${Gdx.input.accelerometerX}\nY: ${Gdx.input.accelerometerY}\nZ: ${Gdx.input.accelerometerY}", 5f, screenHeight - 40)

        spriteBatch.end()

    }

    private fun resetGame(clearCards: Boolean) {

        if(clearCards) {
            // Remove all cards
            for (card in cards){
                card.remove()
            }
            cards.clear()
        }

        // If BSOD is shown
        if(bsodShown) {

            // If there are no cards, setup cards
            if(cards.count() < 1) {
                //setupGame()
            }

            // start drawing frame
            frameBuffer.begin()
            // Draw blue background
            Gdx.gl.glClearColor(0f, 0f, 170f / 255f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

            // Draw bsod texture
            spriteBatch.begin()
            val bsodWidth = 1280f
            val bsodHeight = 800f
            spriteBatch.draw(bsodTexture, screenWidth / 2 - bsodWidth / 2, screenHeight / 2 - bsodHeight / 2, bsodWidth, bsodHeight)
            spriteBatch.end()

            frameBuffer.end()

            // Save texture and draw
            lastTexture = TextureRegion(frameBuffer.colorBufferTexture)
            lastTexture.flip(false, true)
            drawFrame()

            return
        }

        focusedCard = null
        lastTime = 0
        soundPlayed = false

        frameBuffer.begin()

        Gdx.gl.glClearColor(85f / 255f, 170f / 255f, 170f / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Draw solitaire texture
        spriteBatch.begin()
        val bsodWidth = 628f * 2
        val bsodHeight = 442f * 2
        spriteBatch.draw(windowTexture, screenWidth / 2 - bsodWidth / 2, screenHeight / 2 - bsodHeight / 2, bsodWidth, bsodHeight)
        spriteBatch.end()

        frameBuffer.end()
        lastTexture = TextureRegion(frameBuffer.colorBufferTexture)
        lastTexture.flip(false, true)
        drawFrame()
    }

    private fun showBSOD() {
        // If bsod is already shown, skip
        if(bsodShown) { return }

        // Remove all cards
        for (card in cards){
            card.remove()
        }
        cards.clear()

        // Start drawing new frame
        frameBuffer.begin()
        // Draw blue background
        Gdx.gl.glClearColor(0f, 0f, 170f / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        // Draw BSOD texture
        spriteBatch.begin()
        val bsodWidth = 1280f
        val bsodHeight = 800f
        spriteBatch.draw(bsodTexture, screenWidth / 2 - bsodWidth / 2, screenHeight / 2 - bsodHeight / 2, bsodWidth, bsodHeight)
        spriteBatch.end()

        frameBuffer.end()

        // Save frame and draw on screen
        lastTexture = TextureRegion(frameBuffer.colorBufferTexture)
        lastTexture.flip(false, true)
        drawFrame()

        // Flip bsod flag
        bsodShown = true
    }

    /***** INPUT PROCESSOR FUNCTIONS *****/
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val actualxy = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        input.tappedDown(actualxy.x, actualxy.y)

        when {
            // 4 fingers
            pointer > 2 -> {
                // If bsod is already up
                if(bsodShown) {
                    // flip the flag
                    bsodShown = false
                    // reset game to regular green
                    resetGame(true)

                    return true
                }

                // Show BSOD
                showBSOD()
                return true
            }

            // three fingers
            pointer > 1 -> {
                resetGame(false)
                return true
            }

            // two fingers
            pointer > 0 -> {
                //addCard()
                return true
            }

            // Other?????
            else -> return true
        }

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
        if(keycode == com.badlogic.gdx.Input.Keys.BACK) {
            Gdx.app.exit()
        }
        return true
    }


}
