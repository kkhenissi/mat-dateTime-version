export interface IUserPreferences {
  id?: number;
  userId?: number;
}

export class UserPreferences implements IUserPreferences {
  constructor(public id?: number, public userId?: number) {}
}
