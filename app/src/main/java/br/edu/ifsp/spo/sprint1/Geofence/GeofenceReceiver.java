package br.edu.ifsp.spo.sprint1.Geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;

public class GeofenceReceiver extends BroadcastReceiver
{
    private static final String TAG = "Script";
    private GeofenceDB mGeofenceDB;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        mGeofenceDB = new GeofenceDB(context);
        if (geofencingEvent.hasError())
        {
            int errorCode = geofencingEvent.getErrorCode();
            Toast.makeText(context, "Erro no serviço de localização: " + errorCode,
                    Toast.LENGTH_LONG).show();

            Log.i(TAG, "Erro na localização");
        }
        else
        {
            int transicao = geofencingEvent.getGeofenceTransition();
            if (transicao == Geofence.GEOFENCE_TRANSITION_ENTER
                    || transicao == Geofence.GEOFENCE_TRANSITION_EXIT)
            {
                List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();

                String acao = transicao == 1 ? "Entrou" : "Saiu";
                Toast.makeText(context,
                        "Geofence ID: "+ geofences.get(0).getRequestId() +"  "+ acao +" do perímetro",
                        Toast.LENGTH_LONG).show();

                Vibrator rr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                long milliseconds = 500;
                rr.vibrate(milliseconds);
                Log.i(TAG, acao+"do Perimetro");
            } else
            {
                Toast.makeText(context,
                        "Erro no Geofence: " + transicao, Toast.LENGTH_LONG).show();

                Log.i(TAG, "Erro da Transição");
            }
        }
    }
}

