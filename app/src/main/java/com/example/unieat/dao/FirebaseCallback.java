package com.example.unieat.dao;

public interface FirebaseCallback<T> {
    void onSuccess(T result);
    void onFailure(String erro);

    static <T> FirebaseCallback<T> ignore() {
        return new FirebaseCallback<T>() {
            @Override public void onSuccess(T result) {}
            @Override public void onFailure(String error) {}
        };
    }
}
