import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.riskassessmentelements.ThreatSourceEnum;
import org.crmf.model.utility.threatmodel.ThreatSerializerDeserializer;
import org.crmf.persistency.domain.threat.SestThreatModel;
import org.crmf.persistency.mapper.threat.ThreatService;
import org.crmf.threatimport.threatimportmanager.ThreatImportManagerInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ThreatImportManagerInputTest {

  @InjectMocks
  ThreatImportManagerInput threatImportManager;

  @Mock
  ThreatService threatService;

  public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
  private static final DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);

  @BeforeEach()
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void importThreatsInsert() throws Exception {

    JsonObject jsonObjectExisting = new JsonObject();
    JsonArray jsonArrayExisting = new JsonArray();
    jsonObjectExisting.add("Threats", jsonArrayExisting);
    jsonObjectExisting.addProperty("creationTime", "");
    jsonObjectExisting.addProperty("updateTime", "");
    jsonObjectExisting.addProperty("identifier", "");
    jsonObjectExisting.addProperty("objType", "ThreatModel");
    SestThreatModel sestThreatModelExisting = new SestThreatModel();
    sestThreatModelExisting.setThreatModelJson(jsonObjectExisting.toString());

    Threat v1 = new Threat();
    v1.setLastUpdate("15/09/2020 15:00");
    v1.setCatalogue(ThreatSourceEnum.MEHARI);
    v1.setCatalogueId("IF.L.Exp-Exp----");
    v1.setName("(Accidental) Production incident-IF.L.Exp-Exp----");
    v1.setDescription("an accidental production incident within the production premises");
    Threat v2 = new Threat();
    v2.setLastUpdate("15/09/2020 15:00");
    v2.setCatalogueId("ER.P.Pro-Exp--Ain--Ual");
    v2.setCatalogue(ThreatSourceEnum.MEHARI);
    v2.setName("(Error) Error of operation or non compliance of a procedure-ER.P.Pro-Exp--Ain--Ual");
    v2.setDescription("Error of operation or non compliance of a procedure, by a user authorized legitimately connected from the internal network within the production premises");

    ArrayList threats = new ArrayList();
    threats.add(v1);
    threats.add(v2);
    ThreatModel modelToAdd = new ThreatModel();
    modelToAdd.setThreats(threats);
    modelToAdd.setUpdateTime("");
    modelToAdd.setCreationTime("");
    ThreatModel modelToUpdate = new ThreatModel();
    modelToUpdate.setThreats(new ArrayList<>());

    lenient().when(threatService.getThreatRepository(null)).thenReturn(sestThreatModelExisting);

    when(threatService.updateThreatRepository(
      argThat(new ThreatMatcher(modelToAdd)), argThat(new ThreatMatcher(modelToUpdate)))).thenReturn(true);

    InputStream resource = new ClassPathResource("threat.json").getInputStream();
    // call importThreatsFromInput to test
    threatImportManager.importThreatsFromInput(resource);

    verify(threatService, times(1)).updateThreatRepository(
      argThat(new ThreatMatcher(modelToAdd)), argThat(new ThreatMatcher(modelToUpdate)));
  }

  @Test
  public void importThreatsUpdate() throws Exception {

    Threat vExisting = new Threat();
    vExisting.setLastUpdate("15/09/2020 14:59");
    vExisting.setCatalogueId("IF.L.Exp-Exp----");
    Threat vExisting2 = new Threat();
    vExisting2.setLastUpdate("15/09/2020 15:01");
    vExisting2.setCatalogueId("ER.P.Pro-Exp--Ain--Ual");
    ThreatSerializerDeserializer deserializer = new ThreatSerializerDeserializer();
    String vExistingJson = deserializer.getJSONStringFromTM(vExisting);
    String vExistingJson2 = deserializer.getJSONStringFromTM(vExisting2);

    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();
    JsonObject jsonObjectExisting = new JsonObject();
    JsonArray jsonArrayExisting = new JsonArray();
    JsonElement element1 = gson.fromJson(vExistingJson, JsonElement.class);
    JsonElement element2 = gson.fromJson(vExistingJson2, JsonElement.class);
    jsonArrayExisting.add(element1);
    jsonArrayExisting.add(element2);

    jsonObjectExisting.add("threats", jsonArrayExisting);
    jsonObjectExisting.addProperty("creationTime", "");
    jsonObjectExisting.addProperty("updateTime", "");
    jsonObjectExisting.addProperty("identifier", "");
    jsonObjectExisting.addProperty("objType", "ThreatModel");
    SestThreatModel sestThreatModelExisting = new SestThreatModel();
    sestThreatModelExisting.setThreatModelJson(jsonObjectExisting.toString());

    Threat v1 = new Threat();
    v1.setLastUpdate("15/09/2020 15:00");
    v1.setCatalogue(ThreatSourceEnum.MEHARI);
    v1.setCatalogueId("IF.L.Exp-Exp----");
    v1.setName("(Accidental) Production incident-IF.L.Exp-Exp----");
    v1.setDescription("an accidental production incident within the production premises");
    Threat v2 = new Threat();
    v2.setLastUpdate("15/09/2020 15:00");
    v2.setCatalogueId("ER.P.Pro-Exp--Ain--Ual");
    v2.setCatalogue(ThreatSourceEnum.MEHARI);
    v2.setName("(Error) Error of operation or non compliance of a procedure-ER.P.Pro-Exp--Ain--Ual");
    v2.setDescription("Error of operation or non compliance of a procedure, by a user authorized legitimately connected from the internal network within the production premises");

    ArrayList threatsToAdd = new ArrayList();
    ArrayList threatsToupdate = new ArrayList();
    threatsToupdate.add(v1);

    ThreatModel modelToAdd = new ThreatModel();
    modelToAdd.setThreats(threatsToAdd);
    ThreatModel modelToUpdate = new ThreatModel();
    modelToUpdate.setThreats(threatsToupdate);

    lenient().when(threatService.getThreatRepository(null)).thenReturn(sestThreatModelExisting);

    when(threatService.updateThreatRepository(
      argThat(new ThreatMatcher(modelToAdd)), argThat(new ThreatMatcher(modelToUpdate)))).thenReturn(true);

    InputStream resource = new ClassPathResource("threat.json").getInputStream();
    // call importThreatsFromInput to test
    threatImportManager.importThreatsFromInput(resource);

    verify(threatService, times(1)).updateThreatRepository(
      argThat(new ThreatMatcher(modelToAdd)), argThat(new ThreatMatcher(modelToUpdate)));
  }

  @Test
  public void importThreatsInsertAndUpdate() throws Exception {

    Threat vExisting = new Threat();
    vExisting.setLastUpdate("15/09/2020 14:59");
    vExisting.setCatalogue(ThreatSourceEnum.MEHARI);
    vExisting.setCatalogueId("IF.L.Exp-Exp----");
    ThreatSerializerDeserializer deserializer = new ThreatSerializerDeserializer();
    String vExistingJson = deserializer.getJSONStringFromTM(vExisting);

    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();
    JsonObject jsonObjectExisting = new JsonObject();
    JsonArray jsonArrayExisting = new JsonArray();
    JsonElement element = gson.fromJson(vExistingJson, JsonElement.class);
    jsonArrayExisting.add(element);

    jsonObjectExisting.add("threats", jsonArrayExisting);
    jsonObjectExisting.addProperty("creationTime", "");
    jsonObjectExisting.addProperty("updateTime", "");
    jsonObjectExisting.addProperty("identifier", "");
    jsonObjectExisting.addProperty("objType", "ThreatModel");
    SestThreatModel sestThreatModelExisting = new SestThreatModel();
    sestThreatModelExisting.setThreatModelJson(jsonObjectExisting.toString());

    Threat v1 = new Threat();
    v1.setLastUpdate("15/09/2020 15:00");
    v1.setCatalogue(ThreatSourceEnum.MEHARI);
    v1.setCatalogueId("IF.L.Exp-Exp----");
    v1.setName("(Accidental) Production incident-IF.L.Exp-Exp----");
    v1.setDescription("an accidental production incident within the production premises");
    Threat v2 = new Threat();
    v2.setLastUpdate("15/09/2020 15:00");
    v2.setCatalogueId("ER.P.Pro-Exp--Ain--Ual");
    v2.setCatalogue(ThreatSourceEnum.MEHARI);
    v2.setName("(Error) Error of operation or non compliance of a procedure-ER.P.Pro-Exp--Ain--Ual");
    v2.setDescription("Error of operation or non compliance of a procedure, by a user authorized legitimately connected from the internal network within the production premises");

    ArrayList threatsToAdd = new ArrayList();
    threatsToAdd.add(v2);
    ArrayList threatsToupdate = new ArrayList();
    threatsToupdate.add(v1);

    ThreatModel modelToAdd = new ThreatModel();
    modelToAdd.setThreats(threatsToAdd);
    ThreatModel modelToUpdate = new ThreatModel();
    modelToUpdate.setThreats(threatsToupdate);

    lenient().when(threatService.getThreatRepository(null)).thenReturn(sestThreatModelExisting);

    when(threatService.updateThreatRepository(
      argThat(new ThreatMatcher(modelToAdd)), argThat(new ThreatMatcher(modelToUpdate)))).thenReturn(true);

    InputStream resource = new ClassPathResource("threat.json").getInputStream();
    // call importThreatsFromInput to test
    threatImportManager.importThreatsFromInput(resource);

    verify(threatService, times(1)).updateThreatRepository(
      argThat(new ThreatMatcher(modelToAdd)), argThat(new ThreatMatcher(modelToUpdate)));
  }

  @Test
  public void importThreatsWrongInput() throws Exception {

    Assertions.assertThrows(RemoteComponentException.class, () -> {
      threatImportManager.importThreatsFromInput(null);
    });
  }
}
