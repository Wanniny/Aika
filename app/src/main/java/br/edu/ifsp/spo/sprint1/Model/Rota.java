package br.edu.ifsp.spo.sprint1.Model;

/**
 * Created by Wander on 01/05/2016.
 */
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Wander on 22/04/2016.
 */
public class Rota //Pojo da Rota
{
    public List<LatLng> pontos;
    public String tempo;
    public int id;

    public List<LatLng> getPontos()
    {
        return pontos;
    }

    public void setPontos(List<LatLng> pontos)
    {
        this.pontos = pontos;
    }

    public String getTempo()
    {
        return tempo;
    }

    public void setTempo(String tempo)
    {
        this.tempo = tempo;
    }

    public Rota(List<LatLng> pontos, String tempo)
    {
        this.pontos = pontos;
        this.tempo = tempo;
    }
    public Rota()
    {

    }
}
