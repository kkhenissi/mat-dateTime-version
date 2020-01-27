import { Moment } from 'moment';
import { IOutputFile } from 'app/shared/model/output-file.model';
import { IToolVersion } from 'app/shared/model/tool-version.model';
import { RunStatus } from 'app/shared/model/enumerations/run-status.model';

export interface IRun {
  /** id of run */
  id?: number;
  startDate?: Moment;
  endDate?: Moment;
  status?: RunStatus;
  platformHardwareInfo?: string;
  description?: string;
  outputFiles?: IOutputFile[];
  toolVersions?: IToolVersion[];
  ownerId?: number;
  scenarioId?: number;
}

/**
 * Run implementation of IRun.
 */
export class Run implements IRun {
  /**
   * Run constructor.
   * @param id technical id of Run
   * @param startDate start date
   * @param endDate end date
   * @param status status (INPROGRESS, DONE, FAILLED)
   * @param platformHardwareInfo platform hardware information
   * @param description description
   * @param outputFiles outputFiles generate by Run
   * @param toolVersions version of tools
   * @param ownerId ref on owner
   * @param scenarioId ref on run scenario
   */
  constructor(
    public id?: number,
    public startDate?: Moment,
    public endDate?: Moment,
    public status?: RunStatus,
    public platformHardwareInfo?: string,
    public description?: string,
    public outputFiles?: IOutputFile[],
    public toolVersions?: IToolVersion[],
    public ownerId?: number,
    public scenarioId?: number
  ) {}
}
