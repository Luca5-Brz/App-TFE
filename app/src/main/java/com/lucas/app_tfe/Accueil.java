package com.lucas.app_tfe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Accueil extends AppCompatActivity {

    //public String BaseUrlSrv = "https://launcher.carrieresduhainaut.com/launcherdev/lucas/pageAndroid";
    String BaseUrl = "http://192.168.1.253/PageAndroid";

    TextView mTextViewBnj;
    Button mBtnBoissons;
    Button mBtnRepas;
    Button mBtnSnacks;
    Button mBtnFuts;
    Button mBtnScanning;
    Button mBtnAdmin;

    Intent affichageProduits;
    Intent GestionCarte;
    Intent scan;

    String login_admin;
    String niveauAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        Bundle extras = getIntent().getExtras();
        login_admin = extras.get("login").toString();
        niveauAdmin = extras.get("niveauAdmin").toString();
        Log.e("TestgetExtras Login",login_admin+","+niveauAdmin);

        initializeComponents();

        affichageProduits = new Intent(Accueil.this,AffichageProduitsActivity.class);
        GestionCarte = new Intent(Accueil.this,GestionCarteActivity.class);
        scan = new Intent(Accueil.this,ScanActivity.class);

        onClick();
        greetServeur(login_admin);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        greetServeur(login_admin);
    }

    public void initializeComponents(){
        mTextViewBnj = findViewById(R.id.main_textview_bonjour);
        mBtnBoissons = findViewById(R.id.main_button_boisson);
        mBtnRepas = findViewById(R.id.main_button_repas);
        mBtnSnacks = findViewById(R.id.main_button_snacks);
        mBtnFuts = findViewById(R.id.main_button_futs);
        mBtnScanning = findViewById(R.id.main_button_scanning);
        mBtnAdmin = findViewById(R.id.main_button_admin);
    }

    public void onClick(){

        mBtnRepas.setOnClickListener(view -> {
            scan.putExtra("type_produits","Repas");
            scan.putExtra("gestion","Produits");
            scan.putExtra("resultSrv","");
            scan.putExtra("login_admin",login_admin);
            startActivity(scan);
        });

        mBtnBoissons.setOnClickListener(view -> {
            scan.putExtra("type_produits","Boisson");
            scan.putExtra("gestion","Produits");
            scan.putExtra("resultSrv","");
            scan.putExtra("login_admin",login_admin);
            startActivity(scan);
        });

        mBtnSnacks.setOnClickListener(view -> {
            scan.putExtra("type_produits","Snacks");
            scan.putExtra("gestion","Produits");
            scan.putExtra("resultSrv","");
            scan.putExtra("login_admin",login_admin);
            startActivity(scan);
        });

        mBtnFuts.setOnClickListener(view -> {
            /*scan.putExtra("type_produits","Fut");
            scan.putExtra("gestion","Produits");
            scan.putExtra("resultSrv","");
            scan.putExtra("login_admin",login_admin);
            startActivity(scan);*/
            affichageProduits.putExtra("type_produits","Fut");
            affichageProduits.putExtra("id_carte","");
            affichageProduits.putExtra("login_admin",login_admin);
            startActivity(affichageProduits);
        });

        if(Integer.parseInt(niveauAdmin)>=4){
            mBtnScanning.setVisibility(View.VISIBLE);
            mBtnScanning.setOnClickListener(view -> {


                GestionCarte.putExtra("type_produits","");
                GestionCarte.putExtra("gestion","Cartes");
                scan.putExtra("login_admin",login_admin);
                startActivity(GestionCarte);
            });
        }

        if(niveauAdmin.equals("5")){

            mBtnAdmin.setVisibility(View.VISIBLE);

            mBtnAdmin.setOnClickListener(view -> {

                Intent scan = new Intent(this,ScanActivity.class);
                scan.putExtra("gestion","Admins");
                scan.putExtra("type_produits","");
                scan.putExtra("resultSrv","");
                scan.putExtra("login_admin","");
                startActivity(scan);

            });

        }

    }

    public void greetServeur(String prenom){

        if (prenom!=null){
            mTextViewBnj.setText(getString(R.string.bonjour_prenom,prenom));
        }

    }

}
