package com.mygdx.game.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.mygdx.game.Config
import com.mygdx.game.Config.Achievement.*
import com.mygdx.game.DebugUtils
import com.mygdx.game.actors.game.*
import com.mygdx.game.actors.game.cookie.*
import com.mygdx.game.api.AnimationType
import com.mygdx.game.api.Callback
import com.mygdx.game.api.Scrollable
import com.mygdx.game.api.WallActor
import com.mygdx.game.data.Assets
import com.mygdx.game.managers.AudioManager
import com.mygdx.game.managers.ScreenManager
import com.mygdx.game.managers.ScreenManager.Param.*
import com.mygdx.game.managers.ScreenManager.Screens.GAME_OVER
import com.mygdx.game.managers.VibrationManager
import com.mygdx.game.managers.VibrationManager.VibrationType.*
import com.mygdx.game.services.ServicesController
import kotlin.random.Random
import com.mygdx.game.actors.Shadow as SceneShadow


class GameWorld(private val manager: AssetManager) {

    val stage = Stage(ScreenViewport())

    private val background = Background(manager)
    private val table = Table(manager, 240f)
    private val window = Window(manager, 270f)
    private val city = City(manager, window)
    private val flower = FlowerInPot(manager, window)
    private val cookie = Cookie(manager, table.worktopY, Config.WIDTH_GAME / 2)
    private val jumpDust = JumpDust(manager, cookie)
    private val fallDust = FallDust(manager, cookie)
    private val sunglasses = CookieItem(manager, cookie, Assets.CookieAtlas.SUNGLASSES)
    private val hat = CookieItem(manager, cookie, Assets.CookieAtlas.HAT)
    private val boots = CookieItem(manager, cookie, Assets.CookieAtlas.BOOTS)
    private val belt = CookieItem(manager, cookie, Assets.CookieAtlas.BELT)
    private val gun = CookieItem(manager, cookie, Assets.CookieAtlas.GUN)
    private val bullets = CookieItem(manager, cookie, Assets.CookieAtlas.BULLETS)
    private val shot = Shot(manager, cookie)
    private val cookieShadow = CookieShadow(manager, cookie)
    private val shadow = Shadow(manager)
    private val cupboard = Cupboard(manager, 510f)
    private val score = Score(manager)
    private val arm = Arm(manager, cookie)
    private val items = TableItems(manager, table, cookie)
    private val sceneShadow = SceneShadow(manager)
    val actors: Array<Actor> = Array()
    private val wallActors: Array<WallActor> = Array()

    private var touchable = true
    private var isGameOver = false
    private var isWinGame = false

