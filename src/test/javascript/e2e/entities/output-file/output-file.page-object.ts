import { element, by, ElementFinder } from 'protractor';

export class OutputFileComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-output-file div table .btn-danger'));
  title = element.all(by.css('jhi-output-file div h2#page-heading span')).first();

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

export class OutputFileUpdatePage {
  pageTitle = element(by.id('jhi-output-file-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  relativePathInSTInput = element(by.id('field_relativePathInST'));
  lTInsertionDateInput = element(by.id('field_lTInsertionDate'));
  pathInLTInput = element(by.id('field_pathInLT'));
  fileTypeInput = element(by.id('field_fileType'));
  formatInput = element(by.id('field_format'));
  subSystemAtOriginOfDataInput = element(by.id('field_subSystemAtOriginOfData'));
  timeOfDataInput = element(by.id('field_timeOfData'));
  securityLevelSelect = element(by.id('field_securityLevel'));
  crcInput = element(by.id('field_crc'));
  ownerSelect = element(by.id('field_owner'));
  runSelect = element(by.id('field_run'));
  jobSelect = element(by.id('field_job'));

  async getPageTitle() {
    return this.pageTitle.getText();
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

  async setFileTypeInput(fileType) {
    await this.fileTypeInput.sendKeys(fileType);
  }

  async getFileTypeInput() {
    return await this.fileTypeInput.getAttribute('value');
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

  async runSelectLastOption(timeout?: number) {
    await this.runSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async runSelectOption(option) {
    await this.runSelect.sendKeys(option);
  }

  getRunSelect(): ElementFinder {
    return this.runSelect;
  }

  async getRunSelectedOption() {
    return await this.runSelect.element(by.css('option:checked')).getText();
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

export class OutputFileDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-outputFile-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-outputFile'));

  async getDialogTitle() {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
