package product.clicklabs.jugnoo.tutorials.newtutorials.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.view.ViewPager
import kotlinx.android.synthetic.main.tutorial_activity.*
import product.clicklabs.jugnoo.*
import product.clicklabs.jugnoo.tutorials.newtutorials.adapters.TutorialDAO
import product.clicklabs.jugnoo.tutorials.newtutorials.adapters.TutorialPageAdapter
import product.clicklabs.jugnoo.tutorials.newtutorials.viewmodels.TutorialViewModel
import product.clicklabs.jugnoo.utils.Prefs


class TutorialActivites : BaseAppCompatActivity() {

    private lateinit var viewModel: TutorialViewModel
    private var pListOfTutorialImages = ArrayList<TutorialDAO>()
    private lateinit var pViewPageAdapter: TutorialPageAdapter
    private lateinit var mSavePosition: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationUpdatesExplicit()
        getDataFromPrevIntent()
        MyApplication.getInstance().setmCurrentActivity(this@TutorialActivites)
        setContentView(R.layout.tutorial_activity)
        setUpViewPager()
        val params = HashMap<String, String>()
        params["access_token"] = Data.userData.accessToken
        params["section"] = "10"
        params[Constants.KEY_LATITUDE] = "" + Data.loginLatitude
        params[Constants.KEY_LONGITUDE] = "" + Data.loginLongitude
        /*Make APi call*/
        setUpViewModel(params = params)
        initView()
    }

    fun setUpViewModel(params: HashMap<String, String>) {
        viewModel = ViewModelProviders.of(this).get(TutorialViewModel::class.java)
        viewModel.getTutorialData().observe(this@TutorialActivites, object : Observer<ArrayList<TutorialDAO>> {
            override fun onChanged(t: ArrayList<TutorialDAO>?) {
                when (!t.isNullOrEmpty()) {
                    true -> {
                        pListOfTutorialImages.clear()
                        pListOfTutorialImages.addAll(t)
                        pViewPageAdapter.notifyDataSetChanged()
                        indicator.setViewPager(viewpager)
                        if (t.size == pListOfTutorialImages.size - 1) {
                            btNextText.text = resources.getString(R.string.finish)
                        } else {
                            btNextText.text = resources.getString(R.string.next)
                        }
                    }
                    false -> {
                        openFinalActivity()
                    }
                }
            }
        })
        viewModel.syncDataFromServer(params)
    }

    private fun getDataFromPrevIntent() {
        when (intent != null) {
            true -> when (intent.hasExtra(TutorialActivites::class.java.simpleName.plus("position"))) {
                true -> {
                    mSavePosition = intent.getStringExtra(TutorialActivites::class.java.simpleName.plus("position"))!!
                    checkIsUserAlredyViewTutorialAtPosition()
                }
            }
        }
    }

    private fun checkIsUserAlredyViewTutorialAtPosition() {
        when (Prefs.with(this@TutorialActivites).getInt(mSavePosition, -1)) {
            1 -> {
                openFinalActivity(saveTutorialFinish = false)
            }
        }
    }

    private fun setUpViewPager() {
        pViewPageAdapter = TutorialPageAdapter(pListOfTutorialImages)
        viewpager.adapter = pViewPageAdapter
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(p0: Int) {
                if (p0 == pListOfTutorialImages.size - 1) {
                    btNextText.text = resources.getString(R.string.finish)
                } else {
                    btNextText.text = resources.getString(R.string.next)
                }
            }
        })
        indicator.setViewPager(viewpager)
    }

    private fun initView() {
        btSkip.setOnClickListener {
            openFinalActivity()
        }
        btNextText.setOnClickListener {
            if (viewpager.currentItem == pListOfTutorialImages.size - 1) {
                openFinalActivity(saveTutorialFinish = true)
            } else {
                viewpager.currentItem = ++viewpager.currentItem
            }
        }
    }

    private fun openFinalActivity(saveTutorialFinish: Boolean = false) {
        when (saveTutorialFinish && ::mSavePosition.isInitialized) {
            true -> {
                Prefs.with(this@TutorialActivites).save(mSavePosition, 1)
            }
        }
        when (MyApplication.getInstance().getmOpenActivityAfterFinishTutorial() != null) {
            true -> {
                startActivity(MyApplication.getInstance().getmOpenActivityAfterFinishTutorial())
            }
        }
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
