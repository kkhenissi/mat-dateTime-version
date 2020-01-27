import { element, by, ElementFinder } from 'protractor';

export class ScenarioFileComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-scenario-file div table .btn-danger'));
  title = element.all(by.css('jhi-scenario-file div h2#page-heading span')).first();

  async clickOnCreateButton(timeout?: number) {
    await this.createButton.click();
  }

  async clickOnLastDeleteButton(timeout?: number) {
    await this.deleteButtons.last().click();
  }

  async countDeleteButtons() {
    return this.deleteButtons.count();
  }

  async getTitle() {
    return this.title.getText();
  }
}

export class ScenarioFileUpdatePage {
  pageTitle = element(by.id('jhi-scenario-file-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  fileTypeInput = element(by.id('field_fileType'));
  relativePathInSTInput = element(by.id('field_relativePathInST'));
  lTInsertionDateInput = element(by.id('field_lTInsertionDate'));
  pathInLTInput = element(by.id('field_pathInLT'));
  formatInput = element(by.id('field_format'));
  subSystemAtOriginOfDataInput = element(by.id('field_subSystemAtOriginOfData'));
  timeOfDataInput = element(by.id('field_timeOfData'));
  securityLevelSelect = element(by.id('field_securityLevel'));
  crcInput = element(by.id('field_crc'));
  ownerSelect = element(by.id('field_owner'));
  scenariosSelect = element(by.id('field_scenarios'));
  jobSelect = element(by.id('field_job'));
  datasetSelect = element(by.id('field_dataset'));
  configDatasetSelect = element(by.id('field_configDataset'));

  async getPageTitle() {
    return this.pageTitle.getText();
  }

  async setFileTypeInput(fileType) {
    await this.fileTypeInput.sendKeys(fileType);
  }

  async getFileTypeInput() {
    return await this.fileTypeInput.getAttribute('value');
  }

  async setRelativePathInSTInput(relativePathInST) {
    await this.relativePathInSTInput.sendKeys(relativePathInST);
  }

  async getRelativePathInSTInput() {
    return await this.relativePathInSTInput.getAttribute('value');
  }

  async setLTInsertionDateInput(lTInsertionDate) {
    await this.lTInsertionDateInput.sendKeys(lTInsertionDate);
  }

  async getLTInsertionDateInput() {
    return await this.lTInsertionDateInput.getAttribute('value');
  }

  async setPathInLTInput(pathInLT) {
    await this.pathInLTInput.sendKeys(pathInLT);
  }

  async getPathInLTInput() {
    return await this.pathInLTInput.getAttribute('value');
  }

  async setFormatInput(format) {
    await this.formatInput.sendKeys(format);
  }

  async getFormatInput() {
    return await this.formatInput.getAttribute('value');
  }

  async setSubSystemAtOriginOfDataInput(subSystemAtOriginOfData) {
    await this.subSystemAtOriginOfDataInput.sendKeys(subSystemAtOriginOfData);
  }

  async getSubSystemAtOriginOfDataInput() {
    return await this.subSystemAtOriginOfDataInput.getAttribute('value');
  }

  async setTimeOfDataInput(timeOfData) {
    await this.timeOfDataInput.sendKeys(timeOfData);
  }

  async getTimeOfDataInput() {
    return await this.timeOfDataInput.getAttribute('value');
  }

  async setSecurityLevelSelect(securityLevel) {
    await this.securityLevelSelect.sendKeys(securityLevel);
  }

  async getSecurityLevelSelect() {
    return await this.securityLevelSelect.element(by.css('option:checked')).getText();
  }

  async securityLevelSelectLastOption(timeout?: number) {
    await this.securityLevelSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setCrcInput(crc) {
    await this.crcInput.sendKeys(crc);
  }

  async getCrcInput() {
    return await this.crcInput.getAttribute('value');
  }

  async ownerSelectLastOption(timeout?: number) {
    await this.ownerSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async ownerSelectOption(option) {
    await this.ownerSelect.sendKeys(option);
  }

  getOwnerSelect(): ElementFinder {
    return this.ownerSelect;
  }

  async getOwnerSelectedOption() {
    return await this.ownerSelect.element(by.css('option:checked')).getText();
  }

  async scenariosSelectLastOption(timeout?: number) {
    await this.scenariosSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async scenariosSelectOption(option) {
    await this.scenariosSelect.sendKeys(option);
  }

  getScenariosSelect(): ElementFinder {
    return this.scenariosSelect;
  }

  async getScenariosSelectedOption() {
    return await this.scenariosSelect.element(by.css('option:checked')).getText();
  }

  async jobSelectLastOption(timeout?: number) {
    await this.jobSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async jobSelectOption(option) {
    await this.jobSelect.sendKeys(option);
  }

  getJobSelect(): ElementFinder {
    return this.jobSelect;
  }

  async getJobSelectedOption() {
    return await this.jobSelect.element(by.css('option:checked')).getText();
  }

  async datasetSelectLastOption(timeout?: number) {
    await this.datasetSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async datasetSelectOption(option) {
    await this.datasetSelect.sendKeys(option);
  }

  getDatasetSelect(): ElementFinder {
    return this.datasetSelect;
  }

  async getDatasetSelectedOption() {
    return await this.datasetSelect.element(by.css('option:checked')).getText();
  }

  async configDatasetSelectLastOption(timeout?: number) {
    await this.configDatasetSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async configDatasetSelectOption(option) {
    await this.configDatasetSelect.sendKeys(option);
  }

  getConfigDatasetSelect(): ElementFinder {
    return this.configDatasetSelect;
  }

  async getConfigDatasetSelectedOption() {
    return await this.configDatasetSelect.element(by.css('option:checked')).getText();
  }

  async save(timeout?: number) {
    await this.saveButton.click();
  }

  async cancel(timeout?: number) {
    await this.cancelButton.click();
  }

  getSaveButton(): ElementFinder {
    return this.saveButton;
  }
}

export class ScenarioFileDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-scenarioFile-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-scenarioFile'));

  async getDialogTitle() {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
