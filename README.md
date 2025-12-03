\#Database Instructions
-Have a Docker Container running PostgreSQL connection
-Navigate to loadDbScript
-Update String csvFile in beginning in PopulateAnimeTable.java file
to the correct path and update password to the correct password in your IDE (Intellij)
-- If you forgot your password, check it in GitBash with docker inspect postgres | grep POSTGRES\_PASSWORD
---

-In src/main/resources/sql/myanimelist.sql where you have the PostgreSQL connection in VS code
run the first line to create the database. Then right click on query in the public schema and run the remaining commands.
-Run the PopulateAnimeTable.java to see your populated anime table with the CSV data.

---

