package product.clicklabs.jugnoo.newui.activity

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_rewards.*
import product.clicklabs.jugnoo.BaseFragmentActivity
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.newui.adapter.RewardsAdapter
import product.clicklabs.jugnoo.newui.dialog.RewardsDialog

class RewardsActivity : BaseFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards)

        rvRewards.layoutManager = GridLayoutManager(this@RewardsActivity, 2)
        rvRewards.isNestedScrollingEnabled = false
        rvRewards.adapter = RewardsAdapter(rvRewards, object : RewardsAdapter.RewardCardListener {
            override fun onCardScratched() {
                
            }

            override fun onCardClicked() {
                val ft = supportFragmentManager.beginTransaction()
                val prev = supportFragmentManager.findFragmentByTag("scratchDialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                val dialogFragment = RewardsDialog.newInstance()
                dialogFragment.show(ft, "scratchDialog")
            }

        })
    }
}