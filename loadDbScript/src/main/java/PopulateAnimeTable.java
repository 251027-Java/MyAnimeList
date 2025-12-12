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

public class PopulateAnimeTable {

    public static void main(String[] args) {
        String csvFile = "src/main/java/AnimeList.csv"; // path to your CSV file

        String url = "jdbc:postgresql://localhost:5433/postgres";
        String user = "postgres";
        String password = "xaR501%=";

        String insertSQL = "INSERT INTO myanimelist.anime (title, total_episodes, status) VALUES (?, ?, ?)";

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
                    if(status.equals("Finished Airing")){
                        pstmt.setString(3, Status.COMPLETED.name());
                    }
                    else if(status.equals("Currently Airing")){
                        pstmt.setString(3, Status.ONGOING.name());
                    }
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