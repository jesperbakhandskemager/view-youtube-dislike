package com.jesperh.showyoutubedislikes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class DisplayMessageActivity extends AppCompatActivity {

    Pattern YoutubeRegex = Pattern.compile("^(https?\\:\\/\\/)?((www\\.)?youtube\\.com|youtu\\.be)\\/.+$", Pattern.CASE_INSENSITIVE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        String message = "";

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                message = intent.getStringExtra(Intent.EXTRA_TEXT);

            }
        }
        else {
            // Handle other intents, such as being started from the home screen
            message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        }
        if (!YoutubeRegex.matcher(message).find())
        {
            ErrorPopup();
        }
        GetDataAPI(message);
    }

    void ErrorPopup()
    {
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(DisplayMessageActivity.this);

        // Set the message show for the Alert time
        builder.setMessage("Only Youtube URLS supported!");

        // Set Alert Title
        builder.setTitle("Alert !");

        // Set Cancelable false
        // for when the user clicks on the outside
        // the Dialog Box then it will remain show
        builder.setCancelable(false);

        AlertDialog.Builder Yes = builder
                .setPositiveButton(
                        "Okay",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                // When the user click Okay button
                                // then app returns to the front page
                                finish();
                            }
                        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();
    }


    public static String API_BASE_URL = "https://returnyoutubedislikeapi.com/";
    public static String API_GET_VOTES_QUERY = "votes?videoId=";


    public String GetDataAPI(String YouTubeLink)
    {
        String VIDEO_ID = "";
        if (YouTubeLink.contains("https://youtu.be"))
        {
            VIDEO_ID = YouTubeLink.substring(17, 28);
        }
        else if (YouTubeLink.contains("youtube.com"))
        {
            String[] arrOfStr = YouTubeLink.split("=", 2);
            VIDEO_ID = arrOfStr[1];
        }
        else
        {
            return "";
        }

        String FINAL_URL = API_BASE_URL + API_GET_VOTES_QUERY + VIDEO_ID;

        // Define the text boxes
        final TextView textViewDislikes = (TextView) findViewById(R.id.YTDislikes);
        final TextView textViewLikes = (TextView) findViewById(R.id.YTLikes);
        final TextView textViewViews = (TextView) findViewById(R.id.YTViews);
        final TextView textViewVideoLink = (TextView) findViewById(R.id.YTVideoLink);
        final TextView textViewRatio = (TextView) findViewById(R.id.YTRatio);

        StringRequest myRequest = new StringRequest(Request.Method.GET, FINAL_URL,
                response -> {
                    try{
                        //Create a JSON object containing information from the API.
                        JSONObject myJsonObject = new JSONObject(response);
                        textViewDislikes.setText(AddComma(myJsonObject.getString("dislikes")) + " \uD83D\uDC4E");
                        textViewLikes.setText(AddComma(myJsonObject.getString("likes")) + " \uD83D\uDC4D");
                        textViewRatio.setText(myJsonObject.getString("rating").substring(0, 3) + " / 5 ⭐");
                        textViewViews.setText(AddComma(myJsonObject.getString("viewCount")) + " \uD83D\uDC41️");
                        textViewVideoLink.setText(YouTubeLink + " \uD83D\uDD17");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> ErrorDownloading()
                // Toast.makeText(DisplayMessageActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show()
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(myRequest);

        return "";
    }

    public void ErrorDownloading()
    {
        final TextView textViewDislikes = (TextView) findViewById(R.id.YTDislikes);
        final TextView textViewLikes = (TextView) findViewById(R.id.YTLikes);
        final TextView textViewViews = (TextView) findViewById(R.id.YTViews);
        final TextView textViewVideoLink = (TextView) findViewById(R.id.YTVideoLink);
        final TextView textViewRatio = (TextView) findViewById(R.id.YTRatio);

        textViewDislikes.setText("Error Downloading!");
        textViewLikes.setText("Error Downloading!");
        textViewRatio.setText("Error Downloading!");
        textViewViews.setText("Error Downloading!");
        textViewVideoLink.setText("Error Downloading!");

    }

    public String AddComma(String number)
    {
        double parsedNumber = Double.parseDouble(number);
        DecimalFormat formatter = new DecimalFormat("#,###");
        number = formatter.format(parsedNumber);
        return number;
    }

    public void ClickReturnYouTubeDislikeLink(View view)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://returnyoutubedislike.com/"));
        startActivity(browserIntent);
    }

}