/*
 * Copyright (C) Art-_-master - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.run.cookie.run.game.services

import com.run.cookie.run.game.Config
import java.lang.Exception

interface ServicesController{
    fun isSignedIn(): Boolean
    fun signIn()
    fun signInSilently()
    fun signOut()
    fun submitScore(highScore: Long)
    fun showLeaderboard()
    fun getPlayerCenteredScores(callBack: CallBack)
    fun getTopScores(scoreType: Int, callBack: CallBack)
    fun unlockAchievement(achievement: Config.Achievement)
    fun incrementAchievement(achievement: Config.Achievement, value: Int)
    fun showAllAchievements()
    fun share(score: Int)
}

interface CallBack{
    fun success(list: List<String>)
    fun fail(exception: Exception, string: String)
}