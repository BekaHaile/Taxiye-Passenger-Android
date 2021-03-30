package product.clicklabs.jugnoo.credits.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.country.picker.CountryPicker
import com.google.zxing.integration.android.IntentIntegrator
import com.jugnoo.pay.utils.ApiResponseFlags
import com.sabkuchfresh.feed.models.FeedCommonResponse
import kotlinx.android.synthetic.main.send_credits_to_customer_fragment.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.credits.UserType
import product.clicklabs.jugnoo.rentals.qrscanner.ScannerActivity
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Utils

class SendCreditsToCustomerFragment : Fragment() {

    companion object {
        fun newInstance() = SendCreditsToCustomerFragment()
    }

    private lateinit var viewModel: SendCreditsToCustomerViewModel
    private lateinit var countryPicker: CountryPicker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(
                R.layout.send_credits_to_customer_fragment,
                container,
                false
        )
    }

    private fun showResponse(pMessage: String, isCloseOnClick: Boolean = true) {
        DialogPopup.alertPopupWithListener(requireActivity(), "", pMessage) {
            when (isCloseOnClick) {
                true -> activity?.finish()!!
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SendCreditsToCustomerViewModel::class.java)
        viewModel.getShareCreditsLiveData().observe(this, Observer<FeedCommonResponse> {
            when (it?.flag == ApiResponseFlags.ACTION_FAILED.getOrdinal() && it.error != null) {
                true -> {
                    showResponse(it?.error!!, false)
                }
            }
            when (it?.flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal() && it.message != null) {
                true -> {
                    showResponse(it?.message!!)
                }
            }
            when (it?.flag == ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() && it.message != null) {
                true -> {
                    showResponse(it?.message!!)
                }
            }
        })
        ivBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        ivScanQr.setOnClickListener{
            IntentIntegrator(requireActivity()).setCaptureActivity(ScannerActivity::class.java).initiateScan()
        }
        etCredits.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                if (etCredits.text.toString().isNotEmpty() && Integer.parseInt(etCredits.text.toString()) <= 0) {
//                    etCredits.setText("1")
//                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        rbDriver.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isPressed) {
                rbDriver.isChecked = true
                rbCustomer.isChecked = false
            }
        }
        rbCustomer.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isPressed) {
                rbDriver.isChecked = false
                rbCustomer.isChecked = true
            }
        }
        btSendCredits.setOnClickListener {

            if (!rbCustomer.isChecked && !rbDriver.isChecked) {
                Utils.showToast(activity, getString(R.string.select_transfer_to))
                return@setOnClickListener
            }

            if (etCredits.text.toString().isEmpty() || Integer.parseInt(etCredits.text.toString()) <= 0) {
                setErrorToView(etCredits)
                return@setOnClickListener
            }
            if (bt_country_Code.text.toString().isEmpty()) {
                setErrorToView(bt_country_Code)
                return@setOnClickListener
            }
            if (etPhoneNumber.text.toString().isEmpty()) {
                setErrorToView(etPhoneNumber)
                return@setOnClickListener
            }

            if(rbCustomer.isChecked){
                viewModel.shareCreditsToUser(
                        pAmount = etCredits.text.toString(),
                        pCountryCode = bt_country_Code.text.toString(),
                        pPhoneNo = etPhoneNumber.text.toString(), pTransferTo = UserType.CUSTOMER.ordinal)
            }
            else{
            viewModel.shareCreditsToUser(
                    pAmount = etCredits.text.toString(),
                    pCountryCode = bt_country_Code.text.toString(),
                    pPhoneNo = etPhoneNumber.text.toString(), pTransferTo = UserType.DRIVER.ordinal)
            }
        }
        bt_country_Code.setOnClickListener {
            openCountryPicker()
        }
        countryPicker = CountryPicker.Builder().with(context!!)
                .listener { bt_country_Code.text = it.dialCode }
                .build()
        bt_country_Code.text= Utils.getCountryCode(activity)
    }

    private fun setErrorToView(pView: TextView, pMessage: String = resources.getString(R.string.required_field)) {
        pView.error = pMessage
    }

    private fun openCountryPicker() {
        countryPicker.showDialog(childFragmentManager)
    }

    override fun onDestroyView() {
        if (viewModel.mJob.isActive) {
            viewModel.mJob.cancel()
        }
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}
