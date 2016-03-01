package com.cac.sam;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cac.entities.*;
import com.cac.services.SyncServerService;
import com.cac.tools.BackupBD;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FragmentManager frm;
    private boolean returningWithResult = false;
    private Fragment actualFragment;
    private String ACTUALFRAGMENT = "MainFragment";
    private ServerStarter serverStarter;

    public static MainActivity context;

    private static String USER = "";

    //App Menu
    private TextView tv_userName;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private MenuItem statusConnection;

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

    private AlertDialog dialog = null;
    private View dialogLayout;
    private EditText userName;
    private EditText password;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        try {
            Log.d("decode", new String(Base64.decode("bWNydXo=".getBytes(), Base64.DEFAULT),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) {
            ACTUALFRAGMENT = savedInstanceState.getString("started");
            USER = savedInstanceState.getString("user");
        }

        initComponent();

        //Working with the services class
        startService(new Intent(this, SyncServerService.class));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SyncServerService.SAM);
        serverStarter = new ServerStarter(this);

        try {
            registerReceiver(serverStarter, intentFilter);
        }catch (NullPointerException npe){}

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        configMenu(data.getExtras().getString("user"), true);
        returningWithResult = true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        returningWithResult = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("started", ((MainComponentEdit) actualFragment).getTAG());
        outState.putString("user", USER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        statusConnection = menu.findItem(R.id.connect);
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

    @Override
    public void onBackPressed() {
        if(!drawerLayout.isDrawerOpen(Gravity.LEFT)) drawerLayout.openDrawer(Gravity.LEFT);
        else drawerLayout.closeDrawers();
    }

    //</editor-fold>

    private void configMenu(String user, boolean show){

        try {
            JSONObject json = new JSONObject(user);
            Menu menu;

            if (show) Snackbar.make(btn_fab_left, getResources().getString(R.string.login_success), Snackbar.LENGTH_SHORT).show();

            String userLow;

            userLow = json.getString(Users.ROLE).toLowerCase();

            if(json.has(Users.USER)) tv_userName.setText(json.getString(Users.USER));
            else tv_userName.setText(json.getString(Users.EMAIL));

            startTransactionByTagFragment(MainFragment.getInstance().getTAG());

            if (userLow.equals("user")){
                menu = navigationView.getMenu();
                menu.findItem(R.id.client_sync).setEnabled(false);
                menu.findItem(R.id.server_sync).setEnabled(false);
                menu.findItem(R.id.setting).setEnabled(false);
                menu.findItem(R.id.nav_bk_database).setEnabled(false);
                menu.findItem(R.id.nav_up_database).setEnabled(false);

            }else if(userLow.equals("admin")){
                menu = navigationView.getMenu();
                menu.findItem(R.id.client_sync).setEnabled(true);
                menu.findItem(R.id.server_sync).setEnabled(true);
                menu.findItem(R.id.setting).setEnabled(true);
                menu.findItem(R.id.nav_bk_database).setEnabled(true);
                menu.findItem(R.id.nav_up_database).setEnabled(true);

            }else{
                USER = "";
                finish();
            }

            drawerLayout.invalidate();
            USER = user;

        } catch (JSONException e) {
            initOtherActivity();
        }

    }

    private void initComponent(){
        //Bar Menu
        toolbar = (Toolbar)findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(R.string.app_description_name);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mainRelativeLayout = (RelativeLayout)findViewById(R.id.main_body_action_layout);

        getEntityManager();

        drawerLayout = (android.support.v4.widget.DrawerLayout)findViewById(R.id.drawer_layout);
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
        tv_userName = (TextView)findViewById(R.id.nav_userName);

        btn_fab_right = (ImageButton)findViewById(R.id.btn_fab_right);
        btn_fab_left  = (ImageButton)findViewById(R.id.btn_fab_left);

        dialogLayout = getLayoutInflater().inflate(R.layout.login_dialog, null);
        userName = (EditText)dialogLayout.findViewById(R.id.dialig_username);
        password = (EditText)dialogLayout.findViewById(R.id.dialog_password);

        //init fist main fragment
        frm = getFragmentManager();
        startTransactionByTagFragment(ACTUALFRAGMENT);



        if(USER.equals("")) initOtherActivity();
        else configMenu(USER,false);


    }

    private void initOtherActivity (){
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivityForResult(intent, 0);
    }

    public EntityManager getEntityManager() {
        if ( entityManager == null ) {
            entityManager = new EntityManager(this, getResources().getString(R.string.db_name), null,
                    Integer.parseInt(getResources().getString(R.string.db_version))){

                @Override
                public void onCreateDataBase(ConnectSQLite conn, SQLiteDatabase db) {
                    List<List<Entity>> entities = new ArrayList<List<Entity>>();
                    entities.add(new Users().getDefaultInsert());
                    conn.setEntitiesBackup(entities);

                }

                @Override
                public void onDataBaseCreated(ConnectSQLite conn, SQLiteDatabase db) {
                    for(List<Entity> entities : (List<List<Entity>>)conn.getEntitiesBackup())
                        for (Entity entity : entities)
                            db.insert(entity.getName(),null,entity.getContentValues());
                }

                @Override
                public void onDatabaseUpdate(ConnectSQLite conn, SQLiteDatabase db) {
                    List<List<Entity>> entities = new ArrayList<List<Entity>>();
                    List<Entity> value = new ArrayList<Entity>();;

                    entities.add(new Users().getDefaultInsert());

                    //Backingup database tables.
                    for (String str : getTablesNames()){
                        Cursor cursor = db.rawQuery("select * from "+str,null);
                        setListFromCursor(cursor,value,getClassByName(str));
                    }

                    if(value != null)
                        entities.add(value);

                    conn.setEntitiesBackup(entities);
                }

                @Override
                public void onUpdatedDataBase(ConnectSQLite conn, SQLiteDatabase db){
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
                    case R.id.nav_change_user:
                        userName.setText("");
                        password.setText("");
                        userName.requestFocus();
                        if (dialog == null){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                    .setView(dialogLayout)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String user = LoginActivity.findUser(userName.getText().toString(), password.getText().toString(),
                                                    getEntityManager()).getJSON().toString();
                                            if (!user.equals("{}")) configMenu(user, true);
                                            else
                                                Snackbar.make(btn_fab_left, getResources().getString(R.string.login_reject), Snackbar.LENGTH_SHORT).show();

                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            dialog = builder.create();
                        }

                        dialog.show();

                        break;
                    case R.id.nav_sign_out:
                        USER = "";
                        initOtherActivity();
                        break;
                    case R.id.nav_bk_database:
                        new BackupBD(MainActivity.this,getEntityManager()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
                        break;
                    case R.id.nav_up_database:
                        new BackupBD(MainActivity.this,getEntityManager()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,1);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    public MenuItem getStatusConnection(){
        return statusConnection;
    }
}
