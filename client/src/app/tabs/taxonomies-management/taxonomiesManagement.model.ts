/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="taxonomies-management.model.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

export enum TaxonomiesManagementTypeEnum {
  VULNERABILITIES = 'Vulnerabilities',
  THREATS = 'Threats',
  RISKSCENARIOS = 'Risk Scenarios'
}

export enum ImpactValueEnum {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  VERY_HIGH = 'VERY_HIGH'
}

export enum ImpactValueVulEnum {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export enum ImpactTypeEnum {
  CONFIDENTIALITY ='Confidentiality',
  INTEGRITY = 'Integrity',
  AVAILABILITY = 'Availability',
  EFFICIENCY = 'Efficiency'
}

export enum ThreatClassEnum {
  ACCIDENTAL = 'Accidental',
  DELIBERATE = 'Deliberate',
  NATURAL = 'Natural',
  ERROR = 'Error'
}

export interface VulnerabilityTaxonomy {

  affectedAssetsCategories: string [],
  catalogue?: string,
  catalogueId: string,
  description: string,
  name: string,
  phase: string,
  associatedThreats: string [],
  score: Score,
  identifier?: string,
  author?: string
}

export interface ThreatTaxonomy {
  affectedAssetsCategories: string [],
  catalogue: string,
  catalogueId: string,
  description: string,
  name: string,
  phase: string,
  threatClass: string,
  associatedVulnerabilities: string[],
  actor?: Actor,
  event: Event,
  score: ThreatScore,
  place?: Place,
  time?: Time,
  access?: Access,
  process?: Process,
  identifier?: string,
  author?: string
}

export interface Safeguard {
  category: string,
  value: string,
  answers?: string[],
  gasf?: string[],
  children?: string[]
}

export interface RiskScenario {

