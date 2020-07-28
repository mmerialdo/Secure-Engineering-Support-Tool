/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="permission-type.class.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

export class PermissionType {

  public static readonly User = 'User';
  public static readonly AssessmentProfile = 'AssessmentProfile';
  public static readonly ThreatModel = 'ThreatModel';
  public static readonly SafeguardModel = 'SafeguardModel';
  public static readonly AssessmentProject = 'AssessmentProject';
  public static readonly RiskModel = 'RiskModel';
  public static readonly RiskTreatmentModel = 'RiskTreatmentModel';
  public static readonly Audit = 'Audit';
  public static readonly AssetModel = 'AssetModel';
  public static readonly VulnerabilityModel = 'VulnerabilityModel';
  public static readonly AssessmentProcedure = 'AssessmentProcedure';
  public static readonly AssessmentTemplate = 'AssessmentTemplate';
  public static readonly Settings = 'Settings';
  public static readonly Taxonomy = 'Taxonomy';

  public static readonly LIST = [
    PermissionType.AssessmentProcedure, PermissionType.AssessmentProfile, PermissionType.AssessmentProject,
    PermissionType.AssetModel, PermissionType.Audit, PermissionType.RiskModel, PermissionType.RiskTreatmentModel,
    PermissionType.SafeguardModel, PermissionType.Settings, PermissionType.Taxonomy
  ];
}
