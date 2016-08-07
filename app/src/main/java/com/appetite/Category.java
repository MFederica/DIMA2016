package com.appetite;


import android.graphics.Bitmap;

/**
 * Created by Federica on 06/08/2016.
 */
public class Category {

    private String title;
    private int image;

        public Category() {
        }

        public Category(String title, int image) {
            this.title = title;
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String name) {
            this.title = name;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }
    }