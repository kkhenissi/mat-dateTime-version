import { Moment } from 'moment';
import { EnumSecurityLevel } from 'app/shared/model/enumerations/security-level.model';

export interface IOutputFile {
  id?: number;
  relativePathInST?: string;
  lTInsertionDate?: Moment;
  pathInLT?: string;
  fileType?: string;
  format?: string;
  subSystemAtOriginOfData?: string;
  timeOfData?: Moment;
  securityLevel?: EnumSecurityLevel;
  crc?: string;
  ownerId?: number;
  runId?: number;
  jobId?: number;
}

export class OutputFile implements IOutputFile {
  constructor(
    public id?: number,
    public relativePathInST?: string,
    public lTInsertionDate?: Moment,
    public pathInLT?: string,
    public fileType?: string,
    public format?: string,
    public subSystemAtOriginOfData?: string,
    public timeOfData?: Moment,
    public securityLevel?: EnumSecurityLevel,
    public crc?: string,
    public ownerId?: number,
    public runId?: number,
    public jobId?: number
  ) {}
}
