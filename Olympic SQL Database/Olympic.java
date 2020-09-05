import java.util.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Olympic
{
  private static Connection dbcon;
  private static String username = "qhv1";
  private static String password = "4183882";
  private static String currentUser;

  private static int userid;

  public static void main(String[] args) throws SQLException
  {
    DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
    String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";
    dbcon = DriverManager.getConnection(url, username, password);
    dbcon.setAutoCommit(false);
    while(true)
    {


      System.out.println("Welcome to the Olympic database");
      System.out.println("Please enter one of the following:");
      System.out.println("[1] Login");
      System.out.println("[2] Exit");

      Scanner input = new Scanner(System.in);
      int option = Integer.parseInt(input.nextLine());
      int user = 0;
      boolean innerMenu = false;

      if(option == 1)
      {
        do
        {
          if(user == -1)
          {
            System.out.println("ERROR: no user found by those login credentials, please retry");
            innerMenu = false;
          }
          System.out.println("Please enter login information (type 'exit' to quit): NAME = ");
          String name = input.nextLine();
          currentUser = name;
          if(name.equalsIgnoreCase("exit"))
            break;
          System.out.println("Please enter login information: PASSWORD = ");
          String passwordLogin = input.nextLine();

          user = login(name, passwordLogin);
          innerMenu = true;
        } while(user == -1);
      }
      else if(option == 2)
      {
        break;
      }
      while(innerMenu)
      {
        System.out.println("Please enter one of the following: ");
        System.out.println("[1] Display Sport");
        System.out.println("[2] Display Event");
        System.out.println("[3] Country Ranking");
        System.out.println("[4] Top k Athletes");
        System.out.println("[5] Connected Athletes");
        if(user == 1)
        {
          System.out.println("[6] Create User");
          System.out.println("[7] Drop User");
          System.out.println("[8] Create Event");
          System.out.println("[9] Add Event Outcome");
        }
        else if(user == 2)
        {
          System.out.println("[6] Create Team");
          System.out.println("[7] Register Team");
          System.out.println("[8] Add Participant");
          System.out.println("[9] Add Team Member");
          System.out.println("[10] Drop Team Member");
        }
        System.out.println("\n[0] Logout");
        option = Integer.parseInt(input.nextLine());
        if(option == 0)
        {
          logout();
          innerMenu = false;
          break;
        }
        else if(option == 1)
        {
          System.out.println("Enter sport name: ");
          String sportName = input.nextLine();

          displaySport(sportName);
        }
        else if(option == 2)
        {
          System.out.println("Enter Olympic City: ");
          String city = input.nextLine();

          System.out.println("Enter Olympic Year: ");
          String year = input.nextLine();

          System.out.println("Enter Event ID: ");
          int id = Integer.parseInt(input.nextLine());

          displayEvent(id, year, city);
        }
        else if(option == 3)
        {
          System.out.println("Enter Olympic ID: ");
          int id = Integer.parseInt(input.nextLine());

          countryRanking(id);
        }
        else if(option == 4)
        {
          System.out.println("Enter Olympic ID: ");
          int olympicID = Integer.parseInt(input.nextLine());

          System.out.println("Enter K Top Athletes: ");
          int k = Integer.parseInt(input.nextLine());

          topKAthletes(olympicID, k);
        }
        else if(option == 5)
        {
          System.out.println("Enter Olympic ID: ");
          int olympicID = Integer.parseInt(input.nextLine());

          System.out.println("Enter Participant ID: ");
          int participantID = Integer.parseInt(input.nextLine());

          System.out.println("Enter N hops: ");
          int n = Integer.parseInt(input.nextLine());

          connectedAthletes(olympicID, participantID, n);
        }
        else if(option == 6 && user == 1)
        {
          System.out.println("Enter Username: ");
          String u = input.nextLine();

          System.out.println("Enter Password: ");
          String passkey = input.nextLine();

          System.out.println("Enter RoleID: ");
          int roleID = Integer.parseInt(input.nextLine());

          createUser(u, passkey, roleID);
        }
        else if(option == 7 && user == 1)
        {
          System.out.println("Enter UserID: ");
          int userID = Integer.parseInt(input.nextLine());

          System.out.println("Type 'YES' to confirm");

          if(input.nextLine().equalsIgnoreCase("YES"))
            dropUser(userID);
        }
        else if(option == 8 && user == 1)
        {
            System.out.println("Enter SportID: ");
            int sportID = Integer.parseInt(input.nextLine());

            System.out.println("Enter VenueID: ");
            int venueID = Integer.parseInt(input.nextLine());

            System.out.println("Enter date *FORMAT* mm/dd/yyyy_hh24:mi:ss where underscore refers to space: ");
            String date = input.nextLine();

            System.out.println("Enter Gender (m or w)");
            String gender = Character.toString(input.nextLine().charAt(0));

            createEvent(sportID, venueID, date, gender);
        }
        else if(option == 9 && user == 1)
        {
          System.out.println("Enter Olympic Games ID: ");
          int olympicID = Integer.parseInt(input.nextLine());

          System.out.println("Enter Event ID: ");
          int eventID = Integer.parseInt(input.nextLine());

          System.out.println("Enter Team ID: ");
          int teamID = Integer.parseInt(input.nextLine());

          System.out.println("Enter Participant ID: ");
          int participantID = Integer.parseInt(input.nextLine());

          System.out.println("Enter position: ");
          int position = Integer.parseInt(input.nextLine());

          addEventOutcome(olympicID, teamID, eventID, participantID, position);
        }
        else if(option == 6 && user == 2)
        {
          System.out.println("Enter Olympic City: ");
          String city = input.nextLine();

          System.out.println("Enter Olympic Year: ");
          String year = input.nextLine();

          System.out.println("Enter Sport name: ");
          String sportName = input.nextLine();

          System.out.println("Enter Country name: ");
          String country = input.nextLine();

          System.out.println("Enter Team name: ");
          String name = input.nextLine();

          createTeam(year, city, sportName, country, name);
        }
        else if(option == 7 && user == 2)
        {
          System.out.println("Enter Team ID: ");
          int teamID = Integer.parseInt(input.nextLine());

          System.out.println("Enter Event ID: ");
          int eventID = Integer.parseInt(input.nextLine());

          registerTeam(teamID, eventID);
        }
        else if(option == 8 && user == 2)
        {
          System.out.println("Enter Participant First Name: ");
          String firstName = input.nextLine();

          System.out.println("Enter Participant Last Name: ");
          String lastName = input.nextLine();

          System.out.println("Enter Participant birthplace: ");
          String birthplace = input.nextLine();

          System.out.println("Enter Participant nationality: ");
          String nationality = input.nextLine();

          System.out.println("Enter Participant DOB *FORMAT* mm/dd/yyyy :");
          String DOB = input.nextLine();

          addParticipant(firstName, lastName, birthplace, nationality, DOB);
        }
        else if(option == 9 && user == 2)
        {
          System.out.println("Enter Team ID: ");
          int teamID = Integer.parseInt(input.nextLine());

          System.out.println("Enter Participant ID: ");
          int participantID = Integer.parseInt(input.nextLine());

          addTeamMember(teamID, participantID);
        }
        else if(option == 10 && user == 2)
        {
          System.out.println("Enter participantID: (TYPE 0 to CANCEL)");
          int participantID = Integer.parseInt(input.nextLine());
          if(participantID > 0)
          {
            dropTeamMember(participantID);
          }
        }
      }
    }
  } // ENTERING DATABASE FUNCTIONS
  public static int login(String name, String passwordLogin)
  {
    try
    {
      PreparedStatement st = dbcon.prepareStatement("SELECT * FROM USER_ACCOUNT WHERE username= ? AND passkey= ?");
      st.setString(1, name);
      st.setString(2, passwordLogin);

      ResultSet result = st.executeQuery();
      if(!(result.next()))
        return -1;

      int roleID = result.getInt("role_id");
      userid = result.getInt("user_id");
      st.close();
      return roleID;
    }catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
      return -1;
    }
  }
  public static void logout()
  {
    try
    {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
      LocalDateTime now = LocalDateTime.now();
      String date = dtf.format(now).toString();

      Statement st = dbcon.createStatement(
                  ResultSet.TYPE_SCROLL_INSENSITIVE,
                  ResultSet.CONCUR_UPDATABLE);

      st.executeUpdate("UPDATE USER_ACCOUNT SET last_login = TO_DATE('" + date
      + "', 'mm/dd/yyyy') WHERE user_id = " + userid);

      dbcon.commit();
      st.close();
      System.out.print("\n");
    }
    catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void displaySport(String sportName)
  {
    try
    {
    PreparedStatement st = dbcon.prepareStatement("SELECT dob, event_time, gender, medal_title, nationality, name FROM MEDAL NATURAL JOIN " +
    "(SELECT s.dob, j.event_time, j.gender, medal_id, j.nationality, (j.fname || ' ' || j.lname) as name FROM SPORT s join " +
    "(SELECT * FROM PARTICIPANT NATURAL JOIN(SELECT * FROM SCOREBOARD NATURAL JOIN EVENT)) j ON s.sport_id = j.sport_id where s.sport_name = ?) " +
    "ORDER BY medal_id, event_time ASC");
    st.setString(1, sportName);

    ResultSet result = st.executeQuery();
    boolean isFound = false;
    while(result.next())
    {
      String dob = result.getString("dob");
      String eventTime = result.getString("event_time");
      String gender = result.getString("gender");
      String medalTitle = result.getString("medal_title");
      String nationality = result.getString("nationality");
      String name = result.getString("name");
      System.out.print("DOB: " + dob + " || Event Time: " + eventTime + " || gender: " +
      gender + " || Medal Title: " + medalTitle + " || Nationality: " + nationality + " || Name: " +
      name);
      isFound = true;
      System.out.print("\n");
    }
    if(!isFound)
      System.out.println("Sports not found in database\n");

      st.close();
      System.out.print("\n");
    }catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void displayEvent(int id, String year, String city)
  {
    try
    {
      PreparedStatement st = dbcon.prepareStatement("SELECT sport_name, olympic_num, event_time, name, medal_title from MEDAL natural join " +
      "(SELECT olympic_num, sport_name, event_time, name, medal_id FROM OLYMPICS NATURAL JOIN " +
      "(SELECT * FROM SPORT NATURAL JOIN " +
      "(SELECT sport_id, event_time, olympic_id, name, medal_id FROM EVENT e JOIN " +
      "(SELECT event_id, olympic_id, (fname || ' ' || lname) as name, medal_id " +
      "FROM SCOREBOARD NATURAL JOIN PARTICIPANT WHERE EVENT_ID = ?) j ON e.event_id = j.event_id)) " +
      "WHERE EXTRACT(YEAR FROM closing_date) = ? AND host_city = ?)");

      st.setInt(1, id);
      st.setString(2, year);
      st.setString(3, city);

      ResultSet result = st.executeQuery();
      boolean isFound = false;
      while(result.next())
      {
        String sportName = result.getString("sport_name");
        String olympicNum = result.getString("olympic_num");
        String eventTime = result.getString("event_time");
        String name = result.getString("name");
        String medalTitle = result.getString("medal_title");

        System.out.print("Sport Name: " + sportName + " || Event Time: " + eventTime + " || Olympic Number: " +
        olympicNum + " || Medal Title: " + medalTitle + " || Name: " + name);
        isFound = true;
        System.out.print("\n");
      }
      if(!isFound)
        System.out.println("Events not found in database");
      st.close();
      System.out.println("\n");
    }catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void countryRanking(int id)
  {
    try
    {
      PreparedStatement st = dbcon.prepareStatement("SELECT MIN(Introduction) as introduce, country_code, sum(score) as score FROM " +
      "(SELECT Introduction, country_code, SUM(points) as score " +
      "FROM MEDAL NATURAL JOIN " +
      "(SELECT MIN(event_time) as Introduction, country_code, medal_id, olympic_id FROM EVENT_PARTICIPATION NATURAL JOIN " +
      "(SELECT * FROM EVENT NATURAL JOIN " +
      "(SELECT UNIQUE event_id, team_id, medal_id, olympic_id, country_code FROM SCOREBOARD NATURAL JOIN " +
      "(SELECT * FROM COUNTRY NATURAL JOIN TEAM))) WHERE status = 'e' GROUP BY country_code, medal_id, olympic_id) " +
      "WHERE olympic_id = ? " +
      "GROUP BY country_code, Introduction) " +
      "GROUP BY country_code " +
      "ORDER BY score DESC");

      st.setInt(1, id);
      ResultSet result = st.executeQuery();
      boolean isFound = false;

      while(result.next())
      {
        String country = result.getString("country_code");
        String intro = result.getString("Introduce");
        int score = result.getInt("score");

        System.out.print("Country Code: " + country + " || First showing: " + intro + " || Score: " + score);
        isFound = true;
        System.out.print("\n");
      }

      if(!isFound)
        System.out.println("Olympic game not found in database");
      st.close();
      System.out.print("\n");
    }catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void createUser(String user, String passkey, int roleID)
  {
    try
    {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
      LocalDateTime now = LocalDateTime.now();
      String date = dtf.format(now).toString();
      PreparedStatement st = dbcon.prepareStatement("INSERT INTO USER_ACCOUNT VALUES (user_seq.NEXTVAL, ?, ?, ?, TO_DATE('" +
      date + "', 'mm/dd/yyyy'))");

      st.setString(1, user);
      st.setString(2, passkey);
      st.setInt(3, roleID);

      st.executeUpdate();
      dbcon.commit();
      st.close();
    }catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void dropUser(int userID)
  {
    try
    {
      PreparedStatement st = dbcon.prepareStatement("DELETE FROM USER_ACCOUNT WHERE user_id = ?");
      st.setInt(1, userID);

      st.executeUpdate();
      dbcon.commit();
      st.close();
    }catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void createEvent(int sportID, int venueID, String date, String gender)
  {
    try
    {
      PreparedStatement st = dbcon.prepareStatement("INSERT INTO EVENT VALUES (event_seq.NEXTVAL, ?, ?, ?, TO_DATE(?, 'mm/dd/yyyy hh24:mi:ss'))");
      st.setInt(1, sportID);
      st.setInt(2, venueID);
      st.setString(4, date);
      st.setString(3, gender);

      st.executeUpdate();
      dbcon.commit();
      st.close();
    }
    catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void addEventOutcome(int olympicID, int teamID, int eventID, int participantID, int position)
  {
    try
    {
      PreparedStatement st = dbcon.prepareStatement("INSERT INTO SCOREBOARD VALUES (?, ?, ?, ?, ?, null)");
      st.setInt(1, olympicID);
      st.setInt(2, eventID);
      st.setInt(3, teamID);
      st.setInt(4, participantID);
      st.setInt(5, position);

      st.executeUpdate();
      st.close();
    }catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void createTeam(String year, String city, String sportName, String country, String name)
  {
    try
    {
      PreparedStatement st = dbcon.prepareStatement("SELECT participant_id FROM PARTICIPANT WHERE fname || ' ' || lname = ?");
      st.setString(1, currentUser);

      ResultSet result = st.executeQuery();
      result.next();
      int coachID = result.getInt("participant_id");

      st = dbcon.prepareStatement("SELECT olympic_id FROM OLYMPICS WHERE host_city = ? AND EXTRACT(YEAR FROM closing_date) = ?");
      st.setString(1, city);
      st.setString(2, year);

      result = st.executeQuery();
      result.next();
      int olympicID = result.getInt("olympic_id");

      st = dbcon.prepareStatement("SELECT country_id FROM COUNTRY WHERE country = ?");
      st.setString(1, country);

      result = st.executeQuery();
      result.next();
      int countryID = result.getInt("country_id");

      st = dbcon.prepareStatement("SELECT sport_id from SPORT WHERE sport_name = ?");
      st.setString(1, sportName);

      result = st.executeQuery();
      result.next();
      int sportID = result.getInt("sport_id");

      st = dbcon.prepareStatement("INSERT INTO TEAM VALUES (team_seq.NEXTVAL, ?, ?, ?, ?, ?)");
      st.setInt(1, olympicID);
      st.setString(2, name);
      st.setInt(3, countryID);
      st.setInt(4, sportID);
      st.setInt(5, coachID);

      st.executeUpdate();
      dbcon.commit();
      st.close();


    }catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void registerTeam(int teamID, int eventID)
  {
    try
    {
      PreparedStatement st = dbcon.prepareStatement("INSERT INTO EVENT_PARTICIPATION VALUES (?, ?, 'e')");
      st.setInt(teamID, 2);
      st.setInt(eventID, 1);

      st.executeUpdate();
      dbcon.commit();
      st.close();

    }catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void addParticipant(String firstName, String lastName,
  String birthplace, String nationality, String DOB)
  {
    try
    {
      PreparedStatement st = dbcon.prepareStatement("INSERT INTO PARTICIPANT VALUES(participant_seq.NEXTVAL, " +
      "?, ?, ?, ?, TO_DATE(?, 'mm/dd/yyyy'))");
      st.setString(1, firstName);
      st.setString(2, lastName);
      st.setString(3, nationality);
      st.setString(4, birthplace);
      st.setString(5, DOB);

      st.executeUpdate();
      dbcon.commit();
      st.close();
    }catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void addTeamMember(int teamID, int participantID)
  {
    try
    {
      PreparedStatement st = dbcon.prepareStatement("INSERT INTO TEAM_MEMBER VALUES (?, ?)");
      st.setInt(1, teamID);
      st.setInt(2, participantID);

      st.executeUpdate();
      dbcon.commit();
      st.close();

    }catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void dropTeamMember(int participantID)
  {
    try
    {
      PreparedStatement st = dbcon.prepareStatement("DELETE FROM TEAM_MEMBER WHERE participant_id = ?");
      st.setInt(1, participantID);

      st.executeUpdate();
      dbcon.commit();
      st.close();

    }catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void topKAthletes(int olympicID, int k)
  {
    try
    {
      PreparedStatement st = dbcon.prepareStatement("SELECT * FROM(SELECT (fname || ' ' || lname) AS name, sum(points) as score FROM MEDAL NATURAL JOIN" +
      "(SELECT * FROM SCOREBOARD NATURAL JOIN PARTICIPANT) WHERE olympic_id = ? GROUP BY fname || ' ' || lname ORDER BY score desc) WHERE ROWNUM <= ?");

      st.setInt(1, olympicID);
      st.setInt(2, k);

      ResultSet result = st.executeQuery();
      while(result.next())
      {
        String name = result.getString("name");
        int score = result.getInt("score");
        System.out.println("Name: " + name + " ||Score: " + score);
      }
      st.close();
    }
    catch(SQLException e1)
    {
      while (e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }
  public static void connectedAthletes(int olympicID, int participantID, int n)
  {
    try
    {
      PreparedStatement st = dbcon.prepareStatement("SELECT participant_id FROM SCOREBOARD WHERE " +
      "olympic_id = ? AND participant_id = ?");
      st.setInt(1, olympicID);
      st.setInt(2, participantID);
      ResultSet result = st.executeQuery();

      if(!(result.next()))
      {
        System.out.println("Athlete not found!");
        return;
      }
      st = dbcon.prepareStatement("SELECT participant_id FROM SCOREBOARD WHERE " +
      "olympic_id = ? and participant_id != ?");
      st.setInt(1, olympicID);
      st.setInt(2, participantID);
      ResultSet connected = st.executeQuery();
      int hashTable[] = new int[5000];
      //ArrayList<String> name = new ArrayList<String>();
      while(connected.next())
      {
        int nextParticipant = connected.getInt("participant_id");
        int j = 1;
        for(int i = olympicID - 1; i >= olympicID - n && i > 0; i--)
        {
          PreparedStatement findST = dbcon.prepareStatement("SELECT participant_id FROM SCOREBOARD WHERE " +
          "olympic_id = ? and participant_id = ?");
          findST.setInt(1, i);
          findST.setInt(2, nextParticipant);

          ResultSet find = findST.executeQuery();
          if(find.next())
          {
            PreparedStatement foundST = dbcon.prepareStatement("SELECT participant_id FROM SCOREBOARD WHERE " +
            "olympic_id = ? and participant_id != ?");
            foundST.setInt(1, i);
            foundST.setInt(2, nextParticipant);

            ResultSet hops = foundST.executeQuery();
            boolean isFound = false;
            while(hops.next())
            {
              int foundParticipant = hops.getInt("participant_id");
              hashTable[foundParticipant] = j;
              isFound = true;
            }
            if(isFound)
              continue;
          }

        }
      }


    }catch(SQLException e1)
    {
      while(e1 != null)
      {
        System.out.println("Message = " + e1.getMessage());
        System.out.println("SQLState = " + e1.getErrorCode());
        System.out.println("SQLState = " + e1.getSQLState());

        e1 = e1.getNextException();
      }
    }
  }

}
