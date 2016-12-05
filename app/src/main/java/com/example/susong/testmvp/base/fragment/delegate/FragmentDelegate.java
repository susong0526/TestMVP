package com.example.susong.testmvp.base.fragment.delegate;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public interface FragmentDelegate {
    void onAttach(Context context);

    void onCreate(Bundle savedInstanceState);

    void onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    void onActivityCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroyView();

    void onFragmentDestroy();

    void onDetach();
}
