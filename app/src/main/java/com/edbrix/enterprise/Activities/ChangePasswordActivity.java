package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.MainActivity;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class ChangePasswordActivity extends AppCompatActivity {

    Context context;

    RelativeLayout layout;
    TextInputEditText _change_password_edit_text_password;
    TextInputEditText _change_password_edit_text_confirm_password;
    Button _change_password_button_submit;
    ProgressBar _change_password_progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        context = this;
        getSupportActionBar().setTitle("Change Password ");

        final ActionBar ab = ((AppCompatActivity) context).getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        _change_password_edit_text_password = findViewById(R.id.change_password_edit_text_password);
        _change_password_edit_text_confirm_password = findViewById(R.id.change_password_edit_text_confirm_password);
        _change_password_button_submit = findViewById(R.id.change_password_button_submit);
        _change_password_progress_bar = findViewById(R.id.change_password_progress_bar);

        _change_password_button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setChangePassword(String password) {

        User user = SettingsMy.getActiveUser();

        if (user!=null) {

            JSONObject jo = new JSONObject();
            try {

                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("UserType", user.getUserType());
                jo.put("Password", password);

            } catch (JSONException e) {
                Timber.e(e, "Parse logInWithEmail exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Login user: %s", jo.toString());

            GsonRequest<ResponseData> userChangePasswordRequest = new GsonRequest<>(Request.Method.POST, Constants.changePassword, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {

                            Timber.d("response: %s", response.toString());
                            if (response.getErrorCode()==null) {
                                Toast.makeText(context, "Success, Please login with new password ", Toast.LENGTH_SHORT).show();
                                SettingsMy.setActiveUser(null);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            else {

                                try {
                                    Timber.d("Error: %s", response.getErrorMessage());
                                    Snackbar.make(layout, response.getErrorMessage(), Snackbar.LENGTH_LONG).show();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                    Timber.d("Error: %s", response.getErrorMessage());
                                    Toast.makeText(context, response.getErrorMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            });
            userChangePasswordRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            userChangePasswordRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(userChangePasswordRequest, "change_password_requests");
        }
    }

}
