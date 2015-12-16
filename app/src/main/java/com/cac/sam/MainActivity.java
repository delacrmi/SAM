package com.cac.sam;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.cac.entities.*;
import com.cac.services.SyncServerService;
import com.cac.tools.MainComponentEdit;
import com.cac.tools.ServerStarter;
import com.cac.viewer.CutterReportFragment;
import com.cac.viewer.CutterWorkFragment;
import com.cac.viewer.CuttingParametersFragment;
import com.cac.viewer.MainFragment;
import com.cac.viewer.SettingFragment;
import com.cac.viewer.SyncFragment;
import com.delacrmi.connection.ConnectSQLite;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FragmentManager frm;
    private boolean returningWithResult = false;
    private Fragment actualFragment;
    private String ACTUALFRAGMENT = "MainFragment";
    private ServerStarter serverStarter;

    private static int USER = 0;

    //static code activities values;
    private static final int EXIT = 0;
    private static final int LOGIN = 3;

    //App Menu
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private RelativeLayout mainRelativeLayout;
    public static int VISIBLE_ACTION = GridLayout.LayoutParams.WRAP_CONTENT;

    private ImageButton btn_fab_right;
    private ImageButton btn_fab_left;

    //Fragments
    private MainFragment mainFragment;
    private SyncFragment syncFragment;
    private SettingFragment settingFragment;
    private CuttingParametersFragment cuttingParametersFragment;
    private CutterWorkFragment cutterWorkFragment;
    private CutterReportFragment cutterReportFragment;

    //Events References
    private OnClickListener onClickListener;

    private EntityManager entityManager;
    private SharedPreferences sharedPreferences;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d("decode", new String(Base64.decode("bWNydXo=".getBytes(), Base64.DEFAULT),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
        if (data.getExtras().getInt("user") == 1){
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.setting).setEnabled(false);
            menu.findItem(R.id.client_sync).setEnabled(false);
            menu.findItem(R.id.server_sync).setEnabled(false);
        }else if(data.getExtras().getInt("user") == 2){
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.setting).setEnabled(true);
            menu.findItem(R.id.client_sync).setEnabled(true);
            menu.findItem(R.id.server_sync).setEnabled(true);
        }else if(data.getExtras().getInt("user") == 0){
            finish();
        }

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
        mainRelativeLayout = (RelativeLayout)findViewById(R.id.main_body_action_layout);

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

        btn_fab_right = (ImageButton)findViewById(R.id.btn_fab_right);
        btn_fab_left  = (ImageButton)findViewById(R.id.btn_fab_left);

        //init fist main fragment
        frm = getFragmentManager();
        startTransactionByTagFragment(ACTUALFRAGMENT);

        initOtherActivity(LOGIN);

    }

    private void initOtherActivity (int activityId){
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivityForResult(intent, activityId);
    }

    public EntityManager getEntityManager() {
        if ( entityManager == null ) {
            entityManager = new EntityManager(this, getResources().getString(R.string.db_name), null,
                    Integer.parseInt(getResources().getString(R.string.db_version))){

                @Override
                public void onCreateDataBase(ConnectSQLite conn, SQLiteDatabase db) {
                    List<List<Entity>> entities = new ArrayList<List<Entity>>();
                    List<Entity> value;

                    entities.add(new Users().getDefaultInsert());

                    value = new Transaccion(this).getDefaultInsert();
                    if(value != null)
                        entities.add(value);

                    value = new TransactionDetails(this).getDefaultInsert();
                    if(value != null)
                        entities.add(value);

                    conn.setEntitiesBackup(entities);

                }

                @Override
                public void onDataBaseCreated(ConnectSQLite conn, SQLiteDatabase db) {
                    for(List<Entity> entities : (List<List<Entity>>)conn.getEntitiesBackup())
                        for (Entity entity : entities)
                            db.insert(entity.getName(),null,entity.getContentValues());
                }

            }
            .addTable(Caniales.class).addTable(Empleados.class)
            .addTable(Empresas.class).addTable(Fincas.class)
            .addTable(Frentes.class).addTable(Lotes.class)
            .addTable(Periodos.class).addTable(Rangos.class)
            .addTable(Transaccion.class).addTable(TransactionDetails.class)
            .addTable(Vehiculos.class).addTable(Users.class).init();
        }

        return entityManager;
    }



    /**
     *@args: Fragment fragment
     * This method is the responsible to change the dynamics fragments
     */
    private void startTransaction(Fragment fragment){
        FragmentTransaction frt = frm.beginTransaction();
        frt.replace(R.id.main_body_layout, fragment, ACTUALFRAGMENT);
        frt.commit();
        ((MainComponentEdit) fragment).
                mainViewConfig(new View[]{mainRelativeLayout,btn_fab_right, btn_fab_left});
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
            case CutterReportFragment.TAG:
                actualFragment = getCutterReportFragment();
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
            settingFragment = SettingFragment.getInstance(this);
        return settingFragment;
    }

    public CuttingParametersFragment getCuttingParametersFragment() {
        if ( cuttingParametersFragment == null )
            cuttingParametersFragment = cuttingParametersFragment.init(this, getEntityManager());
        return cuttingParametersFragment;
    }

    public CutterWorkFragment getCutterWorkFragment(){
        if(cutterWorkFragment == null)
            cutterWorkFragment = CutterWorkFragment.init(this,entityManager);
        return cutterWorkFragment;
    }

    public CutterReportFragment getCutterReportFragment(){
        if ( cutterReportFragment == null )
            cutterReportFragment = CutterReportFragment.init(this);
        return cutterReportFragment;
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
                    case R.id.cutter_report:
                        startTransactionByTagFragment(CutterReportFragment.TAG);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }
}
