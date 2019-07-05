package product.clicklabs.jugnoo.home.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;


/**
 * Created by shankar on 5/2/16.
 */
public class NotesDialog implements GACategory, GAAction {

    private final String TAG = NotesDialog.class.getSimpleName();
    private Context context;
    private NotesCallback callback;
    private Dialog dialog = null;
    private String mNotes = "";

    public NotesDialog(Context context, String notes, NotesCallback callback) {
        this.context = context;
        this.callback = callback;
        mNotes = notes;
    }

    public NotesDialog show(ProductType productType) {
        try {
            dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.dialog_notes);
            dialog.setOnDismissListener(dialog -> {

            });
            RelativeLayout relative =
                    dialog.findViewById(R.id.relative);
            new ASSL(context, relative, 1134, 720, false);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            LinearLayout linearLayoutInner = dialog.findViewById(R.id.linearLayoutInner);

            Button btnSaveNotes = dialog.findViewById(R.id.btnSaveNotes);
            TextView tvHeading = dialog.findViewById(R.id.tvNotesHeading);
            tvHeading.setTypeface(Fonts.mavenMedium(context));

            final EditText etNotes = dialog.findViewById(R.id.etNotes);
            if (!TextUtils.isEmpty(mNotes)) {
                etNotes.setText(mNotes);
                etNotes.setSelection(etNotes.getText().toString().length());
            }
            etNotes.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE);
                } else {
                    dialog.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);
                }
            });
            etNotes.setTypeface(Fonts.mavenRegular(context));
            btnSaveNotes.setTypeface(Fonts.mavenMedium(context));
            btnSaveNotes.setOnClickListener(v -> {
                mNotes = etNotes.getText().toString().trim();
                if(TextUtils.isEmpty(mNotes)){
                    Toast.makeText(context, context.getString(R.string.alert_provide_some_notes), Toast.LENGTH_SHORT).show();
                    return;
                }
                callback.onSaveNotes(mNotes);
                dialog.dismiss();
            });

            ImageView ivClose = dialog.findViewById(R.id.ivClose);
            if (ivClose != null) {
                ivClose.setOnClickListener(v -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                });

            }
            relative.setOnClickListener(v -> dialog.dismiss());

            linearLayoutInner.setOnClickListener(v -> {
            });

            if(!TextUtils.isEmpty(mNotes)){
                etNotes.setText(mNotes);
            }

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public interface NotesCallback {
        void onSaveNotes(String notes);
    }
}