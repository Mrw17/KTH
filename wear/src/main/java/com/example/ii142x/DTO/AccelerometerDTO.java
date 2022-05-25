package com.example.ii142x.DTO;

import java.io.*;

/**
 * Activity that are responsible for holding Accelerometer-data
 * it will be used for sending data between mobile and smartwatch
 */
public class AccelerometerDTO implements Serializable {
    float x;
    float y;
    float z;

    public AccelerometerDTO(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z ;
    }

    public AccelerometerDTO() {}

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        oos.flush();
        return bos.toByteArray();
    }

    public static AccelerometerDTO deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);

        return (AccelerometerDTO) is.readObject();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }



}
