package org.of8.straightback

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

    private val mMainButton: FloatingActionButton
        get() = findViewById(R.id.startButton) as FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.elevation = 0F
        setContentView(R.layout.activity_main)
        update(button = mMainButton, started = isServiceRunning())
    }

    fun onButtonClick(view: View) {
        switchButton(isServiceRunning = isServiceRunning())
    }

    private fun switchButton(isServiceRunning: Boolean) {
        if (isServiceRunning) {
            NotificationEventReceiver().cancelAlarm(this)
        } else {
            NotificationEventReceiver().setupAlarm(this)
        }
        update(button = mMainButton, started = !isServiceRunning)
    }

    private fun update(button: ImageButton, started: Boolean) {
        button.setImageResource(buttonImage(started))
    }

    private fun buttonImage(started: Boolean) : Int {
        return if (started) {
            R.drawable.ic_stop_white_36dp
        } else {
            R.drawable.ic_play_arrow_white_36dp
        }
    }

    private fun isServiceRunning(): Boolean {
        return NotificationEventReceiver().isAlarmRunning(context = this)
    }

}
