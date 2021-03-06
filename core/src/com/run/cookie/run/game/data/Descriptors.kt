/*
 * Copyright (C) Art-_-master - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.run.cookie.run.game.data

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas

object Descriptors {

    private val textureParams = TextureLoader.TextureParameter().apply {
        genMipMaps = true
        loadedCallback = AssetLoaderParameters.LoadedCallback { assetManager, fileName, _ ->
            run {
                assetManager.get<Texture>(fileName).setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.Nearest)
            }
        }
    }

    val background = AssetDescriptor(Assets.BackgroundTexture.NAME, Texture::class.java, textureParams)
    val gameOverBackground = AssetDescriptor(Assets.GameOverBackground.NAME, Texture::class.java, textureParams)
    val comics = AssetDescriptor(Assets.ComicsAtlas.NAME, TextureAtlas::class.java)
    val cookie = AssetDescriptor(Assets.CookieAtlas.NAME, TextureAtlas::class.java)
    val environment = AssetDescriptor(Assets.EnvironmentAtlas.NAME, TextureAtlas::class.java)
    val menu = AssetDescriptor(Assets.MainMenuAtlas.NAME, TextureAtlas::class.java)
    val progressBar = AssetDescriptor(Assets.ProgressAtlas.NAME, TextureAtlas::class.java)

    val scoreFont = AssetDescriptor(FontParam.SCORE.fontName, BitmapFont::class.java, FontParam.SCORE.get())
    val currentScoreFont = AssetDescriptor(FontParam.CURRENT_SCORE.fontName, BitmapFont::class.java, FontParam.CURRENT_SCORE.get())
    val bestScoreFont = AssetDescriptor(FontParam.BEST_SCORE.fontName, BitmapFont::class.java, FontParam.BEST_SCORE.get())
}