  assetType: string,
  supportingAsset: string,
  vulnerabilityCode: string,
  eventType: string,
  eventSubType: string,
  aice: string,
  actor?: string,
  place?: string,
  time?: string,
  access?: string,
  process?: string,
  prevention?: string,
  confining?: string,
  dissuasion?: string,
  palliative?: string,
  identifier?: string,
  author?: string
}

export interface Score {
  exploitabilityBasic?: string,
  consequences?: Consequence[],
  exploitability?: string,
  score?: string,
  scoringType?: string,
  businessImpacts?: string
}

export interface Consequence {
  description: string,
  securityImpacts: SecurityImpact[]
}

export interface SecurityImpact {
  impact: string,
  scope: string,
  technicalImpacts: string[]
}

export enum PrimaryAssetCategoryEnum {
  Data_DataFile_Database = 'Data_DataFile_Database',
  Data_Shared_Office_File = 'Data_Shared_Office_File',
  Data_Personal_Office_File = 'Data_Personal_Office_File',
  Data_Physical_File = 'Data_Physical_File',
  Data_Exchanged_Message = 'Data_Exchanged_Message',
  Data_Digital_Mail = 'Data_Digital_Mail',
  Data_Physical_Mail = 'Data_Physical_Mail',
  Data_Physical_Archive = 'Data_Physical_Archive',
  Data_IT_Archive = 'Data_IT_Archive',
  Data_Published_Data = 'Data_Published_Data',
  Service_User_Workspace = 'Service_User_Workspace',
  Service_Telecommunication_Service = 'Service_Telecommunication_Service',
  Service_Extended_Network_Service = 'Service_Extended_Network_Service',
  Service_Local_Network_Service = 'Service_Local_Network_Service',
  Service_Application_Service = 'Service_Application_Service',
  Service_Shared_Service = 'Service_Shared_Service',
  Service_User_Hardware = 'Service_User_Hardware',
  Service_Common_Service = 'Service_Common_Service',
  Service_Web_editing_Service = 'Service_Web_editing_Service',
  Compliance_Policy_Personal_Information_Protection = 'Compliance_Policy_Personal_Information_Protection',
  Compliance_Policy_Financial_Communication = 'Compliance_Policy_Financial_Communication',
  Compliance_Policy_Digital_Accounting_Control = 'Compliance_Policy_Digital_Accounting_Control',
  Compliance_Policy_Intellectual_Property = 'Compliance_Policy_Intellectual_Property',
  Compliance_Policy_Protection_Of_Information_Systems = 'Compliance_Policy_Protection_Of_Information_Systems',
  Compliance_Policy_People_And_Environment_Safety = 'Compliance_Policy_People_And_Environment_Safety',
}

export enum SecondaryAssetCategoryEnum {
  Data_File = 'Data_File',
  Electronic_Media = 'Electronic_Media',
  Data_Access_Mean = 'Data_Access_Mean',
  Non_Electronic_Media = 'Non_Electronic_Media',
  Data_Message = 'Data_Message',
  Premise = 'Premise',
  Software_Configuration = 'Software_Configuration',
  Hardware_Configuration = 'Hardware_Configuration',
  Hardware = 'Hardware',
  Auxiliary_Equipment = 'Auxiliary_Equipment',
  Software_Off_the_Shelf = 'Software_Off_the_Shelf',
  Software_Custom = 'Software_Custom',
  Service_Access_Mean = 'Service_Access_Mean',
  Policy = 'Policy',
  Personnel = 'Personnel',
  File_Programme = 'File_Programme',
  Communication_Network = 'Communication_Network',
  Firmware = 'Firmware'
}

export const primarySecondaryCategoryMap = {
  [PrimaryAssetCategoryEnum.Data_DataFile_Database]: [
    SecondaryAssetCategoryEnum.Data_File, SecondaryAssetCategoryEnum.Electronic_Media,
    SecondaryAssetCategoryEnum.Data_Access_Mean
  ],
  [PrimaryAssetCategoryEnum.Data_Shared_Office_File]: [
    SecondaryAssetCategoryEnum.Data_File, SecondaryAssetCategoryEnum.Electronic_Media,
    SecondaryAssetCategoryEnum.Data_Access_Mean
  ],
  [PrimaryAssetCategoryEnum.Data_Personal_Office_File]: [
    SecondaryAssetCategoryEnum.Data_Access_Mean,
    SecondaryAssetCategoryEnum.Data_File, SecondaryAssetCategoryEnum.Electronic_Media
  ],
  [PrimaryAssetCategoryEnum.Data_Physical_File]: [SecondaryAssetCategoryEnum.Non_Electronic_Media],
  [PrimaryAssetCategoryEnum.Data_Exchanged_Message]: [SecondaryAssetCategoryEnum.Data_Message],
  [PrimaryAssetCategoryEnum.Data_Digital_Mail]: [SecondaryAssetCategoryEnum.Data_Message, SecondaryAssetCategoryEnum.Data_File],
  [PrimaryAssetCategoryEnum.Data_Physical_Mail]:
    [SecondaryAssetCategoryEnum.Data_Message, SecondaryAssetCategoryEnum.Non_Electronic_Media, SecondaryAssetCategoryEnum.Data_File],
  [PrimaryAssetCategoryEnum.Data_Physical_Archive]: [SecondaryAssetCategoryEnum.Non_Electronic_Media],
  [PrimaryAssetCategoryEnum.Data_IT_Archive]: [
    SecondaryAssetCategoryEnum.Data_File, SecondaryAssetCategoryEnum.Electronic_Media,
    SecondaryAssetCategoryEnum.Data_Access_Mean
  ],
  [PrimaryAssetCategoryEnum.Data_Published_Data]: [SecondaryAssetCategoryEnum.Data_File, SecondaryAssetCategoryEnum.Electronic_Media],
  [PrimaryAssetCategoryEnum.Service_User_Workspace]: [SecondaryAssetCategoryEnum.Premise],
  [PrimaryAssetCategoryEnum.Service_Telecommunication_Service]: [
    SecondaryAssetCategoryEnum.Software_Configuration, SecondaryAssetCategoryEnum.Hardware_Configuration,
    SecondaryAssetCategoryEnum.Hardware, SecondaryAssetCategoryEnum.Auxiliary_Equipment
  ],
  [PrimaryAssetCategoryEnum.Service_Extended_Network_Service]: [
    SecondaryAssetCategoryEnum.Software_Configuration, SecondaryAssetCategoryEnum.Hardware_Configuration,
    SecondaryAssetCategoryEnum.Hardware, SecondaryAssetCategoryEnum.Auxiliary_Equipment
  ],
  [PrimaryAssetCategoryEnum.Service_Local_Network_Service]: [
    SecondaryAssetCategoryEnum.Software_Configuration, SecondaryAssetCategoryEnum.Hardware_Configuration,
    SecondaryAssetCategoryEnum.Hardware, SecondaryAssetCategoryEnum.Auxiliary_Equipment
  ],
  [PrimaryAssetCategoryEnum.Service_Application_Service]: [
    SecondaryAssetCategoryEnum.Software_Configuration, SecondaryAssetCategoryEnum.Hardware_Configuration,
    SecondaryAssetCategoryEnum.Electronic_Media, SecondaryAssetCategoryEnum.Software_Off_the_Shelf,
    SecondaryAssetCategoryEnum.Software_Custom, SecondaryAssetCategoryEnum.Hardware,
    SecondaryAssetCategoryEnum.Auxiliary_Equipment,
    SecondaryAssetCategoryEnum.Service_Access_Mean
  ],
  [PrimaryAssetCategoryEnum.Service_Shared_Service]: [
    SecondaryAssetCategoryEnum.Software_Configuration, SecondaryAssetCategoryEnum.Hardware_Configuration,
    SecondaryAssetCategoryEnum.Electronic_Media, SecondaryAssetCategoryEnum.Software_Off_the_Shelf,
    SecondaryAssetCategoryEnum.Software_Custom, SecondaryAssetCategoryEnum.Hardware,
    SecondaryAssetCategoryEnum.Auxiliary_Equipment,
    SecondaryAssetCategoryEnum.Service_Access_Mean
  ],
  [PrimaryAssetCategoryEnum.Service_User_Hardware]: [
    SecondaryAssetCategoryEnum.Software_Off_the_Shelf, SecondaryAssetCategoryEnum.Software_Custom,
    SecondaryAssetCategoryEnum.Software_Configuration, SecondaryAssetCategoryEnum.Hardware
  ],
  [PrimaryAssetCategoryEnum.Service_Common_Service]: [
    SecondaryAssetCategoryEnum.Software_Configuration, SecondaryAssetCategoryEnum.Hardware_Configuration,
    SecondaryAssetCategoryEnum.Software_Off_the_Shelf, SecondaryAssetCategoryEnum.Software_Custom,
    SecondaryAssetCategoryEnum.Electronic_Media, SecondaryAssetCategoryEnum.Hardware,
    SecondaryAssetCategoryEnum.Auxiliary_Equipment,
    SecondaryAssetCategoryEnum.Service_Access_Mean
  ],
  [PrimaryAssetCategoryEnum.Service_Web_editing_Service]: [
    SecondaryAssetCategoryEnum.Software_Configuration, SecondaryAssetCategoryEnum.Hardware_Configuration,
    SecondaryAssetCategoryEnum.Software_Off_the_Shelf, SecondaryAssetCategoryEnum.Software_Custom,
    SecondaryAssetCategoryEnum.Electronic_Media, SecondaryAssetCategoryEnum.Hardware,
    SecondaryAssetCategoryEnum.Auxiliary_Equipment
  ],
  [PrimaryAssetCategoryEnum.Compliance_Policy_Personal_Information_Protection]: [SecondaryAssetCategoryEnum.Policy],
  [PrimaryAssetCategoryEnum.Compliance_Policy_Financial_Communication]: [SecondaryAssetCategoryEnum.Policy],
  [PrimaryAssetCategoryEnum.Compliance_Policy_Digital_Accounting_Control]: [SecondaryAssetCategoryEnum.Policy],
  [PrimaryAssetCategoryEnum.Compliance_Policy_Intellectual_Property]: [SecondaryAssetCategoryEnum.Policy],
  [PrimaryAssetCategoryEnum.Compliance_Policy_Protection_Of_Information_Systems]: [SecondaryAssetCategoryEnum.Policy],
  [PrimaryAssetCategoryEnum.Compliance_Policy_People_And_Environment_Safety]: [SecondaryAssetCategoryEnum.Policy]

};

export interface Actor {
  catalogue: string,
  catalogueId: string,
  description: string,
  name: string,
}

export interface Event {
  catalogue?: string,
  catalogueId?: string,
  description?: string,
  name: string
}

export interface ThreatScore {
  likelihood: string,
  score: string,
}

export interface Place {
  catalogue: string,
  catalogueId: string,
  description: string,
  name: string
}

export interface Time {
  catalogue: string,
  catalogueId: string,
  description: string,
  name: string
}

export interface Access {
  catalogue: string,
  catalogueId: string,
  description: string,
  name: string
}

export interface Process {
  catalogue: string,
  catalogueId: string,
  description: string,
  name: string
}
