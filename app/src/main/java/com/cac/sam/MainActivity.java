package com.cac.sam;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

import com.cac.entities.Caniales;
import com.cac.entities.Empleados;
import com.cac.entities.Empresas;
import com.cac.entities.Fincas;
import com.cac.entities.Frentes;
import com.cac.entities.Lotes;
import com.cac.entities.Periodos;
import com.cac.entities.Rangos;
import com.cac.entities.SubGrupoVehiculos;
import com.cac.entities.Transaccion;
import com.cac.entities.TransactionDetails;
import com.cac.entities.Vehiculos;
import com.cac.services.SyncServerService;
import com.cac.tools.MainComponentEdit;
import com.cac.tools.ServerStarter;
import com.cac.viewer.CutterWorkFragment;
import com.cac.viewer.CuttingParametersFragment;
import com.cac.viewer.MainFragment;
import com.cac.viewer.SettingFragment;
import com.cac.viewer.SyncFragment;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FragmentManager frm;
    private boolean returningWithResult = false;
    private Fragment actualFragment;
    private String ACTUALFRAGMENT = "MainFragment";
    private ServerStarter serverStarter;

    //App Menu
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private FloatingActionButton btn_fab_right;
    private FloatingActionButton btn_fab_left;

    //Fragments
    private MainFragment mainFragment;
    private SyncFragment syncFragment;
    private SettingFragment settingFragment;
    private CuttingParametersFragment cuttingParametersFragment;
    private CutterWorkFragment cutterWorkFragment;

    //Events References
    private OnClickListener onClickListener;

    private EntityManager entityManager;
    private SharedPreferences sharedPreferences;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null)
            ACTUALFRAGMENT = savedInstanceState.getString("started");

        initComponent();

        //Working with the services class
        startService(new Intent(this, SyncServerService.class));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SyncServerService.SYNCHRONIZE_STARTED);
        intentFilter.addAction(SyncServerService.OBJECT_SYNCHRONIZED);
        intentFilter.addAction(SyncServerService.SYNCHRONIZE_END);
        serverStarter = new ServerStarter();

        try {
            registerReceiver(serverStarter, intentFilter);
        }catch (NullPointerException npe){}

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        returningWithResult = true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        returningWithResult = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
       // super.onSaveInstanceState(outState);
        outState.putString("started", ((MainComponentEdit) actualFragment).getTAG());
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

    @Override
    protected void onDestroy() {
        unregisterReceiver(serverStarter);
        super.onDestroy();
    }

    //</editor-fold>

    private void initComponent(){
        //Bar Menu
        toolbar = (Toolbar)findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(R.string.app_description_name);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        getEntityManager();

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

        btn_fab_right = (FloatingActionButton)findViewById(R.id.btn_fab_right);
        btn_fab_left = (FloatingActionButton)findViewById(R.id.btn_fab_left);

        //init fist main fragment
        frm = getFragmentManager();
        startTransactionByTagFragment(ACTUALFRAGMENT);

    }

    public EntityManager getEntityManager() {
        if ( entityManager == null ) {
            entityManager = new EntityManager(this, getResources().getString(R.string.db_name), null,
                    Integer.parseInt(getResources().getString(R.string.db_version)))
            .addTable(Caniales.class)
            .addTable(Empleados.class).addTable(Empresas.class)
            .addTable(Fincas.class).addTable(Frentes.class)
            .addTable(Lotes.class).addTable(Periodos.class)
            .addTable(Rangos.class).addTable(SubGrupoVehiculos.class)
            .addTable(Transaccion.class).addTable(TransactionDetails.class)
            .addTable(Vehiculos.class).init();
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

        ((MainComponentEdit) fragment).
                mainViewConfig(new FloatingActionButton[]{btn_fab_right, btn_fab_left});
        getSupportActionBar().setSubtitle(((MainComponentEdit) fragment).getSubTitle());

    }

    public void startTransactionByTagFragment(String tag){
        switch (tag){
            case "MainFragment":
                actualFragment = getMainFragment();
                break;
            case "SyncFragment":
                actualFragment = getSyncFragment();
                break;
            case "SettingFragment":
                actualFragment = getSettingFragment();
                break;
            case "CuttingParametersFragment":
                actualFragment = getCuttingParametersFragment();
                break;
            case "CutterWorkFragment":
                actualFragment = getCutterWorkFragment();
                break;
        }

        ((MainComponentEdit)actualFragment).setContext(this);
        startTransaction(actualFragment);
    }

    public MainFragment getMainFragment(){
        if(mainFragment == null)
            mainFragment = MainFragment.init(this);
        return mainFragment;
    }

    public SyncFragment getSyncFragment(){
        if(syncFragment == null)
            syncFragment = SyncFragment.init(this,getEntityManager(),
                    sharedPreferences.getString("etp_uri1", ""));
        return syncFragment;
    }

    public SettingFragment getSettingFragment(){
        if(settingFragment == null)
            settingFragment = SettingFragment.getInstance();
        return settingFragment;
    }

    public CuttingParametersFragment getCuttingParametersFragment() {
        if(cuttingParametersFragment == null)
            cuttingParametersFragment = cuttingParametersFragment.init(this);
        return cuttingParametersFragment;
    }

    public CutterWorkFragment getCutterWorkFragment(){
        if(cutterWorkFragment == null)
            cutterWorkFragment = CutterWorkFragment.init(this,entityManager);
        return cutterWorkFragment;
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
                        startTransactionByTagFragment(getMainFragment().getTAG());
                        break;
                    case R.id.client_sync:
                        startTransactionByTagFragment(getSyncFragment().getTAG());
                        break;
                    case R.id.setting:
                        startTransactionByTagFragment(getSettingFragment().getTAG());
                        break;
                    case R.id.fingering_work:
                        startTransactionByTagFragment(getCuttingParametersFragment().getTAG());
                        break;
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
    }
}
