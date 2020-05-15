package com.mygdx.game

import com.badlogic.gdx.Game
import com.mygdx.game.api.GameSettings
import com.mygdx.game.managers.ScreenManager
import com.mygdx.game.managers.ScreenManager.Screens.*

class GdxGame : Game() {

    override fun create() {
        ScreenManager.game = this
        ScreenManager.setScreen(GAME_SCREEN)
        GameSettings.DEBUG.state = true
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {}
}
