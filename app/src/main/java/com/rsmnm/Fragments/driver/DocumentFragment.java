package com.rsmnm.Fragments.driver;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rsmnm.BaseClasses.BaseFragment;
import com.rsmnm.Models.UserItem;
import com.rsmnm.R;
import com.rsmnm.Utils.DateTimeHelper;
import com.rsmnm.Utils.ExtensionUtilsKt;
import com.rsmnm.ViewModels.DocumentViewModel;
import com.rsmnm.Views.TitleBar;

import java.io.Console;
import java.util.Date;

public class DocumentFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private DocumentViewModel mDocumentViewModel;

    private TextView mCarInsuranceExpiredOnTextView, mCarInsuranceUpdatedOnTextView, mCarInspectionExpiredOnTextView,
            mCarInspectionUpdatedOnTextView, mCarLicenseExpiredOnTextView, mCarLicenseUpdatedOnTextView, mCarRegistrationUpdatedOnTextView,
            mCarRegistrationExpiredOnTextView , mStudentIDUpdatedOnTextView;

    private View mCarLicenseLayout, mCarInsuranceLayout, mCarInspectionLayout , mCarRegistrationLayout , mCarStudentIDLayout;

    @Override
    protected int getLayout() {
        return R.layout.document_fragment;
    }

    @Override
    protected void getTitleBar(TitleBar titleBar) {
        titleBar.resetTitleBar().enableBack().setTitle("Documents");
    }

    @Override
    protected void activityCreated(Bundle savedInstanceState) {

    }

    @Override
    public void inits() {
        mDocumentViewModel = ViewModelProviders.of(getActivity()).get(DocumentViewModel.class);

        mCarLicenseUpdatedOnTextView = view.findViewById(R.id.license_updated_on);
        mCarLicenseExpiredOnTextView = view.findViewById(R.id.license_expired_on);

        mCarInspectionUpdatedOnTextView = view.findViewById(R.id.inspection_updated_on);
        mCarInspectionExpiredOnTextView = view.findViewById(R.id.inspection_expired_on);

        mCarInsuranceUpdatedOnTextView = view.findViewById(R.id.insurance_updated_on);
        mCarInsuranceExpiredOnTextView = view.findViewById(R.id.insurance_expired_on);

        mCarRegistrationUpdatedOnTextView = view.findViewById(R.id.registration_updated_on);
        mCarRegistrationExpiredOnTextView = view.findViewById(R.id.registration_expired_on);

        mStudentIDUpdatedOnTextView = view.findViewById(R.id.student_id_updated_on);

        mCarLicenseLayout = view.findViewById(R.id.car_license);
        mCarInsuranceLayout = view.findViewById(R.id.vehicle_insaurance);
        mCarInspectionLayout = view.findViewById(R.id.vehicle_inspection);
        mCarRegistrationLayout = view.findViewById(R.id.vehicle_registration);
        mCarStudentIDLayout = view.findViewById(R.id.student_id_layout);

        mCarInspectionLayout.setOnClickListener(this);
        mCarLicenseLayout.setOnClickListener(this);
        mCarInsuranceLayout.setOnClickListener(this);
        mCarRegistrationLayout.setOnClickListener(this);
        mCarStudentIDLayout.setOnClickListener(this);

        onRefresh();
    }

    private void getAllDocuments() {
        showLoader();
        mDocumentViewModel.getAllDocuments().observe(this, resource -> {
            hideLoader();
            switch (resource.status) {
                case error:
                    makeSnackbar(resource.data);
                    break;
                case loading:
                    showLoader();
                    break;

                case success:
                    UserItem webResponseResource = resource.data.body;
                    Log.d("UserData",webResponseResource.vehicle_registration +"");

                    if (webResponseResource.license_pic != null) {
                        mCarLicenseUpdatedOnTextView.setText(DateTimeHelper.getFormattedDate(webResponseResource.license_pic.getUploadedAt()));
                        mCarLicenseExpiredOnTextView.setText(DateTimeHelper.getFormattedDate(webResponseResource.license_pic.getExpiryDate()));
                    }
                    if (webResponseResource.inspection_pic != null) {
                        mCarInspectionUpdatedOnTextView.setText(DateTimeHelper.getFormattedDate(webResponseResource.inspection_pic.getUploadedAt()));
                        mCarInspectionExpiredOnTextView.setText(DateTimeHelper.getFormattedDate(webResponseResource.inspection_pic.getExpiryDate()));
                    }

                    if (webResponseResource.insurance_pic != null) {
                        mCarInsuranceUpdatedOnTextView.setText(DateTimeHelper.getFormattedDate(webResponseResource.insurance_pic.getUploadedAt()));
                        mCarInsuranceExpiredOnTextView.setText(DateTimeHelper.getFormattedDate(webResponseResource.insurance_pic.getExpiryDate()));
                    }

                    if (webResponseResource.vehicle_registration_pic != null) {
                        mCarRegistrationUpdatedOnTextView.setText(DateTimeHelper.getFormattedDate(webResponseResource.vehicle_registration_pic.getUploadedAt()));
                        mCarRegistrationExpiredOnTextView.setText(DateTimeHelper.getFormattedDate(webResponseResource.vehicle_registration_pic.getExpiryDate()));
                    }

                    if (webResponseResource.student_id != null) {
                        mStudentIDUpdatedOnTextView.setText(DateTimeHelper.getFormattedDate(webResponseResource.student_id.getUploadedAt()));
                        //mCarRegistrationExpiredOnTextView.setText(DateTimeHelper.getFormattedDate(webResponseResource.vehicle_registration_pic.getExpiryDate()));
                    }

                    break;
            }
        });
    }

    @Override
    public void setEvents() {

    }

    @Override
    public void onRefresh() {
        getAllDocuments();
    }

    @Override
    public void onClick(View view) {
        if (mCarLicenseLayout == view) {
            frragmentActivity.replaceFragmentWithBackstack(
                    UploadDocumentFragment.newInstance(DocumentViewModel.DocumentType.CAR_LICENSE)
            );
            return;
        }

        if (mCarInspectionLayout == view) {
            frragmentActivity.replaceFragmentWithBackstack(
                    UploadDocumentFragment.newInstance(DocumentViewModel.DocumentType.CAR_INSPECTION)
            );
            return;
        }

        if (mCarInsuranceLayout == view) {
            frragmentActivity.replaceFragmentWithBackstack(
                    UploadDocumentFragment.newInstance(DocumentViewModel.DocumentType.CAR_INSURANCE)
            );
            return;
        }

        if (mCarRegistrationLayout == view) {
            frragmentActivity.replaceFragmentWithBackstack(
                    UploadDocumentFragment.newInstance(DocumentViewModel.DocumentType.CAR_REGISTRATION)
            );
            return;
        }

        if (mCarStudentIDLayout == view) {
            frragmentActivity.replaceFragmentWithBackstack(
                    UploadDocumentFragment.newInstance(DocumentViewModel.DocumentType.STUDENT_ID)
            );
            return;
        }
    }
}