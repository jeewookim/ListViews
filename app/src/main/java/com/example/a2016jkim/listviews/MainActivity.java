package com.example.a2016jkim.listviews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String username = "";
    private String hometown = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Construct the data source
        final ArrayList<User> arrayOfUsers = new ArrayList<User>();
        // Create the adapter to convert the array to views
        final UsersAdapter adapter = new UsersAdapter(this, arrayOfUsers);
        // Attach the adapter to a ListView
        final ListView listView = (ListView) findViewById(R.id.lvItems);
        listView.setAdapter(adapter);

        final EditText editText = (EditText) findViewById(R.id.uedittext);
        editText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username = editText.getText().toString();
                Log.i("name ", "" + username);
            }
        });


        final EditText editText2 = (EditText) findViewById(R.id.hedittext);
        editText2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hometown = editText2.getText().toString();
                Log.i("hometown ", "" + hometown);
            }
        });

        //create new
        Button button = (Button)findViewById(R.id.createnew);
        final Firebase ref = new Firebase("https://listing-user-homes.firebaseio.com");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vew) {
                User madeUser = new User(username, hometown);
                Log.i("hometown2: ", "" +hometown);
                adapter.add(madeUser);

                ref.createUser(username, hometown, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        ref.child("new user").setValue("new user: " + username, "hometown: " + hometown);
                        Log.i("hometown3:", hometown + "");
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.a2016jkim.listviews");
                        startActivity(launchIntent);
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                    }
                });
            }
        });

        //retrieve home
        Button rhome = (Button) findViewById(R.id.retrievehome);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String person = username;
                String home = "";

                ref.child("new user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        System.out.println(snapshot.getValue());
                    }
                    @Override public void onCancelled(FirebaseError error) { }
                });

                adapter.add(new User(person, home));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class User {
        public String name;
        public String hometown;

        public User(String name, String hometown) {
            this.name = name;
            this.hometown = hometown;
        }
    }


    public class UsersAdapter extends ArrayAdapter<User> {
        public UsersAdapter(Context context, ArrayList<User> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            User user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
            // Populate the data into the template view using the data object
            tvName.setText(user.name);
            tvHome.setText(user.hometown);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}


