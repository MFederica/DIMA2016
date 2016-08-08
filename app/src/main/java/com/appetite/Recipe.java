package com.appetite;

public class Recipe {

    private String title;
    private int image;

        public Recipe() {
        }

        public Recipe(String title, int image) {
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