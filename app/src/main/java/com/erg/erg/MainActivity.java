package com.erg.erg;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.erg.erg.R.array.array_country;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int UPDATE_FREQUENCY = 500;
    private static final int PERMS_REQUEST_CODE = 123;
    private final Handler handler = new Handler();
    ArrayAdapter<String> adapter;
    int size;
    SharedPreferences settings;
    SearchView searchView;
    String[] songsArray;
    String swsong;
    String current_Msuic_File = "xvalite_boga_nebes";
    Intent intent = null, chooser;
    DownloadManager downloadManager;
    String sdMusicPath;
    private SeekBar seekbar = null;
    private MediaPlayer player = null;
    private final Runnable updatePositionRunnable = new Runnable() {
        public void run() {
            updatePosition();
        }
    };
    private ImageButton playButton = null;
    private TextView selectedFile;
    private TextView textView;
    private String erg;
    private ListView lv;
    private boolean isMoveingSeekBar = false;
    private View.OnClickListener onButtonClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {


            if (player.isPlaying()) {
                handler.removeCallbacks(updatePositionRunnable);
                player.pause();
                //player.reset();
                playButton.setImageResource(android.R.drawable.ic_media_play);
            } else {


                if (isExist(current_Msuic_File)) {
                    if (!Objects.equals(sdMusicPath, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString() + "/" + current_Msuic_File + ".mp3")) {
                        sdMusicPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString() + "/" + current_Msuic_File + ".mp3";
                        try {
                            player.setDataSource(sdMusicPath);
                            player.prepare();
                            seekbar.setProgress(0);
                            seekbar.setMax(player.getDuration());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }


                player.start();
                seekbar.setMax(player.getDuration());
                updatePosition();
                playButton.setImageResource(android.R.drawable.ic_media_pause);


            }





        }
    };
    private SeekBar.OnSeekBarChangeListener seekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isMoveingSeekBar = false;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isMoveingSeekBar = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (isMoveingSeekBar) {
                player.seekTo(progress);

                Log.i("OnSeekBarChangeListener", "onProgressChanged");
            }
        }
    };

    private boolean isExist(String musName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/" + musName + ".mp3");
        if (file.exists()) {
            selectedFile.setText(musName);
            return true;

        } else {
            selectedFile.setText("not found");
            download(current_Msuic_File);
            return false;
        }
    }

    private void updatePosition() {
        handler.removeCallbacks(updatePositionRunnable);

        seekbar.setProgress(player.getCurrentPosition());

        handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.settings = getSharedPreferences("IDvalue", 0);
        size = this.settings.getInt("init", 14);     //intenti @ndunum

        textView = (TextView) findViewById(R.id.textView);
        selectedFile = (TextView) findViewById(R.id.selectedfile);
        textView.setTextSize(size);
        songsArray = getResources().getStringArray(R.array.array_country);

        erg = " Որովհետեւ Աստուած այնպէս սիրեց աշխարհքը որ իր միածին Որդին տուաւ. որ ամեն նորան հաւատացողը չկորչի, այլ յաւիտենական կեանքն ունենայ։";
        textView.setText(Html.fromHtml("<p>Որովհետեւ Աստուած <br> այնպէս սիրեց աշխարհքը <br> որ իր միածին Որդին տուաւ. <br> որ ամեն նորան <br> հաւատացողը չկորչի, <br> այլ յաւիտենական <br> կեանքն ունենայ։<p> <br> Հովհանուես 3։16"));

        lv = (ListView) findViewById(R.id.listViewCountry);
        ArrayList<String> arrayCountry = new ArrayList<>();
        arrayCountry.addAll(Arrays.asList(getResources().getStringArray(array_country)));

        adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                arrayCountry);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { // serch need to work
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // searchView.clearFocus();
                // searchView.setIconified(true);
                // searchView.setIconifiedByDefault(false);
                searchView.setIconified(true);
                //searchView.setInputType(InputType.TYPE_NULL);
                lv.setAdapter(null);
                selecttext(adapter.getItem(position));


            }
        });

        seekbar = (SeekBar) findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(seekBarChanged);

        // seekbar = (SeekBar) findViewById(R.id.seekbar);
        playButton = (ImageButton) findViewById(R.id.play);

        player = new MediaPlayer();
        //player = MediaPlayer.create(this, R.raw.xvalite_boga_nebes);
        // playButton.setOnClickListener(onButtonClick);
        // seekbar.setOnSeekBarChangeListener(seekBarChanged);

        // player.stop();
        // player.reset();
        playButton.setImageResource(android.R.drawable.ic_media_play);
        //player.start();
        // handler.removeCallbacks(updatePositionRunnable);
        //seekbar.setProgress(0);
        playButton.setOnClickListener(onButtonClick);


        if (hasPermissions()) {
            // our app has permissions.

        } else {
            //our app doesn't have permissions, So i m requesting permissions.
            requestPerms();
        }


    }

    private void download(String name_uri_mus) {

        Uri uri = Uri.parse("https://armrugby.mybluemix.net/minus/" + name_uri_mus + ".mp3");
        //long downloadReference;

        // Create request for android download manager
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Download music");

        //Setting description of request
        request.setDescription("Download in music folder");

        //Set the local destination for the downloaded file to a path
        //within the application's external files directory

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, current_Msuic_File + ".mp3");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //Enqueue download and save into referenceId
        //downloadReference =
        downloadManager.enqueue(request);


    }

    @Override
    public void onBackPressed() {
        settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = settings.edit();
        ed.putInt("init", size);
        ed.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }   // on back pressed


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                lv.setAdapter(adapter);
                textView.setText(null);

                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        size = data.getIntExtra("size", 14);
        textView.setTextSize(size);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            intent = new Intent(this, Settings.class);
            intent.putExtra("erg", erg);
            startActivityForResult(intent, 1);

        }
        if (id == R.id.Mail) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto:"));
            String[] to = {"Haykhakobyan95@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL, to);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Hi This mail from my ERG App");
            intent.setType("message/rfc822");
            chooser = Intent.createChooser(intent, "Send Email");
            startActivity(chooser);

        }

        return super.onOptionsItemSelected(item);
    }

    /* private SeekBar.OnSeekBarChangeListener seekBarChanged = new SeekBar.OnSeekBarChangeListener() {
         @Override
         public void onStopTrackingTouch(SeekBar seekBar) {
             isMoveingSeekBar = false;
         }

         @Override
         public void onStartTrackingTouch(SeekBar seekBar) {
             isMoveingSeekBar = true;
         }

         @Override
         public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
             if (isMoveingSeekBar) {
                 player.seekTo(progress);

                 Log.i("OnSeekBarChangeListener", "onProgressChanged");
             }
         }
     };*/

    @SuppressLint("WrongConstant") // permishions
    private boolean hasPermissions() {
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestPerms() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMS_REQUEST_CODE);
        }


    } // permishions

    public void Inch_mec_e_Astvac(View view) {
        Inch_mec_e_Astvac obj = new Inch_mec_e_Astvac();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ինչ մեծ է Աստված");
        textView.setText(Html.fromHtml(erg));
    }

    public void Im_angin(View view) {
        Im_angin obj = new Im_angin();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Իմ Անգին");
        textView.setText(Html.fromHtml(erg));
    }

    public void mardiq_cnvum_en(View view) {
        mardiq_cnvum_en obj = new mardiq_cnvum_en();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Մարդիք ծնվում են");
        textView.setText(Html.fromHtml(erg));
    }

    public void Aha_kangnac_em(View view) {
        Aha_kangnac_em obj = new Aha_kangnac_em();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ահա կանգնած եմ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Aha_menq_mer_urax_ergerov(View view) {
        Aha_menq_mer_urax_ergerov obj = new Aha_menq_mer_urax_ergerov();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ահա, մենք մեր ուրախ Երգերով");
        textView.setText(Html.fromHtml(erg));
    }

    public void Amenic_ver(View view) {
        Amenic_ver obj = new Amenic_ver();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ամենից վեր");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ararich_erknqi(View view) {
        Ararich_erknqi obj = new Ararich_erknqi();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Արարիչ երկնքի");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ayd_inch_xaxax_ajker_unes_du(View view) {
        Ayd_inch_xaxax_ajker_unes_du obj = new Ayd_inch_xaxax_ajker_unes_du();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Այդ ինչ խաղաղ աչքեր ունես");
        textView.setText(Html.fromHtml(erg));
    }

    public void Bacir_im_hogu_achqer(View view) {
        Bacir_im_hogu_achqer obj = new Bacir_im_hogu_achqer();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Բացի՚ր, իմ հոգու");
        textView.setText(Html.fromHtml(erg));
    }

    public void Barcial_arqa(View view) {
        Barcial_arqa obj = new Barcial_arqa();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Բարձյալ Արքա");
        textView.setText(Html.fromHtml(erg));
    }

    public void Bari_lur(View view) {
        Bari_lur obj = new Bari_lur();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Բարի Լուր");
        textView.setText(Html.fromHtml(erg));
    }

    public void Beranis_xosqer(View view) {
        Beranis_xosqer obj = new Beranis_xosqer();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Բերնիս խոսքերը");
        textView.setText(Html.fromHtml(erg));
    }

    public void davanenq(View view) {
        davanenq obj = new davanenq();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Դավանենք");
        textView.setText(Html.fromHtml(erg));
    }

    public void De_ijir_ijir(View view) {
        De_ijir_ijir obj = new De_ijir_ijir();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Դե իջիր իջիր");
        textView.setText(Html.fromHtml(erg));
    }

    public void Djvar_pahin(View view) {
        Djvar_pahin obj = new Djvar_pahin();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Դժվար պահին");
        textView.setText(Html.fromHtml(erg));
    }

    public void Dolorasan(View view) {
        Dolorasan obj = new Dolorasan();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Դոլորոսան");
        textView.setText(Html.fromHtml(erg));
    }

    public void Du_es_ser_im_kjanqi(View view) {
        Du_es_ser_im_kjanqi obj = new Du_es_ser_im_kjanqi();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Դու ես սերը իմ կյանքի");
        textView.setText(Html.fromHtml(erg));
    }

    public void Erb_korac_ei(View view) {
        Erb_korac_ei obj = new Erb_korac_ei();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Երբ կորաց էի");
        textView.setText(Html.fromHtml(erg));
    }

    public void Erb_nayum_em(View view) {
        Erb_nayum_em obj = new Erb_nayum_em();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Երբ նայում եմ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Erb_vor_na_amperov(View view) {
        Erb_vor_na_amperov obj = new Erb_vor_na_amperov();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Երբ որ Նա ամպերով");
        textView.setText(Html.fromHtml(erg));
    }

    public void Erknqi_dur_bac_e(View view) {
        Erknqi_dur_bac_e obj = new Erknqi_dur_bac_e();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Երկնքի դուռը բաց է");
        textView.setText(Html.fromHtml(erg));
    }

    public void Es_hayr_unem(View view) {
        Es_hayr_unem obj = new Es_hayr_unem();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ես հայր ունեմ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ev_astvac_aynpes(View view) {
        Ev_astvac_aynpes obj = new Ev_astvac_aynpes();
        erg = obj.get_song();
        getSupportActionBar().setTitle("ԵՎ Աստված այնպես");
        textView.setText(Html.fromHtml(erg));
    }

    public void Galis_em(View view) {
        Galis_em obj = new Galis_em();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Գալիս եմ  ներկայոթյանդ մեջ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Garun_e_hima_im_hoqum(View view) {
        Garun_e_hima_im_hoqum obj = new Garun_e_hima_im_hoqum();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Գարուն է հիմա իմ հոգում");
        textView.setText(Html.fromHtml(erg));
    }

    public void Garun_hrashali(View view) {
        Garun_hrashali obj = new Garun_hrashali();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Գարուն հրաշալի");
        textView.setText(Html.fromHtml(erg));
    }

    public void Havatqs_chem_urana(View view) {
        Havatqs_chem_urana obj = new Havatqs_chem_urana();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Հավատքս չեմ ուրանա");
        textView.setText(Html.fromHtml(erg));
    }

    public void Hisus_Dues_ayn_jar(View view) {
        Hisus_Dues_ayn_jar obj = new Hisus_Dues_ayn_jar();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Հիսուս դու ես այն ժայռը");
        textView.setText(Html.fromHtml(erg));
    }

    public void Hisusi_ser(View view) {
        Hisusi_ser obj = new Hisusi_ser();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Հիսուսի սերը");
        textView.setText(Html.fromHtml(erg));
    }

    public void Hisusi_ser_orhni_mez(View view) {
        Hisusi_ser_orhni_mez obj = new Hisusi_ser_orhni_mez();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Հիսուսի սերը օրհնի մեզ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Hisus_e_kyanq(View view) {
        Hisus_e_kyanq obj = new Hisus_e_kyanq();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Հիսուսն է կյանքը");
        textView.setText(Html.fromHtml(erg));
    }

    public void Inch_anun_ashxar_ekav(View view) {
        Inch_anun_ashxar_ekav obj = new Inch_anun_ashxar_ekav();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ինչ անուն աշխարհ եկավ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Inch_mec_u_hrashali(View view) {
        Inch_mec_u_hrashali obj = new Inch_mec_u_hrashali();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ինչ մեծ ու հրաշալի");
        textView.setText(Html.fromHtml(erg));
    }

    public void Kangneq_mardiq(View view) {
        Kangneq_mardiq obj = new Kangneq_mardiq();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Կանգնեք մարդիկ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Lur_Gisher(View view) {
        Lur_Gisher obj = new Lur_Gisher();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Լուռ գիշեր");
        textView.setText(Html.fromHtml(erg));
    }

    public void menq_enq_mer_hoqov(View view) {
        menq_enq_mer_hoqov obj = new menq_enq_mer_hoqov();
        erg = obj.get_song();
        getSupportActionBar().setTitle("մենք ենք մեր հոքով");
        textView.setText(Html.fromHtml(erg));
    }

    public void mets_u_Hrashali_en(View view) {
        mets_u_Hrashali_en obj = new mets_u_Hrashali_en();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Մեծ ու հրաշալի");
        textView.setText(Html.fromHtml(erg));
    }

    public void mtel_em_srbutyant_srboc(View view) {
        mtel_em_srbutyant_srboc obj = new mtel_em_srbutyant_srboc();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Մտել եմ սրբությանտ սրբոցը");
        textView.setText(Html.fromHtml(erg));
    }

    public void Nerir_ter_im(View view) {
        Nerir_ter_im obj = new Nerir_ter_im();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ներիր Տեր իմ");
        textView.setText(Html.fromHtml(erg));
    }

    public void O_inch_hrashq_metutyunes(View view) {
        O_inch_hrashq_metutyunes obj = new O_inch_hrashq_metutyunes();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Օ ինչ հրաշք մեծություն ես");
        textView.setText(Html.fromHtml(erg));
    }

    public void Orhni_mez_orhnyal(View view) {
        Orhni_mez_orhnyal obj = new Orhni_mez_orhnyal();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Օրհնիր մեզ օրհնյալ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ov_Hisus_imdz_mot_ari(View view) {
        Ov_Hisus_imdz_mot_ari obj = new Ov_Hisus_imdz_mot_ari();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ով Հիսուս ինձ մոտ արի");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ovkianos(View view) {
        Ovkianos obj = new Ovkianos();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Օվկիանոս");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ovsanna(View view) {
        Ovsanna obj = new Ovsanna();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Օվսաննա");
        textView.setText(Html.fromHtml(erg));
    }

    public void Parq_enq_talis_qez(View view) {
        Parq_enq_talis_qez obj = new Parq_enq_talis_qez();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Փառք ենք տալիս քեզ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Parq_tveq_Ascun(View view) {
        Parq_tveq_Ascun obj = new Parq_tveq_Ascun();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Փառք տվեք Ասծուն");
        textView.setText(Html.fromHtml(erg));
    }

    public void Qezem_nvirum(View view) {
        Qezem_nvirum obj = new Qezem_nvirum();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Քեզեմ նվիրում");
        textView.setText(Html.fromHtml(erg));
    }

    public void Qich_m_arev(View view) {
        Qich_m_arev obj = new Qich_m_arev();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Քիչմը արև");
        textView.setText(Html.fromHtml(erg));
    }

    public void Qo_ser_barcr_e(View view) {
        Qo_ser_barcr_e obj = new Qo_ser_barcr_e();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Քո սերը բարձր է");
        textView.setText(Html.fromHtml(erg));
    }

    public void Qo_sirov_indz_ayceleles(View view) {
        Qo_sirov_indz_ayceleles obj = new Qo_sirov_indz_ayceleles();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Քո սիրով ինձ այցելել ես");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ser(View view) {
        Ser obj = new Ser();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Սեր");
        textView.setText(Html.fromHtml(erg));
    }

    public void Shnorhakal_enq_ter_Hisusin(View view) {
        Shnorhakal_enq_ter_Hisusin obj = new Shnorhakal_enq_ter_Hisusin();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Շնորհակալ ենք Տեր Հիսուս");
        textView.setText(Html.fromHtml(erg));
    }

    public void Srtis_miak_papag(View view) {
        Srtis_miak_papag obj = new Srtis_miak_papag();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Սրտիս միակ փափագն");
        textView.setText(Html.fromHtml(erg));
        current_Msuic_File = "Srtis_papag";
    } // done

    public void Ter_du_gites(View view) {
        Ter_du_gites obj = new Ter_du_gites();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Տեր դու գիտես");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ter_Hisus_ter_Hisus(View view) {
        Ter_Hisus_ter_Hisus obj = new Ter_Hisus_ter_Hisus();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Տեր դու գիտես");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ter_pahe_zis(View view) {
        Ter_pahe_zis obj = new Ter_pahe_zis();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Տեր պահի զիս");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ter_im_hovivn_e(View view) {
        Ter_im_hovivn_e obj = new Ter_im_hovivn_e();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Տերը իմ, Հովիվս է");
        textView.setText(Html.fromHtml(erg));
    }

    public void Tern_e_im_luysn_u_prkutyun(View view) {
        Tern_e_im_luysn_u_prkutyun obj = new Tern_e_im_luysn_u_prkutyun();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Տերն է իմ լույսն ու փրկությունը");
        textView.setText(Html.fromHtml(erg));
    }

    public void Tirojme_kxndrem(View view) {
        Tirojme_kxndrem obj = new Tirojme_kxndrem();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Տերոջմե կխնդրեմ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Uzumem_motenal_qez(View view) {
        Uzumem_motenal_qez obj = new Uzumem_motenal_qez();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ուզում եմ մոտենալ Քեզ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Vordis(View view) {
        Vordis obj = new Vordis();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Որդիս");
        textView.setText(Html.fromHtml(erg));
    }

    public void Vortex_Tiroj_hoqin(View view) {
        Vortex_Tiroj_hoqin obj = new Vortex_Tiroj_hoqin();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Որտեղ Տիրոջ Հոգին ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Xachin_mot(View view) {
        Xachin_mot obj = new Xachin_mot();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Խաչին մոտ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Zorutyun_qajutyun(View view) {
        Zorutyun_qajutyun obj = new Zorutyun_qajutyun();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Զորություն Քաջություն");
        textView.setText(Html.fromHtml(erg));
    }

    public void Astco_ser(View view) {
        Astco_ser obj = new Astco_ser();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Աստծո սերը");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ekeq_miananq(View view) {
        Ekeq_miananq obj = new Ekeq_miananq();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Եկեք  միանաք");
        textView.setText(Html.fromHtml(erg));
    }

    public void Tsap_zark(View view) {
        Tsap_zark obj = new Tsap_zark();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ծափ զարկ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Hachaxakai_ari_du_goxgota(View view) {
        Hachaxakai_ari_du_goxgota obj = new Hachaxakai_ari_du_goxgota();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Հաճախակի արի դու գողգոթա");
        textView.setText(Html.fromHtml(erg));
    }

    public void Hisus_dues_im_ter(View view) {
        Hisus_dues_im_ter obj = new Hisus_dues_im_ter();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Հիսուս Դու ես իմ Տերը");
        textView.setText(Html.fromHtml(erg));
    }

    public void Hisus_sirum_enq(View view) {
        Hisus_sirum_enq obj = new Hisus_sirum_enq();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Հիսուս սիրում ենք");
        textView.setText(Html.fromHtml(erg));
    }

    public void Miayn_qez_nayem(View view) {
        Miayn_qez_nayem obj = new Miayn_qez_nayem();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Միայն քեզի նայիմ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Parq_tanq(View view) {
        Parq_tanq obj = new Parq_tanq();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Փառք տանք");
        textView.setText(Html.fromHtml(erg));
    }

    public void Qristos_ov_chgiti(View view) {
        Qristos_ov_chgiti obj = new Qristos_ov_chgiti();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Քրիստոս ով չգիտի");
        textView.setText(Html.fromHtml(erg));
    }

    public void Qristosn_e_ayn_jayr(View view) {
        Qristosn_e_ayn_jayr obj = new Qristosn_e_ayn_jayr();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Քրիստոսն է ժայռը");
        textView.setText(Html.fromHtml(erg));
    }

    public void Orhnyal_erashxiq(View view) {
        Orhnyal_erashxiq obj = new Orhnyal_erashxiq();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Օրհնյալ երաշխիք");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ter_zorutyun_e(View view) {
        Ter_zorutyun_e obj = new Ter_zorutyun_e();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Տերը զորություն է");
        textView.setText(Html.fromHtml(erg));
    }

    public void Du_erknqic_erkir_ijar(View view) {
        Du_erknqic_erkir_ijar obj = new Du_erknqic_erkir_ijar();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Դու երկնքից երկիր իջար");
        textView.setText(Html.fromHtml(erg));
    }

    public void Hnern_ancan(View view) {
        Hnern_ancan obj = new Hnern_ancan();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Հներն անցան");
        textView.setText(Html.fromHtml(erg));
    }

    public void Tiroj_parq_tveq(View view) {
        Tiroj_parq_tveq obj = new Tiroj_parq_tveq();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Տիրոջը փառք տվեք");
        textView.setText(Html.fromHtml(erg));
    }

    public void Du_gites_tiroj_patverner(View view) {
        Du_gites_tiroj_patverner obj = new Du_gites_tiroj_patverner();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Դու գիտես Տիրոջ պատվերները");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ur_gnam(View view) {
        Ur_gnam obj = new Ur_gnam();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ուր գնամ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Tern_e_mer_arqaneri_arqan(View view) {
        Tern_e_mer_arqaneri_arqan obj = new Tern_e_mer_arqaneri_arqan();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Տերն է մեր Արքաների Արքան");
        textView.setText(Html.fromHtml(erg));
    }

    public void Du_petkes_indz(View view) {
        Du_petkes_indz obj = new Du_petkes_indz();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Դու պետք ես ինձ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Kergem(View view) {
        Kergem obj = new Kergem();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Կերգեմ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Es_mecarum_em(View view) {
        Es_mecarum_em obj = new Es_mecarum_em();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ես մեծարում եմ քեզ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Qo_carayin(View view) {
        Qo_carayin obj = new Qo_carayin();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Քո ծառային");
        textView.setText(Html.fromHtml(erg));
    }

    public void Aleluyaner(View view) {
        Aleluyaner obj = new Aleluyaner();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ալելույաներ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Erkinq_Asco_parq_kpatmi(View view) {
        Erkinq_Asco_parq_kpatmi obj = new Erkinq_Asco_parq_kpatmi();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Երկինքը Աստծո Փառքը կպատմե");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ek_ov_surb_hoqi(View view) {
        Ek_ov_surb_hoqi obj = new Ek_ov_surb_hoqi();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Եկ ով Սուրբ Հոգի");
        textView.setText(Html.fromHtml(erg));
    }

    public void Mek_ka_vor_arjani_e(View view) {
        Mek_ka_vor_arjani_e obj = new Mek_ka_vor_arjani_e();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Միայն մեկը կա որ արժանի է");
        textView.setText(Html.fromHtml(erg));
    }

    public void Im_kyanqi_huysnes_du(View view) {
        Im_kyanqi_huysnes_du obj = new Im_kyanqi_huysnes_du();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Իմ կյանքի հույսն ես դու");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ter_inch_gexecik_es(View view) {
        Ter_inch_gexecik_es obj = new Ter_inch_gexecik_es();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Տեր ինչ գեղեցիկ ես");
        textView.setText(Html.fromHtml(erg));
    }

    public void Vorqan_uzumem_em(View view) {
        Vorqan_uzumem_em obj = new Vorqan_uzumem_em();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Դու իմ ամուր ժայռն");
        textView.setText(Html.fromHtml(erg));
    }

    public void Es_sirum_em_Hisusin(View view) {
        Es_sirum_em_Hisusin obj = new Es_sirum_em_Hisusin();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ես սիրում եմ Հիսուսին");
        textView.setText(Html.fromHtml(erg));
    }

    public void Du_es_surb(View view) {
        Du_es_surb obj = new Du_es_surb();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Դու ես սուրբ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Du_es_miayn_surb(View view) {
        Du_es_miayn_surb obj = new Du_es_miayn_surb();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Դու ես միայն Սուրբ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Es_sirum_em_qez_sirov(View view) {
        Es_sirum_em_qez_sirov obj = new Es_sirum_em_qez_sirov();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ես սիրում եմ քեզ սիրով");
        textView.setText(Html.fromHtml(erg));
    }

    public void Hisus_erkrpagum_em_qez(View view) {
        Hisus_erkrpagum_em_qez obj = new Hisus_erkrpagum_em_qez();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Հիսուս երկրպագում եմ քեզ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Tesel_em_Hisusi_haxtwutyun(View view) {
        Tesel_em_Hisusi_haxtwutyun obj = new Tesel_em_Hisusi_haxtwutyun();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Տեսել եմ ես Հիսուսի հաղթությունը");
        textView.setText(Html.fromHtml(erg));
    }

    public void Hisus_krak_e(View view) {
        Hisus_krak_e obj = new Hisus_krak_e();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Հիսուս կրակ է");
        textView.setText(Html.fromHtml(erg));
    }

    public void Zinvor(View view) {
        Zinvor obj = new Zinvor();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Զինվոր");
        textView.setText(Html.fromHtml(erg));
    }

    public void Aranc_qez_im_kyanq(View view) {
        Aranc_qez_im_kyanq obj = new Aranc_qez_im_kyanq();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Առանց Քեզ իմ կյանքը");
        textView.setText(Html.fromHtml(erg));
    }

    public void Bari_hoviv(View view) {
        Bari_hoviv obj = new Bari_hoviv();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Բարի Հովիվ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Chka_huys_indz_hamar(View view) {
        Chka_huys_indz_hamar obj = new Chka_huys_indz_hamar();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Չկար հույս ինձ համար");
        textView.setText(Html.fromHtml(erg));
    }

    public void Kaxotem_qez_hamar(View view) {
        Kaxotem_qez_hamar obj = new Kaxotem_qez_hamar();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Կաղոթեմ քեզ համար");
        textView.setText(Html.fromHtml(erg));
    }

    public void Amen_tsunk_ktsrvi(View view) {
        Amen_tsunk_ktsrvi obj = new Amen_tsunk_ktsrvi();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ամեն ծունկ ծռի");
        textView.setText(Html.fromHtml(erg));
    }

    public void Umn_e_misht_parq(View view) {
        Umn_e_misht_parq obj = new Umn_e_misht_parq();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ումն է միշտ փառքը");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ergerov_qez_kmecarem(View view) {
        Ergerov_qez_kmecarem obj = new Ergerov_qez_kmecarem();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Երգերով Քեզ կմեծարեմ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ekexeci(View view) {
        Ekexeci obj = new Ekexeci();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Եկեղեցի");
        textView.setText(Html.fromHtml(erg));
    }

    public void Tsnvec_ter_Hsius(View view) {
        Tsnvec_ter_Hsius obj = new Tsnvec_ter_Hsius();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ծնվեց Տեր Հիսուս");
        textView.setText(Html.fromHtml(erg));
    }

    public void Xachin_misht_nayelov(View view) {
        Xachin_misht_nayelov obj = new Xachin_misht_nayelov();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Խաչին միշտ նայելով");
        textView.setText(Html.fromHtml(erg));
    }

    public void Tsnndyan_tonin(View view) {
        Tsnndyan_tonin obj = new Tsnndyan_tonin();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Ծննդյան տոնին մենք");
        textView.setText(Html.fromHtml(erg));
    }

    public void Hisus_parqi_e_arjani(View view) {
        Hisus_parqi_e_arjani obj = new Hisus_parqi_e_arjani();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Հիսուս փառքի ես արժանի");
        textView.setText(Html.fromHtml(erg));
    }

    public void Ir_xachov_mez(View view) {
        Ir_xachov_mez obj = new Ir_xachov_mez();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Իր խաչով մեզ");
        textView.setText(Html.fromHtml(erg));
    }

    public void Parq_tanq_Hisusin(View view) {
        Parq_tanq_Hisusin obj = new Parq_tanq_Hisusin();
        erg = obj.get_song();
        getSupportActionBar().setTitle("Փառք տանք մենք Հիսուսին");
        textView.setText(Html.fromHtml(erg));
    }

    public void selecttext(String swtext) {
        switch (swtext) {
            case "Արարիչ երկնքի": {
                Ararich_erknqi(null);
                break;
            }
            case "Ալելույաներ": {
                Aleluyaner(null);
                break;
            }
            case "Ահա կանգնած եմ": {
                Aha_kangnac_em(null);
                break;
            }
            case "Ահա, մենք մեր ուրախ Երգերով": {
                Aha_menq_mer_urax_ergerov(null);
                break;
            }
            case "Ամենից վեր": {
                Amenic_ver(null);
                break;
            }
            case "Ամեն ծունկ ծռի": {
                Amen_tsunk_ktsrvi(null);
                break;
            }
            case "Այդ ինչ խաղաղ աչքեր ունես": {
                Ayd_inch_xaxax_ajker_unes_du(null);
                break;
            }
            case "Առանց Քեզ իմ կյանքը": {
                Aranc_qez_im_kyanq(null);
                break;
            }
            case "Աստծո սերը": {
                Astco_ser(null);
                break;
            }
            case "Բարի Լուր": {
                Bari_lur(null);
                break;
            }
            case "Բարձյալ Արքա": {
                Barcial_arqa(null);
                break;
            }
            case "Բարի Հովիվ": {
                Bari_hoviv(null);
                break;
            }
            case "Բացի՚ր, իմ հոգու": {
                Bacir_im_hogu_achqer(null);
                break;
            }
            case "Բերնիս խոսքերը": {
                Beranis_xosqer(null);
                break;
            }
            case "Գալիս եմ  ներկայոթյանդ մեջ": {
                Galis_em(null);
                break;
            }
            case "Գարուն է հիմա իմ հոգում": {
                Garun_e_hima_im_hoqum(null);
                break;
            }
            case "Գարուն հրաշալի": {
                Garun_hrashali(null);
                break;
            }
            case "Դավանենք": {
                davanenq(null);
                break;
            }
            case "Դե իջիր իջիր": {
                De_ijir_ijir(null);
                break;
            }
            case "Դժվար պահին": {
                Djvar_pahin(null);
                break;
            }
            case "Դոլորոսան": {
                Dolorasan(null);
                break;
            }
            case "Դու գիտես Տիրոջ պատվերները": {
                Du_gites_tiroj_patverner(null);
                break;
            }
            case "Դու ես սերը իմ կյանքի": {
                Du_es_ser_im_kjanqi(null);
                break;
            }
            case "Դու ես միայն Սուրբ": {
                Du_es_miayn_surb(null);
                break;
            }
            case "Դու ես սուրբ": {
                Du_es_surb(null);
                break;
            }
            case "Դու երկնքից երկիր իջար": {
                Du_erknqic_erkir_ijar(null);
                break;
            }
            case "Դու պետք ես ինձ": {
                Du_petkes_indz(null);
                break;
            }
            case "Եկեղեցի": {
                Ekexeci(null);
                break;
            }
            case "Եկեք  միանաք": {
                Ekeq_miananq(null);
                break;
            }
            case "Եկ ով Սուրբ Հոգի": {
                Ek_ov_surb_hoqi(null);
                break;
            }
            case "Ես սիրում եմ քեզ սիրով": {
                Es_sirum_em_qez_sirov(null);
                break;
            }
            case "Ես հայր ունեմ": {
                Es_hayr_unem(null);
                break;
            }
            case "Ես մեծարում եմ քեզ": {
                Es_mecarum_em(null);
                break;
            }
            case "Ես սիրում եմ Հիսուսին": {
                Es_sirum_em_Hisusin(null);
                break;
            }
            case "ԵՎ Աստված այնպես": {
                Ev_astvac_aynpes(null);
                break;
            }
            case "Երգերով Քեզ կմեծարեմ": {
                Ergerov_qez_kmecarem(null);
                break;
            }
            case "Երբ կորաց էի": {
                Erb_korac_ei(null);
                break;
            }
            case "Երբ նայում եմ": {
                Erb_nayum_em(null);
                break;
            }
            case "Երբ որ Նա ամպերով": {
                Erb_vor_na_amperov(null);
                break;
            }
            case "Երկինքը Աստծո Փառքը կպատմե": {
                Erkinq_Asco_parq_kpatmi(null);
                break;
            }
            case "Երկնքի դուռը բաց է": {
                Erknqi_dur_bac_e(null);
                break;
            }
            case "Զինվոր": {
                Zinvor(null);
                break;
            }
            case "Զորություն Քաջություն": {
                Zorutyun_qajutyun(null);
                break;
            }
            case "Իմ Անգին": {
                Im_angin(null);
                break;
            }
            case "Իմ կյանքի հույսն ես դու": {
                Im_kyanqi_huysnes_du(null);
                break;
            }
            case "Ինչ անուն աշխարհ եկավ": {
                Inch_anun_ashxar_ekav(null);
                break;
            }
            case "Ինչ մեծ է Աստված": {
                Inch_mec_e_Astvac(null);
                break;
            }
            case "Ինչ մեծ ու հրաշալի": {
                Inch_mec_u_hrashali(null);
                break;
            }
            case "Իր խաչով մեզ": {
                Ir_xachov_mez(null);
                break;
            }
            case "Լուռ գիշեր": {
                Lur_Gisher(null);
                break;
            }
            case "Խաչին մոտ": {
                Xachin_mot(null);
                break;
            }
            case "Խաչին միշտ նայելով": {
                Xachin_misht_nayelov(null);
                break;
            }
            case "Ծափ զարկ": {
                Tsap_zark(null);
                break;
            }
            case "Ծննդյան տոնին մենք": {
                Tsnndyan_tonin(null);
                break;
            }
            case "Ծնվեց Տեր Հիսուս": {
                Tsnvec_ter_Hsius(null);
                break;
            }
            case "Կաղոթեմ քեզ համար": {
                Kaxotem_qez_hamar(null);
                break;
            }
            case "Կանգնեք մարդիկ": {
                Kangneq_mardiq(null);
                break;
            }
            case "Կերգեմ": {
                Kergem(null);
                break;
            }
            case "Հաճախակի արի դու գողգոթա": {
                Hachaxakai_ari_du_goxgota(null);
                break;
            }
            case "Հավատքս չեմ ուրանա": {
                Havatqs_chem_urana(null);
                break;
            }
            case "Հիսուսի սերը": {
                Hisusi_ser(null);
                break;
            }
            case "Հիսուսի սերը օրհնի մեզ": {
                Hisusi_ser_orhni_mez(null);
                break;
            }
            case "Հիսուսն է կյանքը": {
                Hisus_e_kyanq(null);
                break;
            }
            case "Հիսուս երկրպագում եմ քեզ": {
                Hisus_erkrpagum_em_qez(null);
                break;
            }
            case "Հիսուս դու ես այն ժայռը": {
                Hisus_Dues_ayn_jar(null);
                break;
            }
            case "Հիսուս Դու ես իմ Տերը": {
                Hisus_dues_im_ter(null);
                break;
            }
            case "Հիսուս կրակ է": {
                Hisus_krak_e(null);
                break;
            }
            case "Հիսուս սիրում ենք": {
                Hisus_sirum_enq(null);
                break;
            }
            case "Հներն անցան": {
                Hnern_ancan(null);
                break;
            }
            case "Մարդիք ծնվում են": {
                mardiq_cnvum_en(null);
                break;
            }
            case "Մենք ենք մեր հոքով": {
                menq_enq_mer_hoqov(null);
                break;
            }
            case "Մեծ ու հրաշալի": {
                mets_u_Hrashali_en(null);
                break;
            }
            case "Միայն քեզի նայիմ": {
                Miayn_qez_nayem(null);
                break;
            }
            case "Միայն մեկը կա որ արժանի է": {
                Mek_ka_vor_arjani_e(null);
                break;
            }
            case "Մտել եմ սրբությանտ սրբոցը": {
                mtel_em_srbutyant_srboc(null);
                break;
            }
            case "Ներիր Տեր իմ": {
                Nerir_ter_im(null);
                break;
            }
            case "Շնորհակալ ենք Տեր Հիսուս": {
                Shnorhakal_enq_ter_Hisusin(null);
                break;
            }
            case "Ով Հիսուս ինձ մոտ արի": {
                Ov_Hisus_imdz_mot_ari(null);
                break;
            }
            case "Որդիս": {
                Vordis(null);
                break;
            }
            case "Որտեղ Տիրոջ Հոգին": {
                Vortex_Tiroj_hoqin(null);
                break;
            }
            case "Ուզում եմ մոտենալ Քեզ": {
                Uzumem_motenal_qez(null);
                break;
            }
            case "Ումն է միշտ փառքը": {
                Umn_e_misht_parq(null);
                break;
            }
            case "Ուր գնամ": {
                Ur_gnam(null);
                break;
            }
            case "Չկար հույս ինձ համար": {
                Chka_huys_indz_hamar(null);
                break;
            }
            case "Սեր": {
                Ser(null);
                break;
            }
            case "Սրտիս միակ փափագն": {
                Srtis_miak_papag(null);
                break;
            }
            case "Տիրոջը փառք տվեք": {
                Tiroj_parq_tveq(null);
                break;
            }
            case "Տեսել եմ ես Հիսուսի հաղթությունը": {
                Tesel_em_Hisusi_haxtwutyun(null);
                break;
            }
            case "Տեր դու գիտես": {
                Ter_du_gites(null);
                break;
            }
            case "Տեր ինչ գեղեցիկ ես": {
                Ter_inch_gexecik_es(null);
                break;
            }
            case "Տեր պահի զիս": {
                Ter_pahe_zis(null);
                break;
            }
            case "Տերը զորություն է": {
                Ter_zorutyun_e(null);
                break;
            }
            case "Տերը իմ, Հովիվս է": {
                Ter_im_hovivn_e(null);
                break;
            }
            case "Տերն է իմ լույսն ու փրկությունը": {
                Tern_e_im_luysn_u_prkutyun(null);
                break;
            }
            case "Տերն է մեր Արքաների Արքան": {
                Tern_e_mer_arqaneri_arqan(null);
                break;
            }
            case "Տերոջմե կխնդրեմ": {
                Tirojme_kxndrem(null);
                break;
            }
            case "Փառք տանք": {
                Parq_tanq(null);
                break;
            }
            case "Փառք տանք մենք Հիսուսին": {
                Parq_tanq_Hisusin(null);
                break;
            }
            case "Փառք ենք տալիս քեզ": {
                Parq_enq_talis_qez(null);
                break;
            }
            case "Քեզեմ նվիրում": {
                Qezem_nvirum(null);
                break;
            }
            case "Քիչմը արև": {
                Qich_m_arev(null);
                break;
            }
            case "Քո ծառային": {
                Qo_carayin(null);
                break;
            }
            case "Քո սերը բարձր է": {
                Qo_ser_barcr_e(null);
                break;
            }
            case "Քո սիրով ինձ այցելել ես": {
                Qo_sirov_indz_ayceleles(null);
                break;
            }
            case "Քրիստոսն է ժայռը": {
                Qristosn_e_ayn_jayr(null);
                break;
            }
            case "Քրիստոս ով չգիտի": {
                Qristos_ov_chgiti(null);
                break;
            }
            case "Օ ինչ հրաշք մեծություն ես": {
                O_inch_hrashq_metutyunes(null);
                break;
            }
            case "Օրհնիր մեզ օրհնյալ": {
                Orhni_mez_orhnyal(null);
                break;
            }
            case "Օրհնյալ երաշխիք": {
                Orhnyal_erashxiq(null);
                break;
            }
            case "Օվսաննա": {
                Ovsanna(null);
                break;
            }
            case "Օվկիանոս": {
                Ovkianos(null);
                break;
            }
            case "Տեր Հիսուս Տեր Հիսուս": {
                Ter_Hisus_ter_Hisus(null);
                break;
            }
            case "Փառք տվեք Ասծուն": {
                Parq_tveq_Ascun(null);
                break;
            }
            case "Որքան ուզում եմ": {
                Vorqan_uzumem_em(null);
                break;
            }
            case "Հիսուս փառքի ես արժանի": {
                Hisus_parqi_e_arjani(null);
                break;
            }


        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        CharSequence title = item.getTitle();
        swsong = (String) title;
        selecttext(swsong);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
