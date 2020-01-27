import { IScenarioFile } from 'app/shared/model/scenario-file.model';

export interface IConfigDataSet {
  id?: number;
  name?: string;
  configFiles?: IScenarioFile[];
}

export class ConfigDataSet implements IConfigDataSet {
  constructor(public id?: number, public name?: string, public configFiles?: IScenarioFile[]) {}
}
