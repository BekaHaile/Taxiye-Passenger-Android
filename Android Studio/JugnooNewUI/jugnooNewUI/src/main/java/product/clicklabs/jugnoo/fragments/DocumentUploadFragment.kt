package product.clicklabs.jugnoo.fragments


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.picker.image.model.ImageEntry
import com.picker.image.util.Picker
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName
import com.sabkuchfresh.utils.ImageCompression
import com.sabkuchfresh.utils.Utils
import com.squareup.picasso.Picasso
import com.squareup.picasso.RoundBorderTransform
import kotlinx.android.synthetic.main.fragment_document_upload.*
import kotlinx.android.synthetic.main.list_item_documents.*
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.permission.PermissionCommon
import product.clicklabs.jugnoo.retrofit.model.DocumentData
import product.clicklabs.jugnoo.retrofit.model.UploadDocumentResponse
import product.clicklabs.jugnoo.utils.DialogPopup
import retrofit.mime.MultipartTypedOutput
import retrofit.mime.TypedFile
import retrofit.mime.TypedString
import java.io.File
import java.util.*

class DocumentUploadFragment : Fragment() {
    private var documentData: DocumentData? = null

    private var mPermissionCommon: PermissionCommon? = null
    private var picker: Picker? = null
    private var imageCompressionTask: ImageCompression? = null
    private val imageObjectList = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.documentData = arguments!!.getParcelable("documentData")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_document_upload, container, false)

        mPermissionCommon = PermissionCommon(this).setCallback(object : PermissionCommon.PermissionListener {
            override fun permissionGranted(requestCode: Int) {
                pickImages()
            }

            override fun permissionDenied(requestCode: Int, neverAsk: Boolean): Boolean {
                return true
            }

            override fun onRationalRequestIntercepted(requestCode: Int) {

            }
        })

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (documentData!!.imagesList != null && documentData!!.imagesList.size > 0) {
            imageObjectList.addAll(documentData!!.imagesList)
            refreshImageUI()
        }

        deleteImage1.setOnClickListener {
            deleteDocDialog(0)
        }

        deleteImage2.setOnClickListener {
            deleteDocDialog(1)
        }

        imageViewUploadDoc.setOnClickListener { mPermissionCommon!!.getPermission(REQ_CODE_IMAGE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE) }
        btSubmit.setOnClickListener {
            if (imageObjectList.size == documentData!!.numImagesRequired || documentData!!.status == DocStatus.VERIFIED.i) {
                activity?.onBackPressed()
            } else {
                Snackbar.make(view, getString(R.string.min_documents_required, documentData!!.numImagesRequired.toString()), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImages() {

        if (picker == null) {
            picker = Picker.Builder(activity!!, R.style.AppThemePicker_NoActionBar).setPickMode(Picker.PickMode.SINGLE_IMAGE).build()
        }
        picker?.limit = 1
        picker!!.startActivity(this@DocumentUploadFragment, activity, REQUEST_CODE_SELECT_IMAGES)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGES && resultCode == RESULT_OK) {
            if (data != null && data.getSerializableExtra("imagesList") != null) {
                val images = data.getSerializableExtra("imagesList") as ArrayList<ImageEntry>
                if (!images.isNullOrEmpty()) {
                    imageObjectList.addAll(images)
                    tvDocStatus!!.text = getString(R.string.uploaded)
                    uploadImage(imageObjectList.size - 1, documentData!!.documentId, documentData!!.documentType)
                }
            }
        }
    }

    fun refreshImageUI() {
        if (imageObjectList.size == 2) {
            try {
                if (documentData?.status == DocStatus.UPLOADED.i) {
                    deleteImage1!!.visibility = View.VISIBLE
                    deleteImage2!!.visibility = View.VISIBLE
                } else {
                    deleteImage1!!.visibility = View.GONE
                    deleteImage2!!.visibility = View.GONE
                }
                rlAddImage2!!.visibility = View.VISIBLE
                rlAddImage1!!.visibility = View.VISIBLE
                imageViewUploadDoc!!.visibility = View.GONE
                if (imageObjectList[0] is ImageEntry) {
                    Picasso.with(activity).load(File((imageObjectList[0] as ImageEntry).path))
                            .transform(RoundBorderTransform(6, 0))
                            .resize(300, 300)
                            .centerCrop()
                            .into(ivSetCapturedImage)
                } else {
                    Picasso.with(activity).load(imageObjectList[0] as String)
                            .transform(RoundBorderTransform(6, 0))
                            .resize(300, 300)
                            .centerCrop()
                            .into(ivSetCapturedImage)
                }
                if (imageObjectList[1] is ImageEntry) {
                    Picasso.with(activity).load(File((imageObjectList[1] as ImageEntry).path))
                            .transform(RoundBorderTransform(6, 0))
                            .resize(300, 300)
                            .centerCrop()
                            .into(ivSetCapturedImage2)
                } else {
                    Picasso.with(activity).load(imageObjectList[1] as String)
                            .transform(RoundBorderTransform(6, 0))
                            .resize(300, 300)
                            .centerCrop()
                            .into(ivSetCapturedImage2)
                }
            } catch (unhandled: Exception) {
            }

        } else if (imageObjectList.size == 1) {
            try {
                rlAddImage1!!.visibility = View.VISIBLE
                if (documentData?.status == DocStatus.UPLOADED.i) {
                    deleteImage1!!.visibility = View.VISIBLE
                } else {
                    deleteImage1!!.visibility = View.GONE
                }
                rlAddImage2!!.visibility = View.GONE
                deleteImage2!!.visibility = View.GONE
                if (documentData?.numImagesRequired!! == 1) {
                    imageViewUploadDoc!!.visibility = View.GONE
                } else {
                    imageViewUploadDoc!!.visibility = View.VISIBLE
                }
                if (imageObjectList[0] is ImageEntry) {
                    Picasso.with(activity).load(File((imageObjectList[0] as ImageEntry).path))
                            .transform(RoundBorderTransform(6, 0))
                            .resize(300, 300)
                            .centerCrop()
                            .into(ivSetCapturedImage)
                } else {
                    Picasso.with(activity).load(imageObjectList[0] as String)
                            .transform(RoundBorderTransform(6, 0))
                            .resize(300, 300)
                            .centerCrop()
                            .into(ivSetCapturedImage)
                }
            } catch (unhandled: Exception) {
            }

        } else {
            rlAddImage1!!.visibility = View.GONE
            deleteImage1!!.visibility = View.GONE
            rlAddImage2!!.visibility = View.GONE
            deleteImage2!!.visibility = View.GONE
            imageViewUploadDoc!!.visibility = View.VISIBLE
        }
        if (documentData!!.status == DocStatus.UPLOADED.i) {
            tvDocStatus.text = getString(R.string.uploaded)
        } else if (documentData!!.status == DocStatus.REJECTED.i) {
            tvDocStatus.text = getString(R.string.rejected)
        } else if (documentData!!.status == DocStatus.APPROVAL_PENDING.i) {
            tvDocStatus.text = getString(R.string.approval_pending)
        } else if (documentData!!.status == DocStatus.VERIFIED.i) {
            tvDocStatus.text = getString(R.string.verified)
        } else if (documentData!!.status == DocStatus.NOT_UPLOADED.i) {
            tvDocStatus.text = getString(R.string.upload_pending)
        }

        if (documentData?.status == DocStatus.VERIFIED.i) {
            if (documentData?.imagesList!!.size == 2) {
                deleteImage1.visibility = View.GONE
                rlAddImage1.visibility = View.VISIBLE
                deleteImage2.visibility = View.GONE
                rlAddImage2.visibility = View.VISIBLE
                imageViewUploadDoc.visibility = View.GONE
            } else if (documentData?.imagesList!!.size == 1) {
                deleteImage1.visibility = View.GONE
                rlAddImage1.visibility = View.VISIBLE
                deleteImage2.visibility = View.GONE
                rlAddImage2.visibility = View.GONE
                imageViewUploadDoc.visibility = View.GONE
            }
        }
    }

    fun uploadImage(imagePosition: Int, docId: Int, documentType: String) {
        var imageEntries: ArrayList<String>? = null
        if (imageObjectList[imagePosition] is ImageEntry) {
            imageEntries = ArrayList()
            imageEntries.add((imageObjectList[imagePosition] as ImageEntry).path)

        }
        if (imageEntries != null) {
            imageCompressionTask = ImageCompression(object : ImageCompression.AsyncResponse {
                override fun processFinish(output: Array<ImageCompression.CompressedImageModel>?) {

                    val params = MultipartTypedOutput()
                    if (output != null) {
                        for (file in output) {
                            if (file != null) {
                                params.addPart(Constants.KEY_IMAGE, TypedFile(Constants.MIME_TYPE, file.getFile()))
                            }
                        }
                        params.addPart("doc_id", TypedString(docId.toString()))
                        params.addPart("img_position", TypedString(imagePosition.toString()))
                        ApiCommon<UploadDocumentResponse>(activity).showLoader(true).putAccessToken(true).execute(params, ApiName.UPLOAD_VERICATION_DOCUMENTS, object : APICommonCallback<UploadDocumentResponse>() {
                            override fun onSuccess(uploadDocumentResponse: UploadDocumentResponse, message: String, flag: Int) {
                                if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                    DialogPopup.dismissLoadingDialog()
                                    DialogPopup.alertPopup(activity, "", message)
                                    documentData?.status = uploadDocumentResponse.status
                                    refreshImageUI()
                                } else {
                                    if (imageObjectList[imagePosition] != null) {
                                        imageObjectList.removeAt(imagePosition)
                                    }
                                }

                            }

                            override fun onError(uploadDocumentResponse: UploadDocumentResponse, message: String, flag: Int): Boolean {
                                DialogPopup.dismissLoadingDialog()
                                DialogPopup.alertPopup(activity, "", uploadDocumentResponse.getError())
                                if (imageObjectList[imagePosition] != null) {
                                    imageObjectList.removeAt(imagePosition)
                                }
                                return false
                            }
                        })
                    }
                }

                override fun onError() {
                    DialogPopup.dismissLoadingDialog()

                }
            }, activity)
            imageCompressionTask!!.execute(*imageEntries.toTypedArray())
        } else {
            Utils.showToast(activity, activity!!.getString(R.string.please_upload_images))
        }
    }

//    fun addImage() {
//        try {
//
//            if (documentData!!.status == DocStatus.REJECTED.i) {
//                DialogPopup.alertPopupTwoButtonsWithListeners(activity,
//                        activity!!.resources.getString(R.string.rejection_reason),
//                        documentData!!.reason,
//                        activity!!.resources.getString(R.string.upload_again),
//                        activity!!.resources.getString(R.string.cancel),
//                        { mPermissionCommon!!.getPermission(REQ_CODE_IMAGE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE) },
//                        { }, true, true)
//            } else {
//                mPermissionCommon!!.getPermission(REQ_CODE_IMAGE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionCommon!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDetach() {
        super.onDetach()
        imageObjectList.clear()

    }

    companion object {

        private val REQUEST_CODE_SELECT_IMAGES = 99
        private val REQ_CODE_IMAGE_PERMISSION = 1001

        fun newInstance(documentData: DocumentData): DocumentUploadFragment {
            val fragment = DocumentUploadFragment()
            val args = Bundle()
            args.putParcelable("documentData", documentData)
            fragment.arguments = args
            return fragment
        }
    }

    private fun deleteDocumentApi(docId: Int, imagePosition: Int) {
        val params = hashMapOf(
                "doc_id" to docId.toString(),
                "img_position" to imagePosition.toString()
        )
        ApiCommon<UploadDocumentResponse>(activity).putAccessToken(true).putDefaultParams(true).showLoader(true).execute(params, ApiName.DELETE_DOCUMENT, object : APICommonCallback<UploadDocumentResponse>() {

            override fun onSuccess(t: UploadDocumentResponse?, message: String?, flag: Int) {
                if (t?.flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                    DialogPopup.alertPopup(activity, "", message)
                    imageObjectList.removeAt(imagePosition)
                    documentData?.status = t.status
                    refreshImageUI()
                }
            }

            override fun onError(t: UploadDocumentResponse?, message: String?, flag: Int): Boolean {
                DialogPopup.alertPopup(activity, "", t?.error)
                return false
            }

        })
    }

    private fun deleteDocDialog(imagePosition: Int) {
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, ""
                , getString(R.string.are_you_sure_to_delete_document)
                    , getString(R.string.confirm), getString(R.string.cancel)
                , object : View.OnClickListener {
            override fun onClick(v: View?) {
                deleteDocumentApi(documentData!!.getDocumentId(), imagePosition)
            }
        }
                , null, false, false)

    }

    enum class DocStatus private constructor(i: Int) {
        UPLOADED(4), VERIFIED(3), REJECTED(2), APPROVAL_PENDING(1), NOT_UPLOADED(0);

        var i: Int = 0

        init {
            this.i = i
        }
    }

}