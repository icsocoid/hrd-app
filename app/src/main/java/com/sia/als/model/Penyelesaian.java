package com.sia.als.model;

import android.graphics.Bitmap;

public class Penyelesaian {
    private String photoPerdin;
    private String notePerdin;
    private Bitmap bitmap;



    public String getNotePerdin() {
        return notePerdin;
    }

    public void setNotePerdin(String notePerdin) {
        this.notePerdin = notePerdin;
    }

    public String getPhotoPerdin() {
        return photoPerdin;
    }

    public void setPhotoPerdin(String photoPerdin) {
        this.photoPerdin = photoPerdin;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
