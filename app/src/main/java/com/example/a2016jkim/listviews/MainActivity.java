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

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    private String username = "";
    private String hometown = "";
    private String justname = "";
    private String home = "";

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
                adapter.add(madeUser);
                Log.i("In onclick: " , "true");
                ref.authWithPassword(username, hometown, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                        System.out.println("Successfully authenticated");
                        ref.child("logged in").setValue("user: " + username + " logged in");
                        for (int x = 0; x < username.length()-1; x++)
                        {
                            if (!((username.substring(x,x+1)).equals("@")))
                            {
                                justname += username.substring(x, x+1);
                            }
                            else
                            {
                                x = username.length()-1;
                            }
                        }
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        final String user = username;
                        final String home = hometown;
                        for (int x = 0; x < user.length()-1; x++)
                        {
                            if (!((user.substring(x,x+1)).equals("@")))
                            {
                                justname += username.substring(x, x+1);
                            }
                            else
                            {
                                x = username.length()-1;
                            }
                        }
                        ref.createUser(user, home, new Firebase.ValueResultHandler<Map<String, Object>>() {
                            @Override
                            public void onSuccess(Map<String, Object> result) {
                                String s = ("Successfully created personal user account with uid: " + result.get("uid"));
                                ref.child(justname).setValue( home);
                                System.out.println(s);
                                justname = "";
                            }
                            @Override
                            public void onError(FirebaseError firebaseError) {
                                Log.i("error while: ", "creating new");
                            }
                        });
                    }
                });
            }
        });

        //retrieve home

        Button rhome = (Button) findViewById(R.id.retrievehome);
        rhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String person = justname;
                Log.i("just name: ", justname);
                ref.child(person).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        System.out.println(snapshot.getValue());
                        //home = (snapshot.getValue()).toString();
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
        public String home;

        public User(String name, String hometown) {
            this.name = name;
            this.home = hometown;
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
            tvHome.setText(user.home);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}

