package com.example.michael.pepsiinventory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.carbs.android.avatarimageview.library.AvatarImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private Toolbar toolbar;
    private ImageView imgNavHeaderBg;
    private ViewPager viewPager;
    AvatarImageView img_profile;
    TextView textView;
    String f_name,l_name,firstName,lastName;
    String TAG = MainActivity.class.getSimpleName();
    LoginActivity loginActivity;

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
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        store_id = getIntent().getStringExtra("store_id");
        user_id = getIntent().getStringExtra("user_id");

        mHandler = new Handler();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        viewPager = findViewById(R.id.container);
        setupViewPager(viewPager,user_id,store_id);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
        img_profile = navHeader.findViewById(R.id.img_profile);
        textView = navHeader.findViewById(R.id.full_name);

        // load nav menu header data
        loadNavHeader();

        final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        textView.setText(myPrefs.getString("first_name","").concat(" " + myPrefs.getString("last_name","")));

        // initializing navigation menu
        setUpNavigationView();

//        if (savedInstanceState == null) {
//            navItemIndex = 0;
//            CURRENT_TAG = TAG_HOME;
//            loadHomeFragment();
//        }
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
            adapter.addFragment(adminSalesFragment, "Sales");
            adapter.addFragment(adminExpenseFragment, "Expenses");
            viewPager.setAdapter(adapter);
        }else if(sharedPreferences.getString("role","").equals("Worker")){
            adapter.addFragment(salesFragment, "Sales");
            adapter.addFragment(expenseFragment, "Expenses");
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
                        Intent intent = new Intent(MainActivity.this,AdminSalesTableActivity.class);
//                        Toolbar toolbar = findViewById(R.id.toolbar);
//                        toolbar.setTitle("Sales table");
                        intent.putExtra("frgLoad",CURRENT_TAG);
                        startActivity(intent);
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_myexpenses:
                        Intent intent1 = new Intent(MainActivity.this,AdminExpenseTableActivity.class);
                        startActivity(intent1);
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_admin_panel:
                        final SharedPreferences mpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        if(mpreferences.getString("role","").equals("Main Admin")|| mpreferences.getString("role","").equals("Admin")) {
                            Intent intent2 = new Intent(MainActivity.this, AdminActivity.class);
                            startActivity(intent2);
                            drawer.closeDrawers();
                        }else
                            menuItem.setChecked(false);
                        break;
                    case R.id.nav_settings:
                        Intent intent3 = new Intent(MainActivity.this,SettingsPrefActivity.class);
                        intent3.putExtra("frgLoad",CURRENT_TAG);
                        startActivity(intent3);
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, AboutApp.class));
                        drawer.closeDrawers();
                        return true;
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

        int id = item.getItemId();
//        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        SalesFragment homeFragment = new SalesFragment();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
           startActivity(intent);
            Toast.makeText(getApplicationContext(), "succesfully logged out!", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
