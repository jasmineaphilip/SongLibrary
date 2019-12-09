// Created by Radhe Bangad and Jasmine Philip

package view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.SongObj;

import java.util.StringTokenizer;


public class Controller {

	@FXML         
	ListView<SongObj> listView;
	@FXML Button Add;
	@FXML Button Edit;
	@FXML Button Delete;
	@FXML Button addCancel;
	@FXML Button editCancel;
	@FXML Button deleteCancel;
	@FXML TextField songName;
	@FXML TextField songArtist;
	@FXML TextField songAlbum;
	@FXML TextField songYear;
	@FXML TextField addSongName;
	@FXML TextField addSongArtist;
	@FXML TextField addSongAlbum;
	@FXML TextField addSongYear;
	@FXML TextField editSongName;
	@FXML TextField editSongArtist;
	@FXML TextField editSongAlbum;
	@FXML TextField editSongYear;

	private ObservableList<SongObj> visibleSongLibrary;              

	public void start(Stage mainStage) throws IOException {                

		visibleSongLibrary = FXCollections.observableArrayList();
		
		BufferedReader br = new BufferedReader(new FileReader("allsongs.txt"));     
		if (!(br.readLine() == null)) {
			alphabetize("allsongs.txt");
		}
		br.close();
		
		readFromTxt("allsongs.txt");
		listView.setItems(visibleSongLibrary);

		listView.getSelectionModel().selectFirst();
		if(visibleSongLibrary.isEmpty()) {emptySongInfo();}
		displaySongInfo();
	}
	
	public void onClick(ActionEvent event) throws IOException {
		Button b = (Button)event.getSource();
		if (b == Add) {
			addSong();
			listView.setItems(visibleSongLibrary);
		} else if (b == addCancel) {
			//
		} else if (b == Delete) {
			if(visibleSongLibrary.isEmpty()) {emptySongInfo();}
			else{
				deleteSong();
				listView.setItems(visibleSongLibrary);
			}
		} else if (b == deleteCancel) { //GET RID OF?
			
		} else if (b == Edit) {
			editSong();
			listView.setItems(visibleSongLibrary);
		} else { //edit Cancel
			
		}
	}
	
