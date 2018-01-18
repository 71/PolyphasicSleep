package `is`.gregoirege.polyphasicsleep.dialogs

import `is`.gregoirege.polyphasicsleep.*
import android.app.Dialog
import android.content.Context
import android.support.design.widget.BottomSheetDialogFragment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.themedCardView
import org.jetbrains.anko.support.v4.UI
import java.util.*


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

                        themedImageButton(R.drawable.ic_plus, R.style.NapButton) {
                            setOnClickListener {
                                TimePickerFragment().show(activity.supportFragmentManager, "time-picker")
                            }
                        }
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
        private val naps = context.db.use {
            schedule.orderBy("hour").parseList(NapParser())
        }

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
                    gravity = Gravity.END

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