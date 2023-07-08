package com.example.tattomobile.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tattomobile.BuildConfig;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.databinding.ActivityPaymentBinding;
import com.example.tattomobile.model.PageDataModel;
import com.example.tattomobile.model.TimeSave;
import com.example.tattomobile.utility.Prefs;
import com.example.tattomobile.utility.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {
    private ActivityPaymentBinding binding;
    private String TAG = "PaymentActivity :";
    private UtilityClass utilityClass;
    private int EarnCoin = 0, payCoin = 0;
    private TimeSave timeSave;
    private PageDataModel pageDataModel;
    private int slot_money = 0;
    private int pay_money = 0;
    private String booking_id = "12345";
    boolean ownDesign = false;
    private String use_coin = "0";
    private String bookingMoneyByslotbooking;
    private String bookingSlotByFullMoney;
    private String TotalAMountPaid = "0";
    boolean checked = false;
    boolean payroll = false;
    boolean status=true;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        utilityClass = UtilityClass.getInstance();
        timeSave = TimeSave.getInstance();
        slot_money = moneyUsed(50, payCoin);
        bookingMoneyByslotbooking = String.valueOf(slot_money);
        if (slot_money > 0) {
            binding.btnBookYourSlot.setText("Pay " + slot_money + "/- And Book your slot");
        } else {
            binding.btnBookYourSlot.setText("Book your slot");
        }
        try {
            pageDataModel = (PageDataModel) getIntent().getBundleExtra("data").getSerializable("response");
            booking_id = getIntent().getStringExtra("booking_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ownDesign = getIntent().getExtras().getBoolean("owndesgn");
        binding.ibBack.setOnClickListener(view -> onBackPressed());
        EarnCoin = Integer.parseInt(timeSave.getCoinsEarn());
        payCoin = EarnCoin / 2;
        if (!ownDesign) {
            binding.ortext.setVisibility(View.VISIBLE);
            binding.btnPay.setVisibility(View.VISIBLE);
            binding.btnPay.setText("Pay full amount " + pageDataModel.getOfferAmount() + "/-");
            bookingSlotByFullMoney = String.valueOf(pageDataModel.getOfferAmount());
        }
       getSecurityKey();
        binding.checkmoneyused.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                checked = true;

                utilityClass.showAlertDialog(this, getString(R.string.you_have_opted_to_use_your_earned_coin));
                slot_money = moneyUsed(50, payCoin);
                if (!ownDesign) {
                    pay_money = moneyUsed(pageDataModel.getOfferAmount(), payCoin);
                } else {
                    pay_money = moneyUsed(50, payCoin);
                }
                if (slot_money > 0) {
                    bookingMoneyByslotbooking = String.valueOf(slot_money);
                    binding.btnBookYourSlot.setText("Pay " + slot_money + "/- And Book your slot");

                } else {
                    bookingMoneyByslotbooking = "50";
                    binding.btnBookYourSlot.setText(R.string.book_your_slot);

                }
                if (pay_money > 0) {
                    bookingSlotByFullMoney = String.valueOf(pay_money);
                    binding.btnPay.setText(getString(R.string.pay_full_amount)  +"\t" + pay_money + "/-");

                } else {
                    binding.btnBookYourSlot.setText("Book your slot");
                    if (!ownDesign) {
                        bookingSlotByFullMoney = String.valueOf(pageDataModel.getOfferAmount());
                        binding.btnPay.setText("Pay full amount  " +"\t" +pageDataModel.getOfferAmount() + "/-");
                    }
                }

            } else {
                checked = false;
//                utilityClass.showAlertDialog(this,"You have opted to use your earned coin for this payment. You can use only 50% of amount from your earned coins.");
                use_coin = String.valueOf(0);
                binding.btnBookYourSlot.setText("Pay 50/- And Book your slot");
                bookingMoneyByslotbooking = "50";
                if (!ownDesign) {
                    bookingSlotByFullMoney = String.valueOf(pageDataModel.getOfferAmount());
                    binding.btnPay.setText("Pay full amount " + pageDataModel.getOfferAmount() + "/-");
                }
            }
        });
        binding.showMoneyUsed.setText("Use your earn Coin " + payCoin + "/-");
        try {
            if (!ownDesign) {
                if (pageDataModel.getOfferAmount() != null) {
                    bookingSlotByFullMoney = String.valueOf(pageDataModel.getOfferAmount());
                    binding.btnPay.setText("Pay Your Full Amount " + pageDataModel.getOfferAmount() + "/-");
                } else {

                }
            } else {
                bookingMoneyByslotbooking = String.valueOf(pageDataModel.getOfferAmount());
                binding.btnPay.setText("Pay Your Full Amount " + pageDataModel.getOfferAmount() + "/-");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }

        binding.btnPay.setOnClickListener(view -> {
            if (!utilityClass.checkEmptyValue(binding.etName.getText().toString()) && !utilityClass.checkEmptyValue(binding.etEmail.getText().toString()) && !utilityClass.checkEmptyValue(binding.etMobile.getText().toString())) {
                payroll = false;
               TotalAMountPaid= bookingSlotByFullMoney;
                payByRazorpay(binding.etName.getText().toString(), binding.etEmail.getText().toString(), binding.etMobile.getText().toString(), bookingSlotByFullMoney);

            } else {
                utilityClass.showAlertDialog(this, "Please Filled all required box to process the Payments ");
            }
          //  Toast.makeText(this, "" + use_coin, Toast.LENGTH_SHORT).show();
        });
        binding.btnBookYourSlot.setOnClickListener(view -> {
            payroll = true;
       //     Toast.makeText(this, "" + use_coin, Toast.LENGTH_SHORT).show();
            if (!utilityClass.checkEmptyValue(binding.etName.getText().toString()) && !utilityClass.checkEmptyValue(binding.etEmail.getText().toString()) && !utilityClass.checkEmptyValue(binding.etMobile.getText().toString())) {
                if (!utilityClass.isValidEmail(binding.etEmail.getText().toString())) {
                    utilityClass.showAlertDialog(this, "Please correct Email Address");
                    return;
                }
                if (!utilityClass.checkEmptyValue(binding.etName.getText().toString()) && !utilityClass.checkEmptyValue(binding.etEmail.getText().toString()) && !utilityClass.checkEmptyValue(binding.etMobile.getText().toString())) {
                    payByRazorpay(binding.etName.getText().toString(), binding.etEmail.getText().toString(), binding.etMobile.getText().toString(), bookingMoneyByslotbooking);
                    TotalAMountPaid = bookingMoneyByslotbooking;
                } else {
                    utilityClass.showAlertDialog(this, "Please Filled all required box to process the Payments ");
                }

            } else {
                utilityClass.showAlertDialog(this, "Please filled all above Required ");
            }
        });


    }


    public Integer moneyUsed(Integer offerMoney, Integer usedCoin) {
        int half_money = offerMoney / 2;
        int use_money = half_money;
        int use_coin = half_money - usedCoin;
        if (half_money > usedCoin) {
            return use_money + use_coin;
        } else {
            return use_money;
        }

    }

    @Override
    public void onPaymentSuccess(String s) {
        if (checkPaymentUpdate(s)) {
            updateData(s, "CREDIT", "" + TotalAMountPaid, "Paid using upi", "Not Avaialable");
        }else {
            Toast.makeText(this, "Payment Failed Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        utilityClass.showAlertDialogwithpayment(this, "", "", "", "", "", R.drawable.cancel, R.string.payment_failed_message, "Payment Failed");
    }

    public void updateData(String Payment_id, String Booking_status, String Amount_paid, String Reference, String Fail_reason) {
        if (!UtilityClass.getInstance().checkInternetConnection(PaymentActivity.this)) {
            utilityClass.showAlertDialog(PaymentActivity.this, getResources().getString(R.string.internet_connection));
            return;
        }
        if (checked) {
            if (!payroll) {
                if (pageDataModel != null) {
                    int useCoinFind = usedcoin(Integer.parseInt(TotalAMountPaid), pageDataModel.getOfferAmount());
                    use_coin = String.valueOf(useCoinFind);
                } else {

                }
//            }
//            else {
////                if (Integer.parseInt(TotalAMountPaid) > 50) {
//                    use_coin = "50";
            } else {
                int use = usedcoin(Integer.parseInt(TotalAMountPaid), 50);
                use_coin = String.valueOf(use);
            }
        }
//        }
        else {
            use_coin = "0";
        }
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).updateBookingSlot(BuildConfig.BASE_URL + "booking/" + booking_id + "/update" + "?payment_id=" + Payment_id + "&booking_status=" + Booking_status + "&amount_paid=" + Amount_paid + "&reference=" + Reference + "&fail_reason=" + Fail_reason + "&used_coin=" + use_coin, "Bearer" + Prefs.getToken(), booking_id)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                Gson gson = new Gson();
                                boolean error = jsonObject.optBoolean("error");

                                if (!error) {
                                    finishAffinity();
                                    Intent intent = new Intent(PaymentActivity.this, MyBookingActivity.class);
                                    intent.putExtra("isFrompayment",true);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);


                                } else {
                                    String msg = jsonObject.optString("msg");
                                }
                            } else {

                                utilityClass.showAlertDialog(PaymentActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            utilityClass.showAlertDialog(PaymentActivity.this, getResources().getString(R.string.something_went_wrong));
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        utilityClass.showAlertDialog(PaymentActivity.this, getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    public void payByRazorpay(String Name, String Email, String MobileNumber, String Money) {
        String paymoney = String.valueOf(Integer.parseInt(Money) * 100);
        Checkout checkout = new Checkout();
        checkout.setKeyID(Prefs.getKey());
        JSONObject object = new JSONObject();
        try {
            object.put("name ", Name);
            object.put("description", "TATTO ");
            object.put("theme.color", "#000000");
            object.put("currency", "INR");
            object.put("amount", paymoney);
            object.put("prefill.contact", MobileNumber);
            object.put("prefill.email", Email);
            checkout.open(PaymentActivity.this, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public int usedcoin(int Amountpaid, int offeramount) {
        if (Amountpaid > offeramount) {
            return offeramount;
        }
        return offeramount - Amountpaid;
    }

    public boolean checkPaymentUpdate(String paymentId) {
        String url="https://studystadium.com/tattoo/api/v1/razorpay/payment/"+paymentId+"/details";
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).getUpdateData(url,"Bearer " + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                String error = jsonObject.optString("status");
                               if (error.equalsIgnoreCase("failed")){
                                   status=true;
                               }else {
                                   status=false;
                               }

                            } else {

//                                utilityClass.showAlertDialog(ProfileActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            status=false;
                        }
//
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        status=false;
                    }
                });
        return status;
    }
    public void getSecurityKey(){
        String url="https://studystadium.com/tattoo/api/v1/razorpay/get/keys";
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).getUpdateData(url,"Bearer " + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                               Prefs.secretsSaved( jsonObject.optString("Secrets"));;
                               Prefs.keySaved(jsonObject.optString("key"));

                            } else {
                                getSecurityKey();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        getSecurityKey();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                getSecurityKey();
                    }
                });
    }
}
