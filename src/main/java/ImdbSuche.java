import java.sql.*;

public class ImdbSuche {
    static final String QUERYMovie = "SELECT movie.title, movie.year, genre.genre \n" +
            "FROM movie\n" +
            "JOIN genre ON movie.mid = genre.movie_id\n" +
            "WHERE movie.title LIKE '%Blacks%'\n" +
            "ORDER BY title asc";
    static final String QUERYActors = "SELECT * FROM actor LIMIT 3";
    static final String keyword = "";
    public static void main(String[] args) {
        // Open a connection
        try (Connection conn = DriverManager.getConnection(args[0], args[1], args[2]);
             Statement stmt = conn.createStatement();
             ResultSet rsMovies = stmt.executeQuery(QUERYMovie)) {
            System.out.println("MOVIES");
            // Extract data from result set
            while (rsMovies.next()) {
                // Retrieve by column name
                System.out.print(rsMovies.getString("title") + ", ");
                System.out.print(rsMovies.getInt("year") + "\n");

                System.out.println(rsMovies.getString("genre") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //second query
        try (Connection conn = DriverManager.getConnection(args[0], args[1], args[2]);
             Statement stmt = conn.createStatement();
             ResultSet rsActors = stmt.executeQuery(QUERYActors)) {
            System.out.println("ACTORS");
            while (rsActors.next()) {
                System.out.print(rsActors.getString("first_name") + " ");
                System.out.print(rsActors.getString("last_name") + "\n");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