    init {
        actors.addAll(background, cupboard, shadow, city, window, flower, table)
        if (!Config.Debug.EMPTY_TABLE.state) actors.addAll(items.getActors())
        actors.addAll(cookieShadow, cookie, jumpDust, sunglasses, hat, boots, belt, gun, bullets, fallDust, arm, score, shot, sceneShadow)
        wallActors.addAll(window, cupboard)
        cookie.listeners.addAll(jumpDust, fallDust)

        addActorsToStage()
        stopMoveAllActors()
        startInitAnimation()
        changeScore()
        controlWallActors()

        if (Config.Debug.PERIODIC_JUMP.state) {
            DebugUtils.startPeriodicTimer(1f, 2f) {
                cookie.startJumpForce()
            }
        }


        stage.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (touchable && isWinGame.not()) {
                    cookie.startJumpForce()
                    AudioManager.play(AudioManager.SoundApp.JUMP)
                    VibrationManager.cancel()
                }
                return super.touchDown(event, x, y, pointer, button)
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (touchable && isWinGame.not()) {
                    cookie.endJumpForce()
                }
                super.touchUp(event, x, y, pointer, button)
            }
        }
        )
        Gdx.input.inputProcessor = stage
    }

    private fun addActorsToStage() {
        for (actor in actors) {
            stage.addActor(actor)
        }
    }

    private fun startInitAnimation() {
        sceneShadow.animate(AnimationType.SHOW_ON_SCENE, Runnable {
            cookie.animate(AnimationType.SHOW_ON_SCENE, Runnable {
                startMoveAllActors()
                (wallActors.first() as Scrollable).runMove()
            })
            arm.animate(AnimationType.SHOW_ON_SCENE, Runnable {
                arm.startRepeatableMove()
            })
        })
    }

    private fun changeScore() {
        items.getActors().forEach { controlScore(it) }
    }

    private fun controlWallActors() {
        val random = Random(500)
        for (i in 0 until wallActors.size) {
            val actor = wallActors[i]
            if (i < wallActors.size - 1) actor.nextActor = wallActors.get(i + 1) as Actor
            actor.distancePastListener = {
                (actor.nextActor as WallActor).resetState()
                (actor.nextActor as WallActor).distance = random.nextInt(900, 1200)
            }
        }
        wallActors.apply {
            this.last().nextActor = this.first() as Actor
            this.first().distance = random.nextInt(900, 1200)
        }
    }

    private fun controlScore(actor: RandomTableItem) {
        val controller = ScreenManager.globalParameters[SERVICES_CONTROLLER] as ServicesController
        actor.callbackGoThrough = object : Callback {
            override fun call() {
                score.scoreNum++
                score.animate(AnimationType.SCORE_INCREASE)
                VibrationManager.vibrate()
                controlItemsScrollSpeed()
                when (score.scoreNum) {
                    SUNGLASSES.score -> {
                        sunglasses.animate(AnimationType.SHOW_ON_SCENE, Runnable {
                            controller.unlockAchievement(SUNGLASSES)
                        })
                    }
                    HAT.score -> {
                        hat.animate(AnimationType.SHOW_ON_SCENE, Runnable {
                            controller.unlockAchievement(HAT)
                        })
                    }
                    BOOTS.score -> {
                        boots.animate(AnimationType.SHOW_ON_SCENE, Runnable {
                            controller.unlockAchievement(BOOTS)
                        })
                    }
                    BELT.score -> {
                        belt.animate(AnimationType.SHOW_ON_SCENE, Runnable {
                            controller.unlockAchievement(BELT)
                        })
                    }
                    GUN.score -> {
                        gun.animate(AnimationType.SHOW_ON_SCENE, Runnable {
                            controller.unlockAchievement(GUN)
                        })
                    }
                    BULLETS.score -> {
                        bullets.animate(AnimationType.SHOW_ON_SCENE, Runnable {
                            controller.unlockAchievement(BULLETS)
                        })
                    }
                    FINISH_GAME.score -> {
                        items.isStopGenerate = true
                    }
                }
            }
        }
    }

    private fun controlItemsScrollSpeed() {
        if (score.scoreNum % 10 == 0) {
            Config.currentScrollSpeed = Config.currentScrollSpeed + Config.SPEED_INCREASE_STEP
            foreEachActor {
                if (it is Scrollable) it.updateSpeed()
            }
        }
    }


    private fun stopMoveAllActors() {
        for (actor in actors) {
            if (actor is Scrollable && actor !is Cookie) {
                actor.stopMove()
            }
        }
    }

    private fun startMoveAllActors() {
        foreEachActor {
            if (it is Scrollable && it !is WallActor) it.runMove()
        }
    }

    private fun foreEachActor(callbackData: (Actor) -> Unit) {
        for (actor in actors) {
            callbackData.invoke(actor)
        }
    }

    fun update(delta: Float) {
        stage.act(delta)
        checkContactCookieAndHand()
        if (items.isStopGenerate) controlWinning()

    }

    private fun controlWinning() {
        if (items.isAllObjectsScored()) isWinGame = true
        if (items.isAllObjectLeft()) {
            cookie.win()
            arm.isWinningAnimation = true
            stopMoveAllActors()

            if (cookie.x < cookie.startX) return
            actors.filterIsInstance<CookieItem>().forEach {
                (it as Actor).remove()
            }
            shot.animate(AnimationType.SHOW_ON_SCENE, Runnable {
                sceneShadow.invertColor()
                sceneShadow.animate(AnimationType.SHOW_ON_SCENE)
                VibrationManager.vibrate(BOOM)
                arm.animate(AnimationType.HIDE_FROM_SCENE, Runnable {
                    val shadow = SceneShadow(manager)
                    stage.addActor(shadow)
                    shadow.animate(AnimationType.HIDE_FROM_SCENE, Runnable {
                        ScreenManager.setScreen(GAME_OVER, Pair(SCORE, score.scoreNum), Pair(WAS_WIN_GAME, true))
                    })
                })
            })
        }
    }

    private fun checkContactCookieAndHand() {
        if (arm.x + arm.width >= cookie.x && isGameOver.not()) {
            isGameOver = true
            touchable = false
            stopMoveAllActors()
            cookie.caught()
            arm.actions.clear()
            arm.isGameOverAnimation = true
            VibrationManager.cancel()
            arm.animate(AnimationType.COOKIE_CATCH, Runnable {
                actors.filterIsInstance<CookieItem>().forEach {
                    it.animate(AnimationType.HIDE_FROM_SCENE)
                    VibrationManager.vibrate(ACTOR_CATCH)
                }
                arm.animate(AnimationType.HIDE_FROM_SCENE, Runnable {
                    ScreenManager.setScreen(GAME_OVER, Pair(SCORE, score.scoreNum))
                })
            })
            AudioManager.stopAll()
        }
    }

}