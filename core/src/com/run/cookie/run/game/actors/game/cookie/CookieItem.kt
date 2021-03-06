/*
 * Copyright (C) Art-_-master - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.run.cookie.run.game.actors.game.cookie

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.run.cookie.run.game.Config
import com.run.cookie.run.game.actors.game.cookie.Cookie.State.*
import com.run.cookie.run.game.api.Animated
import com.run.cookie.run.game.api.AnimationType
import com.run.cookie.run.game.api.GameActor
import com.run.cookie.run.game.data.Assets
import com.run.cookie.run.game.data.Descriptors

class CookieItem(manager: AssetManager, val cookie: Cookie, itemName: String) : GameActor(), Animated {

    private val texture = manager.get(Descriptors.cookie)
    private var jumpUpRegion = texture.findRegion(Assets.CookieAtlas.JUMP_UP_PREFIX + itemName)
    private var jumpDownRegion = texture.findRegion(Assets.CookieAtlas.JUMP_DOWN_PREFIX + itemName)
    private var runRegions = texture.findRegions(Assets.CookieAtlas.RUN_PREFIX + itemName)
    private var itemRegion = texture.findRegion(itemName)
    private var circleLoadingRegion = texture.findRegion(Assets.CookieAtlas.LOADING_CIRCLE)
    private val runAnimation = Animation(0.1f, runRegions, Animation.PlayMode.LOOP_PINGPONG)

    private var currentFrame = if (runAnimation.keyFrames.isNotEmpty()) runAnimation.keyFrames.first() else null
    private val frameHeight = currentFrame?.originalHeight?.toFloat()
    private val frameWidth = currentFrame?.originalWidth?.toFloat()

    private var isInvolvedInGame = false
    private var isGameOver = false

    init {
        width = itemRegion.originalWidth.toFloat()
        height = itemRegion.originalHeight.toFloat()
        x = (Config.WIDTH_GAME - width) / 2
        y = Config.HEIGHT_GAME - height - 100
        scaleX = 0.1f
        scaleY = 0.1f
        color.a = 0f
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch!!.color = Color.WHITE
        if (cookie.state == INIT) return
        currentFrame = when {
            currentFrame == null -> null
            cookie.state == STOP -> runRegions.first()
            cookie.state == RUN -> runAnimation.getKeyFrame(cookie.runTime)
            cookie.state == SLIP -> jumpDownRegion
            cookie.state == STUMBLE -> jumpDownRegion
            cookie.state == JUMP -> jumpUpRegion
            cookie.state === FALL && cookie.isJumpPeakPassed().not() -> jumpUpRegion
            cookie.state == FALL -> jumpDownRegion
            cookie.state == WIN -> runAnimation.getKeyFrame(cookie.runTime)
            cookie.isVisible.not() -> jumpUpRegion
            else -> null
        }

        if (isInvolvedInGame.not()) {
            batch.setColor(color.r, color.g, color.b, color.a)
            batch.draw(itemRegion, x, y, width, height)
            drawLoadingCircle(batch)
        }

        batch.setColor(color.r, color.g, color.b, 1f)
        if (isInvolvedInGame && currentFrame !== null) {
            val x = if (isGameOver) x else cookie.x
            val y = if (isGameOver) y else cookie.y
            val originX = if (isGameOver) originX else cookie.originX
            val originY = if (isGameOver) originY else cookie.originY
            batch.draw(currentFrame, x, y, originX, originY, frameWidth!!, frameHeight!!, 1f, 1f, cookie.rotation)
        }
    }

    private fun drawLoadingCircle(batch: Batch) {
        val alpha = if (color.a < 0.5f) color.a else 0.5f
        batch.setColor(color.r, color.g, color.b, alpha)
        val width = circleLoadingRegion.originalWidth.toFloat()
        val height = circleLoadingRegion.originalHeight.toFloat()
        batch.draw(circleLoadingRegion,
                x - (width - this.width) / 2,
                y - (height - this.height) / 2, width / 2,
                height / 2, width, height, scaleX, scaleY, rotation)
    }

    override fun animate(type: AnimationType, runAfter: Runnable) {
        val action = when (type) {
            AnimationType.SHOW_ON_SCENE -> {
                Actions.parallel(
                        Actions.sequence(
                                Actions.parallel(
                                        Actions.scaleTo(1f, 1f, 0.6f, Interpolation.smooth),
                                        Actions.alpha(1f, 0.6f)
                                ),
                                Actions.delay(0.6f),
                                Actions.parallel(
                                        Actions.scaleTo(0f, 0f, 0.3f),
                                        Actions.moveTo(x, 0f, 0.6f),
                                        Actions.alpha(0f, 0.3f)
                                ),
                                Actions.run { isInvolvedInGame = true }
                        ),
                        Actions.rotateBy(-360f, 2f)
                )

            }
            AnimationType.HIDE_FROM_SCENE -> {
                if (isInvolvedInGame.not()) return
                Actions.sequence(
                        Actions.run {
                            isGameOver = true
                            y = cookie.y + (cookie.width / 2)
                            x = cookie.x
                        },
                        Actions.moveTo(-1f, -200f, 0.3f, Interpolation.exp10)
                )

            }
            else -> null
        }
        val run = Actions.run(runAfter)
        val sequence = Actions.sequence(action, run)
        addAction(sequence)
    }
}