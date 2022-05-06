package com.lucas.app_tfe;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class ScanActivity extends AppCompatActivity {

    PendingIntent pendingIntent;
    NfcAdapter nfcAdapter;
    TextView text;

    String type;
    String gestion;
    String resultSrvClients;
    String login_admin;
    String resultSrv;

    String id_carte;
    String[] tabClientsId;

    String BaseUrl="https://launcher.carrieresduhainaut.com/launcherdev/lucas/pageAndroid";
    String urlSrv;

    Intent affichageProduits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        text = findViewById(R.id.text);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        text.setText("Scannez la carte");

        Bundle extras = getIntent().getExtras();
        type = extras.get("type_produits").toString();
        gestion = extras.get("gestion").toString();
        resultSrvClients = extras.get("resultSrv").toString();
        login_admin = extras.get("login_admin").toString();

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        tabClientsId = resultSrvClients.split(";");
        affichageProduits = new Intent(this,AffichageProduitsActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                showWirelessSettings();
            }

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }
            displayMsgs(msgs);
        }
    }

    private void displayMsgs(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0)
            return;
    }

    private String dumpTagData(Tag tag) {

        byte[] id = tag.getId();

        id_carte= String.valueOf(toDec(id));

        switch (gestion){

            case"Produits":
                affichageProduits.putExtra("type_produits",type);
                affichageProduits.putExtra("id_carte",id_carte);
                affichageProduits.putExtra("login_admin",login_admin);
                startActivity(affichageProduits);

                break;

            case"Cartes":
                affichageAlertCarte();

                break;

            case"Clients":
                affichageAlertClient();

                break;

            case"Admins":
                affichageAlertAdmin();

            default: break;

        }

        Log.e("Numéro de Carte",id_carte);
        return String.valueOf(toDec(id));
    }

    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }


    public void affichageAlertCarte(){

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20,20,20,20);

        TextView mTextViewNumCarte = new TextView(this);
        mTextViewNumCarte.setText("Numéro de carte : ");

        TextView mTextViewIdCarte = new TextView(this);
        mTextViewIdCarte.setText(id_carte);

        TextView mTextViewMontant = new TextView(this);
        mTextViewMontant.setText("Montant : ");

        EditText mEditTextMontant = new EditText(this);
        mEditTextMontant.setInputType(InputType.TYPE_CLASS_NUMBER);
        mEditTextMontant.setText("0");

        TextView mTextViewClient = new TextView(this);
        mTextViewClient.setText("Client : ");

        Spinner mSpinnerClients = new Spinner(this);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.ajout_carte);
        mSpinnerClients.setAdapter(spinnerAdapter);

        int j=0;
        while( j < tabClientsId.length){
            spinnerAdapter.add(String.valueOf(tabClientsId[j]));
            j++;
        }


        layout.addView(mTextViewNumCarte);
        layout.addView(mTextViewIdCarte);
        layout.addView(mTextViewMontant);
        layout.addView(mEditTextMontant);
        layout.addView(mTextViewClient);
        layout.addView(mSpinnerClients);


        AlertDialog diagCarte = new AlertDialog.Builder(this)
                .setTitle("Ajouter une carte")
                .setView(layout)
                .setPositiveButton("Ajouter", (dialogInterface, i) -> {
                    String ajoutCarte="";
                    ajoutCarte+=mTextViewIdCarte.getText();
                    ajoutCarte+=",";
                    ajoutCarte+=mEditTextMontant.getText();
                    ajoutCarte+=",";
                    ajoutCarte+=mSpinnerClients.getSelectedItem().toString();

                    urlSrv=BaseUrl+"/ajoutCarte.php?donnees="+ajoutCarte;
                    Log.e("Testtt URL",urlSrv);
                    AjoutCarte conn = new AjoutCarte();
                    conn.execute(urlSrv);
                    finish();
                })
                .setCancelable(true)
                .create();
        diagCarte.show();
    }

    public void affichageAlertClient(){

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20,20,20,20);

        TextView mTextViewNomClient = new TextView(this);
        mTextViewNomClient.setText("Nom");

        EditText mEditTextNomClient=new EditText(this);
        mEditTextNomClient.setHint("Nom du Client");

        TextView mTextViewPrenomClient = new TextView(this);
        mTextViewPrenomClient.setText("Prénom");

        EditText mEditTextPrenomClient = new EditText(this);
        mEditTextPrenomClient.setHint("Prénom du Client");

        TextView mTextViewNumCarte = new TextView(this);
        mTextViewNumCarte.setText("Numéro de la Carte");

        TextView mTextViewIdCarte = new TextView(this);
        mTextViewIdCarte.setText(id_carte);

        TextView mTextViewMontantCarte = new TextView(this);
        mTextViewMontantCarte.setText("Montant");

        EditText mEditTextMontantCarte = new EditText(this);
        mEditTextMontantCarte.setInputType(InputType.TYPE_CLASS_NUMBER);
        mEditTextMontantCarte.setText("0");


        layout.addView(mTextViewNomClient);
        layout.addView(mEditTextNomClient);
        layout.addView(mTextViewPrenomClient);
        layout.addView(mEditTextPrenomClient);
        layout.addView(mTextViewNumCarte);
        layout.addView(mTextViewIdCarte);
        layout.addView(mTextViewMontantCarte);
        layout.addView(mEditTextMontantCarte);

        AlertDialog diagCarte = new AlertDialog.Builder(this)
                .setTitle("Ajouter un client")
                .setView(layout)
                .setPositiveButton("Ajouter", (dialogInterface, i) -> {
                    String ajoutClient="";
                    ajoutClient+=mEditTextNomClient.getText().toString();
                    ajoutClient+=",";
                    ajoutClient+=mEditTextPrenomClient.getText().toString();
                    ajoutClient+=",";
                    ajoutClient+=mTextViewIdCarte.getText().toString();
                    ajoutClient+=",";
                    ajoutClient+=mEditTextMontantCarte.getText().toString();

                    urlSrv=BaseUrl+"/ajoutClient.php?donnees="+ajoutClient;
                    Log.e("Testtt URL",urlSrv);
                    AjoutCarte conn = new AjoutCarte();
                    conn.execute(urlSrv);
                    finish();
                })
                .setCancelable(true)
                .create();
        diagCarte.show();

    }

    public void affichageAlertAdmin(){

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20,20,20,20);

        TextView mTextViewNomAdmin = new TextView(this);
        mTextViewNomAdmin.setText("Login");

        EditText mEditTextNomAdmin=new EditText(this);
        mEditTextNomAdmin.setHint("Login de l'Admin");

        TextView mTextViewMdpAdmin = new TextView(this);
        mTextViewMdpAdmin.setText("Mot de Passe");

        EditText mEditTextMdpAdmin = new EditText(this);
        mEditTextMdpAdmin.setHint("Mot de Passe");
        mEditTextMdpAdmin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        TextView mTextViewNumCarte = new TextView(this);
        mTextViewNumCarte.setText("Numéro de la Carte");

        TextView mTextViewIdCarte = new TextView(this);
        mTextViewIdCarte.setText(id_carte);

        TextView mTextViewNiveauAdmin = new TextView(this);
        mTextViewNiveauAdmin.setText("Niveau");

        Spinner mSpinnerNiveauAdmin = new Spinner(this);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.ajout_carte);
        mSpinnerNiveauAdmin.setAdapter(spinnerAdapter);

        spinnerAdapter.add("1");
        spinnerAdapter.add("2");
        spinnerAdapter.add("3");
        spinnerAdapter.add("4");
        spinnerAdapter.add("5");


        layout.addView(mTextViewNomAdmin);
        layout.addView(mEditTextNomAdmin);
        layout.addView(mTextViewMdpAdmin);
        layout.addView(mEditTextMdpAdmin);
        layout.addView(mTextViewNumCarte);
        layout.addView(mTextViewIdCarte);
        layout.addView(mTextViewNiveauAdmin);
        layout.addView(mSpinnerNiveauAdmin);

        AlertDialog diagCarte = new AlertDialog.Builder(this)
                .setTitle("Ajouter un Admin")
                .setView(layout)
                .setPositiveButton("Ajouter", (dialogInterface, i) -> {

                    MCrypt mcrypt = new MCrypt();
                    /* Encrypt */
                    String MdpEncrypted = null;
                    try {
                        MdpEncrypted = MCrypt.bytesToHex( mcrypt.encrypt(mEditTextMdpAdmin.getText().toString()) );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    String ajoutAdmin="";
                    ajoutAdmin+=mEditTextNomAdmin.getText().toString();
                    ajoutAdmin+="&Mdp=";
                    ajoutAdmin+=MdpEncrypted;
                    ajoutAdmin+="&carte=";
                    ajoutAdmin+=mTextViewIdCarte.getText().toString();
                    ajoutAdmin+="&niveau=";
                    ajoutAdmin+=mSpinnerNiveauAdmin.getSelectedItem().toString();

                    urlSrv=BaseUrl+"/ajoutAdmin.php?login="+ajoutAdmin;
                    Log.e("Testtt URL",urlSrv);
                    AjoutAdminToServer conn = new AjoutAdminToServer(this);
                    conn.execute(urlSrv);
                })
                .setCancelable(true)
                .create();
        diagCarte.show();

    }

    public void ErreurAdmin(){

            if(resultSrv.equals("ErreurAdmin") || resultSrv.equals("ErreurAdminClient")){
                AlertDialog diagError = new AlertDialog.Builder(this)
                        .setTitle("Erreur")
                        .setMessage("Une erreur est survenue. Allez voir l'administrateur")
                        .setPositiveButton("OK",(dialogInterface, i) -> finish())
                        .setCancelable(true)
                        .create();
                diagError.show();

            }else if(resultSrv.equals("NonUnique")){
                AlertDialog diagError = new AlertDialog.Builder(this)
                        .setTitle("Erreur")
                        .setMessage("Le login entré ou la carte existe déjà dans le systéme. Veuillez réessayer")
                        .setPositiveButton("OK",(dialogInterface, i) -> finish())
                        .setCancelable(true)
                        .create();
                diagError.show();
            }
        finish();

    }
}
