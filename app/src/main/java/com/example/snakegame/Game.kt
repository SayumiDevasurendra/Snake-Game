package com.example.snakegame

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

@Suppress("DEPRECATION")
class Game : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var HighestScoreText: TextView
    private var highestScore = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val board = findViewById<RelativeLayout>(R.id.board)
        val border = findViewById<RelativeLayout>(R.id.relativeLayout)
        val arrowPad = findViewById<LinearLayout>(R.id.arrowPad)
        val upButton = findViewById<Button>(R.id.up)
        val downButton = findViewById<Button>(R.id.down)
        val leftButton = findViewById<Button>(R.id.left)
        val rightButton = findViewById<Button>(R.id.right)
        val pauseButton = findViewById<Button>(R.id.pause)
        val newGame = findViewById<Button>(R.id.new_game)
        val resumeButton = findViewById<Button>(R.id.resume)
        val playAgainButton = findViewById<Button>(R.id.playAgain)
        val finalScore = findViewById<Button>(R.id.finalScoreDisplay)
        val playScore = findViewById<Button>(R.id.playingScore)
        val meat = ImageView(this)
        val snake = ImageView(this)
        val snakeSegments = mutableListOf(snake) //where each part of the snake is located on the  board.
        val handler = Handler()
        var delayMillis = 30L // update snake position every 30 milliseconds
        var currentDirection = "right" //move to the Right by default
        var increaseScore = 0
        val snakeSpeed = 8
        HighestScoreText = findViewById(R.id.highestScoreField)

        //shared preference
        sharedPreferences = getSharedPreferences("GamePreferences", Context.MODE_PRIVATE)
        highestScore = sharedPreferences.getInt("highestScore", 0)
        displayHighestScore()

        board.visibility = View.INVISIBLE
        playAgainButton.visibility = View.INVISIBLE
        finalScore.visibility = View.INVISIBLE
        playScore.visibility = View.INVISIBLE

        //new game button click
        newGame.setOnClickListener {
            board.visibility = View.VISIBLE
            newGame.visibility = View.INVISIBLE
            resumeButton.visibility = View.INVISIBLE
            playScore.visibility = View.VISIBLE

            snake.setImageResource(R.drawable.snake)

            //set snake layout parameters to wrap its content
            snake.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            board.addView(snake)    //add snake ImageView to the board layout
            snakeSegments.add(snake) // add the new snake segment to the list

            // Record initial snake's X and Y positions
            var snakeX = snake.x
            var snakeY = snake.y

            meat.setImageResource(R.drawable.meat)

            // set meat layout parameters to wrap its content
            meat.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            board.addView(meat)

            val random = Random()
            val randomX = random.nextInt(801) - 400 // x-side (horizontal) between -400 to 400
            val randomY = random.nextInt(801) - 400 // y-side (vertical) between -400 to 400

            // set meat's x/y positions
            meat.x = randomX.toFloat()
            meat.y = randomY.toFloat()

            fun checkFoodCollision() {
                val collisionThreshold = 50 // maximum distance allowed between the snake's head and the meat

                val distance = sqrt((snake.x - meat.x).pow(2) + (snake.y - meat.y).pow(2))  //calc real distance between head and the meat

                if (distance < collisionThreshold) {

                    val newSnake = ImageView(this) // add a new ImageView for the additional snake segment
                    newSnake.setImageResource(R.drawable.snake)

                    //set layout parameters for the new snake segment
                    newSnake.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    board.addView(newSnake)
                    snakeSegments.add(newSnake)

                    val randomX = random.nextInt(board.width - meat.width)
                    val randomY = random.nextInt(board.height - meat.height - HighestScoreText.height)

                    // set new random x/y positions for the meat
                    meat.x = randomX.toFloat()
                    meat.y = (randomY + HighestScoreText.height).toFloat()

                    delayMillis-- // reduce delay value by 1 to speed up the game
                    increaseScore++

                    playScore.text =   getString(R.string.score_format, increaseScore)

                    updateHighestScore(increaseScore)
                }
            }

            val runnable = object : Runnable {  //runnable object to control the snake movement
                override fun run() {

                    for (i in snakeSegments.size - 1 downTo 1) { //update the position of each snake segment except for the head
                        snakeSegments[i].x = snakeSegments[i - 1].x
                        snakeSegments[i].y = snakeSegments[i - 1].y // move the snake segment vertically
                    }

                    when (currentDirection) {
                        "up" -> {
                            snakeY -= snakeSpeed
                            if (snakeY < -490) { // check if the snake goes off the top of the board
                                snakeY = -490f
                                border.setBackgroundColor(resources.getColor(R.color.accent_colour2))
                                playAgainButton.visibility = View.VISIBLE
                                currentDirection = "pause"
                                arrowPad.visibility = View.INVISIBLE

                                finalScore.text = getString(R.string.your_score, increaseScore)
                                finalScore.visibility = View.VISIBLE
                                playScore.visibility = View.INVISIBLE
                            }

                            snake.translationY = snakeY
                        }
                        "down" -> {
                            snakeY += snakeSpeed
                            val maxY = board.height / 2 - snake.height + 30 // calc the maximum y space
                            if (snakeY > maxY) {
                                snakeY = maxY.toFloat()
                                border.setBackgroundColor(resources.getColor(R.color.accent_colour2))
                                playAgainButton.visibility = View.VISIBLE
                                currentDirection = "pause"
                                arrowPad.visibility = View.INVISIBLE

                                finalScore.text = getString(R.string.your_score, increaseScore)
                                finalScore.visibility = View.VISIBLE
                                playScore.visibility = View.INVISIBLE
                            }
                            snake.translationY = snakeY
                        }
                        "left" -> {
                            snakeX -= snakeSpeed
                            if (snakeX < -490) {
                                snakeX = -490f
                                border.setBackgroundColor(resources.getColor(R.color.accent_colour2))
                                playAgainButton.visibility = View.VISIBLE
                                currentDirection = "pause"
                                arrowPad.visibility = View.INVISIBLE
                                finalScore.text = getString(R.string.your_score, increaseScore)
                                finalScore.visibility = View.VISIBLE
                                playScore.visibility = View.INVISIBLE
                            }
                            snake.translationX = snakeX
                        }
                        "right" -> {
                            snakeX += snakeSpeed
                            val maxX = board.height / 2 - snake.height + 30 // calc the maximum y space
                            if (snakeX > maxX) {
                                snakeX = maxX.toFloat()
                                border.setBackgroundColor(resources.getColor(R.color.accent_colour2))
                                playAgainButton.visibility = View.VISIBLE
                                currentDirection = "pause"
                                arrowPad.visibility = View.INVISIBLE

                                finalScore.text = getString(R.string.your_score, increaseScore)
                                finalScore.visibility = View.VISIBLE
                                playScore.visibility = View.INVISIBLE
                            }
                            snake.translationX = snakeX
                        }
                        "pause" -> {
                            snakeX += 0
                            snake.translationX = snakeX
                        }
                    }
                    checkFoodCollision()    // check for collision between the snake and the meat
                    handler.postDelayed(this, delayMillis)  // make the next iteration after a delay
                }
            }

            handler.postDelayed(runnable, delayMillis)  // start the snake movement loop with a delay

            //onClicks to change the currentDirection
            upButton.setOnClickListener {
                currentDirection = "up"
            }
            downButton.setOnClickListener {
                currentDirection = "down"
            }
            leftButton.setOnClickListener {
                currentDirection = "left"
            }
            rightButton.setOnClickListener {
                currentDirection = "right"
            }
            pauseButton.setOnClickListener {
                currentDirection = "pause"
                board.visibility = View.INVISIBLE
                newGame.visibility = View.VISIBLE
                resumeButton.visibility = View.VISIBLE
            }
            resumeButton.setOnClickListener {
                currentDirection = "right"
                board.visibility = View.VISIBLE
                newGame.visibility = View.INVISIBLE
                resumeButton.visibility = View.INVISIBLE
            }
            playAgainButton.setOnClickListener {
                recreate()
            }
        }
    }

    private fun displayHighestScore() {
        HighestScoreText.text = getString(R.string.highScore, highestScore)
    }

    private fun updateHighestScore(score: Int) {
        if (score > highestScore) {
            highestScore = score
            with(sharedPreferences.edit()) {
                putInt("highestScore", highestScore)
                apply()
            }
            displayHighestScore()
        }
    }
}