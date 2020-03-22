package com.mygdx.game

import com.badlogic.gdx.Gdx


object Config {
    const val WIDTH_GAME = 1920f
    const val HEIGHT_GAME = 1080f
    const val SHADOW_ANIMATION_TIME_S = 1f
    const val BUTTONS_ANIMATION_TIME_S = 1f
    const val VIBRATION_TIME_MS = 100
    const val SOUNDS_FOLDER = "sounds/"

    private val heightDevise = Gdx.graphics.height.toFloat()
    private val widthDevise = Gdx.graphics.width.toFloat()

    var R: Float = 0f

    val zoom: Float
        get() {
            if (Gdx.graphics.height < Gdx.graphics.width) {
                when {
                    HEIGHT_GAME > Gdx.graphics.height -> R = heightDevise / HEIGHT_GAME
                    HEIGHT_GAME < Gdx.graphics.height -> R = HEIGHT_GAME / heightDevise
                    HEIGHT_GAME == heightDevise -> R = 1f
                }
            } else {
                when {
                    WIDTH_GAME > Gdx.graphics.width -> R = widthDevise / WIDTH_GAME
                    WIDTH_GAME < Gdx.graphics.width -> R = WIDTH_GAME / widthDevise
                    WIDTH_GAME == widthDevise -> R = 1f
                }
            }

            return R
        }
}