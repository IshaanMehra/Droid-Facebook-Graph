package com.example.testapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
  private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
     
    public static String first_name, last_name, username, email_id, location, gender, dob, locale;
    
    /* Calling Json Object */
    JSONParser jsonParser = new JSONParser();
    
    /* Json Node Names */
    //private static final String TAG_SUCCESS = "success";
    
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                
                getActionBar().hide();
                
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        Log.d("DEBUG", "Button permission asked");
        LoginButton button = (LoginButton) findViewById(R.id.authButton);
        button.setReadPermissions(Arrays.asList("basic_info","email", "user_location", "user_birthday", "user_likes", "user_interests"));
        Log.d("DEBUG", "button permission received");
        }

    @SuppressWarnings("deprecation")
        private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    	
    	Log.d("DEBUG", "checking if facebook session is null or not");
            if (session != null && session.isOpened()) {
                    Log.d("DEBUG", "facebook session is open ");
                    
                    
                    /* Making the login Button Invisible */
                    LoginButton button = (LoginButton) findViewById(R.id.authButton);
                    button.setVisibility(View.GONE);
                    
                    // make request to the /me API
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                // callback after Graph API response with user object
                    
                @Override
                public void onCompleted(GraphUser user, Response response) {
                        Log.d("DEBUG", "inside on complete ");
                        if (user != null) {
                                Log.d("DEBUG", "email: " + user.asMap().get("email").toString());
                                
                                first_name = user.asMap().get("first_name").toString();
                                last_name = user.asMap().get("last_name").toString();
                                username = user.asMap().get("username").toString();
                                email_id = user.asMap().get("email").toString();
                                location = user.getLocation().getProperty("name").toString();
                                locale = user.getProperty("locale").toString();
                                gender = user.asMap().get("gender").toString();
                                dob = user.getBirthday();
                                
                                Log.d("DEBUG", "First Name " + first_name + last_name + " "+ 
                                           username + location +" "+locale+" "+gender+" "+dob);
                                
                                //new AddInDatabase().execute();
                               
                                //Start a new Activity 
                                Intent intent = new Intent(getApplicationContext(), LandingPage.class);
                                MainActivity.this.finish();
                                startActivity(intent);
                                Log.d("check", "activity diverted");
                        }
                        else
                        	Log.d("Debug", "some error here");
                }
            });
            }
    }
    
        private class AddInDatabase extends AsyncTask<String, String, String> {
        	
        	/**
    		 * Before starting background thread Show Progress Dialog
    		 * */
    		@Override
    		protected void onPreExecute() {
    			super.onPreExecute();
    			/* Making the Loading Text View visible*/
                TextView loading = (TextView) findViewById(R.id.loading);
                loading.setVisibility(View.VISIBLE);
                
                Log.d("DEBUG", "First Name " + first_name + last_name + " "+ 
                        username + location +" "+locale+" "+gender+" "+dob);
    		}
    		
    		/**
    		 * Adding into database
    		 * */
    		protected String doInBackground(String... args) {
    			/* Adding into Database */
                
                /* Building Parameters */
    			List<NameValuePair> params = new ArrayList<NameValuePair>();
    			params.add(new BasicNameValuePair("FirstName", first_name));
    			params.add(new BasicNameValuePair("LastName", last_name));
    			params.add(new BasicNameValuePair("UserName", username));
    			params.add(new BasicNameValuePair("EmailId", email_id));
    			params.add(new BasicNameValuePair("Location", location));
    			params.add(new BasicNameValuePair("Locale", locale));
    			params.add(new BasicNameValuePair("Gender", gender));
    			params.add(new BasicNameValuePair("DOB", dob));
    			
    			Log.d("DEBUG", "Building NameValuePair");
    			
    			/* Calling Json Object */
    		    JSONParser jsonParser = new JSONParser();
    		    
                
    			/* Getting Json Object */
    			/* Note that insert into database URL accepts POST method */
    			//JSONObject json = jsonParser.makeHttpRequest(url_insert_data, "POST", params);
    			
    			Log.d("DEBUG", "Json Object Created");
    			
    			/* Check CAT Log for response */
    			//Log.d("Create Response", json.toString());
    			
    			// check for success tag
    			 /*try {
    			 	   int success = json.getInt(TAG_SUCCESS);
    			 	    
    			 	   if (success == 1 || success == 3) 
    			 	   {
    			 		  /* Start a new Activity */
                          /* Intent intent = new Intent(getApplicationContext(), LandingPage.class);
                           FacebookLogin.this.finish();
                           startActivity(intent);
    					} else 
    					{
    						// failed to add into database
    						System.out.print("Failed to add into the database");
    					}
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}  */

    			
    			/* Finished adding into the database */
    			
    			return null;
    		}
    		
    		
    		/**
    		 * After completing background task Dismiss the progress dialog
    		 * **/
    		protected void onPostExecute(String file_url) {
    			
    		}
        	
        }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}