package com.lucas.app_tfe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GestionCarteActivity extends AppCompatActivity {

    public String BaseUrlSrv = "https://launcher.carrieresduhainaut.com/launcherdev/lucas/pageAndroid";
    String urlSrv;
    String resultSrv;

    Button mButtonClient;
    Button mButtonCarte;

    String id_carte;
    String gestion;

    String[] tabClientsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_carte);

        Bundle extras = getIntent().getExtras();
        gestion = extras.get("gestion").toString();

        initializeComponents();
        setOnClick();

        urlSrv=BaseUrlSrv+"/CheckClients.php";
        CheckClientOnServer conn = new CheckClientOnServer(this);
        conn.execute(urlSrv);



    }

    public void initializeComponents(){
        mButtonCarte = findViewById(R.id.gestionCarte_button_carte);
        mButtonClient = findViewById(R.id.gestionCarte_button_client);

    }

    public void setOnClick(){
        mButtonCarte.setOnClickListener(view ->{

            Intent scan = new Intent(this,ScanActivity.class);
            scan.putExtra("gestion",gestion);
            scan.putExtra("type_produits","");
            scan.putExtra("resultSrv",resultSrv);
            scan.putExtra("login_admin","");
            startActivity(scan);


        });

        mButtonClient.setOnClickListener(view ->{

            Intent scan = new Intent(this,ScanActivity.class);
            scan.putExtra("gestion","Clients");
            scan.putExtra("type_produits","");
            scan.putExtra("resultSrv",resultSrv);
            scan.putExtra("login_admin","");
            startActivity(scan);

        });
    }

    public void splitString(){
        int i=0;
        tabClientsId = resultSrv.split(";");
        while( i < tabClientsId.length){
            Log.e("Testttttttt",String.valueOf(tabClientsId[i]));
            i++;
        }
    }
}