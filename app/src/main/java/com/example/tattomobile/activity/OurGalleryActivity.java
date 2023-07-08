package com.example.tattomobile.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.tattomobile.Adapter.GalleryListAdapter;
import com.example.tattomobile.Adapter.GalleryListAdapterAct;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.RemoteData.DBHelper;
import com.example.tattomobile.databinding.ActivityDetailBinding;
import com.example.tattomobile.databinding.ActivityOurGalleryBinding;
import com.example.tattomobile.databinding.FragmentHomeBinding;
import com.example.tattomobile.model.BannerModel;
import com.example.tattomobile.model.OurWorkListMode;
import com.example.tattomobile.model.PageModel;
import com.example.tattomobile.model.Response_Gallery;
import com.example.tattomobile.model.Response_list;
import com.example.tattomobile.model.ServiceModel;
import com.example.tattomobile.model.VideoModel;
import com.example.tattomobile.utility.Prefs;
import com.example.tattomobile.utility.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OurGalleryActivity extends AppCompatActivity {
     private ActivityOurGalleryBinding binding;
    private UtilityClass utilityClass;
    private List<ServiceModel> serviceModelList = new ArrayList<>();
    private List<BannerModel> bannerModelList = new ArrayList<>();
    private VideoModel videoModelList;
    private OurWorkListMode galleryModelList;
    private PageModel pageModel;
    private Handler handler = new Handler();
    private Runnable mRunnable;
    private int currentPage = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_gallery);
        binding = ActivityOurGalleryBinding.inflate(getLayoutInflater());
        binding.tvHeaderTitle.setText("Our gallery");
        setContentView(binding.getRoot());
        utilityClass = UtilityClass.getInstance();

        binding.ibBack.setOnClickListener(view -> onBackPressed());
        getGalleryData();
    }

    private void getGalleryData() {
        if (!UtilityClass.getInstance().checkInternetConnection(this)) {
            utilityClass.showAlertDialog(this, getResources().getString(R.string.internet_connection));
            return;
        }

        binding.rvMainProgresse.setVisibility(View.VISIBLE);
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).fetchourwork("Bearer " + Prefs.getToken())
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
                                    galleryModelList= gson.fromJson(response.body().toString(), OurWorkListMode.class);
                                    GalleryRecycleViewSetUp();
                                } else {
                                    utilityClass.showAlertDialog(OurGalleryActivity.this, getResources().getString(R.string.something_went_wrong));
                                }
                            } else {
                                utilityClass.showAlertDialog(OurGalleryActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            binding.rvMainProgresse.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(OurGalleryActivity.this, getResources().getString(R.string.something_went_wrong));
                        }
                        binding.rvMainProgresse.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        binding.rvMainProgresse.setVisibility(View.GONE);
                        utilityClass.showAlertDialog(OurGalleryActivity.this, getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    private void GalleryRecycleViewSetUp() {
        binding.rvGallery.setLayoutManager(new LinearLayoutManager(OurGalleryActivity.this, LinearLayoutManager.VERTICAL, false));
        GalleryListAdapterAct adaptergallery = new GalleryListAdapterAct(OurGalleryActivity.this, galleryModelList,1);
        binding.rvGallery.setAdapter(adaptergallery);
    }
}