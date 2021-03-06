/*
 * Copyright (C) Art-_-master - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.run.cookie.run.game.actors.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.run.cookie.run.game.api.GameActor
import com.run.cookie.run.game.api.Hideable
import com.run.cookie.run.game.beans.Position
import com.run.cookie.run.game.data.Assets
import com.run.cookie.run.game.data.Descriptors

class City(manager : AssetManager, private val window : Window) : GameActor() {
    private val texture = manager.get(Descriptors.environment)
    private val cityRegion = texture.findRegion(Assets.EnvironmentAtlas.CITY)

    private val posCity = Position(
            0f,
            window.getWindowsillY(),
            cityRegion.originalWidth.toFloat(),
            cityRegion.originalHeight.toFloat())

    override fun draw(batch: Batch?, parentAlpha: Float) {

        val windowPos = Position(
                window.scroll.getX() + 85,
                window.scroll.getY() - 0,
                window.scroll.width - 150f,
                window.scroll.height - 100f)

        val cityHidePos = Hideable(posCity, windowPos)

        batch!!.draw(
                cityRegion.texture,
                cityHidePos.getX(),
                cityHidePos.getY(),
                cityHidePos.getX(),
                cityHidePos.getY(),
                cityHidePos.getDrawWidth(),
                cityHidePos.getDrawHeight(),
                1f,
                1f,
                0f,
                cityRegion.regionX + cityHidePos.getTextureX().toInt(),
                cityRegion.regionY + cityHidePos.getTextureY().toInt(),
                cityHidePos.getDrawWidth().toInt(),
                cityHidePos.getDrawHeight().toInt(),
                false,
                false)
    }
}