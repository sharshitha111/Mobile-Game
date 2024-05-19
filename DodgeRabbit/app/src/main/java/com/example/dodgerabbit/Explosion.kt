package com.example.dodgerabbit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Explosion(context: Context) {
    var explosion = arrayOfNulls<Bitmap>(4)
    @JvmField
    var explosionFrame = 0
    @JvmField
    var explosionX = 0
    @JvmField
    var explosionY = 0

    init {
        explosion[0] = BitmapFactory.decodeResource(context.resources, R.drawable.explode0)
        explosion[1] = BitmapFactory.decodeResource(context.resources, R.drawable.explode1)
        explosion[2] = BitmapFactory.decodeResource(context.resources, R.drawable.explode2)
        explosion[3] = BitmapFactory.decodeResource(context.resources, R.drawable.explode3)
    }

    fun getExplosion(explosionFrame: Int): Bitmap? {
        return explosion[explosionFrame]
    }
}
