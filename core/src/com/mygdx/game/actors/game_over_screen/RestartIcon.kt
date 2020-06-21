package com.mygdx.game.actors.game_over_screen

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.mygdx.game.api.Animated
import com.mygdx.game.api.AnimationType
import com.mygdx.game.api.GameActor
import com.mygdx.game.data.Assets
import com.mygdx.game.data.Descriptors

class RestartIcon(manager : AssetManager) : GameActor(), Animated {

    private val texture = manager.get(Descriptors.menu)
    private val replayButton = texture.findRegion(Assets.MainMenuAtlas.REPLAY_BUTTON)
    private val buttonPlay = texture.findRegion(Assets.MainMenuAtlas.BUTTON_PLAY_MINI)

    private var centerX = 0f
    private var centerY = 0f

    init {
        x = 1420f
        y = 300f
        width = replayButton.originalWidth.toFloat()
        height = replayButton.originalHeight.toFloat()
        centerX = x + (width / 2)
        centerY = y + (height / 2)
        setOrigin(width / 2f, height / 2f)
        addRotateAnimation()
    }

    private  fun addRotateAnimation(){
        addAction(Actions.parallel(Actions.repeat(RepeatAction.FOREVER,
                Actions.sequence(
                        Actions.rotateBy(-360f, 10f),
                        Actions.rotateTo(0f)))))
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch!!.color = Color.WHITE
        drawReplayIcon(batch)
        drawPlayIcon(batch)
    }

    private fun drawReplayIcon(batch: Batch){
        batch.draw(replayButton, x, y, originX, originY, width, height,1f,1f, rotation)
    }

    private fun drawPlayIcon(batch: Batch){
        val iconWidth = buttonPlay.originalWidth.toFloat()
        val iconHeight = buttonPlay.originalHeight.toFloat()
        val x = centerX - (iconWidth / 2)
        val y = centerY - (iconHeight / 2)

        batch.draw(buttonPlay, x, y, iconWidth, iconHeight)
    }

    override fun animate(type: AnimationType, runAfter: Runnable) {
    }
}