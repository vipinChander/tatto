package com.example.tattomobile.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.tattomobile.BuildConfig;
import com.example.tattomobile.Home.HomeActivity;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.databinding.ActivityBookingDetailBinding;
import com.example.tattomobile.model.Response_b;
import com.example.tattomobile.utility.Prefs;
import com.example.tattomobile.utility.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class BookingDetailActivity extends AppCompatActivity {

    private ActivityBookingDetailBinding binding;
    private KProgressHUD pDialog;
    private UtilityClass utilityClass;
    private boolean isShowing = false;
    private Response_b response_b;
    private static final int PICK_FROM_CAMERA = 1;
    private final int select_photo = 1;
    String image_URL_Upload = "";
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        utilityClass = UtilityClass.getInstance();
        pDialog = utilityClass.getProgressDialog(this);
        binding.ibBack.setOnClickListener(view -> onBackPressed());
        response_b = (Response_b) getIntent().getBundleExtra("value").get("response");
        intonfig();
        binding.tvBookingDateAndTime.setText("Payment Date And Time: "+response_b.getCreatedAt());
        try {
            if(response_b.getBooking_img() != null && !response_b.getBooking_img().isEmpty()) {
                Picasso.get()
                        .load(response_b.getBooking_img())
                        .into(binding.ivThumbnails);
            }else {
                Picasso.get()
                        .load(response_b.getProduct().getProductImg())
                        .into(binding.ivThumbnails);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        if (!response_b.getBookingStatus().equalsIgnoreCase("CREDIT")) {
            binding.btnBookingStatusDone.setBackgroundColor(getColor(R.color.yellow_color));
            binding.btnBookingStatusDone.setTextColor(getColor(R.color.white));
            binding.btnBookingStatusDone.setText("Pending");

        }
        if (!response_b.getService_Status().equalsIgnoreCase("COMPLETE")) {
            binding.btnSubmit.setVisibility(View.GONE);
            binding.etCommentBox.setVisibility(View.GONE);
            binding.etReview.setVisibility(View.GONE);
            binding.etAmount.setVisibility(View.GONE);
            binding.tvAmount.setVisibility(View.GONE);
            binding.tvReview.setVisibility(View.GONE);
            binding.tvCommentSuggestion.setVisibility(View.GONE);
            binding.imageChose.setVisibility(View.GONE);
            binding.imageSelLay.setVisibility(View.GONE);
        }
        binding.imageSelLay.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_CAMERA);
            } else {

                Intent in = new Intent(Intent.ACTION_PICK);
                in.setType("image/*");
                startActivityForResult(in, select_photo);// start
            }

        });
        binding.btnPaymentStatusDone.setText(response_b.getService_Status());
