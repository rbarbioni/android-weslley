package br.com.wesley;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final static String URL = "https://api.github.com/users";

    ImageView profileImageView;

    TextView nameTextView;

    TextView campanyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText profileText = (EditText) this.findViewById(R.id.profileText);
        final Button findButton = (Button) this.findViewById(R.id.findButton);

        profileImageView = (ImageView) this.findViewById(R.id.profileImageView);
        nameTextView = (TextView) this.findViewById(R.id.nameTextView);
        campanyTextView = (TextView) this.findViewById(R.id.campanyTextView);

        profileText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                final String profile = profileText.getText().toString();

                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    getResult(profile);
                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                }

                return true;
            }
        });

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String profile = profileText.getText().toString();

                getResult(profile);

            }
        });
    }

    private void getResult(final String profile){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    java.net.URL url = new URL(URL + "/" + profile);

                    Log.d("GET ", url.toString());
                    InputStream inputStream = url.openStream();
                    final String result = getStringFromInputStream(inputStream);
                    Log.d("RESULT: ", result);

                    try {

                        final JSONObject json = new JSONObject(result);
                        final java.net.URL imageURL = new URL(json.getString("avatar_url"));
                        final InputStream inputStreamImage = imageURL.openStream();
                        final String name = json.getString("name");
                        final String company = json.getString("company");
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStreamImage);

                        profileImageView.getHandler().post(new Runnable() {
                            @Override
                            public void run() {

                                if(bitmap != null){
                                    profileImageView.setImageBitmap(bitmap);
                                }

                                if(name != null){
                                    nameTextView.setText(name);
                                }

                                if(company != null){
                                    campanyTextView.setText(company);
                                }
                            }
                        });


                    } catch (JSONException e) {
                        Log.e("ERROR","JSON", e);
                    }


                } catch (MalformedURLException e) {
                    Log.e("ERROR","URL", e);
                } catch (IOException e) {
                    Log.e("ERROR", "HTTP", e);
                }
            }
        }).start();
    }

    public static String getStringFromInputStream(InputStream stream) throws IOException {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
        return writer.toString();
    }
}
