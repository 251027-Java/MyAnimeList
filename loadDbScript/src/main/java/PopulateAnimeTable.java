import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;

public class PopulateAnimeTable {

    public static void main(String[] args) {
        String csvFile = "src/main/java/AnimeList.csv"; // path to your CSV file

        String url = "jdbc:postgresql://localhost:5433/postgres";
        String user = "postgres";
        String password = "postgres";

        String insertSQL = "INSERT INTO myanimelist.anime (title, total_episodes, status, avg_rating, image_url) VALUES (?, ?, ?, ?, ?)";

        try (
                CSVReader reader = new CSVReader(new FileReader(csvFile));
                Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            System.out.println("Connected to PostgreSQL.");

            reader.readNext(); // skip header row

            String[] fields;
            while ((fields = reader.readNext()) != null) {
                String title = getField(fields, 1);
                if (title == null || title.isBlank()) {
                    System.out.println("Skipping row with missing title: " + Arrays.toString(fields));
                    continue;
                }

                Integer episodes = parseInteger(getField(fields, 8));
                String status = getField(fields, 9);

                pstmt.setString(1, title);
                if (episodes != null) {
                    pstmt.setInt(2, episodes);
                } else {
                    pstmt.setNull(2, Types.INTEGER);
                }

                if (status == null || status.isBlank()) {
                    pstmt.setNull(3, Types.VARCHAR);
                } else {
                    if (status.equals("Finished Airing")) {
                        pstmt.setString(3, Status.COMPLETED.name());
                    } else if (status.equals("Currently Airing")) {
                        pstmt.setString(3, Status.ONGOING.name());
                    }
                }

                pstmt.setDouble(4, Math.round(((Math.random() * 9) + 1) * 100.0) / 100.0);

                String imageUrl = getField(fields, 5);
                if (imageUrl == null || imageUrl.isBlank()) {
                    pstmt.setNull(5, Types.VARCHAR);
                } else {
                    String archivedUrl = "https://web.archive.org/web/20160826131916/" + imageUrl;
                    pstmt.setString(5, archivedUrl);
                }

                pstmt.executeUpdate();
            }

            System.out.println("CSV data inserted successfully!");

        } catch (IOException | SQLException | CsvValidationException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getField(String[] fields, int index) {
        if (fields == null || index < 0 || index >= fields.length) {
            return null;
        }
        return fields[index] == null ? null : fields[index].trim();
    }

    private static Integer parseInteger(String value) {
        if (value == null || value.isBlank() || value.equalsIgnoreCase("Unknown")) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
