package com.cac.sam;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.cac.services.SyncServerService;
import com.cac.tools.MainComponentEdit;
import com.cac.tools.ServerStarter;
import com.cac.viewer.MainFragment;
import com.cac.viewer.SettingFragment;
import com.cac.viewer.SyncFragment;
import com.delacrmi.controller.EntityManager;

public class MainActivity extends AppCompatActivity {
    private FragmentManager frm;
    private Fragment actualFragment;
    private String ACTUALFRAGMENT = "MainFragment";

    //App Menu
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private FloatingActionButton btn_fab;

    //Fragments
    private MainFragment mainFragment;
    private SyncFragment syncFragment;
    private SettingFragment settingFragment;

    //Events References
    private OnClickListener onClickListener;

    private EntityManager entityManager;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        events();

        if(savedInstanceState != null)
            ACTUALFRAGMENT = savedInstanceState.getString("started");

        initComponent();

        //Working with the services class
        startService(new Intent(this, SyncServerService.class));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SyncServerService.SYNCHRONIZE_STARTED);
        intentFilter.addAction(SyncServerService.OBJECT_SYNCHRONIZED);
        intentFilter.addAction(SyncServerService.SYNCHRONIZE_END);
        ServerStarter serverStarter = new ServerStarter();
        registerReceiver(serverStarter,intentFilter);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("started", ((MainComponentEdit) actualFragment).getTAG());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //</editor-fold>

    private void initComponent(){
        //Bar Menu
        toolbar = (Toolbar)findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(R.string.app_description_name);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                        R.string.openDrawer, R.string.closeDrawer){

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

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = (NavigationView)findViewById(R.id.nav_view);
        setDrawerMenu();

        btn_fab = (FloatingActionButton)findViewById(R.id.btn_fab);
        btn_fab.setOnClickListener(onClickListener);

        //init fist main fragment
        frm = getFragmentManager();
        startTransactionByFragmentTag(ACTUALFRAGMENT);

    }

    private void events(){
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_fab:
                    ((MainComponentEdit)actualFragment).onClickFloating();
                    break;
            }
            }
        };
    }

    public EntityManager getEntityManager() {
        if ( entityManager == null ) {
            entityManager = new EntityManager(this,
                    getResources().getString(R.string.db_name),
                    null,
                    Integer.parseInt(getResources().getString(R.string.db_version)));
        }
        return entityManager;
    }

    /**
     *@args: Fragment fragment
     * This method is the responsible to change the dynamics fragments
     */
    private void startTransaction(Fragment fragment){
        FragmentTransaction frt = frm.beginTransaction();
        frt.replace(R.id.body_layout, fragment, ACTUALFRAGMENT);
        frt.commit();

        actualFragment = fragment;
        ((MainComponentEdit)fragment).FloatingButtonConfig(btn_fab);
        getSupportActionBar().setSubtitle(((MainComponentEdit) fragment).getSubTitle());

    }

    private void startTransactionByFragmentTag(String tag){
        switch (tag){
            case "MainFragment":
                startTransaction(getMainFragment());
                break;
            case "SyncFragment":
                startTransaction(getSyncFragment());
                break;
            case "SettingFragment":
                startTransaction(getSettingFragment());
        }
    }

    public MainFragment getMainFragment(){
        if(mainFragment == null)
            mainFragment = MainFragment.init(this);
        return mainFragment;
    }

    public SyncFragment getSyncFragment(){
        if(syncFragment == null)
            syncFragment = SyncFragment.init(this,getEntityManager(),"http:/100.10.20.171:3000");
        return syncFragment;
    }

    public SettingFragment getSettingFragment(){
        if(settingFragment == null)
            settingFragment = SettingFragment.getInstance();
        return settingFragment;
    }

    /**
     * @args
     * Setting the Drawer Menu Options
     */
    public void setDrawerMenu(){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        startTransaction(getMainFragment());
                        break;
                    case R.id.client_sync:
                        startTransaction(getSyncFragment());
                        break;
                    case R.id.setting:
                        startTransaction(getSettingFragment());
                        break;
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
    }
}
