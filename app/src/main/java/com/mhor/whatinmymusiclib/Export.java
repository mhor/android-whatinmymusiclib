package com.mhor.whatinmymusiclib;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mhor on 12/08/17.
 */

public class Export {
    private String sourceName;
    private Date createdAt;
    private ArrayList<Track> tracks;

    public Export() {
        this.createdAt = new Date();
        this.tracks = new ArrayList<Track>();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
