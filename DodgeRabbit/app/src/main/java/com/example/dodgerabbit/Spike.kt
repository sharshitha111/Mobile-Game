package com.example.dodgerabbit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.Random

class Spike(context: Context) {
    var spike = arrayOfNulls<Bitmap>(3)
    var spikeFrame = 0
    var spikeX = 0
    var spikeY = 0
    var spikeVelocity = 0
    var random: Random

    init {
        spike[0] = BitmapFactory.decodeResource(context.resources, R.drawable.spike0)
        spike[1] = BitmapFactory.decodeResource(context.resources, R.drawable.spike1)
        spike[2] = BitmapFactory.decodeResource(context.resources, R.drawable.spike2)
        random = Random()
        resetPosition()
    }

    fun getSpike(spikeFrame: Int): Bitmap? {
        return spike[spikeFrame]
    }

    val spikeWidth: Int
        get() = spike[0]!!.getWidth()
    val spikeHeight: Int
        get() = spike[0]!!.getHeight()

    fun resetPosition() {
        spikeX = random.nextInt(GameView.dWidth - spikeWidth)
        spikeY = -200 + random.nextInt(600) * -1
        spikeVelocity = 35 + random.nextInt(16)
    }
}
