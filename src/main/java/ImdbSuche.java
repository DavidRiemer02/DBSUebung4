import java.sql.*;

public class ImdbSuche {
    public String keyWord = "Blacks";
    private String getKeyWord(){
        return keyWord;
    }

    static final String QUERYMovie1 = "SELECT movie.title, movie.year, string_agg(genre, ', ') AS genres \n" +
            "FROM movie\n" +
            "JOIN genre ON movie.mid = genre.movie_id\n" +
            "WHERE movie.title LIKE '%Blacks%'\n" +
            "GROUP BY movie.title, movie.year \n" +
            "ORDER BY title asc";
    static final String QUERYMovie2 = "SELECT DISTINCT actor.name  \n" +
            "FROM actor\n" +
            "WHERE actor.movie_id LIKE '%Blacks%'\n" +
            "UNION \n" +
            "SELECT DISTINCT actress.name\n" +
            "FROM actress\n" +
            "WHERE actress.movie_id LIKE '%Blacks%'\n" +
            "LIMIT 5";
    static final String QUERYActors1 = "SELECT DISTINCT actor.name  \n" +
            "FROM actor\n" +
            "WHERE actor.name LIKE '%Blacks%'\n" +
            "UNION \n" +
            "SELECT DISTINCT actress.name\n" +
            "FROM actress\n" +
            "WHERE actress.name LIKE '%Blacks%'\n";


    static final String QUERYActors2 = "SELECT DISTINCT movie.title\n" +
            "FROM actor\n" +
            "JOIN movie ON actor.movie_id = movie.mid\n" +
            "WHERE actor.name LIKE '%Blacks%'\n" +
            "UNION\n" +
            "SELECT DISTINCT movie.title\n" +
            "FROM movie\n" +
            "JOIN actress ON actress.movie_id = movie.mid\n" +
            "WHERE actress.name LIKE '%Blacks%'";




    static final String QUERYActors3 = "SELECT actor.name \n" +
            "FROM actor \n" +
            "INNER JOIN (SELECT DISTINCT movie.title\n" +
            "FROM actor\n" +
            "JOIN movie ON actor.movie_id = movie.mid\n" +
            "WHERE actor.name LIKE '%Blacks%'\n" +
            "UNION\n" +
            "SELECT DISTINCT movie.title\n" +
            "FROM movie\n" +
            "JOIN actress ON actress.movie_id = movie.mid\n" +
            "WHERE actress.name LIKE '%Blacks%') AS movieTitle \n" +
            "ON actor.movie_id LIKE '%' || movieTitle.title || '%'";

    public static void main(String[] args) {
        // Open a connection
        try (Connection conn = DriverManager.getConnection(args[0], args[1], args[2]);
             Statement stmt = conn.createStatement();
             ResultSet rsMovies = stmt.executeQuery(QUERYMovie1)) {
            System.out.println("MOVIES");
            // Extract data from result set
            while (rsMovies.next()) {
                // Retrieve by column name
                System.out.print(rsMovies.getString("title") + ", ");
                System.out.print(rsMovies.getInt("year") + ", ");
                System.out.print(rsMovies.getString("genres")+ "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //MovieActorQuery
        try (Connection conn = DriverManager.getConnection(args[0], args[1], args[2]);
             Statement stmt = conn.createStatement();
             ResultSet rsMovies2 = stmt.executeQuery(QUERYMovie2)) {
            System.out.println("MOVIES");
            while (rsMovies2.next()) {
                System.out.println("\t"+rsMovies2.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Second query
        try (Connection conn = DriverManager.getConnection(args[0], args[1], args[2]);
             Statement stmt = conn.createStatement();
             ResultSet rsActors = stmt.executeQuery(QUERYActors1)) {
            System.out.println("ACTORS");
            while (rsActors.next()) {
                System.out.println(rsActors.getString("name") + " ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection conn = DriverManager.getConnection(args[0], args[1], args[2]);
             Statement stmt = conn.createStatement();
             ResultSet rsActors2 = stmt.executeQuery(QUERYActors2)) {
            System.out.println("\t"+ "PLAYED IN");
            while (rsActors2.next()) {
                System.out.println("\t"+"\t"+rsActors2.getString("title") + " ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection conn = DriverManager.getConnection(args[0], args[1], args[2]);
             Statement stmt = conn.createStatement();
             ResultSet rsActors3 = stmt.executeQuery(QUERYActors3)) {
            System.out.println("\t"+ "CO-STARS");
            while (rsActors3.next()) {
                System.out.print("\t"+"\t"+rsActors3.getString("name") + "\n ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
