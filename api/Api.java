package api;

import conf.Endpoints;
import java.io.IOException;
import java.time.*;
import java.time.format.*;
import java.util.Locale;
import tools.*;

public class Api {
  Endpoints end = new Endpoints();

  final DateTimeFormatter date = DateTimeFormatter.ofPattern("dd. MM. yyyy");
  final DateTimeFormatter today = DateTimeFormatter.ofPattern("EEEE", new Locale("sl"));
  final DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");

  private String parseJson(String data, String valuePath) {
    return JsonParser.getInstance().getValue(data, valuePath);
  }

  // 6.1
  public String login(String username, String password) throws IOException {
    HttpService httpService = new HttpService();
    httpService.setConnection(end.loginURL, "POST",
        "uporabnik=" + username + "&geslo=" + password);
    if (parseJson(httpService.getBody(), "status").equals("ok")) {
      Auth.setCredentials(username, password);
      httpService.resetConnection();

      return "\n" + getWelcome();
    } else {
      httpService.resetConnection();
      throw new IOException("Napačni prijavni podatki. Ponovno se prijavi.");
    }
  }

  public String getWelcome() throws IOException {
    String bearer = getBearer();
    HttpService httpService = new HttpService();
    httpService.setConnection(end.welcomeURL, "GET");
    httpService.addHeader("authorization", bearer);
    String output = httpService.getBody();
    httpService.resetConnection();

    String gender = parseJson(output, "gender");
    String name = parseJson(output, "display_name");

    if (gender.equals("m")) {
      return "Pozdravljen " + name + ", prijava uspešna.";
    } else if (gender.equals("f")) {
      return "Pozdravljena " + name + ", prijava uspešna.";
    } else {
      return "Pozdravljen robot, prijava uspešna.";
    }
  }

  public String getBearer() throws IOException {
    if (Auth.credentialsWritten()) {
      HttpService httpService = new HttpService();
      httpService.setConnection(end.loginURL, "POST",
          "uporabnik=" + Auth.getUsername() +
              "&geslo=" + Auth.getPassword());
      if (parseJson(httpService.getBody(), "status").equals("ok")) {
        String cookie = httpService.getHeader("set-cookie");
        httpService.resetConnection();

        httpService.setConnection(end.bearerURL, "GET");
        httpService.addHeader("cookie", cookie);
        String bearer = AuthBear.getBearer(httpService.getBody());
        httpService.resetConnection();
        return bearer;
      } else {
        throw new IOException("Napačni prijavni podatki. Ponovno se prijavi.");
      }
    } else {
      throw new IOException("Ni shranjenega računa. Najprej se prijavi.");
    }
  }

  // 6.2
  public void logout() throws IOException {
    if (Auth.credentialsWritten()) {
      Auth.deleteCredentials();
      System.out.println("Odjava uspešna");
    } else {
      throw new IOException("Niste prijavljeni.");
    }
  }

  // 1
  public String getGrade() throws IOException {
    String bearer = getBearer();
    HttpService httpService = new HttpService();
    httpService.setConnection(end.gradeURL, "GET");
    httpService.addHeader("authorization", bearer);
    String output = httpService.getBody();
    httpService.resetConnection();

    String predmet = parseJson(output, "items/0/short_name");
    String ocena = parseJson(output, "items/0/semesters/0/grades/0/value");
    String vrsta = parseJson(output, "items/0/semesters/0/grades/0/type_name");
    String komentar = parseJson(output, "items/0/semesters/0/grades/0/comment");
    String avg = parseJson(output, "items/0/average_grade");
    if (komentar != null) {
      return "Predmet: " + predmet + "\nVrsta:" + vrsta + "\nOcena: " + ocena +
          "\nKomentar: " + komentar + "\nPovprečje: " + avg;
    } else {
      return "Predmet: " + predmet + "\nVrsta:" + vrsta + "\nOcena: " + ocena +
          "\nPovprečje: " + avg;
    }
  }

