package com.lucas.app_tfe;

import static java.lang.Integer.parseInt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class AffichageProduitsActivity extends MainActivity {

    String mIdProduit;
    String mNomProduits ="Pinte";
    String mPrixProduits = "2";
    String mQuantiteProduits = "0";

    String[][] tabPrixProd = new String[50][5]; // [nomProd] [PrixProd] [QteProd]
    String resultSrv;

    TableLayout mTableLayout;
    float total =0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affichage_produits);

        mTableLayout = findViewById(R.id.affichage_prod_tableLayout);

        CheckProduitsOnServer conn = new CheckProduitsOnServer(this);
        conn.execute(BaseUrlSrv+"/getProd.php?type=Boisson");

        //afficherProd();

    }

    public void afficherProd(){

        String[] tabSplit = resultSrv.split(";");
        int i=0;

        for ( String s : tabSplit)
        {
            String[] tabSplit2 = s.split(",");

            mIdProduit = tabSplit[0];
            mNomProduits = tabSplit2[1];
            mPrixProduits = tabSplit2[2];

            tabPrixProd [i][0] = mIdProduit;
            tabPrixProd [i][1] = mNomProduits;
            tabPrixProd [i][2] = mQuantiteProduits;
            tabPrixProd [i][3] = mPrixProduits;

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
            btnPlus.setOnClickListener(view -> {
                String temp = tabPrixProd[btnPlus.getId()][2];
                int temp2 = parseInt(temp)+1;
                tabPrixProd[btnPlus.getId()][2] = String.valueOf(temp2);
                txtQteProd.setText(String.valueOf(temp2));

                total();
            });

            Button btnMoins = new Button(this);
            btnMoins.setText("-");
            btnMoins.setEnabled(true);
            btnMoins.setId(i);
            btnMoins.setOnClickListener(view ->{
                String temp = tabPrixProd[btnMoins.getId()][2];
                if(parseInt(temp)>0)
                {
                    int temp2 = parseInt(temp)-1;
                    tabPrixProd[btnMoins.getId()][2] = String.valueOf(temp2);
                    txtQteProd.setText(String.valueOf(temp2));
                }
                total();
            });

            tbRow.addView(txtProd);
            tbRow.addView(txtPrixProd);
            tbRow.addView(txtQteProd);
            tbRow.addView(btnPlus);
            tbRow.addView(btnMoins);

            mTableLayout.addView(tbRow,layoutParams);
            i++;
        }

    }

    public void total(){
        for (int i=0;i< tabPrixProd.length;i++) {
            if(tabPrixProd[i][3]!=null){
                float prix = Float.parseFloat(tabPrixProd[i][3]);
                float qte = Float.parseFloat(tabPrixProd[i][2]);

                float totalLigne = prix * qte;
                total += totalLigne;
            }
        }
        setTitle("Total : "+total+"€");
    }

}