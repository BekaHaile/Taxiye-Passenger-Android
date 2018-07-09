package product.clicklabs.jugnoo.widgets;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.emergency.models.ContactBean;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 2/24/16.
 */
public class ContactsCompletionView extends TokenCompleteTextView<ContactBean> {
	private Context context;
	public ContactsCompletionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected View getViewForObject(ContactBean contactBean) {
		LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		LinearLayout view = (LinearLayout)l.inflate(R.layout.contact_token, (ViewGroup)ContactsCompletionView.this.getParent(), false);
		((TextView)view.findViewById(R.id.name)).setTypeface(Fonts.mavenLight(context));
		((TextView)view.findViewById(R.id.name)).setText(contactBean.getName());
		return view;
	}

	@Override
	protected ContactBean defaultObject(String completionText) {
		return new ContactBean("", "","", "", ContactBean.ContactBeanViewType.CONTACT);
	}

}