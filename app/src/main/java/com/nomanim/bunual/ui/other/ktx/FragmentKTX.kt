package com.nomanim.bunual.ui.other.ktx

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.crowdfire.cfalertdialog.CFAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.kaopiz.kprogresshud.KProgressHUD
import com.nomanim.bunual.R
import com.nomanim.bunual.models.ModelColors
import com.nomanim.bunual.ui.activities.MainActivity

fun Fragment.showDialogOfCloseActivity() {

    val builder = CFAlertDialog.Builder(requireContext())
        .setTitle(R.string.are_you_sure)
        .setMessage(R.string.changes_will_canceled)
        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
        .addButton(getString(R.string.do_not_want_continue),
            ContextCompat.getColor(requireContext(), R.color.white)
            ,ContextCompat.getColor(requireContext(), R.color.cancel_button_color_red)
            ,CFAlertDialog.CFAlertActionStyle.POSITIVE
            ,CFAlertDialog.CFAlertActionAlignment.JUSTIFIED,
            DialogInterface.OnClickListener { dialog, which ->

                val intent = Intent(activity, MainActivity::class.java)
                activity?.finish()
                activity?.startActivity(intent)

            })
        .addButton(getString(R.string.dismiss),
            ContextCompat.getColor(requireContext(), R.color.white)
            ,ContextCompat.getColor(requireContext(),R.color.dismiss_button_color_green)
            ,CFAlertDialog.CFAlertActionStyle.NEGATIVE
            ,CFAlertDialog.CFAlertActionAlignment.JUSTIFIED,
            DialogInterface.OnClickListener { dialog, which ->

                dialog.dismiss()
            })


    builder.show()
}

fun Fragment.showFeaturesBottomSheet(list: ArrayList<String>, textView: TextView, dialogTitle: String) {

    val builder = CFAlertDialog.Builder(requireContext())
        .setTitle(dialogTitle)
        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
        .setItems(list.toTypedArray()) { dialog, which ->

            textView.text = list[which]
            dialog?.dismiss()
        }

    builder.show()
}

fun Fragment.addCurrentUserPhoneNumberToSharedPref(auth: FirebaseAuth, sharedPref: SharedPreferences?) {

    val currentPhoneNumber = auth.currentUser?.phoneNumber.toString()
    val editor = sharedPref?.edit()
    editor?.putString("userPhoneNumber",currentPhoneNumber)
    editor?.apply()
}

fun Fragment.loadingProgressBarInDialog(label: String, detailsLabel: String) : KProgressHUD {

    val kProgressHUD = KProgressHUD.create(requireContext())
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setLabel(label)
        .setDetailsLabel(detailsLabel)
        .setCancellable(true)
        .setAnimationSpeed(2)
        .setDimAmount(0.3f)

    return kProgressHUD
}
