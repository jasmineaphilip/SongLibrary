// Created by Radhe Bangad and Jasmine Philip

package model;

public class SongObj {
	public String name;
	public String artist;
	public String album;
	public String year;
	
	public SongObj(String name, String artist, String album, String year) {
		this.name = name;
		this.artist = artist;
		this.album = album;
		this.year = year;
	}
	
	public String toString() {
		return name + " - " + artist;
	}
	
	public String getName() {
		return name;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getAlbum() {
		return album;
	}
	
	public String getYear() {
		return year;
	}
}
