package org.example.music_library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    private static final String URL = "jdbc:postgresql://localhost:5432/music_final";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123";
    private SongsData songsData = new SongsData();
    ;
    @FXML
    public ListView songListView;
    @FXML
    private TextField trackTitleField, trackArtistField, trackAlbumField, trackGenreField, trackDurationField;


    ObservableList<Track> tracks = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        songsData = new SongsData();
        songListView.setItems(tracks);
        handleGetAllSongs(null);
    }

    @FXML
    protected void handleAddSong(ActionEvent event) throws SQLException {
        String title = trackTitleField.getText();
        String artist = trackArtistField.getText();
        String album = trackAlbumField.getText();
        String genre = trackGenreField.getText();
        String duration = trackDurationField.getText();

        if (title.isEmpty() || artist.isEmpty() || genre.isEmpty() || duration.isEmpty()) {
            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        int duuration = Integer.parseInt(trackDurationField.getText());

        Track tr = new Track(title, artist, album, genre, duuration);
        songsData = new SongsData();
        int id = songsData.addSong(title, artist, album, genre, duuration);
        tr.setTrackID(id);
        tracks.add(tr);

        clearFields(trackTitleField, trackArtistField, trackAlbumField, trackGenreField, trackDurationField);
        showAlert("Success", "Track added successfully!");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @FXML
    protected void handleGetAllSongs(ActionEvent event) {
        List<Songs> songs = songsData.getAllSongs();

        tracks.clear();

        for (Songs song : songs) {
            Track track = new Track(
                    song.getTitle(),
                    song.getArtist(),
                    song.getAlbum(),
                    song.getGenre(),
                    song.getDuration()
            );
            track.setTrackID(song.getId());
            tracks.add(track);
        }

        songListView.setItems(tracks);

    }

    public void updateSong(int id, String title, String artist, String album, String genre, int duration) {
        String query = "UPDATE songs SET title = ?, artist = ?, album = ?, genre = ?, duration = ? WHERE id = ?";

        try (Connection conn = getConnection();  // Use the getConnection method
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            stmt.setString(2, artist);
            stmt.setString(3, album);
            stmt.setString(4, genre);
            stmt.setInt(5, duration);
            stmt.setInt(6, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleUpdateSong(ActionEvent event) {
        int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            showAlert("Error", "Please select a track to update.");
            return;
        }

        Track selectedTrack = tracks.get(selectedIndex);

        String title = trackTitleField.getText();
        String artist = trackArtistField.getText();
        String album = trackAlbumField.getText();
        String genre = trackGenreField.getText();
        String durationStr = trackDurationField.getText();

        if (title.isEmpty() || artist.isEmpty() || genre.isEmpty() || durationStr.isEmpty()) {
            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        int duration = Integer.parseInt(durationStr);  // Convert duration to int

        int trackId = selectedTrack.getTrackID();  // Assuming the Track class has a getTrackID() method

        songsData.updateSong(trackId, title, artist, album, genre, duration);

        selectedTrack.setTitle(title);
        selectedTrack.setArtist(artist);
        selectedTrack.setAlbum(album);
        selectedTrack.setGenre(genre);
        selectedTrack.setDuration(duration);

        tracks.set(selectedIndex, selectedTrack);

        clearFields(trackTitleField, trackArtistField, trackAlbumField, trackGenreField, trackDurationField);

        showAlert("Success", "Track updated successfully!");
    }


    // Delete Track
    @FXML
    protected void handleDeleteSong(ActionEvent event) {
        int selectedIndex = songListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex == -1) {
            showAlert("Error", "Please select a track to delete.");
            return;
        }
        if (selectedIndex < 0 || selectedIndex >= tracks.size()) {
            showAlert("Error", "Invalid track selected.");
            return;
        }

        Track selectedTrack = tracks.get(selectedIndex);
        String title = selectedTrack.getTitle();
        tracks.remove(selectedIndex);

        songsData.deleteSong(title);

        showAlert("Success", "Track deleted successfully!");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    @FXML
    private String selectTracksview() {
        int id = songListView.getSelectionModel().getSelectedIndex();
        if (id == -1) {
            showAlert("Error", "Please select a track first.");
            return null;
        }

        String title = tracks.get(id).getTitle();
        return title;
    }


}

