package com.appetite.recipe;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.appetite.model.Recipe;

import java.util.List;

public class NetworkRecipeRequestFragment extends Fragment {
    //logger tag for debugging
    private final static String TAG = NetworkRecipeRequestFragment.class.getSimpleName();

    private static final String RECIPE_NAME = "com.appetite.recipe.NetworkRecipeRequestFragment.RECIPE_NAME";
    private String recipeName;

    public enum DownloadState {
        IDLE, DOWNLOADING, COMPLETED, ERROR, STOPPED
    }

    DownloadState downloadState = DownloadState.IDLE;

    /**
     * Creates a new instance of NetworkRecipeRequestFragment
     * @param recipeName 
     * @return
     */
    public static NetworkRecipeRequestFragment newInstance(String recipeName) {
        NetworkRecipeRequestFragment fragment = new NetworkRecipeRequestFragment();
        Bundle args = new Bundle();
        args.putString(RECIPE_NAME, recipeName);
        fragment.setArguments(args);
        return fragment;
    }

    // Declare some sort of interface that AsyncTask will use to communicate with the Activity
    public interface NetworkRecipeRequestListener {
        void onRequestStarted(boolean isStarted);
        void onRequestProgressUpdate(int progress);
        void onRequestFinished(Recipe result);
    }

    private NetworkTask mTask;
    private NetworkRecipeRequestListener mListener;

    AmazonDynamoDB dynamoDBClient = AWSMobileClient.defaultMobileClient().getDynamoDBClient();
    DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);

    private Recipe mResult;

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: ");
        super.onAttach(context);
        
        // Try to use the Activity as a listener
        if (context instanceof NetworkRecipeRequestListener) {
            mListener = (NetworkRecipeRequestListener) context;
        } else {
            // You can decide if you want to mandate that the Activity implements your callback interface
            // in which case you should throw an exception if it doesn't:
            throw new IllegalStateException("Parent activity must implement NetworkRequestListener");
            // or you could just swallow it and allow a state where nobody is listening
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeName = getArguments().getString(RECIPE_NAME);
        }

        // Retain this Fragment so that it will not be destroyed when an orientation
        // change happens and we can keep our AsyncTask running
        setRetainInstance(true);

        startTask(recipeName);
    }

    /**
     * The Activity can call this when it wants to start the task
     */
    public void startTask(String recipe) {
        Log.d(TAG, "startTask: recipe = " + recipe);
        mTask = new NetworkTask();
        mTask.execute(recipe);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
        // If the AsyncTask finished when we didn't have a listener we can
        // deliver the result here
        if ((mResult != null) && (mListener != null)) {
            mListener.onRequestFinished(mResult);
            mResult = null;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();

        // We still have to cancel the task in onDestroy because if the user exits the app or
        // finishes the Activity, we don't want the task to keep running
        // Since we are retaining the Fragment, onDestroy won't be called for an orientation change
        // so this won't affect our ability to keep the task running when the user rotates the device
        if ((mTask != null) && (mTask.getStatus() == AsyncTask.Status.RUNNING)) {
            Log.d(TAG, "onDestroy: TASK CANCELLATO");
            mTask.cancel(true);
            //TODO state STOPPED?!?! boh..si distrugge tanto..sembra funzionare così
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // This is VERY important to avoid a memory leak (because mListener is really a reference to an Activity)
        // When the orientation change occurs, onDetach will be called and since the Activity is being destroyed
        // we don't want to keep any references to it
        // When the Activity is being re-created, onAttach will be called and we will get our listener back
        mListener = null;
    }

    /**
     * AsyncTask that downloads a Recipe from DB
     */
    private class NetworkTask extends AsyncTask<String, Integer, Recipe> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: ");
            ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if(isConnected) {
                downloadState = DownloadState.DOWNLOADING;
                if (mListener != null) {
                    mListener.onRequestStarted(true);
                }
            }
            else {
                downloadState = DownloadState.ERROR;
                this.cancel(true);
                if (mListener != null) {
                    mListener.onRequestStarted(false);
                }
            }
        }

        @Override
        protected Recipe doInBackground(String... urls) {
            Log.d(TAG, "doInBackground: recipe = " + urls[0]);
                try {
                    Recipe recipeItem = new Recipe();
                    recipeItem.setName(urls[0]);
                    DynamoDBQueryExpression<Recipe> queryExpression = new DynamoDBQueryExpression<Recipe>()
                            .withHashKeyValues(recipeItem);

                    List<Recipe> result = mapper.query(Recipe.class, queryExpression);
                    if(result.size() != 0) {
                        String imageUri = result.get(0).getCategory() + "/" + result.get(0).getImage() + ".jpg";
                        result.get(0).setImage(imageUri);
                        downloadState = DownloadState.COMPLETED;
                        return result.get(0);
                    }  else {
                        downloadState = DownloadState.ERROR; //TODO recipe non presente nel DB
                        return null;
                    }
                } catch (RuntimeException e) {
                    downloadState = DownloadState.ERROR;
                    Log.e(TAG, "doInBackground: RuntimeException: " + e.getMessage()); //TODO errore connessione
                    return null;
                }
            }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG, "onProgressUpdate: ");
            if (mListener != null) {
                mListener.onRequestProgressUpdate(progress[0]);
            }
        }

        @Override
        protected void onPostExecute(Recipe result) {
            Log.d(TAG, "onPostExecute: ");
            if (mListener != null) {
                mListener.onRequestFinished(result);
            } else {
                // If the task finishes while the orientation change is happening and while
                // the Fragment is not attached to an Activity, our mListener might be null
                // If you need to make sure that the result eventually gets to the Activity
                // you could save the result here, then in onActivityCreated you can pass it back
                // to the Activity
                mResult = result;
            }
        }

    }

    public DownloadState getDownloadState() {
        return downloadState;
    }
}