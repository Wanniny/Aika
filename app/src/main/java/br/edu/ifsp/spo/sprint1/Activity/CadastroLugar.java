package br.edu.ifsp.spo.sprint1.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifsp.spo.sprint1.Model.Local;
import br.edu.ifsp.spo.sprint1.R;

public class CadastroLugar extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,  AdapterView.OnItemSelectedListener

{


    public static final String TAG = "Script";
    EditText edtLugar;
    private ArrayList<Local> locais;
    private String modo;
    private LatLng minhaLocalizacao;
    GoogleApiClient mGoogleApiClient;
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_lugar);
        edtLugar = (EditText) findViewById(R.id.edtLugar);
        spinner = (Spinner) findViewById(R.id.spinnerTransporte);

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

        if(mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> modosDeTransporte = new ArrayList<>();
        modosDeTransporte.add("A pé");
        modosDeTransporte.add("Carro");
        modosDeTransporte.add("Bicicleta");
        modosDeTransporte.add("Transporte Publico");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modosDeTransporte);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onRestart()
    {
        super.onRestart();
        Log.i(TAG, "CadastroLugar.onRestart()");
        finish();
        Log.i(TAG, "Cadastro lugar destruido");
    }

    public void clique(View view)
    {
        Button bt = (Button) view;
        Intent intent;


            if (edtLugar.getText().toString().equalsIgnoreCase(null) || edtLugar.getText().toString().equalsIgnoreCase("") || modo==null)
            {
                Toast.makeText(this, "Campo Nome da Rua Vazio ou Não Selecionou o meio de transporte", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String nomeLocal = edtLugar.getText().toString().toUpperCase();
                edtLugar.setText(null);
                Toast.makeText(this, "Local Adicionado", Toast.LENGTH_SHORT).show();
                Local local = new Local();
                local.setNome(nomeLocal);
                local.setCoordenada(getLocationFromAddress(this, nomeLocal));
                local.setModoDeTransporte(modo);
                locais.add(local);

            }


        }





    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        List<Address> endereco;
        LatLng p1 = null;

        try {

            endereco = coder.getFromLocation(minhaLocalizacao.latitude, minhaLocalizacao.longitude, 1);
            address = coder.getFromLocationName(strAddress + "," + endereco.get(0).getAdminArea(), 5);
            Log.i(TAG, "Nome da cidade: " + endereco.get(0).getAdminArea());
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p1;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null)
            {
                minhaLocalizacao = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }

        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        switch (position)
        {
            case 0: modo = "walking"; break;
            case 1: modo = "driving"; break;
            case 2: modo = "bycicling"; break;
            case 3: modo = "transit"; break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        Intent intent = new Intent(CadastroLugar.this, ListaLugares.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("Lista de Locais", locais);
        //intent.putParcelableArrayListExtra("Lista de Locais", locais);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

