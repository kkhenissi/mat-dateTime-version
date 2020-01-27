import { IScenarioFile } from 'app/shared/model/scenario-file.model';

export interface IDataSet {
  id?: number;
  name?: string;
  inputFiles?: IScenarioFile[];
}

export class DataSet implements IDataSet {
  constructor(public id?: number, public name?: string, public inputFiles?: IScenarioFile[]) {}
}
