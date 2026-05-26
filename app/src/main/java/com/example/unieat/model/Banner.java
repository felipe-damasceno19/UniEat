package com.example.unieat.model;

import java.util.ArrayList;
import java.util.List;

public class Banner {
    private String id, tag, title, subtitle, imageUrl;
    private List<String> dishIds;

    public Banner() { dishIds = new ArrayList<>(); }

    public Banner(String id, String tag, String title, String subtitle,
                  String imageUrl, List<String> dishIds) {
        this.id = id;
        this.tag = tag;
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.dishIds = dishIds != null ? dishIds : new ArrayList<>();
    }

    public String getId()       { return id; }
    public String getTag()      { return tag; }
    public String getTitle()    { return title; }
    public String getSubtitle() { return subtitle; }
    public String getImageUrl() { return imageUrl; }
    public List<String> getDishIds() { return dishIds; }

    public void setId(String id)             { this.id = id; }
    public void setTag(String tag)           { this.tag = tag; }
    public void setTitle(String title)       { this.title = title; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setDishIds(List<String> ids) { this.dishIds = ids; }
}
