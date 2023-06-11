import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ImdbSearchBela {

    private static List<String> getGenres(String mid, Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT genre " +
                "FROM genre " +
                "WHERE movie_id = ?");
        stmt.setString(1, mid);
        ResultSet rs = stmt.executeQuery();
        List<String> genres = new ArrayList<>();
        while (rs.next()) {
            genres.add( rs.getString("genre"));
        }
        return genres;
    }


    private static List<String> getActors(String mid, Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT name " +
                "FROM (SELECT name, movie_id FROM actor\n" +
                        "UNION SELECT name, movie_id FROM actress ) AS act1 " +
                "WHERE movie_id = ? " +
                "ORDER BY name ASC");
        stmt.setString(1, mid);
        ResultSet rs = stmt.executeQuery();
        List<String> actors = new ArrayList<>();
        while (rs.next()) {
            actors.add( rs.getString("name"));
        }
        actors.sort(String::compareTo);
        return actors;
    }


    public static void main(String[] args) throws SQLException {


        //get parameters from args
        //default values
        String databasename = "imdb";
        String databaseIP = "localhost";
        String databasePort = "5432";
        String user = "postgres";
        String password = "postgres";
        String keyword = "Blacks";

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-d":
                    databasename = args[i + 1];
                    break;
                case "-s":
                    databaseIP = args[i + 1];
                    break;
                case "-p":
                    databasePort = args[i + 1];
                    break;
                case "-u":
                    user = args[i + 1];
                    break;
                case "-pw":
                    password = args[i + 1];
                    break;
                case "-k":
                    keyword = args[i + 1];
                    break;
            }
        }

        //get connection
        String url = "jdbc:postgresql://" + databaseIP + ":" + databasePort + "/" + databasename;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Connection failure.");
            throw new RuntimeException(e);
        }
        if (conn != null) {
            System.out.println("Connection successful!");
        } else {
            System.out.println("Failed to make connection!");
            return;
        }

        //a) get movies with keyword
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * " +
                "FROM movie " +
                "WHERE title LIKE ? " +
                "ORDER BY title ASC");
        stmt.setString(1, "%" + keyword + "%");
        ResultSet rs = stmt.executeQuery();
        System.out.println("MOVIES");

        //b) display movies
        //get actors for each movie
        while (rs.next()) {
            List<String> genres = getGenres(rs.getString("mid"), conn);
            String genreList = "";
            for (int i = 0; i < genres.size(); i++) {
                genreList += genres.get(i);
                if (i < genres.size() - 1) {
                    genreList += ", ";
                }
            }
            System.out.println(rs.getString("title") +", " + rs.getString("year") + ", " + genreList);
            List<String> actors = getActors(rs.getString("mid"), conn);
            for (int i = 0; i < min(actors.size(),5); i++) {
                System.out.println("\t" + actors.get(i));
            }
        }
        System.out.println();

        //c) get actors with keyword
        stmt = conn.prepareStatement(
                "SELECT * " +
                "FROM (SELECT name, movie_id FROM actor " +
                    "UNION SELECT name, movie_id FROM actress ) AS act " +
                "WHERE act.name LIKE ? " +
                "ORDER BY act.name ASC");
        stmt.setString(1, "%" + keyword + "%");
        rs = stmt.executeQuery();
        System.out.println("ACTORS");

        //d) display actors
        //get movies + costars for each actor
        while (rs.next()) {
            System.out.println(rs.getString("name"));
            //get movies
            PreparedStatement stmt2 = conn.prepareStatement(
                    "SELECT movie.title, movie.year " +
                    "FROM movie " +
                    "WHERE mid = ? " +
                    "ORDER BY title ASC");
            stmt2.setString(1, rs.getString("movie_id"));
            ResultSet rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                System.out.println("\tPLAYED IN");
                System.out.println("\t\t" + rs2.getString("title") );
                //get costars
                System.out.println("\tCO-STARS");
                PreparedStatement stmt3 = conn.prepareStatement(
                        "SELECT actor2.name, COUNT(actor2.name)\n" +
                                "FROM\n" +
                                "    (SELECT name, movie_id\n" +
                                "    FROM (SELECT name, movie_id FROM actor\n" +
                                "    UNION SELECT name, movie_id FROM actress ) AS act1\n" +
                                "    WHERE name = ?) AS actor1\n" +
                                "JOIN\n" +
                                "    (SELECT name, movie_id\n" +
                                "    FROM (SELECT name, movie_id FROM actor\n" +
                                "    UNION SELECT name, movie_id FROM actress ) AS act2) as actor2\n" +
                                "ON actor1.movie_id = actor2.movie_id\n" +
                                "WHERE actor2.name != ?\n" +
                                "GROUP BY actor2.name\n" +
                                "ORDER BY COUNT(actor2.name) DESC, actor2.name ASC\n " +
                                "LIMIT 5;");
                stmt3.setString(1, rs.getString("name"));
                stmt3.setString(2, rs.getString("name"));
                ResultSet rs3 = stmt3.executeQuery();
                while (rs3.next()) {
                    System.out.println("\t\t" + rs3.getString("name")+" (" + rs3.getInt("count")+")");
                }
            }
        }
    }
}
