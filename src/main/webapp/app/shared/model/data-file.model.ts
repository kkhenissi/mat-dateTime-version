export interface IDataFile {
  id?: number;
  pathInLT?: string;
  rawContentType?: string;
  raw?: any;
}

export class DataFile implements IDataFile {
  constructor(public id?: number, public pathInLT?: string, public rawContentType?: string, public raw?: any) {}
}
