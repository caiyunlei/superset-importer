import com.apache.superset.tool.Importer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class ImporterTest {
  @Test
  @Tag("Superset docker version: 57f55e8bdd45")
  public void should_import_success_when_given_user_and_password() throws IOException {
    var user = "admin";
    var password = "admin";
    var dashboardFilePath = "/Users/ylcai/code/superset-importer/src/test/resources/20220308_025438.json";
    File file = new File(dashboardFilePath);

    Importer importer = new Importer(user, password, "http://localhost:8080");
    importer.importDashboard(file);
  }
}
