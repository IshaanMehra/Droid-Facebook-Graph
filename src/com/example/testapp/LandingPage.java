package com.example.testapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

@SuppressLint("NewApi")
public class LandingPage extends Activity {
  private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
     
    private Facebook mFacebook;
    
    
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final int REAUTH_ACTIVITY_CODE = 100;
    
    public static String first_name, last_name, username, email_id, location, gender, dob, locale;
    
    
    JSONArray likes = null;
    JSONArray interests = null;
    
    private ProgressDialog pDialog;
    
    public TextView details, ulikes, uinterests;
    
    static final String TAG_DATA = "data";
   
    /* Json Node Names */
    //private static final String TAG_SUCCESS = "success";
    Session session = Session.getActiveSession();
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.anding_page);
                

                details = (TextView)findViewById(R.id.details);
                ulikes = (TextView) findViewById(R.id.likes);
                uinterests = (TextView) findViewById(R.id.interests);
                
                LoginButton button = (LoginButton) findViewById(R.id.authButton);
                
                button.setOnClickListener(new View.OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					if (Session.getActiveSession() != null) {
    					    Session.getActiveSession().closeAndClearTokenInformation();
    					    
    					    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            LandingPage.this.finish();
                            startActivity(intent);
    					}

    					Session.setActiveSession(null);
    				}
    			});
                //getActionBar().hide();
                
                if (session != null &&
                        (session.isOpened() || session.isClosed()) ) {
                     onSessionStateChange(session, session.getState(), null);
                     
                     Log.d("DEBUG", "checking if facebook session is null or not");
                     if (session != null && session.isOpened()) {
                             Log.d("DEBUG", "facebook session is open ");
                             
                             
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
                                         
                                         String user_info = "First Name : " + first_name + "\n" +
                                         		"Last Name : " + last_name + "\n" +
                                         		"Username : " + username + "\n" +
                                         		"Email Id : " + email_id + "\n" +
                                         		"Location : " + location + "\n" +
                                         		"Gender : " + gender + "\n" +
                                         		"DOB : " + dob;
                                         		
                                         		 
                                         Log.d("user_info", "very nice");   
                                         Log.d("DEBUG", "First Name " + first_name + last_name + " "+ 
                                                    username + location +" "+locale+" "+gender+" "+dob);
                                         
                                         details.setText(user_info);
                                 }
                                 
                         }
                     });
                     
                     new Request(
                     	    session,
                     	    "/me/likes",
                     	    null,
                     	    HttpMethod.GET,
                     	    new Request.Callback() {
                     	        public void onCompleted(Response response) {
                     	        	try
                     	            {
                     	        		Log.d("likes", "is it?");
                     	                GraphObject go  = response.getGraphObject();
                     	                JSONObject  jso = go.getInnerJSONObject();
                     	                JSONArray   arr = jso.getJSONArray( "data" );
                                         String like_string = null;
                     	                for ( int i = 0; i < ( arr.length() ); i++ )
                     	                {
                     	                    JSONObject json_obj = arr.getJSONObject( i );

                     	                    String name     = json_obj.getString( "name").toString();
                     	                    String category   = json_obj.getString( "category").toString();
                                             Log.d("likes",name);
                                             like_string = like_string + name + "\n" + category;
                                             like_string = like_string + "\n\n";
                     	                    
                     	                }
                     	                ulikes.setText(like_string);
                     	            }
                     	            catch ( Throwable t )
                     	            {
                     	                t.printStackTrace();
                     	            }
                     	        	
                     	        }
                     	    }
                     	).executeAsync();
                     }
                     new Request(
                     	    session,
                     	    "/me/interests",
                     	    null,
                     	    HttpMethod.GET,
                     	    new Request.Callback() {
                     	        public void onCompleted(Response response) {
                     	            /* handle the result */
                     	        	
                     	        	try
                     	            {
                     	                GraphObject go  = response.getGraphObject();
                     	                JSONObject  jso = go.getInnerJSONObject();
                     	                JSONArray   arr = jso.getJSONArray( "data" );
                                         String interest_string = null;
                     	                for ( int i = 0; i < ( arr.length() ); i++ )
                     	                {
                     	                    JSONObject json_obj = arr.getJSONObject( i );

                     	                    String name     = json_obj.getString( "name").toString();
                     	                    String category   = json_obj.getString( "category").toString();
                                             Log.d("interstS", name);
                                             interest_string = interest_string + name + "\n" + category;
                                             interest_string = interest_string + "\n\n";
                     	                    
                     	                }
                     	                uinterests.setText(interest_string);
                     	            }
                     	            catch ( Throwable t )
                     	            {
                     	                t.printStackTrace();
                     	            }
                     	        	
                     	        
                     	        }
                     	    }
                     	).executeAsync();
                 }

                
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        
        }
        
        

    @SuppressWarnings("deprecation")
        private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    	
            
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
 
        return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.action_shout:
            // location found
			AlertDialog.Builder build = new AlertDialog.Builder(LandingPage.this);
			LayoutInflater inflater = LandingPage.this.getLayoutInflater();
			View view = null;
			view = inflater.inflate(R.layout.update, null);
			build.setTitle("Post an Update");
			build.setView(view);
			
			final EditText update = (EditText)view.findViewById(R.id.update);
			
			            
			if (session != null &&
                    (session.isOpened() || session.isClosed()) ) {
                 onSessionStateChange(session, session.getState(), null);
                 
             	List<String> permissions = session.getPermissions();

             	if (!permissions.contains("publish_actions"))
             	{
             	Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(LandingPage.this, Arrays.asList("publish_actions"));

             	session.requestNewPublishPermissions(newPermissionsRequest);
             	}
             	
			}
			
			build.setPositiveButton("Update", new DialogInterface.OnClickListener()
			{
				@Override
	               public void onClick(DialogInterface dialog, int id) {
					
					//AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);
					final String text = update.getText().toString();

					Bundle postParams = new Bundle();
				    postParams.putString("message", text);
			        postParams.putString("name", "Test App");
			        postParams.putString("caption", "testing the android app");
			        
			       //new PostMessage().execute(text);
			        
			        Request request = Request
			                .newStatusUpdateRequest(Session.getActiveSession(), text, new Request.Callback() {
			                    @Override
			                    public void onCompleted(Response response) {
			                        showPublishResult(text, response.getGraphObject(), response.getError());
			                    }
			                });
			        request.executeAsync();
				}
			        
				});
		    
				        
			
			
			build.setNegativeButton("Go Back", new DialogInterface.OnClickListener()
			{
				@Override
	               public void onClick(DialogInterface dialog, int id) {
					 // Do nothing !
	               }
			});
			
			build.show();
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
	
	
	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		//menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
	    String title = null;
	    String alertMessage = null;
	    if (error == null) {
	        title = "Success";

	        alertMessage = "Status Updated";
	    } else {
	        title = "Error";
	        alertMessage = error.getErrorMessage();
	    }

	    new AlertDialog.Builder(this)
	            .setTitle(title)
	            .setMessage(alertMessage)
	            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                    finish();
	                }
	            })
	            .show();
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
    
    class PostMessage extends AsyncTask<String, Process, Void> {

  		int check = 0;
  		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LandingPage.this);
			pDialog.setMessage("Posting to Facebook\nPlease wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

  		@Override
  		protected void onPostExecute(Void result) {
  		// TODO Auto-generated method stub
  		super.onPostExecute(result);
  		pDialog.dismiss();
  		
  		//Toast.makeText(getApplicationContext(), "Posted to Facebook", Toast.LENGTH_SHORT).show();
  		
  		}
  		@SuppressWarnings("deprecation")
		@Override
  		protected Void doInBackground(String... params) {
  			// TODO Auto-generated method stub
  			String news = params[0];
  			Bundle parameters = new Bundle();
  			parameters.putString("message", news);
  			parameters.putString("description", "Test App");
  			try {
  				
  				//Toast.makeText(getApplicationContext(), "Something will happen now", Toast.LENGTH_SHORT).show();
  				Request.newStatusUpdateRequest(
  	             	    session,
  	             	    news, null); 
  				
  				
  			
  			//finish();
  			} catch (Exception e) {
            Log.d("Fails", "every time");
  			e.printStackTrace();

  			}
  			return null;
  		}

  	}
}