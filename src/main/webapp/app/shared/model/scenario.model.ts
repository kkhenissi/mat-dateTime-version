import { Moment } from 'moment';
import { IRun } from 'app/shared/model/run.model';
import { IScenarioFile } from 'app/shared/model/scenario-file.model';
 
import moment = require('moment');
import { SimulationModeType } from './enumerations/simulation-mode-type.model';

export interface IScenario {
  id?: number;
  name?: string;
  creationDate?: Moment;
  simulation?: SimulationModeType;
  startSimulatedDate?: string;
  endSimulatedDate?: string;
  startSimulatedTime?: string;
  endSimulatedTime?: string;
  description?: string;
  runs?: IRun[];
  ownerId?: number;
  scenarioFiles?: IScenarioFile[];
}

export class Scenario implements IScenario {
  constructor(
    public id?: number,
    public name?: string,
    public creationDate?: Moment,
    public simulation?: SimulationModeType,
    public startSimulatedDate?: string,
    public endSimulatedDate?: string,
    public startSimulatedTime?: string,
    public endSimulatedTime?: string,
    public description?: string,
    public runs?: IRun[],
    public ownerId?: number,
    public scenarioFiles?: IScenarioFile[]
  ) {}
}
