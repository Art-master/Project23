package com.mygdx.game.actors.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.mygdx.game.api.GameActor
import com.mygdx.game.data.Assets
import com.mygdx.game.data.Descriptors

class CookieShadow(manager : AssetManager,
                   private val cookie: Cookie): GameActor() {

    private val texture = manager.get(Descriptors.environment)
    private val region = texture.findRegion(Assets.EnvironmentAtlas.CIRCLE_SHADOW)
    private val origWidth = region.originalWidth.toFloat()
    private val origHeight = region.originalHeight.toFloat()
    private var peak = cookie.ground
    private var isDraw = true

    init {
        width = origWidth
        height = origHeight
    }

    override fun act(delta: Float) {
        super.act(delta)
        calculateShadowSize()
    }

    private fun calculateShadowSize(){
        val widthChange = 1f
        val heightChange = 0.4f
        isDraw = true
        if(cookie.y > peak){
            peak = cookie.y
            width -= widthChange
            height -= heightChange
        } else if(cookie.isFalling()){
            width += widthChange
            height += heightChange
        } else if(cookie.isRun() && cookie.y == cookie.startY){
            if(cookie.y <= cookie.ground) peak = cookie.ground
            width = origWidth
            height = origHeight
        } else {
            isDraw = false
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val x = cookie.x
        val y = cookie.startY - 20
        if(isDraw) batch!!.draw(region, x, y, x, y,  width, height, scaleX, scaleY, rotation)
    }
}