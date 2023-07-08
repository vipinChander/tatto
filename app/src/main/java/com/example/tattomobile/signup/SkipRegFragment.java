package com.example.tattomobile.signup;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tattomobile.databinding.FragmentSkipRegBinding;
import com.example.tattomobile.login.MobileLoginActivity;
import com.example.tattomobile.model.UserModel;


public class SkipRegFragment extends Fragment {

    private FragmentSkipRegBinding binding;
    private UserModel userModel;

    public SkipRegFragment() {
        // Required empty public constructor
    }

    public static SkipRegFragment newInstance(UserModel userModel) {
        SkipRegFragment fragment = new SkipRegFragment();
        fragment.userModel = userModel;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSkip.setOnClickListener(v -> {
            loadHomePageScreen(userModel);
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         binding =FragmentSkipRegBinding.inflate(inflater,container,false);
         return binding.getRoot();
    }

    private void loadHomePageScreen(UserModel userModel) {
        Intent homeIntent = new Intent(requireActivity(), MobileLoginActivity.class);
        homeIntent.putExtra("user_model", userModel);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
    }

}