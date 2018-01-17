package `is`.gregoirege.polyphasicsleep

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.tintedImageButton
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.cardview.v7.themedCardView
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.support.v4.UI
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

class SettingsDialog : BottomSheetDialogFragment() {
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)

        UI {
            dialog?.setContentView(
                linearLayout {
                    lparams(width = matchParent, height = wrapContent) {
                        val padding = 20.dp

                        setPadding(padding, padding, padding, padding)
                    }

                    backgroundColor = 0xFF000000.toInt()
                    orientation = LinearLayout.VERTICAL

                    // schedule
                    linearLayout {
                        themedTextView(R.string.schedule, R.style.SectionHeader).lparams(weight = 1f)
                        themedImageButton(R.drawable.ic_plus, R.style.NapButton)
                    }
                    listView {
                        adapter = Adapter(context)
                    }

                    separator()

                    // settings
                    themedTextView(R.string.settings, R.style.SectionHeader)
                    switch {
                        text = context.getString(R.string.notifications_switch)
                        textColorResource = R.color.secondary_text_default_material_dark
                    }

                    separator()

                    // about
                    themedTextView(R.string.about, R.style.SectionHeader)
                    textView(R.string.about_text)
                }
            )
        }
    }

    private class Adapter(val context: Context) : BaseAdapter() {
        private val naps = arrayOf(NapDefinition(8, 30, 20))

        override fun getCount(): Int = naps.count()
        override fun getItem(position: Int): Any = naps[position]
        override fun getItemId(position: Int): Long = naps[position].hashCode().toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View = context.UI {
            val nap = naps[position]

            themedCardView(R.style.HourCard) {
                val start = Calendar.getInstance().apply {
                    set(0, 0, 0, nap.hour, nap.minute, 0)
                }
                val end = (start.clone() as Calendar).apply { add(Calendar.MINUTE, nap.durationInMinutes)  }
                val title = String.format("%1\$tH:%1\$tM - %2\$tH:%2\$tM", start.time, end.time)

                linearLayout {
                    gravity = Gravity.RIGHT

                    textView(title) {
                        textSize = 20f
                        textColorResource = R.color.primary_material_light
                    }.lparams(weight = 1f)

                    themedImageButton(R.drawable.ic_pencil, R.style.NapButton)
                    themedImageButton(R.drawable.ic_delete, R.style.NapButton)
                }
            }
        }.view
    }
}
