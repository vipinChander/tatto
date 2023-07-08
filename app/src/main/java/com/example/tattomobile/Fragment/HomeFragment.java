package com.example.tattomobile.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.PagerAdapter;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tattomobile.Adapter.GalleryListAdapter;
import com.example.tattomobile.Adapter.HomeSliderAdapter;
import com.example.tattomobile.Adapter.PageListAdapter;
import com.example.tattomobile.Adapter.ServiceListAdapter;
import com.example.tattomobile.Adapter.TestimonialsListAdapter;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.RemoteData.DBHelper;
import com.example.tattomobile.RemoteData.Task;
import com.example.tattomobile.activity.OurGalleryActivity;
import com.example.tattomobile.activity.OurWorkActivity;
import com.example.tattomobile.databinding.FragmentHomeBinding;
import com.example.tattomobile.databinding.ShimmerLayoutBinding;
import com.example.tattomobile.model.BannerModel;
import com.example.tattomobile.model.PageModel;
import com.example.tattomobile.model.Response_Gallery;
import com.example.tattomobile.model.ServiceModel;
import com.example.tattomobile.model.VideoModel;
import com.example.tattomobile.model.VideoRespModel;
import com.example.tattomobile.utility.Prefs;
import com.example.tattomobile.utility.UtilityClass;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private KProgressHUD pDialog;
    private UtilityClass utilityClass;
    private FragmentHomeBinding binding;
   private ShimmerLayoutBinding shimmerLayoutBinding;
    private List<ServiceModel> serviceModelList = new ArrayList<>();
    private List<BannerModel> bannerModelList = new ArrayList<>();
    private List<VideoModel> videoModelList = new ArrayList<>();
    private Response_Gallery galleryModelList;
    private PageModel pageModel;
    private Handler handler = new Handler();
    private Runnable mRunnable;
    private int currentPage = 0;
   // private ShimmerFrameLayout shimmerLayoutBinding;
    private DBHelper dbHelper;
    Boolean aBoolean = true;
    private VideoRespModel videoRespModel;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }


    }

    @SuppressLint("WrongThread")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        utilityClass = UtilityClass.getInstance();
        binding.tvHeaderTitle.setText("Welcome, " + Prefs.getUserName());


        getBannerList();

        //shimmerLayoutBinding.setVisibility(View.VISIBLE);
        binding.tvSeemoreOurgall.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), OurGalleryActivity.class);
            getContext().startActivity(intent);

        });
        binding.tvSeemoreOurWork.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), OurWorkActivity.class);
            getContext().startActivity(intent);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void getPaginationList() {
        if (!UtilityClass.getInstance().checkInternetConnection(requireActivity())) {
            utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.internet_connection));
            return;
        }
        if (aBoolean) {
            binding.rvMainProgress.setVisibility(View.VISIBLE);
        }
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).fetchPaginationData("Bearer " + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                boolean error = jsonObject.optBoolean("error");
                                String msg = jsonObject.optString("msg");
                                Gson gson = new Gson();
                                binding.rvMainProgress.setVisibility(View.GONE);
                                if (!error) {
                                    pageModel = gson.fromJson(String.valueOf(jsonObject.optJSONObject("response")), PageModel.class);
                                    pageRecycleViewSetUp();
                                } else {
                                    utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                                }
                            } else {
                                utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            binding.rvMainProgress.setVisibility(View.GONE);
//                            utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                        }
                        binding.rvMainProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        binding.rvMainProgress.setVisibility(View.GONE);
                        utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    private void getGalleryData() {
        if (!UtilityClass.getInstance().checkInternetConnection(requireActivity())) {
            utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.internet_connection));
            return;
        }
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).fetchTestimonialsData("Bearer " + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                boolean error = jsonObject.optBoolean("error");
                                String msg = jsonObject.optString("msg");
                                Gson gson = new Gson();
                                //       binding.progressbarGallary.setVisibility(View.GONE);
                                if (!error) {
                                    galleryModelList = gson.fromJson(jsonObject.optString("response"), Response_Gallery.class);
                                    GalleryRecycleViewSetUp();
                                } else {
                                    utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                                }
                            } else {
                                utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            binding.rvMainProgress.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                        }
                        binding.rvMainProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        binding.rvMainProgress.setVisibility(View.GONE);
                        utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                    }
                });
    }


    private void getBannerList() {
        if (!UtilityClass.getInstance().checkInternetConnection(requireActivity())) {
            utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.internet_connection));
            return;
        }
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).fetchBannerData("Bearer " + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                boolean error = jsonObject.optBoolean("error");
                                String msg = jsonObject.optString("msg");
                                Gson gson = new Gson();
                             //   binding.rvMainProgress.setVisibility(View.GONE);
                                if (!error) {
                                    Type listType = new TypeToken<List<BannerModel>>() {
                                    }.getType();
                                        bannerModelList = gson.fromJson(String.valueOf(jsonObject.optJSONArray("response")), listType);
                                        viewPagerSetUp();
                                } else {

                                    utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));

                                }
                            } else {

                                utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            binding.rvMainProgress.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                        }
                        binding.rvMainProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        binding.rvMainProgress.setVisibility(View.GONE);

                        utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));

                    }
                });
    }

    private void getServiceList() {
        if (!UtilityClass.getInstance().checkInternetConnection(requireActivity())) {
            utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.internet_connection));
            return;
        }
        binding.rvMainProgress.setVisibility(View.VISIBLE);
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).fetchServiceData("Bearer " + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                boolean error = jsonObject.optBoolean("error");
                                String msg = jsonObject.optString("msg");
                                Gson gson = new Gson();
//                                binding.rvPaginationVideoProgress.setVisibility(View.GONE);
//                                utilityClass.hideDpDialogialog();
                                if (!error) {
                                    Type listType = new TypeToken<List<ServiceModel>>() {
                                    }.getType();

                                    serviceModelList = gson.fromJson(String.valueOf(jsonObject.optJSONArray("response")), listType);
                                    serviceRecycleViewSetUp();
                                } else {
                                    utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                                }
                            } else {
                                utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Gson gson = new Gson();
//                            binding.rvPaginationVideoProgress.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                        }
                        binding.rvMainProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
//                        utilityClass.hideDialog(pDialog);
                        binding.rvMainProgress.setVisibility(View.GONE);
                        utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                    }
                });
    }


    private void serviceRecycleViewSetUp() {
        binding.rvService.setVisibility(View.VISIBLE);
        binding.rvService.setLayoutManager(new GridLayoutManager(getContext(), 4));
        ServiceListAdapter adapter = new ServiceListAdapter(getContext(), serviceModelList);
        binding.rvService.setAdapter(adapter);
        getPaginationList();
    }

    public void viewPagerSetUp() {
        binding.rlParantViewpage.setVisibility(View.VISIBLE);
        binding.tabLayout.setVisibility(View.GONE);
        PagerAdapter adapter = new HomeSliderAdapter(requireActivity(), bannerModelList);
        binding.pager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.pager, true);
        getServiceList();
    }

    private void GalleryRecycleViewSetUp() {
        binding.tvGalleryView.setVisibility(View.VISIBLE);
        binding.rvGallery.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        GalleryListAdapter adaptergallery = new GalleryListAdapter(getContext(), galleryModelList, 0);
        binding.rvGallery.setAdapter(adaptergallery);
    }

    private void pageRecycleViewSetUp() {
        binding.rvPaginationVideo.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvPaginationVideo.setLayoutManager(linearLayoutManager);
        PageListAdapter adapterNewlyLaunch = new PageListAdapter(requireActivity(), pageModel.getData(), binding.rvPaginationVideo, false);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.rvPaginationVideo);
        binding.rvPaginationVideo.setAdapter(adapterNewlyLaunch);
        binding.rvPaginationVideo.setNestedScrollingEnabled(false);
        getTestimonialsList();
        // shimmerLayoutBinding.getRoot().setVisibility(View.GONE);
    }

    private void testimonialsRecycleViewSetUp() {
        binding.rvTestimonialsView.setVisibility(View.VISIBLE);
        binding.rvTestimonials.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (videoModelList != null) {
            TestimonialsListAdapter adapter = new TestimonialsListAdapter(getContext(), videoModelList, 0);
            binding.rvTestimonials.setAdapter(adapter);
            getGalleryData();
        }
    }

    private void getTestimonialsList() {
        if (!UtilityClass.getInstance().checkInternetConnection(requireActivity())) {
            utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.internet_connection));
            return;
        }
        binding.rvMainProgress.setVisibility(View.VISIBLE);
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).fetchourworkData("Bearer " + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                boolean error = jsonObject.optBoolean("error");
                                String msg = jsonObject.optString("msg");
                                Gson gson = new Gson();
                                binding.rvMainProgress.setVisibility(View.GONE);
                                if (!error) {
                                    videoRespModel = gson.fromJson(jsonObject.optString("response"), VideoRespModel.class);
                                    videoModelList = videoRespModel.getData();
                                    testimonialsRecycleViewSetUp();
                                } else {
                                    utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                                }
                            } else {
                                utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            binding.proOurWork.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                        }
//                        binding.proOurWork.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
//                      binding.proOurWork.setVisibility(View.GONE);
                        utilityClass.showAlertDialog(requireActivity(), getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    public void getRefressdata() {
        aBoolean = false;
        getServiceList();
        getBannerList();
    }
}