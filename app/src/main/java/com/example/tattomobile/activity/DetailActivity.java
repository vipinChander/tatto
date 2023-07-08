package com.example.tattomobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tattomobile.Adapter.PageListAdapter;
import com.example.tattomobile.BuildConfig;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.databinding.ActivityDetailBinding;
import com.example.tattomobile.model.PageModel;
import com.example.tattomobile.utility.Prefs;
import com.example.tattomobile.utility.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private KProgressHUD pDialog;
    private UtilityClass utilityClass;
    private PageModel pageModel;
    private int serviceId = 0;
    private String productName = "";
    private Handler handler = new Handler();
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle b = getIntent().getExtras();
        if (b != null) {
            serviceId = b.getInt("service_id");
            productName = b.getString("product_name");
        }
        binding.ibBack.setOnClickListener(view -> onBackPressed());

        if (!productName.equalsIgnoreCase("tattoo")) {
            binding.tvOr.setVisibility(View.GONE);
            binding.btnBookWithYour.setVisibility(View.GONE);
        }

        binding.btnBookWithYour.setOnClickListener(view -> {

            Intent intent = new Intent(DetailActivity.this, CalenderActivity.class);
            intent.putExtra("showData", "1");
            intent.putExtra("service_id", serviceId);
            intent.putExtra("owndesign", true);
            startActivity(intent);
        });

        binding.tvHeaderTitle.setText(productName);

        utilityClass = UtilityClass.getInstance();
        pDialog = utilityClass.getProgressDialog(this);

        getBannerList();

    }

    private void getBannerList() {
        if (!UtilityClass.getInstance().checkInternetConnection(DetailActivity.this)) {
            utilityClass.showAlertDialog(DetailActivity.this, getResources().getString(R.string.internet_connection));
            return;
        }
        binding.progressDetailsPage.setVisibility(View.VISIBLE);
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).isProductsByID(BuildConfig.BASE_URL + "products/get/" + serviceId + "/list", "Bearer " + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                boolean error = jsonObject.optBoolean("error");
                                String msg = jsonObject.optString("msg");
                                Gson gson = new Gson();
                                binding.progressDetailsPage.setVisibility(View.GONE);
                                if (!error) {
                                    pageModel = gson.fromJson(String.valueOf(jsonObject.optJSONObject("response")), PageModel.class);
                                    pageRecycleViewSetUp();
                                } else {
                                    utilityClass.showAlertDialog(DetailActivity.this, getResources().getString(R.string.something_went_wrong));
                                }
                            } else {
                                utilityClass.showAlertDialog(DetailActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            binding.progressDetailsPage.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(DetailActivity.this, getResources().getString(R.string.something_went_wrong));
                        }
                        binding.progressDetailsPage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        binding.progressDetailsPage.setVisibility(View.GONE);
                        utilityClass.showAlertDialog(DetailActivity.this, getResources().getString(R.string.something_went_wrong));

                    }
                });

    }

    private void pageRecycleViewSetUp() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
        if (pageModel.getData().size() <= 0) {
            binding.correptedFiles.setVisibility(View.VISIBLE);
        }
        PageListAdapter adapter = new PageListAdapter(DetailActivity.this, pageModel.getData(), true);
        binding.recyclerView.setAdapter(adapter);

        if (productName.equalsIgnoreCase("tattoo")) {
            binding.tvOr.setVisibility(View.VISIBLE);
            binding.btnBookWithYour.setVisibility(View.VISIBLE);

        }
    }

}