package com.sabkuchfresh.fragments

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sabkuchfresh.bus.UpdateMainList
import com.sabkuchfresh.dialogs.ReviewImagePagerDialog
import com.sabkuchfresh.home.FreshActivity
import com.sabkuchfresh.retrofit.model.menus.Item
import com.sabkuchfresh.retrofit.model.menus.ItemSelected
import com.sabkuchfresh.utils.gone
import com.sabkuchfresh.utils.toast
import com.sabkuchfresh.utils.visible
import kotlinx.android.synthetic.main.fragment_menu_item_details.*
import kotlinx.android.synthetic.main.layout_item_quantity_selector.*
import kotlinx.android.synthetic.main.layout_menus_customize_add_to_cart.*
import kotlinx.android.synthetic.main.list_item_customize_special_instructions.*
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.utils.ASSL
import product.clicklabs.jugnoo.utils.Utils

class ItemDetailFragment : Fragment() {

    private lateinit var item: Item
    private var pos = -1
    private lateinit var itemCallback: ItemCallback
    private var quantity = 1


    companion object {
        @JvmStatic
        fun newInstance(pos: Int): ItemDetailFragment {
            val fragment = ItemDetailFragment()
            val bundle = Bundle()
            bundle.putInt(POS_ITEM, pos)
            fragment.arguments = bundle
            return fragment
        }

        const val TAG = "ItemDetailFragment"
        const val POS_ITEM = "pos_item"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item = itemCallback.getItem()

        Utils.hideKeyboard(activity)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        pos = arguments?.getInt(POS_ITEM) ?: -1

        itemCallback.fragmentUISetup(this)
        shadowTopAddToCart.gone()

        textViewQuantity.text = (quantity.toString())
        calculatePrice()
        selectedQuantitySelector()
        setPriceAndOfferText(item, tvPrice)

        ASSL.DoMagic(linearLayoutQuantitySelector)

        imageViewMinus.setOnClickListener { removeItem() }
        imageViewPlus.setOnClickListener { addItem() }
        llAddToCart.setOnClickListener { addItemToCart() }

        tvItemName.text = item.itemName
        if (!item.itemImage.isNullOrBlank()) {
            Glide.with(activity!!).load(item.itemImage)
                    .apply(RequestOptions().fitCenter())
                    .apply(RequestOptions().centerCrop())
                    .apply(RequestOptions().placeholder(R.drawable.ic_fresh_new_placeholder))
                    .apply(RequestOptions().error(R.drawable.ic_fresh_new_placeholder))
                    .into(ivDisplayImage)
            ivDisplayImage.visible()

            ivDisplayImage.setOnClickListener { openItemImage() }
        } else {
            ivDisplayImage.gone()
        }
    }

    private fun openItemImage() {
        try {
            if (!item.itemImage.isNullOrBlank()) {
                val dialog = ReviewImagePagerDialog.newInstance(0, item.itemImage)
                dialog.show(activity!!.fragmentManager, ReviewImagePagerDialog::class.java.simpleName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectedQuantitySelector() {
        imageViewPlus.isEnabled = quantity < 50
        imageViewMinus.isEnabled = quantity > 1
    }

    private fun addItem() {
        if (quantity == 50) {
            context?.toast(R.string.order_quantity_limited)
            return
        }
        textViewQuantity.text = (++quantity).toString()
        calculatePrice()
        selectedQuantitySelector()
    }

    private fun removeItem() {
        if (quantity == 1) {
            return
        }
        textViewQuantity.text = (--quantity).toString()
        calculatePrice()
        selectedQuantitySelector()
    }

    private fun calculatePrice() {
        tvItemTotalValue.text = getString(R.string.rupees_value_format,
                Utils.getMoneyDecimalFormat().format(item.price * quantity.toDouble()).toString())
    }

    private fun addItemToCart() {

        val instructions = etInstructions.text.toString().trim()
        if (!item.itemSelectedList.isNullOrEmpty()) {
            // item has already been added to the cart
            // check if added item has same special instructions, if same increase quantity else add new item

            var sameInstructions = false
            for ((i,itemSelected)  in item.itemSelectedList.withIndex()) {
                if (itemSelected.itemInstructions == instructions) {
                    item.itemSelectedList[i].quantity = item.itemSelectedList[i].quantity!! + quantity
                    sameInstructions = true
                    break
                }
            }
            if (!sameInstructions) {
                val itemSelected = ItemSelected()
                itemSelected.restaurantItemId = item.restaurantItemId
                itemSelected.quantity = quantity
                itemSelected.totalPrice = item.price
                itemSelected.itemInstructions = instructions
                item.itemSelectedList.add(itemSelected)
            }
        } else {
            val itemSelected = ItemSelected()
            itemSelected.restaurantItemId = item.restaurantItemId
            itemSelected.quantity = quantity
            itemSelected.totalPrice = item.price
            itemSelected.itemInstructions = instructions
            item.itemSelectedList.add(itemSelected)
        }
        (activity as FreshActivity).performBackPressed(false)
        (activity as FreshActivity).vendorMenuFragment.onUpdateListEvent(UpdateMainList(true))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        itemCallback = context as ItemCallback
    }

    private fun setPriceAndOfferText(item: Item, textView: TextView) {

        if (!item.itemDetails.isNullOrBlank()) tvItemDetails.text = item.itemDetails else tvItemDetails.gone()

        if (!item.displayPrice.isNullOrBlank()) textView.append(getString(R.string.price_colon, item.displayPrice.toString()))
        else textView.append(context!!.getString(R.string.rupees_value_format, Utils.getMoneyDecimalFormat().format(item.price)))


        if (item.oldPrice != null && item.oldPrice != item.price) {
            val sts = StrikethroughSpan()
            val fcs = ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.theme_color))
            val sbst = SpannableStringBuilder(context!!.getString(R.string.rupees_value_format,
                    Utils.getMoneyDecimalFormat().format(item.oldPrice)))
            sbst.setSpan(sts, 0, sbst.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            sbst.setSpan(fcs, 0, sbst.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            textView.append("  ")
            textView.append(sbst)
        }
        if (!item.offerText.isNullOrBlank()) {
            val fcs = ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.theme_color))
            val sbfcs = SpannableStringBuilder(item.offerText)
            sbfcs.setSpan(fcs, 0, sbfcs.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            textView.append("  ")
            textView.append(sbfcs)
        }
    }

    interface ItemCallback {

        fun getItem(): Item

        fun fragmentUISetup(fragment: Fragment)

        fun setTitle(text: String)
    }
}