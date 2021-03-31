package product.clicklabs.jugnoo.credits

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.send_credits_to_customer_fragment.*
import product.clicklabs.jugnoo.BaseAppCompatActivity
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.MyApplication
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.credits.fragments.SendCreditsToCustomerFragment
import product.clicklabs.jugnoo.utils.Utils

class SendCreditsToCustomer : BaseAppCompatActivity() {

    private var qrCode: String? = ""
    private var qrCodeDetails: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.send_credits_to_customer_activity)
        MyApplication.getInstance().setmActivity(this)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, SendCreditsToCustomerFragment.newInstance())
                    .commitNow()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IntentIntegrator.REQUEST_CODE) run {

            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if (result != null) {
                if (result.contents != null) {
                    var bikeNumber = ""
                    qrCodeDetails = result.contents

                    bikeNumber = extractQRCode(result.contents)
                    if (bikeNumber != "error") {
                        qrCode = bikeNumber
                        var fragment = supportFragmentManager.findFragmentById(R.id.container) as SendCreditsToCustomerFragment
                        fragment.etPhoneNumber.setText(Utils.retrievePhoneNumberTenChars(qrCode, Data.userData.countryCode))
                    } else {
                        Toast.makeText(this, getString(R.string.incorrect_qr_code), Toast.LENGTH_SHORT).show()
                    }
                } else if (data != null) {
                    qrCode = data.getStringExtra("qrCode")
                    qrCodeDetails = data.getStringExtra("qr_code_details")
                    var fragment = supportFragmentManager.findFragmentById(R.id.container) as SendCreditsToCustomerFragment
                    fragment.etPhoneNumber.setText(Utils.retrievePhoneNumberTenChars(qrCode, Data.userData.countryCode))
                }
            }
        }
    }

    fun extractQRCode(result: String): String {

        //        String bikeNumber;
        //        if (result.indexOf("no=") > 0 && result.indexOf("no=") + 10 < result.length()) {
        //            bikeNumber = result.substring(result.indexOf("no=") + 3, result.indexOf("no=") + 13);
        //        } else if (result.length() == 11) {
        //            // TODO apply the check that all 11 digits must be numbers
        //            bikeNumber = result;
        //        } else {
        //            bikeNumber = "error";
        //        }
        //        return bikeNumber;

        var bikeNumber = ""
        if (result.indexOf("no=") > 0 && result.indexOf("no=") + 10 < result.length) {
            bikeNumber = result.substring(result.indexOf("no=") + 3, result.indexOf("no=") + 13)
        } else {
            bikeNumber = result
        }
        return bikeNumber
    }
}
