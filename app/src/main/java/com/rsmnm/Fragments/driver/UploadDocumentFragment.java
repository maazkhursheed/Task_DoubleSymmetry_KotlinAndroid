package com.rsmnm.Fragments.driver;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.rsmnm.BaseClasses.ImageChooserFragment;
import com.rsmnm.Models.UserItem;
import com.rsmnm.R;
import com.rsmnm.Utils.DateTimeHelper;
import com.rsmnm.ViewModels.DocumentViewModel;
import com.rsmnm.Views.RippleView;
import com.rsmnm.Views.TitleBar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;

public class UploadDocumentFragment extends ImageChooserFragment implements RippleView.OnRippleCompleteListener, View.OnClickListener {
    private DocumentViewModel.DocumentType mDocumentType;
    private TextView mTitleTextView;
    private RippleView mUploadButton;
    private RippleView mCancelButton;
    private File mImageFileToUpload;
    private ImageView mImageContainerView;
    private TextView mClickToCaptureView;
    private View mImageContainerLayout;
    private DocumentViewModel mDocumentViewModel;



    public static UploadDocumentFragment newInstance(DocumentViewModel.DocumentType documentType) {
        UploadDocumentFragment f = new UploadDocumentFragment();
        f.setDocumentType(documentType);
        return f;
    }

    private void setDocumentType(DocumentViewModel.DocumentType documentType) {
        mDocumentType = documentType;
    }

    @Override
    protected int getLayout() {
        return R.layout.upload_document_fragment;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.enableBack().setTitle("Upload documents");
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {
        mDocumentViewModel = ViewModelProviders.of(getActivity()).get(DocumentViewModel.class);
    }

    @Override
    public void inits() {

        mTitleTextView = view.findViewById(R.id.driver_license_textview);
        mImageContainerLayout = view.findViewById(R.id.img_container_layout);
        mUploadButton = view.findViewById(R.id.btn_upload);
        mCancelButton = view.findViewById(R.id.btn_cancel);
        mImageContainerView = view.findViewById(R.id.img_container);
        mClickToCaptureView = view.findViewById(R.id.click_to_capture);

        mImageContainerLayout.setOnClickListener(this);

        mUploadButton.setOnRippleCompleteListener(this);
        mCancelButton.setOnRippleCompleteListener(this);

        switch (mDocumentType) {
            case CAR_LICENSE:
                mTitleTextView.setText("Driver License");
                break;
            case CAR_INSURANCE:
                mTitleTextView.setText("Car Insurance");
                break;
            case CAR_INSPECTION:
                mTitleTextView.setText("Car Inspection");
                break;
            case CAR_REGISTRATION:
                mTitleTextView.setText("Car Registration");
                break;
            case STUDENT_ID:
                mTitleTextView.setText("Academic ID");
                break;
        }
    }

    @Override
    public void setEvents() {

    }

    @Override
    public void removeImage() {

    }

    @Override
    public void onComplete(RippleView rippleView) {
        if (rippleView == mUploadButton) {
            if (mImageFileToUpload == null) {
                makeSnackbar("Please pick document!");
                return;
            }
            mDocumentViewModel.uploadDocument(mDocumentType, mImageFileToUpload).observe(this, resource -> {
                hideLoader();
                switch (resource.status) {
                    case error:
                        makeSnackbar(resource.data);
                        break;
                    case loading:
                        showLoader();
                        break;
                    case success:
                        makeSnackbar("Uploaded successfully!");
                        getFragmentManager().popBackStack();
                        break;
                }
            });
        }
        if (rippleView == mCancelButton) {
            getFragmentManager().popBackStack();
            return;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mImageContainerLayout) {
            pickImage();
            return;
        }
    }

    @Override
    public void onImageChosen(ChosenImage chosenImage) {
        handler.post(() -> {
            mImageFileToUpload = new File(chosenImage.getFileThumbnail());
            Picasso.get().load(mImageFileToUpload).fit().centerCrop().into(mImageContainerView);
            mClickToCaptureView.setVisibility(View.GONE);
        });
    }
}