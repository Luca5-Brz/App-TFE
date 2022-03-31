package com.lucas.app_tfe;

import static java.lang.Integer.parseInt;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class AffichageProduitsActivity extends MainActivity {

    String mIdProduit;
    String mNomProduits ="Pinte";
    String mPrixProduits = "2";
    String mQuantiteProduits = "0";

    String[][] tabPrixProd = new String[50][5]; // [nomProd] [PrixProd] [QteProd]
    String resultSrv;

    TableLayout mTableLayout;
    TableLayout mTableLayoutTitle;

    String type_prod;
    String id_carte;

    String montantCarte;
    String urlSrv;

    TextView txtTotalTitle;
    TextView txtMontantTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affichage_produits);

        mTableLayout = findViewById(R.id.affichage_prod_tableLayout);
        mTableLayoutTitle = findViewById(R.id.affichage_prod_tableLayout_title);

        Bundle extras = getIntent().getExtras();
        type_prod = extras.get("type_produits").toString();
        id_carte = extras.get("id_carte").toString();

        urlSrv = BaseUrlSrv+"/getProd.php?type="+type_prod+"&idCarte="+id_carte;
        CheckProduitsOnServer conn = new CheckProduitsOnServer(this);
        conn.execute(urlSrv);
        Log.e("urlllll",urlSrv);
    }

    public void afficherProd(){

        String[] tabSplit = resultSrv.split(";");
        int i=0;

        TableRow tbRowTitle = new TableRow(this);
        TableLayout.LayoutParams layoutParamsTitle = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT);

        txtTotalTitle = new TextView(this);
        txtTotalTitle.setText("Total : 0€");
        txtTotalTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtTotalTitle.setTextSize(18);

        txtMontantTitle = new TextView(this);
        txtMontantTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtMontantTitle.setTextSize(18);

        tbRowTitle.addView(txtTotalTitle);
        tbRowTitle.addView(txtMontantTitle);

        mTableLayoutTitle.addView(tbRowTitle,layoutParamsTitle);

        for ( String s : tabSplit)
        {
            String[] tabSplit2 = s.split(",");

            mIdProduit = tabSplit[0];
            mNomProduits = tabSplit2[1];
            mPrixProduits = tabSplit2[2];
            montantCarte = tabSplit2[3];

            tabPrixProd [i][0] = mIdProduit;
            tabPrixProd [i][1] = mNomProduits;
            tabPrixProd [i][2] = mQuantiteProduits;
            tabPrixProd [i][3] = mPrixProduits;


            txtMontantTitle.setText("Montant : "+montantCarte+"€");

            TableRow tbRow = new TableRow(this);
            TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT);


            TextView txtProd = new TextView(this);
            txtProd.setText(mNomProduits);
            txtProd.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            TextView txtPrixProd = new TextView(this);
            txtPrixProd.setText(mPrixProduits+"€");
            txtPrixProd.setPadding(15,0,15,0);
            txtPrixProd.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            TextView txtQteProd = new TextView(this);
            txtQteProd.setText(mQuantiteProduits);
            txtQteProd.setPadding(50,0,50,0);
            txtQteProd.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            Button btnPlus = new Button(this);
            btnPlus.setText("+");
            btnPlus.setEnabled(true);
            btnPlus.setId(i);
            btnPlus.setBackgroundTintList(ColorStateList.valueOf(0xffa4c408));
            btnPlus.setOnClickListener(view -> {
                
                float total=total();
                if(total<=(Float.valueOf(montantCarte))-Float.valueOf(tabPrixProd [btnPlus.getId()][3])){

                    String temp = tabPrixProd[btnPlus.getId()][2];
                    int temp2 = parseInt(temp)+1;
                    tabPrixProd[btnPlus.getId()][2] = String.valueOf(temp2);
                    txtQteProd.setText(String.valueOf(temp2));

                    total=total();

                }else{
                    Toast.makeText(this, "T'as pas de thune :"+total, Toast.LENGTH_SHORT).show();
                }
                
            });

            Button btnMoins = new Button(this);
            btnMoins.setText("-");
            btnMoins.setEnabled(true);
            btnMoins.setId(i);
            btnMoins.setBackgroundTintList(ColorStateList.valueOf(0xffa4c408));
            btnMoins.setOnClickListener(view ->{

                float total=total();
                String temp = tabPrixProd[btnMoins.getId()][2];
                if(parseInt(temp)>0)
                {
                    int temp2 = parseInt(temp)-1;
                    tabPrixProd[btnMoins.getId()][2] = String.valueOf(temp2);
                    txtQteProd.setText(String.valueOf(temp2));
                    total=total();
                }

            });

            tbRow.addView(txtProd);
            tbRow.addView(txtPrixProd);
            tbRow.addView(txtQteProd);
            tbRow.addView(btnPlus);
            tbRow.addView(btnMoins);

            mTableLayout.addView(tbRow,layoutParams);
            i++;
        }

        Button btnValider = new Button(this);
        btnValider.setText("Valider !");
        btnValider.setEnabled(true);
        btnValider.setBackgroundTintList(ColorStateList.valueOf(0xffa4c408));
        btnValider.setOnClickListener(view -> {

            StringBuilder messageCommande= new StringBuilder();

            for (int j=0;j< tabPrixProd.length;j++) {
                if(tabPrixProd[j][3]!=null){
                    String produit = tabPrixProd[j][1];
                    int qte = parseInt(tabPrixProd[j][2]);

                    messageCommande.append(produit);
                    messageCommande.append("     ");
                    messageCommande.append(qte);
                    messageCommande.append("\n");

                }
            }

            AlertDialog diagCommande = new AlertDialog.Builder(this)
                    .setTitle("Récapitulatif de Commande")
                    .setPositiveButton("ok",null)
                    .setMessage(messageCommande.toString())
                    .create();
            diagCommande.show();




            urlSrv=BaseUrlSrv+"/validerCommande.php";
            ValiderCommande conn = new ValiderCommande(this);
            conn.execute(urlSrv);
        });
        mTableLayout.addView(btnValider);
    }

    public float total(){
        float total=0;
        for (int i=0;i< tabPrixProd.length;i++) {
            if(tabPrixProd[i][3]!=null){
                float prix = Float.parseFloat(tabPrixProd[i][3]);
                float qte = Float.parseFloat(tabPrixProd[i][2]);

                float totalLigne = prix * qte;
                total += totalLigne;
            }
        }
        txtTotalTitle.setText("Total : "+total+"€");
        txtMontantTitle.setText("Montant : "+montantCarte+"€");
        return total;
    }

}