package com.mygdx.game

import com.badlogic.gdx.Game
import com.mygdx.game.ScreenManager.Screens.*

class GdxGame : Game() {

    override fun create() {
        ScreenManager.game = this
        ScreenManager.setScreen(START_SCREEN)
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {}
}
