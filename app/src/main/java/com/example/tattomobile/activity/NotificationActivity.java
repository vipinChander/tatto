package com.example.tattomobile.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.tattomobile.Adapter.NotificationListAdapter;
import com.example.tattomobile.Adapter.PageListAdapter;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.databinding.ActivityNotificationBinding;
import com.example.tattomobile.model.NotificationModel;
import com.example.tattomobile.model.PageModel;
import com.example.tattomobile.model.ServiceModel;
import com.example.tattomobile.model.VideoModel;
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

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;
    private KProgressHUD pDialog;
    private UtilityClass utilityClass;
    private List<NotificationModel> notificationModelList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        utilityClass = UtilityClass.getInstance();
        pDialog = utilityClass.getProgressDialog(NotificationActivity.this);

        binding.ibBack.setOnClickListener(view -> onBackPressed());

        getNotificationList();
    }

    private void getNotificationList() {
        if (!UtilityClass.getInstance().checkInternetConnection(NotificationActivity.this)) {
            utilityClass.showAlertDialog(NotificationActivity.this, getResources().getString(R.string.internet_connection));
            return;
        }
     binding.progressNotificationsPage.setVisibility(View.VISIBLE);
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).fetchNotificationData("Bearer "+ Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                boolean error = jsonObject.optBoolean("error");
                                String msg = jsonObject.optString("msg");
                                Gson gson = new Gson();
                                binding.progressNotificationsPage.setVisibility(View.GONE);
                                if (!error) {
                                    Type listType = new TypeToken<List<NotificationModel>>() {
                                    }.getType();
                                    notificationModelList = gson.fromJson(String.valueOf(jsonObject.optJSONArray("response")), listType);
                                 if(!(notificationModelList.size() ==0)){
                                     notificationRecycleViewSetUp();
                                 }else {
                                    binding.correptedFiles.setVisibility(View.VISIBLE);
                                 }

                                } else {
                                    utilityClass.showAlertDialog(NotificationActivity.this, getResources().getString(R.string.something_went_wrong));
                                }
                            } else {
                                utilityClass.showAlertDialog(NotificationActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            binding.progressNotificationsPage.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(NotificationActivity.this, getResources().getString(R.string.something_went_wrong));
                        }
                        binding.progressNotificationsPage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        binding.progressNotificationsPage.setVisibility(View.GONE);
                        utilityClass.showAlertDialog(NotificationActivity.this, getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    private void notificationRecycleViewSetUp() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
        NotificationListAdapter adapter = new NotificationListAdapter(NotificationActivity.this, notificationModelList);
        binding.recyclerView.setAdapter(adapter);
    }

}