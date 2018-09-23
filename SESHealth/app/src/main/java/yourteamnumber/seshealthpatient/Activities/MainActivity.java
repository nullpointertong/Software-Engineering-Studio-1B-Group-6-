package yourteamnumber.seshealthpatient.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;



import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import yourteamnumber.seshealthpatient.Fragments.DataPacketFragment;
import yourteamnumber.seshealthpatient.Fragments.DoctorInformationFragment;
import yourteamnumber.seshealthpatient.Fragments.HeartRateFragment;
import yourteamnumber.seshealthpatient.Fragments.ListDataPacketFragment;
import yourteamnumber.seshealthpatient.Fragments.MapFragment;
import yourteamnumber.seshealthpatient.Fragments.PatientInformationFragment;
import yourteamnumber.seshealthpatient.Fragments.UpdateDoctorInformationFragment;
import yourteamnumber.seshealthpatient.Fragments.UpdatePatientInformationFragment;
import yourteamnumber.seshealthpatient.Fragments.RecordVideoFragment;
import yourteamnumber.seshealthpatient.Fragments.SendFileFragment;
import yourteamnumber.seshealthpatient.Fragments.ViewMyDoctorsFragment;
import yourteamnumber.seshealthpatient.R;


/**
 * Class: MainActivity
 * Extends:  {@link AppCompatActivity}
 * Author:  Carlos Tirado < Carlos.TiradoCorts@uts.edu.au>, and YOU!
 * Description:
 * <p>
 * For this project I encourage you to use Fragments. It is up to you to build up the app as
 * you want, but it will be a good practice to learn on how to use Fragments. A very good tutorial
 * on how to use fragments can be found on this site:
 * http://www.vogella.com/tutorials/AndroidFragments/article.html
 * <p>
 * I basically chose to use fragments because of the design of the app, again, you can choose to change
 * completely the design of the app, but for this design specifically I will use Fragments.
 * <p>
 */
public class MainActivity extends AppCompatActivity {

    /**
     * A basic Drawer layout that helps you build the side menu. I followed the steps on how to
     * build a menu from this site:
     * https://developer.android.com/training/implementing-navigation/nav-drawer
     * I recommend you to have a read of it if you need to do any changes to the code.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * A reference to the toolbar
     */
    private Toolbar toolbar;

    /**
     * Helps to manage the fragment that is being used in the main view.
     */
    private FragmentManager fragmentManager;

    /**
     * TAG to use
     */
    private static String TAG = "MainActivity";

    /**
     * I am using this enum to know which is the current fragment being displayed, you will see
     * what I mean with this later in this code.
     */
    private enum MenuStates {
        PATIENT_INFO, DATA_PACKET, LIST_DATA_PACKET, HEARTRATE, RECORD_VIDEO, SEND_FILE, NAVIGATION_MAP, UPDATE_PATIENT_INFO, DOCTOR_INFO, UPDATE_DOCTOR_INFO,VIEW_MYDOCTORS
    }

    /**
     * The current fragment being displayed.
     */
    private MenuStates currentState;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // the default fragment on display is the patient information
        currentState = MenuStates.PATIENT_INFO;

