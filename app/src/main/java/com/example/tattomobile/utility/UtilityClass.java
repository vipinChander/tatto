package com.example.tattomobile.utility;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;


import com.example.tattomobile.R;
import com.example.tattomobile.activity.CalenderActivity;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;

public class UtilityClass {

    private static UtilityClass INSTANCE = null;

    public static KProgressHUD progressDialog = null;
    public static Dialog utilityDialog;
    CalenderActivity fragmentPresenter;

    private UtilityClass() {
    }

    public static UtilityClass getInstance() {
        if (INSTANCE == null)
            INSTANCE = new UtilityClass();
        return INSTANCE;
    }

    public boolean checkInternetConnection(Context context) {
        ConnectivityManager conMgr = null;
        try {
            conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (conMgr != null &&
                conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            System.out.println("Internet Connection Not Present");
            return false;
        }
    }

    public int getWidth(Context con) {
        int desplayWidth;
        WindowManager wm = (WindowManager) con.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        desplayWidth = (width * 60) / 100;
        return desplayWidth;
    }

    public int getHeight(Context con) {
        int topVideoViewHeight;
        WindowManager wm = (WindowManager) con.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        topVideoViewHeight = ((size.x) * 9) / 16;
        return topVideoViewHeight;
    }

    public void showAlertDialog(Activity activity, String text) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.internetpopup_dialog);
        TextView textView = dialog.findViewById(R.id.textViewMessage);

        textView.setText(text);
        Button okBtn = dialog.findViewById(R.id.btnRetry);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView line = dialog.findViewById(R.id.verticalLinePopup);
        line.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        okBtn.setOnClickListener(v -> {
            fragmentPresenter = new CalenderActivity();
            fragmentPresenter.callBackPayment(true);
            dialog.dismiss();
        });


        dialog.show();
    }

    public void showAlertDialog(Activity activity, String text,int tag, MyCallbackResponse myCallbackResponse) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.internetpopup_dialog);
        TextView textView = dialog.findViewById(R.id.textViewMessage);

        textView.setText(text);
        Button okBtn = dialog.findViewById(R.id.btnRetry);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView line = dialog.findViewById(R.id.verticalLinePopup);
        line.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        okBtn.setOnClickListener(v -> {
            myCallbackResponse.okButton(tag);
            dialog.dismiss();
        });
        dialog.show();
    }

    public void setColorStatusBar(Activity actvity) {
        Window window = actvity.getWindow();
        Drawable drawable = actvity.getDrawable(R.drawable.gradient_background);
        window.setStatusBarColor(actvity.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(drawable);
    }

    /*
    public void showDialogWithTwoButtons(DialogTwoButtonResponse dialogTwoButtonResponse, String text, int tag) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.internetpopup_dialog);
        TextView textView = dialog.findViewById(R.id.textViewMessage);

        textView.setText(text);
        Button okBtn = dialog.findViewById(R.id.btnRetry);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView line = dialog.findViewById(R.id.verticalLinePopup);
        okBtn.setText(context.getString(R.string.retry));
        btnCancel.setText(context.getString(R.string.exit));
        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTwoButtonResponse.dialogLeftButtonPressed(dialog, tag);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTwoButtonResponse.dialogRightButtonPressed(dialog, tag);
            }
        });

    }

    public void showDialogDeleteYesCancel(DialogOkButtonResponse dialogOkButtonResponse, String text, int tag) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.delete_video_dialog);
        TextView textView = dialog.findViewById(R.id.textViewMessage);
        textView.setText(text);
        Button btnNo = dialog.findViewById(R.id.btnNo);
        Button btnYes = dialog.findViewById(R.id.btnYes);
        TextView line = dialog.findViewById(R.id.verticalLinePopup);

        dialog.show();

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOkButtonResponse.dialogOkButtonPressed(dialog, tag);
            }
        });

    }

    public void showCustomDialog(DialogOkButtonResponse dialogOkButtonResponse, String text, int tag) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.custom_popup_dialog);
        TextView textView = dialog.findViewById(R.id.textViewMessage);

        textView.setText(text);
        Button okBtn = dialog.findViewById(R.id.btnOk);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();
                dialogOkButtonResponse.dialogOkButtonPressed(dialog, tag);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showDialogForLogin(DialogLoginButtonResponse dialogLoginButtonResponse, String text, int tag) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.internetpopup_dialog);
        TextView textView = dialog.findViewById(R.id.textViewMessage);

        textView.setText(text);
        Button okBtn = dialog.findViewById(R.id.btnRetry);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView line = dialog.findViewById(R.id.verticalLinePopup);
        okBtn.setText(context.getString(R.string.no));
        btnCancel.setText(context.getString(R.string.yes));
        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                dialogLoginButtonResponse.yesBtnLoginResponse();
            }
        });

    }*/


    public KProgressHUD getProgressDialog(Context context, String loaderText) {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.getMessage();
        }
        progressDialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(loaderText)
                .setCancellable(true)
                .setDimAmount(0.5f)
                .setAnimationSpeed(2);

        return progressDialog;
    }

    public KProgressHUD getProgressDialog(Context context) {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.getMessage();
        }
        progressDialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setDimAmount(0.5f)
                .setAnimationSpeed(2);

        return progressDialog;
    }

    public void showDialog(KProgressHUD pDialog) {
        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
    }

    public void hideDialog(KProgressHUD pDialog) {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
//        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public boolean isValidEmail(CharSequence email) {
        if (email == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public TextView createLink(TextView targetTextView, String completeString, String partToClick, ClickableSpan clickableAction) {
        SpannableString spannableString = new SpannableString(completeString);
        int startPosition = completeString.indexOf(partToClick);
        int endPosition = completeString.lastIndexOf(partToClick) + partToClick.length();
        spannableString.setSpan(clickableAction, startPosition, endPosition, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        targetTextView.setText(spannableString);
        targetTextView.setMovementMethod(LinkMovementMethod.getInstance());
        return targetTextView;
    }

    public boolean checkPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(context, RECORD_AUDIO);
        int result3 = ContextCompat.checkSelfPermission(context, CAMERA);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkEmptyValue(String value) {
        if (value == null || value.equals("")) {
            return true;
        }
        return false;

    }

    public void showAlertDialogwithpayment(Activity activity, String OrderId, String PaymentId, String paymentDate, String Amount, String modepay, int iamge, int paymentMessage, String status) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.payment_suceessfully_page);
        TextView textStatus=dialog.findViewById(R.id.payment_successfulltext);
        TextView showMessage=dialog.findViewById(R.id.payment_message);
        ImageView imageView=dialog.findViewById(R.id.image_suceess);
        imageView.setImageDrawable(activity.getDrawable(iamge));
        showMessage.setText(paymentMessage);
        textStatus.setText(status);
        Button okBtn = dialog.findViewById(R.id.btnRetry);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView line = dialog.findViewById(R.id.verticalLinePopup);
        line.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }
}
