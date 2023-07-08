package com.example.tattomobile.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.tattomobile.Adapter.TestimonialsListAdapter;
import com.example.tattomobile.Adapter.TestimonialsListAdapterVlist;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.databinding.ActivityOurGalleryBinding;
import com.example.tattomobile.databinding.ActivityOurWorkBinding;
import com.example.tattomobile.databinding.FragmentHomeBinding;
import com.example.tattomobile.model.BannerModel;
import com.example.tattomobile.model.PageModel;
import com.example.tattomobile.model.Response_Gallery;
import com.example.tattomobile.model.Response_vlist;
import com.example.tattomobile.model.ServiceModel;
import com.example.tattomobile.model.VideoModel;
import com.example.tattomobile.model.VideoModelList;
import com.example.tattomobile.utility.Prefs;
import com.example.tattomobile.utility.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OurWorkActivity extends AppCompatActivity {
    ActivityOurWorkBinding binding;
    private UtilityClass utilityClass;
    private List<VideoModel> videoModelList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_work);
        binding = ActivityOurWorkBinding.inflate(getLayoutInflater());
        binding.tvHeaderTitle.setText("Our Videos");
        setContentView(binding.getRoot());
        getTestimonialsList();
        binding.ibBack.setOnClickListener(view -> onBackPressed());
    }

    private void getTestimonialsList() {
        if (!UtilityClass.getInstance().checkInternetConnection(OurWorkActivity.this)) {
            utilityClass.showAlertDialog(OurWorkActivity.this, getResources().getString(R.string.internet_connection));
            return;
        }
        binding.rvMainProgress.setVisibility(View.VISIBLE);
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).fetchourworkDatalist("Bearer " + Prefs.getToken())
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
                                    Type listType = new TypeToken<List<VideoModel>>() {
                                    }.getType();
                                    videoModelList =gson.fromJson(String.valueOf(jsonObject.optJSONArray("response")), listType);
                                    testimonialsRecycleViewSetUp();
                                } else {
                                    utilityClass.showAlertDialog(OurWorkActivity.this, getResources().getString(R.string.something_went_wrong));
                                }
                            } else {
                                utilityClass.showAlertDialog(OurWorkActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            utilityClass.showAlertDialog(OurWorkActivity.this, getResources().getString(R.string.something_went_wrong));
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        utilityClass.showAlertDialog(OurWorkActivity.this, getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    private void testimonialsRecycleViewSetUp() {
        binding.rvTestimonials.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (videoModelList != null) {
            TestimonialsListAdapter adapter = new TestimonialsListAdapter(this, videoModelList, 1);
            binding.rvTestimonials.setAdapter(adapter);
        }
    }
}