package br.edu.ifsp.spo.sprint1.Activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.edu.ifsp.spo.sprint1.Model.Local;
import br.edu.ifsp.spo.sprint1.R;

public class ListaLugares extends Activity
{
    private ArrayList<Local> locais;
    private ArrayList<String> nomes = new ArrayList<>();
    public static final String TAG = "Script";
    Button btnAddLugar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_lista_lugares);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                Bundle bundleAux = getIntent().getExtras();
                locais = bundleAux.getParcelableArrayList("Lista de Locais");
            } else {
                locais = new ArrayList<>();
            }
        }
        for (int i = 0; i < locais.size(); i++)
        {
            nomes.add(locais.get(i).getNome());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nomes);

        ListView lv = (ListView) findViewById(R.id.list);
        TextView tv = (TextView) findViewById(R.id.emptyView);
        btnAddLugar = (Button) findViewById(R.id.btnAddLugar);
        btnAddLugar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ListaLugares.this, CadastroLugar.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("Lista de Locais", locais);
                //intent.putParcelableArrayListExtra("Lista de Locais", locais);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        Log.i(TAG, ""+locais.size());
        if (locais.size() == 0)
        {
            lv.setEmptyView(findViewById(R.id.emptyView));
            lv.setVisibility(View.INVISIBLE);
        }
        else
        {
            lv.setAdapter(adapter);

            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Toast.makeText(ListaLugares.this, "Eu fui clickado loucamente", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });


        }
    }
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "ListaLugares.OnRestart()");
        Intent intent = getIntent();
        if (intent != null)
        {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                Bundle bundleAux = getIntent().getExtras();
                locais = bundleAux.getParcelableArrayList("Lista de Locais");
            }

        }


    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        Intent intent = new Intent(ListaLugares.this, Mapa.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("Lista de Locais", locais);
        //intent.putParcelableArrayListExtra("Lista de Locais", locais);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
