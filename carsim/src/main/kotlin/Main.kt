import pt.isel.canvas.*
import kotlin.concurrent.thread
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

data class game(var rpm: Int, var redline: Int, var tol: Int, var idle : Int, var on: Boolean)
data class point(var x: Double, var y: Double)

var curr = game(1000, 7500,100, 950, false)
const val W = 1000
const val H = 800


const val Wret = 600
const val Hret = 200
const val SLIDER_WIDTH = 400
const val SLIDER_HEIGHT = 20
const val SLIDER_PADDING = 10
val arena = Canvas(W, H, 0xFFFFFF)

fun main(args: Array<String>) {
    arena.drawImage("tach.png", 0, 0, W, H)
    drawrevpointer(curr.rpm)

    arena.drawText(100, 100, "Turning on...",BLACK,25)
    var i = 0;
    var next = 0;

    arena.onTimeProgress(15){
        if(curr.on == false){
            if(i > curr.redline){
                if(next > 0){
                    curr.on = true
                }
                next++

            }
            if(i < 0){
                curr.on = true
            }
            if(next == 0){
                i+=50
            }else{
                i-=50
            }
            arena.drawImage("tach.png", 0, 0, W, H)
            drawrevpointer(i)
            arena.drawText(100, 100, "Turning on...",BLACK,25)

        }else{
            arena.drawSlider(Wret / 2 - SLIDER_WIDTH / 2, Hret / 2 - SLIDER_HEIGHT / 2, SLIDER_WIDTH, SLIDER_HEIGHT, curr.rpm) { newValue ->

                if(curr.rpm < curr.redline){curr.rpm +=( newValue * 1.5).toInt()}
                else{
                    curr.rpm -= curr.tol

                }
                println("newvalue: ${newValue}")
            }
            if(curr.rpm > curr.idle){
                curr.rpm -= 51
            }
            if(curr.rpm < curr.idle){
                curr.rpm += 16
            }

            arena.erase()
            arena.drawImage("tach.png", 0, 0, W, H)
            drawrevpointer(curr.rpm)
            if(curr.rpm > curr.redline){
                revlimlight(true)
            }
            println(curr.rpm)
        }

    }

    }



fun revlimlight(ison: Boolean){
    println("RANDADNADANADN")
    if(ison){
        arena.drawCircle(100,100,25,RED)
    }
}


fun Canvas.drawSlider(x: Int, y: Int, width: Int, height: Int, value: Int, onChange: (Int) -> Unit) {
    if(mouse.down){
        val mouseX = mouse.x

        var newValue = min(8000, max(0, ((mouseX - x) * 8000) / width))
            newValue = newValue/80
        onChange(newValue)

        drawRect(x, y, width, height)
        drawRect(x + SLIDER_PADDING, y + SLIDER_PADDING / 2, min(width - SLIDER_PADDING, (width * value) / 8000), height - SLIDER_PADDING, BLACK)

    }
}


fun toRad(degrees: Int): Double{
    return (degrees * 3.14159265359)/180;
}


fun drawrevpointer(RPM: Int){
    val rpmScale = when {
        RPM >= 0 && RPM < 1000 -> 215 - (RPM - 0) * (215 - 180) / (1000 - 0)
        RPM >= 1000 && RPM < 4000 -> 180 - (RPM - 1000) * (180 - 90) / (4000 - 1000)
        RPM >= 4000 && RPM < 7000 -> 90 - (RPM - 4000) * (90 - 0) / (7000 - 4000)
        RPM >= 7000 && RPM < 8000 -> 0 - (RPM - 7000) * (0 - (-35)) / (8000 - 7000)
        else -> -35
    }
    val angleRad = toRad(rpmScale)
    val pcircle = point(500 + (320 * cos(angleRad)), 430 - (320 * sin(angleRad)))
    arena.drawLine(500, 430, pcircle.x.toInt(), pcircle.y.toInt(), RED, 10)
}

fun delay(milliseconds: Long){
    Thread.sleep(milliseconds)
}