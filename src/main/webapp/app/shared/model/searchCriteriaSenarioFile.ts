
export interface CritereSearchScenarioFile {
    page?: number,
    size?: number,
    'inputType'?: string,
    'scenariosId'?: number,
    'lTInsertionDate'?:Date,
    'fileType'?: string,
    'ownerId'?:number,
    'configDataSet'?:number,
    'timeOfData'?:Date,
    'name'?: string,
    'format'?: string,
    'subSystemAtOriginOfData'?:string,
    'securityLevel'?: string


    nbElemPerPage?: number;
    pageAAficher?: number;
  }

 