package com.rsmnm.Networking;


import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;

public class WebResponse<T> {



    @Expose
    public boolean status;

    @Expose
    public String error_code;

    @Expose
    public PagingInfo paging;

    @Nullable
    @Expose
    public String message;

    @Nullable
    @Expose
    public T body;

    public boolean isSuccess() {
        return status;
    }

    public boolean isExpired() {
        try {
            if (error_code.equalsIgnoreCase("invalid_token"))
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean areValidDocuments() {
        try {
            if (error_code != null) {
                if (error_code.equalsIgnoreCase("documents_invalid"))
                    return false;
                else
                    return true;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
