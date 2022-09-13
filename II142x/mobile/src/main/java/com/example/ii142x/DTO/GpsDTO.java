package com.example.ii142x.DTO;

import java.io.*;

/**
 * Activity that are responsible for holding GPS-data
 * it will be used for sending data between mobile and smartwatch
 */
public class GpsDTO implements Serializable {
    double latitude = 0;
    double longitude = 0;

    public GpsDTO(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GpsDTO() {
        
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        oos.flush();
        return bos.toByteArray();
    }

    public static GpsDTO deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);

        return (GpsDTO) is.readObject();
    }
}
