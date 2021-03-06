/*
 * Copyright (C) Art-_-master - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.run.cookie.run.game.actors.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.run.cookie.run.game.Config
import com.run.cookie.run.game.api.GameActor
import com.run.cookie.run.game.api.Scrollable
import com.run.cookie.run.game.data.Descriptors
import com.run.cookie.run.game.api.HorizontalScroll

class Background(manager : AssetManager) : GameActor(), Scrollable{

    private val texture = manager.get(Descriptors.background)

    private var scrollerBack = HorizontalScroll(0f, 0f,
            texture.width, texture.height, Config.ItemScrollSpeed.LEVEL_1)

    private var scrollerFront = HorizontalScroll(scrollerBack.getTailX(), 0f,
            texture.width, texture.height, Config.ItemScrollSpeed.LEVEL_1)

    //for excluding rare graphical bug for other screen sizes
    private val textureOffset = 10

    override fun act(delta: Float) {
        super.act(delta)
        scrollerBack.act(delta)
        scrollerFront.act(delta)

        if (scrollerBack.isScrolledLeft) {
            scrollerBack.reset(scrollerFront.getTailX())

        } else if (scrollerFront.isScrolledLeft) {
            scrollerFront.reset(scrollerBack.getTailX())
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch!!.color = Color.WHITE
        batch.draw(texture,
                scrollerBack.getX(),
                scrollerBack.getY(),
                scrollerBack.width.toFloat() + textureOffset,
                scrollerBack.height.toFloat())

        batch.draw(texture,
                scrollerFront.getX(),
                scrollerFront.getY(),
                scrollerFront.width.toFloat() + textureOffset,
                scrollerFront.height.toFloat())

    }
    override fun stopMove() {
        scrollerBack.isStopMove = true
        scrollerFront.isStopMove = true
    }

    override fun runMove() {
        scrollerFront.isStopMove = false
        scrollerBack.isStopMove = false
    }

    override fun updateSpeed() {
        scrollerBack.update()
        scrollerFront.update()
    }
}