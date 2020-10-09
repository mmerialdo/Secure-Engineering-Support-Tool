import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessmentelements.Threat;
import org.mockito.ArgumentMatcher;

import java.util.ArrayList;

public class ThreatMatcher implements ArgumentMatcher<ThreatModel> {

  private ThreatModel threat;

  public ThreatMatcher(ThreatModel threat) {
    this.threat = threat;
  }

  @Override
  public boolean matches(ThreatModel threatToCompare) {
    ArrayList<Threat> threatsToCompare = threatToCompare.getThreats();
    if (threat.getThreats().size() != threatsToCompare.size()) {
      return false;
    }

    for (Threat vuln : threat.getThreats()) {

      boolean foundMatch = false;
      for (Threat vulnToCompare : threatsToCompare) {
        if (vuln.getCatalogueId().equals(vulnToCompare.getCatalogueId())) {
          foundMatch = true;
          if (!(vuln.getCatalogueId().equals(vulnToCompare.getCatalogueId()) &&
            vuln.getCatalogue().equals(vulnToCompare.getCatalogue()) &&
            vuln.getName().equals(vulnToCompare.getName()) &&
            vuln.getDescription().equals(vulnToCompare.getDescription()))) {
            return false;
          }
        }
      }
      if (!foundMatch) {
        return false;
      }
    }
    return true;
  }
}
