import { Moment } from 'moment';
import { IScenario } from 'app/shared/model/scenario.model';
import { EnumSecurityLevel } from 'app/shared/model/enumerations/security-level.model';

export interface IScenarioFile {
  id?: number;
  fileType?: string;
  relativePathInST?: string;
  lTInsertionDate?: Moment;
  pathInLT?: string;
  format?: string;
  subSystemAtOriginOfData?: string;
  timeOfData?: Moment;
  securityLevel?: EnumSecurityLevel;
  crc?: string;
  ownerId?: number;
  scenarios?: IScenario[];
  jobId?: number;
  datasetId?: number;
  configDatasetId?: number;
}

export class ScenarioFile implements IScenarioFile {
  constructor(
    public id?: number,
    public fileType?: string,
    public relativePathInST?: string,
    public lTInsertionDate?: Moment,
    public pathInLT?: string,
    public format?: string,
    public subSystemAtOriginOfData?: string,
    public timeOfData?: Moment,
    public securityLevel?: EnumSecurityLevel,
    public crc?: string,
    public ownerId?: number,
    public scenarios?: IScenario[],
    public jobId?: number,
    public datasetId?: number,
    public configDatasetId?: number
  ) {}
}
