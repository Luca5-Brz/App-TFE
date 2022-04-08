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

import org.w3c.dom.Text;

public class ScanActivity extends MainActivity {

    PendingIntent pendingIntent;
    NfcAdapter nfcAdapter;
    TextView text;

    String type;
    String gestion;
    String resultSrvClients;

    String id_carte;
    String[] tabClientsId;

    String BaseUrl="https://launcher.carrieresduhainaut.com/launcherdev/lucas/pageAndroid";
    String urlSrv;

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

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        tabClientsId = resultSrvClients.split(";");
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

        if(gestion.equals("Produits")){

            affichageProduits.putExtra("type_produits",type);
            affichageProduits.putExtra("id_carte",String.valueOf(toDec(id)));
            startActivity(affichageProduits);


        }else if(gestion.equals("Cartes")){

            /*GestionCarte.putExtra("id_carte",String.valueOf(toDec(id)));
            startActivity(GestionCarte);*/

            affichageAlertCarte();
        }else if(gestion.equals("Clients")){

            affichageAlertClient();
        }

        Log.e("Numéro de Carte",String.valueOf(toDec(id)));
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
            Log.e("Testttttttt",String.valueOf(tabClientsId[j]));
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

                    Log.e("Testtt",ajoutCarte);

                    urlSrv=BaseUrlSrv+"/ajoutCarte.php?donnees="+ajoutCarte;
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

                    Log.e("Testtt",ajoutClient);

                    urlSrv=BaseUrlSrv+"/ajoutClient.php?donnees="+ajoutClient;
                    Log.e("Testtt URL",urlSrv);
                    AjoutCarte conn = new AjoutCarte();
                    conn.execute(urlSrv);
                    finish();
                })
                .setCancelable(true)
                .create();
        diagCarte.show();

    }
}
