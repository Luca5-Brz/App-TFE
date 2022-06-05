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
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    PendingIntent pendingIntent;
    NfcAdapter nfcAdapter;
    TextView text;

    String id_carte;

    //String BaseUrl="https://launcher.carrieresduhainaut.com/launcherdev/lucas/pageAndroid";
    String BaseUrl = "http://192.168.1.253/PageAndroid";
    String urlSrv;
    String resultSrv;

    Intent accueil;
    String login="";
    String  niveau_admin="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.text);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        text.setText("Scannez la carte d'admin");

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
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

        urlSrv=BaseUrl+"/CheckAdmin.php?carte="+id_carte;
        CheckAdmin conn = new CheckAdmin(this);
        conn.execute(urlSrv);

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

    public void debut(){
        String[] retourSrv = resultSrv.split(",");
        String result = retourSrv[0];
        login = retourSrv[1];
        niveau_admin = retourSrv[2];

        accueil = new Intent(MainActivity.this,Accueil.class);
        accueil.putExtra("login",login);
        accueil.putExtra("niveauAdmin",niveau_admin);

        if (result.equals("OK")){
            startActivity(accueil);
        }else{

            AlertDialog diagAdmin = new AlertDialog.Builder(this)
                    .setTitle("Erreur")
                    .setMessage("Vous n'avez pas le droit d'accéder à cette application")
                    .setCancelable(false)
                    .setPositiveButton("OK",null)
                    .create();
            diagAdmin.show();

        }
    }

}