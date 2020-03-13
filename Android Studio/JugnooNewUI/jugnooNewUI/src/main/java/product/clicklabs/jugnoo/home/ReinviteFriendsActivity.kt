package product.clicklabs.jugnoo.home

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_reinvite_friends.*
import product.clicklabs.jugnoo.BaseAppCompatActivity
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.home.fragments.ReinviteFriendsFragment

class ReinviteFriendsActivity : BaseAppCompatActivity(), ReinviteFriendsFragment.InteractionListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reinvite_friends)

        supportFragmentManager.beginTransaction()
                .add(llContainer.id, ReinviteFriendsFragment.newInstance(),
                        ReinviteFriendsFragment::class.java.name)
                .addToBackStack(ReinviteFriendsFragment::class.java.name)
                .commitAllowingStateLoss()

    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1){
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun backPressed() {
        onBackPressed()
    }

}