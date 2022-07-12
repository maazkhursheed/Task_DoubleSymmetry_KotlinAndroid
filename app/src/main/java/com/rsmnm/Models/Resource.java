package com.rsmnm.Models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.rsmnm.Models.Resource.Status.loading;

/**
 * Created by rohail on 27-Oct-17.
 */

public class Resource<T> {

    @NonNull
    public final Status status;
    @Nullable
    public final T data;

    private Resource(@NonNull Status status, @Nullable T data) {
        this.status = status;
        this.data = data;
    }

    public static <T> Resource<T> response(Status status, @Nullable T data) {
        return new Resource<>(status, data);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(loading, null);
    }

    public enum Status {
        initial,
        loading,
        success,
        error,
        connection_error,
        action_signup,
        action_card_not_added,
        add_bank_account
    }
}