//    if (!(response_b.getSlot().getSlotStatus() ==1)){
//        binding.btnPaymentStatusDone.setBackgroundColor(getColor(R.color.yellow_color));
//        binding.btnPaymentStatusDone.setTextColor(getColor(R.color.white));
//        binding.btnPaymentStatusDone.setText("Pending");
//    }
        binding.etCommentBox.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.tvDateAndTime.setText("Date & Time: " + response_b.getBookingDate() + " " + response_b.getSlot().getSlotTime().trim());
        binding.tvHeader.setText(response_b.getProduct().getProductName());
        binding.tvId.setText("ID: " + response_b.getPaymentId());
        binding.btnSubmit.setOnClickListener(view -> {
            if (!utilityClass.checkEmptyValue(binding.etReview.getText().toString()) && !utilityClass.checkEmptyValue(binding.etCommentBox.getText().toString()) && !utilityClass.checkEmptyValue(binding.etAmount.getText().toString())) {
                sendFeedBackData(response_b.getUserId().toString(), response_b.getProductId().toString(), response_b.getPaymentId(), response_b.getServiceId().toString(), binding.etAmount.getText().toString(), binding.etReview.getText().toString(), binding.etCommentBox.getText().toString());
            } else {
                utilityClass.showAlertDialog(BookingDetailActivity.this, getResources().getString(R.string.empty_message));
            }
        });

    }

    private void sendFeedBackData(String userid, String productid, String paymentid, String serviceid, String storepayment, String review, String comment) {
        if (!UtilityClass.getInstance().checkInternetConnection(BookingDetailActivity.this)) {
            utilityClass.showAlertDialog(BookingDetailActivity.this, getResources().getString(R.string.internet_connection));
            return;
        }
        binding.progressBookinDetailsPage.setVisibility(View.VISIBLE);
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).sendUserFeedback(BuildConfig.BASE_URL + "feedback/create?user_id=" + userid + "&product_id=" + productid + "&payment_id=" + paymentid + "&service_id=" + serviceid + "&store_payment=" + storepayment + "&review=" + review + "&comment=" + comment, "Bearer " + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                Gson gson = new Gson();
                                boolean error = jsonObject.optBoolean("error");
                                binding.progressBookinDetailsPage.setVisibility(View.GONE);
                                if (!error) {
                                    Toast.makeText(BookingDetailActivity.this, "we are so happy to hear from you! thank you for your valuable feedback", Toast.LENGTH_SHORT).show();
//                                    utilityClass.showAlertDialog(BookingDetailActivity.this, getResources().getString(R.string.feedback_message));
                                    startActivity(new Intent(BookingDetailActivity.this, HomeActivity.class));
                                } else {
                                    String msg = jsonObject.optString("msg");
                                    Toast.makeText(BookingDetailActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
//                                    utilityClass.showAlertDialog(BookingDetailActivity.this, "we're so happy to hear from you! thank you for your valuable feedback");
                                }
                            } else {
                                binding.progressBookinDetailsPage.setVisibility(View.GONE);
                                utilityClass.showAlertDialog(BookingDetailActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            binding.progressBookinDetailsPage.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(BookingDetailActivity.this, getResources().getString(R.string.something_went_wrong));
                            e.printStackTrace();
                        }
                        binding.etAmount.setText("");
                        binding.etReview.setText("");
                        binding.etCommentBox.setText("");
                        binding.progressBookinDetailsPage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        binding.progressBookinDetailsPage.setVisibility(View.GONE);
                        utilityClass.showAlertDialog(BookingDetailActivity.this, getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    protected void onActivityResult(int requestcode, int resultcode,
                                    Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);
        switch (requestcode) {
            case select_photo:
                if (resultcode == RESULT_OK) {
                    try {

                        Uri imageuri = imagereturnintent.getData();// Get intent
                        // data

                        // Get real path and show over text view
                        String real_Path = getRealPathFromUri(BookingDetailActivity.this,
                                imageuri);

//                        binding.iamgeName.setText(real_Path);
                        addPhotoCloudary(real_Path);

                        Bitmap bitmap = decodeUri(BookingDetailActivity.this, imageuri, 300);// call
                        // deocde
                        // uri
                        // method
                        // Check if bitmap is not null then set image else show
                        // toast
                        ;
                        if (bitmap != null) {
                        }


//                            Toast.makeText(CalenderActivity.this,
//                                    "Error while decoding image.",
//                                    Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {

                        e.printStackTrace();
                        Toast.makeText(BookingDetailActivity.this, "File not found.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    // Method that deocde uri into bitmap. This method is necessary to deocde
    // large size images to load over imageview
    public static Bitmap decodeUri(BookingDetailActivity context, Uri uri,
                                   final int requiredSize) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver()
                .openInputStream(uri), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver()
                .openInputStream(uri), null, o2);
    }

    // Get Original image path
    public static String getRealPathFromUri(BookingDetailActivity context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void addPhotoCloudary(String imagePath) {

        MediaManager.get().upload(imagePath).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                Log.d("TAG", "onStart");
                binding.progressBookinDetailsPage.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                Log.d("TAG", "onProgress");
                binding.progressBookinDetailsPage.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                Log.d("TAG", "onSuccess");
                binding.progressBookinDetailsPage.setVisibility(View.GONE);
                image_URL_Upload = String.valueOf(resultData.get("url"));
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Log.d("TAG", "onError");
                binding.progressBookinDetailsPage.setVisibility(View.GONE);
                ;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                Log.d("TAG", "onReschedule");
                binding.progressBookinDetailsPage.setVisibility(View.GONE);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }).dispatch();

    }
    private void intonfig() {
        try {
            Map config = new HashMap();
            config.put("cloud_name", "sunny-tattoo");
            config.put("api_key", "274367586327336");
            config.put("api_secret", "-iTeJNlyWLq1mP3gE5u9HkgS6fM");
            config.put("secure", true);
            MediaManager.init(this, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



