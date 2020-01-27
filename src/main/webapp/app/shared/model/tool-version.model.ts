export interface IToolVersion {
  id?: number;
  name?: string;
  version?: string;
  runId?: number;
}

export class ToolVersion implements IToolVersion {
  constructor(public id?: number, public name?: string, public version?: string, public runId?: number) {}
}
