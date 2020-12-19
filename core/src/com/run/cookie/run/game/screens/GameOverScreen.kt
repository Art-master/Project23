package com.run.cookie.run.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.run.cookie.run.game.actors.game_over_screen.*
import com.run.cookie.run.game.api.Advertising
import com.run.cookie.run.game.api.AnimationType.*
import com.run.cookie.run.game.data.Assets
import com.run.cookie.run.game.managers.AudioManager
import com.run.cookie.run.game.managers.AudioManager.MusicApp.MAIN_MENU_MUSIC
import com.run.cookie.run.game.managers.AudioManager.SoundApp.CLICK_SOUND
import com.run.cookie.run.game.managers.ScreenManager
import com.run.cookie.run.game.managers.ScreenManager.Param.*
import com.run.cookie.run.game.managers.ScreenManager.Screens.GAME_SCREEN
import com.run.cookie.run.game.managers.ScreenManager.Screens.MAIN_MENU_SCREEN
import com.run.cookie.run.game.managers.VibrationManager
import com.run.cookie.run.game.services.AdsCallback
import com.run.cookie.run.game.services.ServicesController

class GameOverScreen(params: Map<ScreenManager.Param, Any>) : GameScreen(params) {

    private var controller = params[SERVICES_CONTROLLER] as ServicesController
    private var score = params[SCORE] as Int
    private var advertizing = params[FIRST_APP_RUN] as Advertising

    private val scoresActor = Scores(manager, score)

    private var wasWinGame = params[WAS_WIN_GAME] as Boolean?

    init {
        Gdx.input.inputProcessor = stage
        if (controller.isSignedIn() && score > scoresActor.bestScoreNum) {
            controller.submitScore(score.toLong())
        }
    }

    override fun hide() {
    }

    override fun show() {
    }

    override fun render(delta: Float) {
        if (stage.actors.isEmpty) addActorsToStage()
        applyStages(delta)
    }

    private fun addActorsToStage() {
        val background = Background(manager)
        val finalAction = if (wasWinGame == true) Together(manager) else CookieRests(manager)
        val restartIcon = RestartIcon(manager)
        val cup = Cup(manager)
        val topScores = GameOverMenuIcon(manager, 40f, 780f, Assets.MainMenuAtlas.TOP_SCORES)
        val awards = GameOverMenuIcon(manager, 70f, 580f, Assets.MainMenuAtlas.AWARDS)
        val share = GameOverMenuIcon(manager, 1777f, 780f, Assets.MainMenuAtlas.SHARE, false)
        val mainMenu = GameOverMenuIcon(manager, 1777f, 580f, Assets.MainMenuAtlas.MAIN_MENU, false)

        stageBackground.addActor(background)
        stage.apply {
            addActor(cup)
            addActor(finalAction)
            addActor(restartIcon)
            addActor(scoresActor)
            if (adsController.isNetworkAvailable()) {
                addActor(topScores)
                addActor(awards)
            }
            addActor(share)
            addActor(mainMenu)
        }

        restartIcon.animate(SHOW_ON_SCENE)
        shadow.animate(SHOW_ON_SCENE)
        scoresActor.animate(SHOW_ON_SCENE)
        topScores.animate(SHOW_ON_SCENE)
        awards.animate(SHOW_ON_SCENE)
        share.animate(SHOW_ON_SCENE)
        mainMenu.animate(SHOW_ON_SCENE)
        AudioManager.play(MAIN_MENU_MUSIC)

        addClickListener(restartIcon) {
            adsController.hideBannerAd()
            AudioManager.stopAll()
            restartIcon.animate(HIDE_FROM_SCENE)
            scoresActor.animate(HIDE_FROM_SCENE)
            topScores.animate(HIDE_FROM_SCENE)
            awards.animate(HIDE_FROM_SCENE)
            share.animate(HIDE_FROM_SCENE)
            mainMenu.animate(HIDE_FROM_SCENE)
            shadow.animate(HIDE_FROM_SCENE, Runnable {
                onNewScreen(GAME_SCREEN)
            })
        }

        addClickListener(topScores) {
            controller.showLeaderboard()
            topScores.animate(CLICK)
        }
        addClickListener(awards) {
            controller.showAllAchievements()
            awards.animate(CLICK)
        }
        addClickListener(share) {
            share.animate(CLICK)
            controller.share(score)
        }
        addClickListener(mainMenu) {
            restartIcon.animate(HIDE_FROM_SCENE)
            scoresActor.animate(HIDE_FROM_SCENE)
            topScores.animate(HIDE_FROM_SCENE)
            awards.animate(HIDE_FROM_SCENE)
            share.animate(HIDE_FROM_SCENE)
            mainMenu.animate(HIDE_FROM_SCENE)
            shadow.animate(HIDE_FROM_SCENE, Runnable {
                onNewScreen(MAIN_MENU_SCREEN)
            })
        }
    }

    private fun onNewScreen(screen: ScreenManager.Screens) {
        if (adsController.isNetworkAvailable()) {
            showAddIfNeedAndSetScreenAfter(
                    object : AdsCallback {
                        override fun close() {
                            ScreenManager.setScreen(screen)
                        }

                        override fun click() {
                            advertizing.commonClickCount++
                            ScreenManager.setScreen(screen)
                        }

                        override fun fail() {
                            ScreenManager.setScreen(screen)
                        }

                    })
        } else {
            ScreenManager.setScreen(screen)
        }
    }

    private fun showAddIfNeedAndSetScreenAfter(callback: AdsCallback) {
        val lastAd = advertizing.last
        val minCountOneByOne = 2

        if (advertizing.commonClickCount > 5) callback.close()

        if (lastAd.type == Advertising.AdType.NONE && lastAd.lastCountOneByOne == minCountOneByOne) {
            val index = advertizing.history.size - 2

            if (advertizing.history.elementAtOrNull(index) != null) {
                val prev = advertizing.history[index]
                if (prev.type == Advertising.AdType.VIDEO) {
                    prev.type = Advertising.AdType.INTERSTITIAL
                    adsController.showInterstitialAd(callback)
                } else if (prev.type == Advertising.AdType.INTERSTITIAL) {
                    prev.type = Advertising.AdType.VIDEO
                    adsController.showVideoAd(callback)
                }

            } else {
                advertizing.last = Advertising.Adv()
                advertizing.last.type = Advertising.AdType.INTERSTITIAL
                adsController.showInterstitialAd(callback)
            }
        } else if (lastAd.type == Advertising.AdType.NONE) {
            lastAd.type = Advertising.AdType.NONE
            lastAd.lastCountOneByOne++
        }
        advertizing.last.timeMs = System.currentTimeMillis()
        advertizing.last.lastCountOneByOne++
    }


    private fun addClickListener(actor: Actor, function: () -> Unit) {
        actor.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                AudioManager.play(CLICK_SOUND)
                adsController.hideBannerAd()
                VibrationManager.vibrate()
                function()
                return super.touchDown(event, x, y, pointer, button)
            }
        })
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun dispose() {
        super.dispose()
    }
}