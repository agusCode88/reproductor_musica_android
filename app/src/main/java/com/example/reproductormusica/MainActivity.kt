package com.example.reproductormusica

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.reproductormusica.AppConstant.Companion.CURRENT_SONG_INDEX
import com.example.reproductormusica.AppConstant.Companion.LOG_MAIN_ACTIVITY
import com.example.reproductormusica.AppConstant.Companion.MEDIA_PLAYER_POSITION
import com.example.reproductormusica.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private var position:Int = 0
    private lateinit var currentSong: Song
    private var currentSongIndex:Int = 0
    private var isPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(LOG_MAIN_ACTIVITY,"onCreate()")
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentSong = AppConstant.songs[currentSongIndex]

        savedInstanceState?.let {
            position = it.getInt(MEDIA_PLAYER_POSITION)
            currentSongIndex = it.getInt(CURRENT_SONG_INDEX)
            currentSong = AppConstant.songs[currentSongIndex]
        }

        updateUiSong()

        // Forma nueva
        binding.playPauseButton.setOnClickListener {playOrPauseMusic()}
        binding.playNextButton.setOnClickListener { playNextSong() }
        binding.playPreviousButton.setOnClickListener { playPreviousSong() }

        // Forma antigua
       /* binding.playPauseButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                playOrPauseMusic()
            }})*/
    }

    override fun onStart() {
        super.onStart()
        Log.i(LOG_MAIN_ACTIVITY,"onStart()")
        mediaPlayer = MediaPlayer.create(this,currentSong.audioResId)
        if(isPlaying)
            mediaPlayer?.start()

    }

    override fun onResume() {
        super.onResume()
        Log.i(LOG_MAIN_ACTIVITY,"onResume()")

        if(isPlaying){
            mediaPlayer?.start()
            isPlaying = !isPlaying
        }
        mediaPlayer?.seekTo(position)

    }

    override fun onPause() {
        super.onPause()
        Log.i(LOG_MAIN_ACTIVITY,"onPause()")
        if( mediaPlayer != null )
            position = mediaPlayer!!.currentPosition

        mediaPlayer?.pause()
       // isPlaying = false

    }

    override fun onStop() {
        super.onStop()
        Log.i(LOG_MAIN_ACTIVITY,"onStop()")
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(LOG_MAIN_ACTIVITY,"onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(LOG_MAIN_ACTIVITY,"onDestroy()")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MEDIA_PLAYER_POSITION,position)
        outState.putInt(CURRENT_SONG_INDEX,currentSongIndex)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        position = savedInstanceState.getInt(MEDIA_PLAYER_POSITION)
        currentSongIndex = savedInstanceState.getInt(CURRENT_SONG_INDEX)
        currentSong = AppConstant.songs[currentSongIndex]
        mediaPlayer?.seekTo(position)
        mediaPlayer?.start()

    }

    /**
     * ********************* FUNCIONES DE LA ACTIVITY ************************************************
     */

    private fun updateUiSong(){
        binding.titleTextView.text = currentSong.title
        binding.albumCoverImageView.setImageResource(currentSong.imageResId)
        updatePlayPauseButton()
    }

    private fun playOrPauseMusic(){
        if(isPlaying){
            mediaPlayer?.pause()
        }else{
            mediaPlayer?.start()
        }
        isPlaying = !isPlaying
        updatePlayPauseButton()
    }

    private fun updatePlayPauseButton(){
        binding.playPauseButton.text = if(isPlaying) "Pause" else "Play"
    }

    private fun playNextSong(){
        currentSongIndex = (currentSongIndex + 1) % AppConstant.songs.size
        currentSong = AppConstant.songs[currentSongIndex]
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this,currentSong.audioResId)
        mediaPlayer?.start()
        isPlaying = true
        updateUiSong()
    }

    private fun playPreviousSong() {
        // Algoritmo para obtener el indice y hacer una lista circular
        //cancion anterior - tamaño lista de canciones pra que siempre sea positivo
        //% devuelve un número positico si el dividendo es negativo
        currentSongIndex = (currentSongIndex - 1 + AppConstant.songs.size) % AppConstant.songs.size
        currentSong = AppConstant.songs[currentSongIndex]
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, currentSong.audioResId)
        mediaPlayer?.start()
        isPlaying = true
        updateUiSong()
    }
}