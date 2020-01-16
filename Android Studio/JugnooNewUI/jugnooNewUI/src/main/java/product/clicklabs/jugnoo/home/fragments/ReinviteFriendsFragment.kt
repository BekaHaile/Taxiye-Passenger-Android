package product.clicklabs.jugnoo.home.fragments

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName
import kotlinx.android.synthetic.main.fragment_reinvite_friends.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.base.BaseFragment
import product.clicklabs.jugnoo.emergency.ContactsFetchAsync
import product.clicklabs.jugnoo.emergency.models.ContactBean
import product.clicklabs.jugnoo.home.adapters.ReinviteFriendsAdapter
import product.clicklabs.jugnoo.permission.PermissionCommon
import product.clicklabs.jugnoo.retrofit.model.FilterActiveUsersResponse
import product.clicklabs.jugnoo.retrofit.model.FilteredUserDatum
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Fonts
import product.clicklabs.jugnoo.utils.Utils
import java.util.*

class ReinviteFriendsFragment() : BaseFragment(), ReinviteFriendsAdapter.Callback{

    companion object{

        fun newInstance():ReinviteFriendsFragment{
            val fragment = ReinviteFriendsFragment()
            val bundle = Bundle()

            fragment.arguments = bundle
            return fragment
        }

    }

    private lateinit var listener:InteractionListener

    private val contactBeans:MutableList<ContactBean> = mutableListOf()
    private var filteredUsers:MutableList<FilteredUserDatum>? = null

    private var reinviteFriendsAdapter:ReinviteFriendsAdapter? = null

    private val REQUEST_CODE_CONTACT = 1000
    private val permissionCommom: PermissionCommon by lazy {
        PermissionCommon(this@ReinviteFriendsFragment).setCallback(object:PermissionCommon.PermissionListener{
            override fun permissionGranted(requestCode: Int) {
                contactsFetchAsync.execute()
            }

            override fun permissionDenied(requestCode: Int, neverAsk: Boolean): Boolean {
                permissionCommom.getPermission(REQUEST_CODE_CONTACT, Manifest.permission.READ_CONTACTS)
                return false
            }

            override fun onRationalRequestIntercepted(requestCode: Int) {
                permissionCommom.getPermission(REQUEST_CODE_CONTACT, Manifest.permission.READ_CONTACTS)
            }

        })

    }

    private val contactsFetchAsync: ContactsFetchAsync by lazy{
        ContactsFetchAsync(requireContext(), contactBeans as ArrayList<ContactBean>, object:ContactsFetchAsync.Callback{
            override fun onPreExecute() {

            }

            override fun onPostExecute(contactBeans: ArrayList<ContactBean>?) {
                if(contactBeans != null) {
                    val phoneNumbers = contactBeans.map {
                        it.phoneNo
                    }
                    filterUsersApis(phoneNumbers as MutableList<String>)
                } else {
                    groupNoContacts.visibility = View.VISIBLE
                    tvNoContacts.setText(R.string.no_contacts_in_your_phone)
                }
            }

        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is InteractionListener){
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return  inflater.inflate(R.layout.fragment_reinvite_friends, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewTitle.typeface = Fonts.mavenMedium(requireContext())
        textViewSelectAll.typeface = Fonts.mavenRegular(requireContext())
        btnReInvite.typeface = Fonts.mavenMedium(requireContext())
        tvNoContacts.typeface = Fonts.mavenRegular(requireContext())
        rvFriends.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        btnReInvite.visibility = View.GONE
        textViewSelectAll.visibility = View.GONE
        groupNoContacts.visibility = View.GONE


        btnReInvite.setOnClickListener{

        }

        textViewSelectAll.setOnClickListener{
            if(filteredUsers != null && reinviteFriendsAdapter != null){
                filteredUsers!!.forEach{
                    it.isSelected = true
                }
                reinviteFriendsAdapter!!.notifyDataSetChanged()
            }
        }

        imageViewBack.setOnClickListener{
            listener.backPressed()
        }



        permissionCommom.getPermission(REQUEST_CODE_CONTACT, Manifest.permission.READ_CONTACTS)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionCommom.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


    override fun onContactSelected(position: Int, userDatum: FilteredUserDatum) {

    }


    fun filterUsersApis(phoneNumbers:MutableList<String>){
        val params = hashMapOf<String, String>()
        val jsonArray = JSONArray()
        phoneNumbers.forEach{
            jsonArray.put(it)
        }
        params.put(Constants.KEY_PHONE_NOS, jsonArray.toString())
        ApiCommon<FilterActiveUsersResponse>(requireActivity()).execute(params, ApiName.FILTER_ACTIVE_USERS,
                object:APICommonCallback<FilterActiveUsersResponse>(){
            override fun onSuccess(t: FilterActiveUsersResponse?, message: String?, flag: Int) {
                if(t?.filteredUsers != null) {

                    DialogPopup.showLoadingDialog(requireContext(), getString(R.string.loading))

                    GlobalScope.launch(Dispatchers.IO){
                        //match user names
                        t.filteredUsers.forEach{
                            val valueToFind = Utils.retrievePhoneNumberTenChars(it.userPhoneNo, "+91")
                            val index = contactBeans.indexOf(ContactBean(valueToFind))
                            if(index > -1){
                                val contactBean = contactBeans[index]
                                it.userName = contactBean.name
                                if(TextUtils.isEmpty(it.userImage)){
                                    it.imageUri = contactBean.imageUri
                                }
                            }
                        }

                        //sort acc to names
                        t.filteredUsers.sortWith(Comparator { o1, o2 -> o1.userName!!.compareTo(o2.userName!!, ignoreCase = true) })


                        //duplicate phone number entries removed
                        val set = TreeSet(Comparator<FilteredUserDatum> { o1, o2 ->
                            if (o1.userPhoneNo.equals(o2.userPhoneNo, ignoreCase = true)) { 0 } else 1
                        })
                        set.addAll(t.filteredUsers)
                        filteredUsers = ArrayList(set)


                        launch(Dispatchers.Main){
                            DialogPopup.dismissLoadingDialog()

                            reinviteFriendsAdapter = ReinviteFriendsAdapter(rvFriends, filteredUsers!!, this@ReinviteFriendsFragment)
                            rvFriends.adapter = reinviteFriendsAdapter

                            btnReInvite.visibility = View.VISIBLE
                            textViewSelectAll.visibility = View.VISIBLE
                        }
                    }

                } else {
                    groupNoContacts.visibility = View.VISIBLE
                    tvNoContacts.text = getString(R.string.none_of_your_contacts_are_on_app, getString(R.string.app_name))
                }
            }

            override fun onError(t: FilterActiveUsersResponse?, message: String?, flag: Int): Boolean {
                return false
            }

        })
    }


    interface InteractionListener{
        fun backPressed()
    }
}