        // go look for the main drawer layout
        mDrawerLayout = findViewById(R.id.main_drawer_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Set up the menu button
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //Setup buttons in Activity Login
        Button login_btn;

        login_btn = (Button) findViewById(R.id.login_btn);


        // Setup the navigation drawer, most of this code was taken from:
        // https://developer.android.com/training/implementing-navigation/nav-drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Using a switch to see which item on the menu was clicked
                        switch (menuItem.getItemId()) {
                            // You can find these id's at: res -> menu -> drawer_view.xml
                            case R.id.nav_patient_info:
                                // If the user clicked on a different item than the current item
                                if (currentState != MenuStates.PATIENT_INFO) {
                                    // change the fragment to the new fragment
                                    ChangeFragment(MenuStates.PATIENT_INFO);
                                }
                                break;
                            case R.id.nav_update_patient_info:
                                if (currentState != MenuStates.UPDATE_PATIENT_INFO) {
                                    ChangeFragment(MenuStates.UPDATE_PATIENT_INFO);
                                }
                                break;
                            case R.id.nav_viewdoctors:
                                if (currentState != MenuStates.VIEW_MYDOCTORS) {
                                    ChangeFragment(MenuStates.VIEW_MYDOCTORS);
                                }
                                break;
                            case R.id.nav_doctor_info:
                                // If the user clicked on a different item than the current item
                                if (currentState != MenuStates.DOCTOR_INFO) {
                                    // change the fragment to the new fragment
                                    ChangeFragment(MenuStates.DOCTOR_INFO);
                                }
                                break;
                            case R.id.nav_update_doctor_info:
                                if (currentState != MenuStates.UPDATE_DOCTOR_INFO) {
                                    ChangeFragment(MenuStates.UPDATE_DOCTOR_INFO);
                                }
                                break;
                            case R.id.nav_data_packet:
                                if (currentState != MenuStates.DATA_PACKET) {
                                    ChangeFragment(MenuStates.DATA_PACKET);
                                }
                                break;
                            case R.id.nav_list_data_packet:
                                if (currentState != MenuStates.LIST_DATA_PACKET) {
                                    ChangeFragment(MenuStates.LIST_DATA_PACKET);
                                }
                                break;
                            case R.id.nav_map:
                                if (currentState != MenuStates.NAVIGATION_MAP) {
                                    ChangeFragment(MenuStates.NAVIGATION_MAP);
                                }
                                break;
                        }

                        return true;
                    }
                });

        // If you need to listen to specific events from the drawer layout.
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );


        // More on this code, check the tutorial at http://www.vogella.com/tutorials/AndroidFragments/article.html
        fragmentManager = getFragmentManager();
        // Add the default Fragment once the user logged in
        firebaseAuth = FirebaseAuth.getInstance();
                    String userId = firebaseAuth.getUid();
                    DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child("user_id").child(userId);
                    currentUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("UserType").getValue().toString().equals("Patient"))
                            {
                                FragmentTransaction ft = fragmentManager.beginTransaction();
                                ft.add(R.id.fragment_container, new PatientInformationFragment());
                                ft.commit();
                            }
                            else if(dataSnapshot.child("UserType").getValue().toString().equals("Doctor"))
                            {
                                FragmentTransaction ft = fragmentManager.beginTransaction();
                                ft.add(R.id.fragment_container, new DoctorInformationFragment());
                                ft.commit();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

    }

    /**
     * Called when one of the items in the toolbar was clicked, in this case, the menu button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This function changes the title of the fragment.
     */
    public void UpdateFragmentTitle()
    {
        toolbar.setTitle(GetTitleForFragment());
    }


    /**
     * This function allows to change the content of the Fragment holder
     * @param menuState The menu state to be displayed
     */
    private void ChangeFragment(MenuStates menuState) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, GetFragmentForMenuState(menuState));
        transaction.addToBackStack(null);
        transaction.commit();

        currentState = menuState;
        UpdateFragmentTitle();
    }

    private Fragment GetFragmentForMenuState(MenuStates menuState)
    {
        switch (menuState)
        {
            case DATA_PACKET:
                return new DataPacketFragment();
            case LIST_DATA_PACKET:
                return new ListDataPacketFragment();
            case PATIENT_INFO:
                return new PatientInformationFragment();
            case VIEW_MYDOCTORS:
                return new ViewMyDoctorsFragment();
            case UPDATE_PATIENT_INFO:
                return new UpdatePatientInformationFragment();
            case DOCTOR_INFO:
                return new DoctorInformationFragment();
            case UPDATE_DOCTOR_INFO:
                return new UpdateDoctorInformationFragment();
            case NAVIGATION_MAP:
                return new MapFragment();
            default:
                return null;
        }
    }

    private String GetTitleForFragment()
    {
        switch (currentState)
        {
            case DATA_PACKET:
                return getString(R.string.create_data_packet);
            case LIST_DATA_PACKET:
                return getString(R.string.list_data_packet);
            case PATIENT_INFO:
                return getString(R.string.patient_information);
            case UPDATE_PATIENT_INFO:
                return getString(R.string.update_patient_information);
            case VIEW_MYDOCTORS:
                return getString(R.string.view_mydoctors);
            case DOCTOR_INFO:
                return getString(R.string.doctor_information);
            case UPDATE_DOCTOR_INFO:
                return getString(R.string.update_doctor_information);
            case NAVIGATION_MAP:
                return getString(R.string.facilities_map);
            default:
                return null;
        }
    }
}
