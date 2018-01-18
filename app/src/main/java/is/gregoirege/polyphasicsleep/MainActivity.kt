package `is`.gregoirege.polyphasicsleep

import `is`.gregoirege.polyphasicsleep.dialogs.SettingsDialog
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar
import java.util.*
import kotlin.concurrent.timer

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun <T: ViewGroup> T.separator() {
    view {
        layoutParams = ViewGroup.MarginLayoutParams(matchParent, 1.dp).apply {
            val vert = 20.dp
            val horz = 0
            setMargins(horz, vert, horz, vert)
        }

        backgroundColorResource = R.color.separator
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        linearLayout {
            lparams(width = matchParent, height = matchParent)

            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL

            linearLayout {
                lparams(width = wrapContent, height = wrapContent) {
                    setMargins(0, 36.dp, 0, 0)
                    weight = 1f
                }

                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL

                // elements
                val currentTime = textView("00:22") {
                    textSize = 72f
                    textColor = 0xFFFFFFFF.toInt()
                    gravity = Gravity.CENTER_HORIZONTAL

                    setOnClickListener { _ ->
                        runOnUiThread {
                            showDialog()
                        }
                    }
                }

                val wakeUpTime = textView("00:42") {
                    textSize = 27f
                    gravity = Gravity.CENTER_HORIZONTAL

                    setTypeface(null, Typeface.ITALIC)

                    setOnClickListener { _ ->
                        runOnUiThread {
                            showDialog()
                        }
                    }
                }

                // update loop
                timer("update-loop", false, 0, 1000, {
                    val now = Calendar.getInstance().time
                    val then = Calendar.getInstance().apply { add(Calendar.MINUTE, 20) }.time

                    runOnUiThread {
                        currentTime.text = String.format("%1\$tH:%1\$tM", now)
                        wakeUpTime.text = String.format("%1\$tH:%1\$tM", then)
                    }
                })
            }

            themedButton(R.string.nap, R.style.Theme_AppCompat_Light) {
                setPadding(40.dp, 18.dp, 40.dp, 18.dp)

                textSize = 16f
                gravity = Gravity.CENTER_HORIZONTAL

                setTypeface(null, Typeface.BOLD)

                setOnClickListener { view ->
                    runOnUiThread {
                        snackbar(view, "Not implemented yet.")
                    }
                }
            }.lparams(width = wrapContent) {
                setMargins(0, 0, 0, 80.dp)
            }

            post {
                val colors = intArrayOf(0xFF061021.toInt(), 0xFF134d68.toInt(), 0xFF535b5e.toInt())

                background = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors).apply {
                    gradientType = GradientDrawable.RADIAL_GRADIENT
                    gradientRadius = window.decorView.height * 2f

                    setGradientCenter(0.5f, 1f)
                }
            }
        }
    }

    private fun showDialog() {
        val transaction = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("dialog")

        if (prev != null) {
            transaction.remove(prev)
        }

        transaction.addToBackStack(null)
        SettingsDialog().show(transaction, "dialog")
    }
}

