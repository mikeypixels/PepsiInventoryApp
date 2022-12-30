package com.example.michael.pepsiinventory;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import io.getstream.avatarview.AvatarView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private Toolbar toolbar;
    private ImageView imgNavHeaderBg;
    private ViewPager viewPager;
    AvatarView img_profile;
    TextView textView;
    String f_name,l_name;
    String TAG = MainActivity.class.getSimpleName();
    EditText new_passwd, confirm_passwd;
    Button submitBtn, cancelBtn;
    String password_update_url;
    CoordinatorLayout coordinatorLayout;

    private SectionsPageAdapter sectionsPageAdapter;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    public static String CURRENT_TAG = TAG_HOME;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    String store_id,user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);
        getWindow().setAllowEnterTransitionOverlap(true);
        setupWindowAnimations();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        store_id = myPrefs.getString("store_id", "");
        user_id = myPrefs.getString("user_id", "");

        mHandler = new Handler();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        viewPager = findViewById(R.id.container);
        setupViewPager(viewPager,user_id,store_id);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
        img_profile = navHeader.findViewById(R.id.img_profile);
        textView = navHeader.findViewById(R.id.user_name);

        // load nav menu header data
        loadNavHeader();

        textView.setText(myPrefs.getString("first_name","").concat(" " + myPrefs.getString("last_name","")));
        textView.setTextColor(Color.WHITE);

        // initializing navigation menu
        setUpNavigationView();

//        if (savedInstanceState == null) {
//            navItemIndex = 0;
//            CURRENT_TAG = TAG_HOME;
//            loadHomeFragment();
//        }
    }

    private void setupWindowAnimations() {
        Slide slide = new Slide();
        slide.setDuration(500);
        getWindow().setEnterTransition(slide);
    }

    private void setupViewPager(ViewPager viewPager,String user_id, String store_id){

        String TAG = MainActivity.class.getSimpleName();

        String title1 = "Sales";

        Log.d(TAG,"OnReceive: " + user_id);

        SalesFragment salesFragment = new SalesFragment();
        salesFragment.getStoreUserId(store_id,user_id);

        ExpenseFragment expenseFragment = new ExpenseFragment();
        expenseFragment.getStoreUserId(store_id,user_id);

        AdminSalesFragment adminSalesFragment = new AdminSalesFragment();
        adminSalesFragment.getStoreUserId(store_id,user_id);

        AdminExpenseFragment adminExpenseFragment = new AdminExpenseFragment();
        adminExpenseFragment.getStoreUserId(store_id,user_id);

        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if(sharedPreferences.getString("role","").equals("Main Admin")||sharedPreferences.getString("role","").equals("Admin")){
            adapter.addFragment(adminSalesFragment, "Mauzo");
            adapter.addFragment(adminExpenseFragment, "Matumizi");
            viewPager.setAdapter(adapter);
        }else if(sharedPreferences.getString("role","").equals("Worker")){
            adapter.addFragment(salesFragment, "Mauzo");
            adapter.addFragment(expenseFragment, "Matumizi");
            viewPager.setAdapter(adapter);
        }

    }

    private void loadNavHeader() {
        // loading header background image
        imgNavHeaderBg.setImageDrawable(getResources().getDrawable(R.drawable.bg));

    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_mysales:
                        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        if(preferences.getString("role","").equals("Main Admin")||preferences.getString("role","").equals("Admin")){
                            Intent intent = new Intent(MainActivity.this,AdminSalesTableActivity.class);
                            startActivity(intent);
                            drawer.closeDrawers();
                        }else if(preferences.getString("role","").equals("Worker")){
                            Intent intent = new Intent(MainActivity.this,SalesTableActivity.class);
                            startActivity(intent);
                            drawer.closeDrawers();
                        }
                        break;
                    case R.id.nav_myexpenses:
                        final SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        if(preferences1.getString("role","").equals("Main Admin")||preferences1.getString("role","").equals("Admin")) {
                            Intent intent1 = new Intent(MainActivity.this, AdminExpenseTableActivity.class);
                            startActivity(intent1);
                            drawer.closeDrawers();
                        }else if(preferences1.getString("role","").equals("Worker")){
                            Intent intent1 = new Intent(MainActivity.this, ExpenseTableActivity.class);
                            startActivity(intent1);
                            drawer.closeDrawers();
                        }
                        break;
                    case R.id.nav_settings:
                        Intent intent3 = new Intent(MainActivity.this,SettingsPrefActivity.class);
                        intent3.putExtra("frgLoad",CURRENT_TAG);
                        startActivity(intent3);
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_admin_panel:
                        final SharedPreferences mpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        if(mpreferences.getString("role","").equals("Main Admin")|| mpreferences.getString("role","").equals("Admin")) {
                            Intent intent2 = new Intent(MainActivity.this, AdminActivity.class);
                            startActivity(intent2);
                            drawer.closeDrawers();
                        }else{
                            drawer.closeDrawers();
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "samahani Admin tu ndio ana uwezo wa kuingia hapa!", Snackbar.LENGTH_LONG);
                            snackbar.setActionTextColor(Color.RED);
                            View sbView = snackbar.getView();
                            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
                            textView.setTextColor(Color.RED);
                            snackbar.show();
                        }
                        break;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                if(menuItem.getItemId()==0){
                    menuItem.setChecked(true);
                }

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
//        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                AdminSalesFragment homeFragment = new AdminSalesFragment();
                return homeFragment;
            default:
                return new AdminSalesFragment();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 2) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