	public void addSong() throws IOException {
		String addName = addSongName.getText();
		String addArtist = addSongArtist.getText();
		String addAlbum = addSongAlbum.getText();
		String addYear = addSongYear.getText();

		if (addName.equals("")) {
			Alert a = new Alert(AlertType.ERROR, "Enter Song Title", ButtonType.CANCEL);
			a.show();	
			return;
		}
		if (addArtist.equals("")) {
			Alert a = new Alert(AlertType.ERROR, "Enter Artist Name", ButtonType.CANCEL);
			a.show();	
			return;
		}
		if (!addYear.equals("") && !isNumber(addYear)) {
			Alert a = new Alert(AlertType.ERROR, "Enter Valid Year", ButtonType.CANCEL);
			a.show();	
			return;
		}
		
		try {
			if (alreadyExists(addName, addArtist)) {
				Alert a = new Alert(AlertType.ERROR, "This song already exists. Please enter new song information.", ButtonType.CANCEL);
				a.show();
				return;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Alert alert = new Alert(AlertType.CONFIRMATION, "Add " + addName + " by " + addArtist +"?", ButtonType.YES, ButtonType.NO);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES) {
			addSongName.setText("");
			addSongArtist.setText("");
			addSongAlbum.setText("");
			addSongYear.setText("");
			SongObj newSong = new SongObj(addName, addArtist, addAlbum, addYear);
			visibleSongLibrary.add(newSong);
			
			listView.getSelectionModel().select(visibleSongLibrary.indexOf(newSong));
			displaySongInfo();
			appendToAllsongs(newSong);
			alphabetize("allsongs.txt");
			visibleSongLibrary.clear();
			SongObj song = new SongObj(newSong.name, newSong.artist, newSong.album, newSong.year);
			readFromTxt("allsongs.txt");
			
			listView.getSelectionModel().select(equalTo(song));
			//displaySongInfo();
			listView.setItems(visibleSongLibrary);
		} else {
			return;
		}
	}
	
	public int equalTo(SongObj song) throws IOException {
		File inputFile = new File("allsongs.txt");
		//File tempFile = new File("myTempFile.txt");

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		//BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		String lineToFind = (song.name + "/" + song.artist + "/" + song.album + "/" + song.year);
		String currentLine;
		int index = 0;
		while((currentLine = reader.readLine()) != null) {
			index++;
		    // trim newline when comparing with lineToRemove
		    String trimmedLine = currentLine.trim();
		    if(trimmedLine.equals(lineToFind)) {
		    	reader.close();
				return index - 1;
		    }
		    //writer.write(currentLine + System.getProperty("line.separator"));
		}
		reader.close(); 
		return index - 1;
		
		
		/*
		String theSongString = song.name + "/" + song.artist + "/" + song.album + "/" + song.year;
		String x = "";
		int index = 0;
		Scanner file = new Scanner(new File("allsongs.txt"));
		while (file.hasNextLine()) {
			index++;
			x = file.next();
			if (x.equals(theSongString)) {
				file.close();
				return index - 1;
			}
		}
		file.close();
		return index-1;
		*/
	}
	
	public void deleteSong() throws IOException {
		
		while (visibleSongLibrary.isEmpty()) {
			Alert a = new Alert(AlertType.ERROR, "Library is empty. Add songs to proceed.", ButtonType.CANCEL);
			a.show();	
			return;
		}
		
		
		SongObj song = listView.getSelectionModel().getSelectedItem();
		Alert alert = new Alert(AlertType.CONFIRMATION, "Delete " + song.name + " by " + song.artist +"?", ButtonType.YES, ButtonType.NO);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES) {
			if(visibleSongLibrary.isEmpty()) {emptySongInfo(); return;}
			int index = visibleSongLibrary.indexOf(song);
			visibleSongLibrary.remove(index);
			listView.getSelectionModel().select(index);
			displaySongInfo();
			try {
				removeFromAllsongs(song);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!visibleSongLibrary.isEmpty()) {
				deleteLineFromEndOfTxt("allsongs.txt");
			}
		} else {
			return;
		}
	}
	
