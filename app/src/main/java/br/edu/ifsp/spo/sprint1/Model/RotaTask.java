package br.edu.ifsp.spo.sprint1.Model;

import android.app.ProgressDialog;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class RotaTask extends AsyncTask<List<Local>, String, List<Rota>>
{
    private static final String TAG = "Script";
    LatLng origin;
    LatLng destination;
    ProgressDialog progressDialog;

    @Override
    protected void onPreExecute()
    {
        /*progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Carregando...");
        progressDialog.show();*/

    }
    protected List<Rota> doInBackground(List<Local>... params)
    {
        List<Rota> rotas = new ArrayList<>();
        List<Local> locais = params[0];
        for(int i=0; i<((locais.size())-1); i++)
        {
            origin = locais.get(i).getCoordenada();
            destination = locais.get((i+1)).getCoordenada();

            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude + "&" +
                    "destination=" + destination.latitude + "," + destination.longitude + "&sensor=false&mode=" + locais.get(i+1).getModoDeTransporte();

            Log.i(TAG, url);


            HttpResponse response;
            HttpGet request;
            AndroidHttpClient client = AndroidHttpClient.newInstance("route");

            request = new HttpGet(url);
            try {
                response = client.execute(request);
                final String answer = EntityUtils.toString(response.getEntity());
                Log.i(TAG, "Já Estou com a URL");
                try {
                    Rota rota = new Rota();
                    rota.setPontos(buildJSONRoute(answer));
                    rota.setTempo((tempo(answer)));
                    rotas.add(rota);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                client.close();

            }
        }
        return(rotas);
    }




    @Override
    protected void onProgressUpdate(String... params)
    {

    }





    public String tempo(String json) throws JSONException
    {
        JSONObject result = new JSONObject(json);
        JSONArray routes = result.getJSONArray("routes");

        String time = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");

        return time;
    }
    private List<LatLng> decodePolyline(String encoded) {

        List<LatLng> listPoints = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            // Log.i("Script", "POL: LAT: " + p.latitude + " | LNG: " + p.longitude);
            listPoints.add(p);
        }
        return listPoints;
    }

    public List<LatLng> buildJSONRoute(String json) throws JSONException
    {
        JSONObject result = new JSONObject(json);
        JSONArray routes = result.getJSONArray("routes");

        JSONArray steps = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        List<LatLng> lines = new ArrayList<LatLng>();

        for (int i = 0; i < steps.length(); i++)
        {
            String polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points");
            for (LatLng p : decodePolyline(polyline))
            {
                lines.add(p);
            }

        }

        return (lines);
    }
}