  // 2
  public String getExams() throws IOException {
    String bearer = getBearer();
    HttpService httpService = new HttpService();
    httpService.setConnection(end.examsURL, "GET");
    httpService.addHeader("authorization", bearer);
    String output = httpService.getBody();
    httpService.resetConnection();

    // output override
    // output = Reader.readFromFile("responses/exams.json");

    StringBuilder returnOutput = new StringBuilder();
    returnOutput.append(String.format(" %-7s | %-11s | %-12s | %-7s | %s%n",
        "PREDMET", "   VRSTA   ", "   DATUM   ",
        "  URA  ", "KOMENTAR"));
    returnOutput.append(String.format("-%-7s-|-%-11s-|-%-12s-|-%-7s-|-%s%n",
        "-------", "-----------", "------------",
        "-------", "----------------"));

    int jsonArrayLength = JsonParser.getInstance().getArrayLength(output, "items");
    if (jsonArrayLength > 0) {
      for (int i = 0; i < jsonArrayLength; i++) {
        String path = "items/" + i;
        String predmet = convertSubject(parseJson(output, path + "/course"));
        String vrsta = parseJson(output, path + "/type_name");
        String ura = parseJson(output, path + "/period");
        String datum = LocalDate.parse(parseJson(output, path + "/date")).format(date);
        String komentar = parseJson(output, path + "/subject");

        returnOutput.append(String.format(" %-7s | %-11s | %-12s | %-7s | %s%n",
            predmet, vrsta, datum, ura,
            komentar));
      }
      return returnOutput.toString();
    } else {
      return "Ni vpisanih ocenjevanj. 🥳🎉";
    }
  }

