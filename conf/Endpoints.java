package conf;
public class Endpoints {
   public final String baseURL="https://www.easistent.com";
   public final String loginURL=baseURL+"/p/ajax_prijava";
   public final String bearerURL=baseURL+"/webapp";
   public final String welcomeURL=baseURL+"/m/me/child";
   public final String logoutURL=baseURL+"/p/get_odjava";
   public final String gradeURL=baseURL+"/m/grades";
   public final String gradesURL=baseURL+"/m/evaluations?filter=past";
   public final String examsURL=baseURL+"/m/evaluations?filter=future";
   public final String absencesURL=baseURL+"/m/absences";
   // private static final String weekCal=baseURL+"/m/timetable/weekly?from=" + date_from + "&to=" + date_to;
}
