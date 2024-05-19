package com.example.dodgerabbit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import java.util.*

class GameView(context: Context) : View(context) {
    private val background: Bitmap
    private val ground: Bitmap
    private val rabbit: Bitmap
    private val rectBackground: Rect
    private val rectGround: Rect
    private val handler: Handler
    private val runnable: Runnable
    private val textPaint = Paint()
    private val healthPaint = Paint()
    private val random: Random
    private val spikes: ArrayList<Spike>
    private val explosions: ArrayList<Explosion>
    private val updateMillis: Long = 30

    private var points = 0
    private var life = 3
    private var rabbitX: Float
    private var rabbitY: Float
    private var oldX: Float = 0f
    private var oldRabbitX: Float = 0f

    init {
        background = BitmapFactory.decodeResource(resources, R.drawable.background)
        ground = BitmapFactory.decodeResource(resources, R.drawable.ground)
        rabbit = BitmapFactory.decodeResource(resources, R.drawable.rabbit)

        val display = (context as? Activity)?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)
        dWidth = size.x
        dHeight = size.y

        rectBackground = Rect(0, 0, dWidth, dHeight)
        rectGround = Rect(0, dHeight - ground.height, dWidth, dHeight)

        handler = Handler()
        runnable = Runnable { invalidate() }

        textPaint.color = Color.rgb(255, 165, 0)
        textPaint.textSize = TEXT_SIZE
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.typeface = ResourcesCompat.getFont(context, R.font.kenney_blocks)

        healthPaint.color = Color.GREEN

        random = Random()
        rabbitX = (dWidth / 2 - rabbit.width / 2).toFloat()
        rabbitY = (dHeight - ground.height - rabbit.height).toFloat()

        spikes = ArrayList()
        explosions = ArrayList()

        // Add spikes to the game
        for (i in 0..2) {
            spikes.add(Spike(context))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw background and ground
        canvas.drawBitmap(background, null, rectBackground, null)
        canvas.drawBitmap(ground, null, rectGround, null)
        canvas.drawBitmap(rabbit, rabbitX, rabbitY, null)

        // Draw spikes and handle spike movement
        spikes.forEachIndexed { index, spike ->
            spike.getSpike(spike.spikeFrame)?.let {
                canvas.drawBitmap(it, spike.spikeX.toFloat(), spike.spikeY.toFloat(), null)
            }
            spike.spikeFrame = (spike.spikeFrame + 1) % 3
            spike.spikeY += spike.spikeVelocity

            // Handle spike reaching the ground
            if (spike.spikeY + spike.spikeHeight >= dHeight - ground.height) {
                points += 10
                explosions.add(Explosion(context).apply {
                    explosionX = spike.spikeX
                    explosionY = spike.spikeY
                })
                spike.resetPosition()
            }

            // Check collision with rabbit
            if (isCollision(spike, rabbitX, rabbitY)) {
                life--
                spike.resetPosition()
                if (life == 0) {
                    navigateToGameOver()
                }
            }
        }

        // Draw explosions
        val iterator = explosions.listIterator()
        while (iterator.hasNext()) {
            val explosion = iterator.next()
            canvas.drawBitmap(
                explosion.getExplosion(explosion.explosionFrame)!!,
                explosion.explosionX.toFloat(),
                explosion.explosionY.toFloat(),
                null
            )
            explosion.explosionFrame++
            if (explosion.explosionFrame > 3) {
                iterator.remove()
            }
        }

        // Update health bar color based on remaining lives
        when (life) {
            2 -> healthPaint.color = Color.YELLOW
            1 -> healthPaint.color = Color.RED
        }

        // Draw health bar
        canvas.drawRect(
            dWidth - 200f,
            30f,
            dWidth - 200 + 60 * life.toFloat(),
            80f,
            healthPaint
        )

        // Draw points
        canvas.drawText("$points", 20f, TEXT_SIZE, textPaint)

        // Schedule next draw
        handler.postDelayed(runnable, updateMillis)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Check whether the tap is on the left or right half of the screen
                if (event.x < dWidth / 2) {
                    // Tap is on the left half, move the rabbit left
                    rabbitX -= rabbit.width
                    if (rabbitX < 0) {
                        rabbitX = 0f
                    }
                } else {
                    // Tap is on the right half, move the rabbit right
                    rabbitX += rabbit.width
                    if (rabbitX > dWidth - rabbit.width) {
                        rabbitX = (dWidth - rabbit.width).toFloat()
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // Calculate the horizontal shift based on the swipe movement
                val shift = oldX - event.x

                // Update the rabbit's position based on the calculated shift
                rabbitX = (oldRabbitX - shift).coerceIn(0f, dWidth - rabbit.width.toFloat())
            }
        }
        return true
    }


    private fun isCollision(spike: Spike, rabbitX: Float, rabbitY: Float): Boolean {
        val spikeRect = Rect(
            spike.spikeX,
            spike.spikeY,
            spike.spikeX + spike.spikeWidth,
            spike.spikeY + spike.spikeHeight
        )

        val rabbitRect = Rect(
            rabbitX.toInt(),
            rabbitY.toInt(),
            rabbitX.toInt() + rabbit.width,
            rabbitY.toInt() + rabbit.height
        )

        return Rect.intersects(spikeRect, rabbitRect)
    }

    private fun navigateToGameOver() {
        val intent = Intent(context, GameOver::class.java).apply {
            putExtra("points", points)
        }
        context.startActivity(intent)
        (context as? Activity)?.finish()
    }

    companion object {
        const val TEXT_SIZE = 120f
        var dWidth: Int = 0
        var dHeight: Int = 0
    }
}
