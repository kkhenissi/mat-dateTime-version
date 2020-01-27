import { IOutputFile } from 'app/shared/model/output-file.model';
import { IScenarioFile } from 'app/shared/model/scenario-file.model';
import { Direction } from 'app/shared/model/enumerations/direction.model';
import { TransferStatus } from 'app/shared/model/enumerations/transfer-status.model';

export interface ITransfer {
  id?: number;
  name?: string;
  direction?: Direction;
  status?: TransferStatus;
  outputFiles?: IOutputFile[];
  configFiles?: IScenarioFile[];
  ownerId?: number;
}

export class Transfer implements ITransfer {
  constructor(
    public id?: number,
    public name?: string,
    public direction?: Direction,
    public status?: TransferStatus,
    public outputFiles?: IOutputFile[],
    public configFiles?: IScenarioFile[],
    public ownerId?: number
  ) {}
}
