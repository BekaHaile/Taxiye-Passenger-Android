package product.clicklabs.jugnoo.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import static product.clicklabs.jugnoo.Constants.KEY_ERROR;
import static product.clicklabs.jugnoo.Constants.KEY_GOOGLE_PARCEL;


/**
 * Created by socomo20 on 12/18/15.
 */
public class GoogleSigninActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private static final String TAG = "GoogleSigninActivity";
	private static final int RC_SIGN_IN = 9001;

	private GoogleApiClient mGoogleApiClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.requestId()
				.build();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.enableAutoManage(this, this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		android.util.Log.d(TAG, "onConnectionFailed:" + connectionResult);
		Intent intent = new Intent();
		intent.putExtra(KEY_ERROR, connectionResult.getErrorMessage());
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			handleSignInResult(result);
		}
	}

	private void handleSignInResult(GoogleSignInResult result) {
		android.util.Log.d(TAG, "handleSignInResult:" + result.isSuccess());
		if (result.isSuccess()) {
			// Signed in successfully, show authenticated UI.
			GoogleSignInAccount acct = result.getSignInAccount();
			Intent intent = new Intent();
//			intent.putExtra(KEY_GOOGLE_NAME, acct.getDisplayName());
//			intent.putExtra(KEY_GOOGLE_EMAIL, acct.getEmail());
//			intent.putExtra(KEY_GOOGLE_ID, acct.getId());
//			intent.putExtra(KEY_GOOGLE_IMAGE, acct.getPhotoUrl().toString());
			intent.putExtra(KEY_GOOGLE_PARCEL, acct);
			setResult(RESULT_OK, intent);
			signOut();
			finish();
		} else {
			// Signed out, show unauthenticated UI.
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	private void signIn() {
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	private void signOut() {
		Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
				new ResultCallback<Status>() {
					@Override
					public void onResult(Status status) {
					}
				});
	}

	private void revokeAccess() {
		Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
				new ResultCallback<Status>() {
					@Override
					public void onResult(Status status) {
					}
				});
	}

	@Override
	public void onConnected(Bundle bundle) {
		signIn();
	}

	@Override
	public void onConnectionSuspended(int i) {

	}
}
