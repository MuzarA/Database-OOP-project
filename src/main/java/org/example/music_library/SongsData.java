package org.example.music_library;

import java.sql.*;


import java.util.ArrayList;
import java.util.List;

public class SongsData {
    private static final String URL = "jdbc:postgresql://localhost:5432/music_final";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public int addSong(String title , String artist , String album , String genre ,int duration ) {
         int newid = 0;

        String query = "INSERT INTO songs (title, artist, album, genre, duration) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();  // Use the getConnection method
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {

            stmt.setString(1, title);
            stmt.setString(2, artist);
            stmt.setString(3, album);
            stmt.setString(4, genre);
            stmt.setInt(5, duration);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating task failed.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    newid = generatedKeys.getInt(1);
                    System.out.println(newid);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return newid;
    }

    // Get a list of all songs
    public List<Songs> getAllSongs() {
        List<Songs> songs = new ArrayList<>();
        String query = "SELECT * FROM songs";

        try (Connection conn = getConnection();  // Use the getConnection method
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Songs song = new Songs(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getString("genre"),
                        rs.getInt("duration")
                );
                songs.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    // Update an existing song
    public void updateSong(int id, String title, String artist, String album, String genre, int duration) {
        String query = "UPDATE songs SET title = ?, artist = ?, album = ?, genre = ?, duration = ? WHERE id = ?";

        try (Connection conn = getConnection();  // Using your getConnection method
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set parameters in the prepared statement
            stmt.setString(1, title);
            stmt.setString(2, artist);
            stmt.setString(3, album);
            stmt.setString(4, genre);
            stmt.setInt(5, duration);
            stmt.setInt(6, id); // Use the correct ID for the WHERE clause

            // Execute the update query
            int rowsUpdated = stmt.executeUpdate();

            // Check if the update was successful
            if (rowsUpdated == 0) {
                System.out.println("No rows were updated. Make sure the id exists.");
            } else {
                System.out.println("Song updated successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSong(String title) {
        String query = "DELETE FROM songs WHERE title = ?";

        try (Connection conn = getConnection();  // Use the getConnection method
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
