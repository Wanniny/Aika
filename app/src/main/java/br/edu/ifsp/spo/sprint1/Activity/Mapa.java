package br.edu.ifsp.spo.sprint1.Activity;

import android.Manifest;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import br.edu.ifsp.spo.sprint1.Geofence.GeofenceDB;
import br.edu.ifsp.spo.sprint1.Geofence.GeofenceInfo;
import br.edu.ifsp.spo.sprint1.Geofence.GeofenceReceiver;
import br.edu.ifsp.spo.sprint1.Model.Local;
import br.edu.ifsp.spo.sprint1.Model.Rota;
import br.edu.ifsp.spo.sprint1.Model.RotaTask;
import br.edu.ifsp.spo.sprint1.R;

public class Mapa extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "Script";
    private GoogleMap mMap;
    private Polyline polyline;
    private int[] cores = new int[]{Color.BLUE, Color.GREEN, Color.MAGENTA, Color.BLACK, Color.CYAN, Color.DKGRAY, Color.WHITE, Color.LTGRAY, Color.GRAY};
    private Marker marker;
    private ArrayList<Local> locais = new ArrayList<>();
    private LatLng myPosition;
    private GeofenceInfo mGeofenceInfo; //Geofence em si
    private GeofenceDB mGeofenceDB; //Armazena a Geofence
    private GoogleApiClient mGoogleApiClient;
    private List<Rota> rotas;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "Mapa.onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //Pegando os dados que a outra Activity mandou
        Intent intent = getIntent();
        if (intent != null)
        {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                Bundle bundleAux = getIntent().getExtras();
                locais = bundleAux.getParcelableArrayList("Lista de Locais");
            }
            else
            {
                locais = new ArrayList<>();
            }
        }
        conectar();



        mGeofenceDB = new GeofenceDB(this);

    }

    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "Mapa.OnRestart()");
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



    public void conectar()
    {
        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void getActivity(View view)
    {
        Intent intent = new Intent(this, ListaLugares.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("Lista de Locais", locais);
        //intent.putParcelableArrayListExtra("Lista de Locais", locais);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //Quando inicia
    protected void onStart()
    {

        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        //Seta o mapa
        mMap = googleMap;

        //Checka Permissão
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {

        }

        //Coloca um ponto azul onde o usuario está
        mMap.setMyLocationEnabled(true);

        //Efeitos da Camera
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myPosition).zoom(10).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
        //Anima a Camera
        mMap.animateCamera(update, 200, new GoogleMap.CancelableCallback() {

            @Override
            public void onCancel() {
                Log.i("Script", "CancelableCallback().onCancel()");

            }

            @Override
            public void onFinish() {
                Log.i("Script", "CancelableCallback().onFinish()");

            }

        });


        if(locais.size()!= 1)
        {

            //Pegando a lista de rotas
            RotaTask rotaTask = new RotaTask();
            try
            {
                rotas = rotaTask.execute(locais).get();
            }

            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            catch (ExecutionException e)
            {
                e.printStackTrace();
            }


            //Distribui os Marcadores
            for (int i = 0; i < rotas.size(); i++)
            {
                if (i == 0)
                {
                    customAddMarker(locais.get(i + 1).getCoordenada(), "" + locais.get(i + 1).getNome(),
                            "" + rotas.get(i).getTempo());
                }

                else
                {
                    customAddMarker(locais.get((i + 1)).getCoordenada(), "" + locais.get((i + 1)).getNome(),
                            "" + rotas.get(i).getTempo());
                }

            }


            //Desenha Rotas
            for (int i = 0; i < rotas.size(); i++)
            {
                drawRoute(rotas.get(i).getPontos());
            }

            for (int i = 1; i < locais.size(); i++)
            {
                Log.i(TAG, ""+locais.get(i).getCoordenada().latitude);
                Log.i(TAG, ""+locais.get(i).getCoordenada().longitude);
                addGeofenceInMap(locais.get(i).getCoordenada(), "" + i);
            }

            //Mostrando com um ciruclo a Geofence
        }

    }


    //Metodo que pega os pontos e retorna os pontos para a função drawRoute() desenhe
    public void drawRoute(List<LatLng> rotaCoordenada) {
        PolylineOptions po;

        po = new PolylineOptions();

        for (int i = 0, tam = rotaCoordenada.size(); i < tam; i++) {
            po.add(rotaCoordenada.get(i));
        }
        Random random = new Random();
        int numSorteio = random.nextInt(cores.length);
        po.color(cores[numSorteio]).width(6);
        polyline = mMap.addPolyline(po);

    }

    //adiciona Marcador
    public void customAddMarker(LatLng latLng, String title, String snippet) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(title).snippet(snippet).draggable(true);
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marcador));

        marker = mMap.addMarker(options);
    }


    @Override
    public void onConnected(Bundle bundle)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null)
        {
            myPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            if(locais.size()<1)
            {
                Local local = new Local();
                local.setNome("Minha Posição");
                local.setCoordenada(myPosition);
                locais.add(0, local);
            }
        }

        Log.i(TAG, "Minha Latitude: " + myPosition.latitude + " Minha Longitude" + myPosition.longitude);

        mapFragment.getMapAsync(this);
    }

    //Quando a conexao com a GoogleApi é Suspendida
    @Override
    public void onConnectionSuspended(int i)
    {

    }

    //Quando a conexao com a GoogleApi Cai
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    //Metodo para adicionar geofence no mapa
    public void addGeofenceInMap(LatLng latlng, final String i)
    {
        Log.i(TAG, "Adicionar Geofence");
        final List<Geofence> geofences = new ArrayList<Geofence>();
        PendingIntent pit = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(this, GeofenceReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        mGeofenceInfo = new GeofenceInfo(
                "" +i,
                latlng.latitude, latlng.longitude,
                100, // metros
                Geofence.NEVER_EXPIRE,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        Log.i(TAG, "Configurei a Geofence");
        geofences.add(mGeofenceInfo.getGeofence());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {

            return;
        }
        Log.i(TAG, "Prestes a Salvar Geofence");
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geofences, pit)
                .setResultCallback(new ResultCallback<Status>()
                {
                    @Override
                    public void onResult(Status status)
                    {
                        if (status.isSuccess())
                        {
                            Log.i(TAG, "Salvando..");
                            Log.i(TAG, ""+mGeofenceInfo.mLatitude);
                            mGeofenceDB.salvarGeofence("" + i, mGeofenceInfo);
                            Log.i(TAG, "Salvei");
                        }
                    }
                });
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        Intent intent = new Intent(Mapa.this, ListaLugares.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("Lista de Locais", locais);
        //intent.putParcelableArrayListExtra("Lista de Locais", locais);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}