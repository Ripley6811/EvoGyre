package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by Jay on 1/12/2016.
 */
public class AudioAssets {
    private static final String TAG = AudioAssets.class.getName();

    public static final Music INTRO_MUSIC = Gdx.audio.newMusic(Gdx.files.internal("audio/kalmankone_clip.ogg"));
    public static final Sound BULLET_PLINK = Gdx.audio.newSound(Gdx.files.internal("audio/hit_blip.ogg"));
    public static final Sound BULLET_FWAP = Gdx.audio.newSound(Gdx.files.internal("audio/bullet_fwap.ogg"));
    public static final Sound SMALL_EXPLOSION = Gdx.audio.newSound(Gdx.files.internal("audio/explosion_lower.ogg"));


    /**
     * Runs all sounds at a given volume.
     * (Does not play music.)
     *
     * @param volume
     */
    public static void testSounds(float volume) {
        BULLET_PLINK.play(volume);
        SMALL_EXPLOSION.play(volume);
    }

    /**
     * Plays all sounds numerous times at zero volume to force caching of
     * sound in HTML version of game.
     * Useful for HTML games to help load sounds faster.
     */
    public static void forceHtmlSoundCache() {
        float volume = 0f;
        testSounds(volume);
        testSounds(volume);
        testSounds(volume);
        testSounds(volume);
        testSounds(volume);
        testSounds(volume);
        testSounds(volume);
        testSounds(volume);
        testSounds(volume);
        testSounds(volume);
    }
}
