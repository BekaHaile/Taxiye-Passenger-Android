package product.clicklabs.jugnoo.newui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.newui.utils.customview.ScratchView


class RewardsDialog : DialogFragment() {
    private lateinit var rootView : View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    companion object {
        @JvmStatic
        fun newInstance(): RewardsDialog {
            val rewardsDialog = RewardsDialog()
            return rewardsDialog
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.dialog_rewards, container, false)
        setData()
        return rootView
    }

    /**
     *
     */
    private fun setData() {
        val scratchView : ScratchView = rootView.findViewById(R.id.scratch_view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            scratchView.setRevealListener(object : ScratchView.IRevealListener {
                override fun onRevealed(scratchView: ScratchView) {
                    Toast.makeText(context, "Revealed", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        dismiss()
                    }, 1000)
                }

                override fun onRevealPercentChangedListener(scratchView: ScratchView, percent: Float) {
                    if (percent >= 0.5) {
                        Log.d("Reveal Percentage", "onRevealPercentChangedListener: $percent")
                    }
                }
            })
        }

    }
}