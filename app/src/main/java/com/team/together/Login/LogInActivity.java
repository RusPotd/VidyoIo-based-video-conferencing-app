package com.team.together.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import com.team.together.MainActivity;
import com.team.together.Models.UserList;
import com.team.together.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LogInActivity extends AppCompatActivity {
    private Context mContext = LogInActivity.this;

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private boolean mVerificationInProgress = false;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID;


    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ConstraintLayout mLayoutMobileNumber, mLayoutOTP;
    private Button mbtnLogin, mGetOtp, mVerifyOTP;
    private EditText mMobileNumber, mOTPcode;
    private TextView mTxtMbNum, mTxtWrongNum, mTxtTimer, mTxtResendCode;

    private CountryCodePicker mCountryCodePicker;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setupUI(findViewById(R.id.login_activity));

        mbtnLogin = (Button) findViewById(R.id.btn_login);
        mLayoutMobileNumber = (ConstraintLayout) findViewById(R.id.layout_mobile_number);
        mLayoutOTP = (ConstraintLayout) findViewById(R.id.layout_otp);
        mMobileNumber = (EditText) findViewById(R.id.mobile_number);
        mCountryCodePicker = (CountryCodePicker) findViewById(R.id.country_code);
        mCountryCodePicker.registerCarrierNumberEditText(mMobileNumber);

        mOTPcode = (EditText) findViewById(R.id.otp_number);
        mGetOtp = (Button) findViewById(R.id.btn_next);
        mVerifyOTP = (Button) findViewById(R.id.btn_verify_otp);
        mTxtMbNum = (TextView) findViewById(R.id.mb_num);
        mTxtWrongNum = (TextView) findViewById(R.id.wrong_num);
        mTxtTimer = (TextView) findViewById(R.id.txt_timer);
        mTxtResendCode = (TextView) findViewById(R.id.txt_resend_code);
        mTxtResendCode.setVisibility(View.GONE);
        initialState();

        mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.custom);

        mbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMobileNumberLayout();
            }
        });

        mGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mbnum = mCountryCodePicker.getFormattedFullNumber();

                if (!mCountryCodePicker.isValidFullNumber()){
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Invalid Number", Snackbar.LENGTH_LONG);
                    snackbar.show();


                }else {
                    mDialog.show();
                    startPhoneNumberVerification(mbnum);
                    mTxtMbNum.setText(mbnum);


                }

            }
        });

        mVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
                String code = mOTPcode.getText().toString();
                if (code.isEmpty() || code.length() < 6){
                    mDialog.dismiss();
                    Toast.makeText(mContext, "Enter valid code", Toast.LENGTH_SHORT).show();

                }else {
                    verifyPhoneNumberWithCode(mVerificationId, code);
                }

            }
        });

        mTxtResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
                String mbnum = mCountryCodePicker.getFormattedFullNumber();
                resendVerificationCode(mbnum, mResendToken);


            }
        });


        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                mVerificationInProgress = false;
                mOTPcode.setText(credential.getSmsCode());
                mDialog.show();

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(mContext, "error = "+e, Toast.LENGTH_SHORT).show();
                mVerificationInProgress = false;
                mDialog.dismiss();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Invalid Number", Snackbar.LENGTH_LONG);
                    snackbar.show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Limit exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }else {
                    showMobileNumberLayout();
                    Toast.makeText(mContext, "failed try again", Toast.LENGTH_SHORT).show();

                }
                showMobileNumberLayout();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                mTxtResendCode.setVisibility(View.GONE);
                mTxtTimer.setVisibility(View.VISIBLE);
                mDialog.dismiss();
                mVerificationId = verificationId;
                mResendToken = token;
                showOtpLayout();

                //timer
                new CountDownTimer(60000, 1000) {
                    @SuppressLint("SetTextI18n")
                    public void onTick(long millisUntilFinished) {
                        mTxtTimer.setText( ""+millisUntilFinished / 1000);

                    }
                    public void onFinish() {
                        mTxtTimer.setVisibility(View.GONE);
                        mTxtResendCode.setVisibility(View.VISIBLE);

                    }
                }.start();

                Toast.makeText(mContext, "OTP Send", Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void startPhoneNumberVerification(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                LogInActivity.this,
                mCallbacks);
        mVerificationInProgress = true;
    }


    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                LogInActivity.this,
                mCallbacks,
                token);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(credential);
    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = task.getResult().getUser();
                            mAuth = FirebaseAuth.getInstance();
                            mFirebaseDatabase = FirebaseDatabase.getInstance();
                            myRef = mFirebaseDatabase.getReference();
                            String mbnum = mCountryCodePicker.getFormattedFullNumber();


                            userID = user.getUid();

                            UserList userList = new UserList(userID,mbnum,getTimestamp());

                            myRef.child(mContext.getString(R.string.dbname_users_list))
                                    .child(userID)
                                    .setValue(userList);

                            mDialog.dismiss();

                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


                        } else {
                            mDialog.dismiss();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(mContext, "Invalid code.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private String getTimestamp(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(new Date());
    }

    private void initialState(){
        mbtnLogin.setVisibility(View.VISIBLE);
        mLayoutMobileNumber.setVisibility(View.GONE);
        mLayoutOTP.setVisibility(View.GONE);
    }

    private void showMobileNumberLayout(){
        mbtnLogin.setVisibility(View.GONE);
        mLayoutMobileNumber.setVisibility(View.VISIBLE);
        mLayoutOTP.setVisibility(View.GONE);

    }

    private void showOtpLayout(){
        mbtnLogin.setVisibility(View.GONE);
        mLayoutMobileNumber.setVisibility(View.GONE);
        mLayoutOTP.setVisibility(View.VISIBLE);
    }



    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        View focusedView = activity.getCurrentFocus();

        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

        }
    }
    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(LogInActivity.this);
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

}
