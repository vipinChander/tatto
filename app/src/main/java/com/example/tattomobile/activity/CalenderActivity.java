package com.example.tattomobile.activity;

import static com.example.tattomobile.R.drawable.calendar;
import static com.example.tattomobile.R.drawable.progressbar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.tattomobile.Adapter.TimeshowAdapter;
import com.example.tattomobile.BuildConfig;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.RemoteData.DBHelper;
import com.example.tattomobile.RemoteData.DatabaseClient;
import com.example.tattomobile.RemoteData.Task;
import com.example.tattomobile.databinding.ActivityCalenderBinding;
import com.example.tattomobile.model.BannerModel;
import com.example.tattomobile.model.PageDataModel;
import com.example.tattomobile.model.ProfileResponse;
import com.example.tattomobile.model.Response_Gallery;
import com.example.tattomobile.model.Response_b;
import com.example.tattomobile.model.TimeSave;
import com.example.tattomobile.model.TimefetchData;
import com.example.tattomobile.model.TimesDataModel;
import com.example.tattomobile.utility.MyCallbackResponse;
import com.example.tattomobile.utility.Prefs;
import com.example.tattomobile.utility.UtilityClass;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalenderActivity extends AppCompatActivity implements com.example.tattomobile.RemoteApi.Callback, MyCallbackResponse, AdapterView.OnItemSelectedListener {

    private ActivityCalenderBinding binding;
    private UtilityClass utilityClass;
    private KProgressHUD pDialog;
    private TimefetchData timeDataModel;
    private String Date_current;
    private String Date_now;
    private DBHelper dbHelper;
    private PageDataModel pageDataModel;
    private List<TimefetchData> timeDataModels = new ArrayList<>();
    private TimeSave timeSave;
    private Activity activity;
    private Boolean aBoolean = true;
    private Handler handler = new Handler();
    private Runnable mRunnable;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;
    private final int select_photo = 1;
    private String Booking_id = "";
    private String imageUrl = "";
    String showData = "0";
    String service_id;
    String image_URL_Upload = "";
    boolean ownDesign = false;
    boolean checked = false;
    String _branchName = "";
    String[] stateName = {"KOLKATA BAZAR - HAED OFFICE", "PVR MALL - BRANCH OFFICE",};
    String[] branchName = {"1", "2",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalenderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbHelper = new DBHelper(this);
        binding.tvHeaderTitle.setText("Pick Date & Slot");
        utilityClass = UtilityClass.getInstance();
        timeSave = TimeSave.getInstance();
        timeSave.setSlot_time("");
        timeSave.setPositios("0");
        Spinner spino = findViewById(R.id.coursesspinner);

        spino.setOnItemSelectedListener(this);
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                R.layout.spinner_text_layout,
                stateName);
        _branchName = branchName[0];
        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spino.setAdapter(ad);
        intonfig();
        Calendar today = Calendar.getInstance();
        long now = today.getTimeInMillis();
        binding.datepickcal.setMinDate(now);
        binding.datepickcal.clearFocus();

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        binding.imageChose.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_CAMERA);
            } else {

                Intent in = new Intent(Intent.ACTION_PICK);
                in.setType("image/*");
                startActivityForResult(in, select_photo);// start
            }
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        });

        SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        Date_now = formattedDate;
        binding.ibBack.setOnClickListener(view -> onBackPressed());
        try {
            pageDataModel = (PageDataModel) getIntent().getBundleExtra("data").getSerializable("response");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (pageDataModel.getServiceId() != null) {
                service_id = pageDataModel.getServiceId().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (service_id == null) {
            Bundle b = getIntent().getExtras();
            service_id = b.getSerializable("service_id").toString();
        }
        Bundle b = getIntent().getExtras();
        ownDesign = b.getBoolean("owndesign");
//
//        try {
//            Bundle b1 = getIntent().getExtras();
//            showData = b1.getString("showData");
////            showData=getIntent().getStringExtra("showData");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (showData == null) {
//            showData = "0";
//        }
//        if (showData.equalsIgnoreCase("0")) {
//            binding.imageSelLay.setVisibility(View.GONE);
//            binding.textDownloadImg.setVisibility(View.GONE);
//        }
        binding.btnPay.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("response", pageDataModel);
            Intent intent = new Intent(CalenderActivity.this, PaymentActivity.class);
            intent.putExtra("data", bundle);
            intent.putExtra("owndesign", ownDesign);
            startActivity(intent);
        });

        fetchData();


        binding.datepickcal.setOnDateChangeListener((calendarView, i, i1, i2) -> {
            Date_current = i2 + "/" + i1 + "/" + i;
        });

        binding.btnPay.setOnClickListener(view -> {
            if (!timeSave.getSlot_time().isEmpty()) {
                if (!ownDesign) {
                    if (Date_current == null ) {
                        Date_current=Date_now;
                        slotBooked(pageDataModel.getProductId().toString(), Prefs.getUserId(), timeDataModel.getResponse().get(Integer.parseInt(timeSave.getPositios())).getSlotId().toString(), pageDataModel.getServiceId().toString(), Date_current, pageDataModel.getOfferAmount().toString(), "Vipin_12345", image_URL_Upload);

                    }else {
                       // Toast.makeText(this, "Select Date From Calender", Toast.LENGTH_SHORT).show();
                    }
                   slotBooked(pageDataModel.getProductId().toString(), Prefs.getUserId(), timeDataModel.getResponse().get(Integer.parseInt(timeSave.getPositios())).getSlotId().toString(), pageDataModel.getServiceId().toString(), Date_current, pageDataModel.getOfferAmount().toString(), "Vipin_12345", image_URL_Upload);

                } else {
                    if (image_URL_Upload != null && !image_URL_Upload.isEmpty()) {

                        slotBooked("1", Prefs.getUserId(), timeDataModel.getResponse().get(Integer.parseInt(timeSave.getPositios())).getSlotId().toString(), service_id, Date_current, "50", "Vipin_12345", image_URL_Upload);
                    } else {
                        utilityClass.showAlertDialog(this, "Please select Image for Tatto");
                    }
                }

            } else {
                utilityClass.showAlertDialog(this, "Please pick the Time slot");
            }
        });
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

    public void adapterSet() {
        binding.TimesAdapter.setLayoutManager(new GridLayoutManager(CalenderActivity.this, 3));

        TimeshowAdapter adapter = new TimeshowAdapter(CalenderActivity.this, timeDataModel.getResponse());
        binding.TimesAdapter.setAdapter(adapter);
    }

    public void fetchData() {
        if (!UtilityClass.getInstance().checkInternetConnection(this)) {
            utilityClass.showAlertDialog(this, getResources().getString(R.string.internet_connection));
            return;
        }
        if (aBoolean) {
            binding.progressbarcalenderActivity.setProgressDrawable(getApplicationContext().getDrawable(progressbar));
            binding.progressbarcalenderActivity.setVisibility(View.VISIBLE);
        }
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).fetchTimesData(BuildConfig.BASE_URL + "slots/get/" + service_id + "/list", "Bearer" + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                boolean error = jsonObject.optBoolean("error");
                                Gson gson = new Gson();
                                binding.progressbarcalenderActivity.setVisibility(View.GONE);


                                if (!error) {

                                    timeDataModel = gson.fromJson(response.body().toString(), TimefetchData.class);
                                    adapterSet();

                                } else {
                                    utilityClass.showAlertDialog(CalenderActivity.this, getResources().getString(R.string.something_went_wrong));
                                }
                            } else {
                                utilityClass.showAlertDialog(CalenderActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            binding.progressbarcalenderActivity.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(CalenderActivity.this, getResources().getString(R.string.something_went_wrong));
                        }
                        binding.progressbarcalenderActivity.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        utilityClass.hideDialog(pDialog);
                        utilityClass.showAlertDialog(CalenderActivity.this, getResources().getString(R.string.something_went_wrong));
                    }
                });


    }

    public void slotBooked(String product_id, String user_id, String slot_id, String service_id, String booking_date, String product_amount, String order_id, String imageUrlPick) {
        if (!UtilityClass.getInstance().checkInternetConnection(this)) {
            utilityClass.showAlertDialog(this, getResources().getString(R.string.internet_connection));
            return;
        }
    //    Toast.makeText(this, product_id+"Date:"+booking_date+"Time:"+slot_id, Toast.LENGTH_SHORT).show();
        binding.progressbarcalenderActivity.setVisibility(View.VISIBLE);
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).bookedSlot(BuildConfig.BASE_URL + "booking/create" + "?product_id=" + product_id + "&user_id=" + user_id + "&slot_id=" + slot_id + "&service_id=" + service_id + "&booking_date=" + booking_date + "&product_amount=" + product_amount + "&order_id=" + order_id + "&booking_img=" + imageUrlPick+"&branch_id="+_branchName, "Bearer " + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                boolean error = jsonObject.optBoolean("error");
                                String BookinID = jsonObject.getString("booking_id");
                                Booking_id = BookinID;
                                Gson gson = new Gson();
                                binding.progressbarcalenderActivity.setVisibility(View.GONE);
                                if (!error) {
                                    utilityClass.showAlertDialog(CalenderActivity.this, "Your slot has been booked And please do payment for confirmation", 101, CalenderActivity.this);
//                                    responseModelList = gson.fromJson(jsonObject.optString("response"), ProfileResponse.class);
                                } else {
                                    utilityClass.showAlertDialog(CalenderActivity.this, getResources().getString(R.string.something_went_wrong));
                                }
                            } else {
                                utilityClass.showAlertDialog(CalenderActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            utilityClass.showAlertDialog(CalenderActivity.this, getResources().getString(R.string.something_went_wrong));
                        }
                        binding.progressbarcalenderActivity.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        binding.progressbarcalenderActivity.setVisibility(View.GONE);
                        utilityClass.showAlertDialog(CalenderActivity.this, getResources().getString(R.string.something_went_wrong));
                    }
                });


    }

    @Override
    public void callBackPayment(boolean result) {

    }

    public void getRefressdata() {
        aBoolean = false;
        fetchData();
    }

    @Override
    public void okButton(int tag) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("response", pageDataModel);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("data", bundle);
        intent.putExtra("booking_id", Booking_id);
        intent.putExtra("owndesgn", ownDesign);
        startActivity(intent);
    }

    @Override
    public void cancelButton(int tag) {

    }

    protected void onActivityResult(int requestcode, int resultcode,
                                    Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        switch (requestcode) {
            case select_photo:
                if (resultcode == RESULT_OK) {
                    try {

                        Uri imageuri = imagereturnintent.getData();// Get intent
                        // data

                        // Get real path and show over text view
                        String real_Path = getRealPathFromUri(CalenderActivity.this,
                                imageuri);
                        imageUrl = real_Path;
                        binding.iamgeName.setText(real_Path);
                        addPhotoCloudary(real_Path);

                        Bitmap bitmap = decodeUri(CalenderActivity.this, imageuri, 300);// call
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
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        e.printStackTrace();
                        Toast.makeText(CalenderActivity.this, "File not found.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    // Method that deocde uri into bitmap. This method is necessary to deocde
    // large size images to load over imageview
    public static Bitmap decodeUri(CalenderActivity context, Uri uri,
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
    public static String getRealPathFromUri(CalenderActivity context, Uri contentUri) {
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
                binding.progressbarcalenderActivity.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                Log.d("TAG", "onProgress");
                binding.progressbarcalenderActivity.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                Log.d("TAG", "onSuccess");
                binding.progressbarcalenderActivity.setVisibility(View.GONE);
                image_URL_Upload = String.valueOf(resultData.get("url"));
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Log.d("TAG", "onError");
                binding.progressbarcalenderActivity.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                Log.d("TAG", "onReschedule");
                binding.progressbarcalenderActivity.setVisibility(View.GONE);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }).dispatch();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // make toastof name of course
        // which is selected in spinner
//        Toast.makeText(getApplicationContext(),
//                        branchName[position],
//                        Toast.LENGTH_LONG)
//                .show();

        _branchName = stateName[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}