  // 3
  public String getTodayTimetable() throws IOException {
    String bearer = getBearer();
    HttpService httpService = new HttpService();
    httpService.setConnection(end.welcomeURL, "GET");
    httpService.addHeader("authorization", bearer);
    String output = httpService.getBody();
    httpService.resetConnection();

    String dayName = LocalDate.now().format(today);

    // output override
    // output = Reader.readFromFile("responses/child.json");

    StringBuilder returnOutput = new StringBuilder();
    returnOutput.append(String.format("%s", LocalDate.parse(parseJson(output, "timetable/date")).format(date)))
        .append("\n\n");

    returnOutput.append(String.format("%-1s | %-6s | %-6s | %-15s\n", "#", "  OD  ", "  DO  ", "SUMMARY"));
    returnOutput.append(String.format("%-1s-|-%-6s-|-%-6s-|-%-10s-\n", "-", "------", "------", "----------"));
    int jsonArrayLength = JsonParser.getInstance().getArrayLength(output, "timetable/hours");

    if (jsonArrayLength > 0) {
      for (int i = 0; i < (jsonArrayLength - 1); i++) {
        String path = "timetable/hours/" + i;
        String from = LocalDateTime.parse(parseJson(output, path + "/from"), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .format(time);
        String to = LocalDateTime.parse(parseJson(output, path + "/to"), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .format(time);
        String summary = parseJson(output, path + "/summary");
        // String subject = summary.substring(0, summary.indexOf(' '));
        // String classroom = summary.substring(summary.indexOf(" ", summary.indexOf("
        // ") + 1));

        returnOutput.append(String.format("%-1s.| %-6s | %-6s | %-15s\n", i + 1, from, to, summary));
      }
      return returnOutput.toString();
    } else {
      if (dayName.equals("sobota") | dayName.equals("nedelja")) {
        return "Vikend je!";
      } else {
        return "Danes (" + dayName + ") ni vpisov v urnik.";
      }
    }
  }

  // 4
  public String getGrades() throws IOException {
    String bearer = getBearer();
    HttpService httpService = new HttpService();
    httpService.setConnection(end.gradesURL, "GET");
    httpService.addHeader("authorization", bearer);
    String output = httpService.getBody();
    httpService.resetConnection();

    StringBuilder returnOutput = new StringBuilder();

    int jsonArrayLength = JsonParser.getInstance().getArrayLength(output, "items");
    returnOutput.append("Število ocen: ")
        .append(jsonArrayLength)
        .append("\n\n");
    returnOutput.append(String.format(" %-7s | %-12s | %-12s | %-5s | %s%n",
        "PREDMET", "   VRSTA   ", "   DATUM   ", "OCENA", "KOMENTAR"));
    returnOutput.append(String.format("-%-7s-|-%-12s-|-%-12s-|-%-5s-|-%s%n",
        "-------", "------------", "------------", "-----", "------------------"));

    for (int i = 0; i < jsonArrayLength; i++) {
      String path = "items/" + i;
      String predmet = convertSubject(parseJson(output, path + "/course"));
      String ocena = parseJson(output, path + "/grade");

      String vrsta = parseJson(output, path + "/type_name");
      String datum = LocalDate.parse(parseJson(output, path + "/date")).format(date);
      String komentar = parseJson(output, path + "/subject");

      returnOutput.append(
          String.format(" %-7s | %-12s | %-12s |   %-1s   | %s%n", predmet,
              vrsta, datum, ocena != null ? ocena : "/", komentar));
    }
    return returnOutput.toString();
  }

  // TODO: load this from file
  private String convertSubject(String input) {
    return switch (input) {
      case "Laboratorijske vaje - računalništvo" -> "LavRAČ";
      case "Računalništvo" -> "RAČ";
      case "Računalniški sistemi in omrežja" -> "RSO";
      case "Fizika" -> "FIZ";
      case "Matematika" -> "MAT";
      case "Nemščina" -> "NEM";
      case "Angleščina" -> "ANG";
      case "Slovenščina" -> "SLO";
      case "Zgodovina" -> "ZGO";
      case "Filozofija" -> "FIL";
      case "Geografija - izbirni" -> "GEOm";
      case "Športna vzgoja" -> "ŠVZ";
      default -> null;
    };
  }

  // 5
  public String getAbsences() throws IOException {
    String bearer = getBearer();
    HttpService httpService = new HttpService();
    httpService.setConnection(end.absencesURL, "GET");
    httpService.addHeader("authorization", bearer);
    String output = httpService.getBody();
    httpService.resetConnection();

    int jsonArrayLength = JsonParser.getInstance().getArrayLength(output, "items");

    String cakajocaOpravicila = parseJson(output, "summary/pending_hours");
    String opraviceneUre = parseJson(output, "summary/excused_hours");
    String neopraviceneUre = parseJson(output, "summary/unexcused_hours");
    String neurejeniIzostanki = parseJson(output, "summary/unmanaged_absences");
    String izostanki = "opravičene ure: \t" + opraviceneUre +
        "\nneopravičene ure: \t" + neopraviceneUre +
        "\nneurejeni izostanki: \t" + neurejeniIzostanki +
        "\nčakajoča opravičila: \t" + cakajocaOpravicila + "\n";
    StringBuilder returnOutput = new StringBuilder(izostanki);

    returnOutput.append(String.format(" %-12s | %-6s | %-10s | %s%n", "    DATUM   ", "STANJE", "OPRAVICENE", "URE"));
    returnOutput.append(String.format("-%-12s-|-%-6s-|-%-10s-|-%s%n",
        "------------", "------", "----------", "-------------------------"));

    for (int i = 0; i < jsonArrayLength; i++) {
      int hoursJsonArrayLength = JsonParser.getInstance().getArrayLength(output, "items/" + i + "/hours");
      String path = "items/" + i;
      String datum = LocalDate.parse(parseJson(output, path + "/date")).format(date);
      String vse_ure = parseJson(output, path + "/missing_count");
      String opravicene_ure = parseJson(output, path + "/excused_count");
      String stanje;
      if (parseJson(output, path + "/state").equals("managed")) {
        stanje = " ";
      } else {
        stanje = "";
      }

      StringBuilder ure = new StringBuilder();
      for (int j = 0; j < hoursJsonArrayLength; j++) {
        String path_h = path + "/hours/" + j;
        String stevilka_ure = parseJson(output, path_h + "/value");
        String predmet = parseJson(output, path_h + "/class_short_name");
        if (predmet.equals("DOG")) {
          predmet = parseJson(output, path_h + "/class_name");
          ure.append(predmet).append(", ");
        } else {
          ure.append(stevilka_ure).append(".(").append(predmet).append("), ");
        }
      }
      returnOutput.append(
          String.format(" %-12s |   %-4s |    %-7s | %s%n", datum, stanje, opravicene_ure + "/" + vse_ure, ure));
    }
    return returnOutput.toString();
  }
}
