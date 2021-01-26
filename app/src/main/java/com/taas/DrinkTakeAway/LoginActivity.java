package com.taas.DrinkTakeAway;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "prova" ;

    protected CallbackManager facebookCallbackManager;

    //String googleAuthUrl = "http://192.168.1.90:7777/oauth/google";
    //String facebookAuthUrl = "http://192.168.1.90:7777/oauth/facebook";


    String googleAuthUrl = "http://192.168.49.2:30001/oauth/google";
    String facebookAuthUrl = "http://192.168.49.2:30001/oauth/facebook";


    GoogleSignInClient mGoogleSignInClient;
    LoginButton facebookLoginButton;

    Context context=this;

    int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        //GOOGLE
        googleInit();

        //FACEBOOK
        facebookInit();
    }


    public void googleInit(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick2(v);
            }
        };


        signInButton.setOnClickListener(onClickListener);

    }

    public void facebookInit(){

        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_sin_in_button);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile,email,user_friends,user_birthday"));

        facebookCallbackManager = CallbackManager.Factory.create();
        facebookLoginButton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final Profile profile = Profile.getCurrentProfile();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {

                                    System.out.println("Questo è l'object"+object);
                                    System.out.println("Questo è il response"+response);

                                    System.out.println("Questo è il Nome "+object.getString("name"));
                                    System.out.println("Questo è il id"+object.getString("id"));
                                    System.out.println("Questo è il email"+object.getString("email"));

                                    String idToken = AccessToken.getCurrentAccessToken().getToken();
                                    sendIDToken(facebookAuthUrl, idToken);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name, birthday,picture,email");
                request.setParameters(parameters);
                request.executeAsync();

            }
            @Override
            public void onCancel() {

                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });



        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                //System.out.println("Questo è accesso token "+currentAccessToken);
                System.out.println("Questo è accesso OLDtoken " + oldAccessToken);
            }
        };

        // If the access token is available already assign it.
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        System.out.println("Questo è accesso token "+accessToken);

    }


    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        AccessToken facebookAccessToken = AccessToken.getCurrentAccessToken();

        boolean isLoggedInFacebook = facebookAccessToken != null && !facebookAccessToken.isExpired();
        boolean isLoggedInGoogle = googleAccount != null && !googleAccount.isExpired();

        if (isLoggedInGoogle)
            sendIDToken(googleAuthUrl, googleAccount.getIdToken());
        else if (isLoggedInFacebook)
                sendIDToken(facebookAuthUrl, facebookAccessToken.getToken());

        //Intent intent = new Intent(context, MapsActivity.class);
        //startActivity(intent);

        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

    }



    public void onClick2(View view) {
        Log.d(TAG, "onClick started");
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                googleSignIn();
                break;
        }
    }


    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attachS
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }


    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            String idToken = account.getIdToken();
            String id = account.getId();
            String mail = account.getEmail();

            sendIDToken(googleAuthUrl, idToken);

        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }



    public void sendIDToken(String url, String idToken){

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("value", idToken);

        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Intent intent;
                            Toast.makeText(getApplicationContext(), "Login successful",
                                    Toast.LENGTH_LONG).show();

                            //Save token here
                            String token = response.get("token").toString();
                            SharedPreferences preferences = LoginActivity.this.getSharedPreferences("drink_take_away",Context.MODE_PRIVATE);
                            preferences.edit().putString("token",token).apply();

                            if (response.get("role").equals("barista"))
                                intent = new Intent(context, BartenderActivity.class);
                            else
                                intent = new Intent(context, MapsActivity.class);
                            startActivity(intent);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };;
        Volley.newRequestQueue(this).add(req);
    }

}