//    public User getUser(User user){
//        return user;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        final Dialog dialognew = new Dialog(MainActivity.this);
        DisplayMetrics dm = MainActivity.this.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        dialognew.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialognew.setContentView(R.layout.change_password_layout);
        dialognew.getWindow().setLayout((int) (width * .9), (int) (height * .6));
        dialognew.setCancelable(true);

        int id = item.getItemId();
//        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        SalesFragment homeFragment = new SalesFragment();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(user_id);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("user_id");
            editor.remove("first_name");
            editor.remove("last_name");
            editor.remove("role");
            editor.remove("store_id");
            editor.apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            Toast.makeText(getApplicationContext(), "umetoka kikamilifu!", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }else if(id == R.id.action_change_passwd){
            dialognew.setContentView(R.layout.change_password_layout);

            new_passwd = dialognew.findViewById(R.id.new_passwd);
            confirm_passwd = dialognew.findViewById(R.id.confirm_passwd);
            submitBtn = dialognew.findViewById(R.id.submit_btn);
            cancelBtn = dialognew.findViewById(R.id.cancel_btn);

            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Tahadhari");
                    builder.setMessage("Hakikisha kama unataka kubadili nenosiri?");

                    if(new_passwd.getText().toString().isEmpty()||confirm_passwd.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "tafadhali jaza nafasi zote!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if (new_passwd.getText().toString().equals(confirm_passwd.getText().toString())) {
                            builder.setPositiveButton("Ndio", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if(new_passwd.getText().toString().isEmpty()||confirm_passwd.getText().toString().isEmpty()){
                                        Toast.makeText(MainActivity.this, "Tafadhali jaza nafasi zote!", Toast.LENGTH_SHORT).show();
                                    }else {
                                        if (new_passwd.getText().toString().equals(confirm_passwd.getText().toString())) {
                                            new UpdateUserTask(MainActivity.this).execute(sharedPreferences.getString("user_id",""), new_passwd.getText().toString());
                                            dialognew.dismiss();
                                        }
                                        else
                                            Toast.makeText(MainActivity.this, "Nenosiri hazifanani!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                            builder.setNegativeButton("Hapana", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        else
                            Toast.makeText(MainActivity.this, "Nenosiri hazifanani!", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialognew.dismiss();
                }
            });

            dialognew.show();

        }
        return super.onOptionsItemSelected(item);
    }

    public class UpdateUserTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        Context context;

        public UpdateUserTask(Context ctx) {
            this.context = ctx;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String user_id = strings[0];
            String passwd = strings[1];

            password_update_url = getString(R.string.serve_url) + "user/edit_passwd/" + user_id;

            try {
                URL url = new URL(password_update_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("PUT");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(passwd, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response = response.concat(line);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: " + result);

            if (result != null) {
                if (result.contains("Updated")) {
                    String[] userDetails = result.split("-");

                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }
                    Toast.makeText(context, "umebadili kikamilifu!", Toast.LENGTH_SHORT).show();
//                    SlideAnimationUtil.slideOutToLeft(LoginActivity.this, v.getRootView());
                } else {
                    Toast.makeText(context, "Oops... Kuna tatizo mahali", Toast.LENGTH_LONG).show();
                    if (this.dialog != null) {
                        this.dialog.dismiss();
                    }
                }
            } else {
                Toast.makeText(context, "Oops... Kuna tatizo mahali", Toast.LENGTH_LONG).show();
                if (this.dialog != null) {
                    this.dialog.dismiss();
                }
            }
        }
    }

}
