package br.edu.ifsp.spo.sprint1.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Wander on 21/04/2016.
 */
public class Local implements Parcelable
{
    public int id;
    public String nome;
    public LatLng coordenada;
    public String modoDeTransporte;

    //Pojo da Local

    public Local()
    {

    }

    public Local(Parcel parcel)
    {
        this.nome = parcel.readString();
        this.coordenada = (LatLng) parcel.readValue(LatLng.class.getClassLoader());
        this.modoDeTransporte = parcel.readString();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
    public LatLng getCoordenada() {
        return coordenada;
    }

    public void setCoordenada(LatLng coordenada) {
        this.coordenada = coordenada;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }



    public String getModoDeTransporte()
    {
        return modoDeTransporte;
    }


    public void setModoDeTransporte(String modoDeTransporte)
    {
        this.modoDeTransporte = modoDeTransporte;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(nome);
        dest.writeValue(coordenada);
        dest.writeString(modoDeTransporte);
    }

    public static final Parcelable.Creator<Local> CREATOR = new Parcelable.Creator<Local>()
    {

        @Override
        public Local createFromParcel(Parcel source)
        {
            return new Local(source);
        }

        @Override
        public Local[] newArray(int size)
        {
            return new Local[size];
        }
    };

}
