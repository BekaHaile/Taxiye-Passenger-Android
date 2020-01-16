package product.clicklabs.jugnoo.home.fragments

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_reinvite_friends.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.base.BaseFragment
import product.clicklabs.jugnoo.emergency.ContactsFetchAsync
import product.clicklabs.jugnoo.emergency.models.ContactBean
import product.clicklabs.jugnoo.home.adapters.ReinviteFriendsAdapter
import product.clicklabs.jugnoo.permission.PermissionCommon
import product.clicklabs.jugnoo.utils.Fonts
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
                reinviteFriendsAdapter = ReinviteFriendsAdapter(rvFriends, contactBeans!!, this@ReinviteFriendsFragment)
                rvFriends.adapter = reinviteFriendsAdapter

                btnReInvite.visibility = View.VISIBLE
                textViewSelectAll.visibility = View.VISIBLE
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
        rvFriends.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        btnReInvite.visibility = View.GONE
        textViewSelectAll.visibility = View.GONE


        btnReInvite.setOnClickListener{

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


    override fun onContactSelected(position: Int, contactBean: ContactBean) {

    }

    interface InteractionListener{
        fun backPressed()
    }
}