	public void deleteSongEdit(SongObj song) {
		// if(visibleSongLibrary.isEmpty()) {emptySongInfo(); return;}
		int index = visibleSongLibrary.indexOf(song);
		visibleSongLibrary.remove(index);
		listView.getSelectionModel().select(index);
		displaySongInfo();
		try {
			removeFromAllsongs(song);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void editSong() throws IOException {
		
		while (visibleSongLibrary.isEmpty()) {
			Alert a = new Alert(AlertType.ERROR, "Library is empty. Add songs to proceed.", ButtonType.CANCEL);
			a.show();	
			return;
		}
		
		SongObj song = listView.getSelectionModel().getSelectedItem();
		SongObj song1 = new SongObj (editSongName.getText(), editSongArtist.getText(), editSongAlbum.getText(), editSongYear.getText());
			
			
			Alert alert = new Alert(AlertType.CONFIRMATION, "Edit " + song.name + " by " + song.artist +"?", ButtonType.YES, ButtonType.NO);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.YES) {
				if ((song1.name).equals("")) {
					Alert a = new Alert(AlertType.ERROR, "Enter Song Title", ButtonType.CANCEL);
					a.show();	
					return;
				}
				if ((song1.artist).equals("")) {
					Alert a = new Alert(AlertType.ERROR, "Enter Artist Name", ButtonType.CANCEL);
					a.show();	
					return;
				}
				if (!(song1.year).equals("") && !isNumber(song1.year)) {
					Alert a = new Alert(AlertType.ERROR, "Enter Valid Year", ButtonType.CANCEL);
					a.show();	
					return;
				}
				deleteSongEdit(song);
				
				try {
					if (alreadyExists(song1.name, song1.artist)) {
						Alert a = new Alert(AlertType.ERROR, "This song already exists. Please enter new song information.", ButtonType.CANCEL);
						a.show();
						try {
							Files.write(Paths.get("allsongs.txt"), (song.name + "/" + song.artist + "/" + song.album + "/" + song.year).getBytes(), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						alphabetize("allsongs.txt");
						visibleSongLibrary.clear();
						readFromTxt("allsongs.txt");
						listView.setItems(visibleSongLibrary);
						return;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				try {
					Files.write(Paths.get("allsongs.txt"), (song1.name + "/" + song1.artist + "/" + song1.album + "/" + song1.year).getBytes(), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
				} catch (IOException e) {
					e.printStackTrace();
				}
				displaySongInfoEdit(song1);
				alphabetize("allsongs.txt");
				visibleSongLibrary.clear();
				SongObj songSelect = new SongObj(song1.name, song1.artist, song1.album, song1.year);
				readFromTxt("allsongs.txt");
				//visibleSongLibrary.set(visibleSongLibrary.indexOf(song), song);
				listView.getSelectionModel().select(equalTo(songSelect));
				//listView.getSelectionModel().select(visibleSongLibrary.indexOf(song1));
				listView.setItems(visibleSongLibrary);
			}
			
		
		
	}
	public void cancelActionAdd() { 
		addSongName.setText("");
		addSongArtist.setText("");
		addSongAlbum.setText("");
		addSongYear.setText("");
	}
	public void cancelActionEdit() { 
		SongObj song = listView.getSelectionModel().getSelectedItem();
		editSongName.setText(song.name);
		editSongArtist.setText(song.artist);
		editSongAlbum.setText(song.album);
		editSongYear.setText(song.year);		
	}
	public void emptySongInfo() {
		songName.setText("");
		songArtist.setText("");
		songAlbum.setText("");
		songYear.setText("");
		editSongName.setText("");
		editSongArtist.setText("");
		editSongAlbum.setText("");
		editSongYear.setText("");
	}
	public void displaySongInfoEdit(SongObj song) {
		songName.setText(song.getName());
		songArtist.setText(song.getArtist());
		songAlbum.setText(song.getAlbum());
		songYear.setText(song.getYear());
		editSongName.setText(song.getName());
		editSongArtist.setText(song.getArtist());
		editSongAlbum.setText(song.getAlbum());
		editSongYear.setText(song.getYear());
	}
	public void displaySongInfo() {
		if(visibleSongLibrary.isEmpty()) {emptySongInfo(); return;}
		SongObj song = listView.getSelectionModel().getSelectedItem();
		songName.setText(song.getName());
		songArtist.setText(song.getArtist());
		songAlbum.setText(song.getAlbum());
		songYear.setText(song.getYear());
		editSongName.setText(song.getName());
		editSongArtist.setText(song.getArtist());
		editSongAlbum.setText(song.getAlbum());
		editSongYear.setText(song.getYear());
	}
	
	public void readFromTxt(String filePathName) throws FileNotFoundException{
		String name = null;
		String artist = null;
		String album = null;
		String year = null;
		Scanner file = new Scanner(new File(filePathName));
		while (file.hasNextLine()) {
			String songToToken = file.nextLine();
			StringTokenizer tokenizer = new StringTokenizer(songToToken, "/");
			while (tokenizer.hasMoreTokens()) {
				name = tokenizer.nextToken();
				artist = tokenizer.nextToken();
				try {
					album = tokenizer.nextToken();
				} catch (NoSuchElementException e){
					album = "";
				}
				try {
					year = tokenizer.nextToken();
				} catch (NoSuchElementException e){
					year = "";
				}
			}
			visibleSongLibrary.add(new SongObj(name, artist, album, year));
		}
		file.close();
	}
	
	public void deleteLineFromEndOfTxt(String path) throws IOException {
		Scanner file = new Scanner(new File(path));
		int numberOfLines = 0;
		while (file.hasNextLine()) {
			numberOfLines++;
			file.nextLine();
		}
		file.close();
		File inputFile = new File(path);
		File tempFile = new File("myTempFile.txt");

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		

		for(int i = 1; i < numberOfLines; i++) {
			String currentLine = reader.readLine();
		    writer.write(currentLine + System.getProperty("line.separator"));
		}
		String currentLine = reader.readLine();
		writer.write(currentLine);
		writer.close(); 
		reader.close(); 
		tempFile.renameTo(inputFile);
		listView.setItems(visibleSongLibrary);
		
	}
	
	
	public void appendToAllsongs(SongObj song) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("allsongs.txt"));     
		if (br.readLine() == null) {
			try {
				Files.write(Paths.get("allsongs.txt"), (song.name + "/" + song.artist + "/" + song.album + "/" + song.year + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Files.write(Paths.get("allsongs.txt"), (System.lineSeparator() + song.name + "/" + song.artist + "/" + song.album + "/" + song.year).getBytes(), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		br.close();
	}
	
	public void removeFromAllsongs(SongObj song) throws IOException {
		File inputFile = new File("allsongs.txt");
		File tempFile = new File("myTempFile.txt");
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		String lineToRemove = (song.name + "/" + song.artist + "/" + song.album + "/" + song.year);
		String currentLine;

		while((currentLine = reader.readLine()) != null) {
		    // trim newline when comparing with lineToRemove
		    String trimmedLine = currentLine.trim();
		    if(trimmedLine.equals(lineToRemove)) continue;
		    writer.write(currentLine + System.getProperty("line.separator"));
		}
		writer.close(); 
		reader.close(); 
		tempFile.renameTo(inputFile);
	}
	
	public void removeFromTxt(String lineToRemove) throws IOException {
		File inputFile = new File("allsongs.txt");
		File tempFile = new File("myTempFile.txt");

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		String currentLine;

		while((currentLine = reader.readLine()) != null) {
		    // trim newline when comparing with lineToRemove
		    String trimmedLine = currentLine.trim();
		    if(trimmedLine.equals(lineToRemove)) continue;
		    writer.write(currentLine + System.getProperty("line.separator"));
		}
		writer.close(); 
		reader.close(); 
		tempFile.renameTo(inputFile);
		listView.setItems(visibleSongLibrary);
	}
	
	
	public void alphabetize(String filePathName) throws IOException {
		ArrayList<String> inOrder = new ArrayList<>();
		Scanner file = new Scanner(new File(filePathName));
		while (file.hasNextLine()) {
		inOrder.add(file.nextLine());
		}
		file.close();

		
		File inputFile = new File("allsongs.txt");
		File tempFile = new File("myTempFile.txt");
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		writer.close(); 
		reader.close(); 
		tempFile.renameTo(inputFile);
		
//		for (int i = 0; i < inOrder.size(); i++) {
//		removeFromTxt(inOrder.get(i));
//		}

		Collections.sort(inOrder, String.CASE_INSENSITIVE_ORDER);
		int size = inOrder.size();

		try {
			Files.write(Paths.get("allsongs.txt"), inOrder.get(0).getBytes(), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i = 1; i < size; i++) {
			try {
				Files.write(Paths.get("allsongs.txt"), (System.lineSeparator() + inOrder.get(i)).getBytes(), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		listView.setItems(visibleSongLibrary);
	}	
	
	public static boolean isNumber(String addYear) {
		try {
			Integer.parseInt(addYear);
		} catch (NumberFormatException e){
			return false;
		}
		if (Integer.parseInt(addYear) >= 0) {
			return true;
		}
			return false;
	}
	
	public static boolean alreadyExists(String addName, String addArtist) throws FileNotFoundException {
		String name = null;
		String artist = null;
		//String album = null;
		//String year = null;
		Scanner file = new Scanner(new File("allsongs.txt"));
		while (file.hasNextLine()) {
			String songToToken = file.nextLine();
			StringTokenizer tokenizer = new StringTokenizer(songToToken, "/");
			name = tokenizer.nextToken();
			artist = tokenizer.nextToken();
			if (name.equalsIgnoreCase(addName) && artist.equalsIgnoreCase(addArtist)) {
				file.close();
				return true;
			}
			//visibleSongLibrary.add(new SongObj(name, artist, album, year));
		}
		file.close();
		return false;
	}
	
}




