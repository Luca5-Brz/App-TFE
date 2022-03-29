package com.lucas.app_tfe;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView mTextViewBnj;
    EditText mEditTextServeur;
    Button mBtnBoissons;
    Button mBtnRepas;

    private static final String SHARED_PREF_USER_INFO="SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_NAME="SHARED_PREF_USER_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        
        mEditTextServeur.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mBtnBoissons.setEnabled(!s.toString().isEmpty());
                mBtnRepas.setEnabled(!s.toString().isEmpty());
            }
        });
        
        onClick();
        greetServeur();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        greetServeur();
    }

    public void initializeComponents(){
        mTextViewBnj = findViewById(R.id.main_textview_bonjour);
        mEditTextServeur = findViewById(R.id.main_editText_nom);
        mBtnBoissons = findViewById(R.id.main_button_boisson);
        mBtnRepas = findViewById(R.id.main_button_repas);

        mBtnBoissons.setEnabled(false);
        mBtnRepas.setEnabled(false);
    }

    public void onClick(){

        mBtnRepas.setOnClickListener(view -> {
            Toast.makeText(this, "Sert les repas", Toast.LENGTH_SHORT).show();
            checkName(mEditTextServeur.getText().toString());
        });
        mBtnBoissons.setOnClickListener(view -> {
            Toast.makeText(this, "Sert les Boissons", Toast.LENGTH_SHORT).show();
            checkName(mEditTextServeur.getText().toString());
        });

    }

    public void checkName(String name){
        getSharedPreferences(SHARED_PREF_USER_INFO,MODE_PRIVATE)
                .edit()
                .putString(SHARED_PREF_USER_NAME,name)
                .apply();
    }

    public void greetServeur(){
        String prenom = getSharedPreferences(SHARED_PREF_USER_INFO,MODE_PRIVATE).getString(SHARED_PREF_USER_NAME,null);

        if (prenom!=null){
            mTextViewBnj.setText(getString(R.string.bonjour_prenom,prenom));
        }
        mEditTextServeur.setText(prenom);

    }

}