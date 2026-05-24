package com.example.unieat.dao;

public interface FirebaseCallback<T> {
    void onSuccess(T result);
    void onFailure(String erro);
}
