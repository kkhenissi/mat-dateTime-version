import { element, by, ElementFinder } from 'protractor';

export class RunComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-run div table .btn-danger'));
  title = element.all(by.css('jhi-run div h2#page-heading span')).first();

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

export class RunUpdatePage {
  pageTitle = element(by.id('jhi-run-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  startDateInput = element(by.id('field_startDate'));
  endDateInput = element(by.id('field_endDate'));
  statusSelect = element(by.id('field_status'));
  platformHardwareInfoInput = element(by.id('field_platformHardwareInfo'));
  descriptionInput = element(by.id('field_description'));
  ownerSelect = element(by.id('field_owner'));
  scenarioSelect = element(by.id('field_scenario'));

  async getPageTitle() {
    return this.pageTitle.getText();
  }

  async setStartDateInput(startDate) {
    await this.startDateInput.sendKeys(startDate);
  }

  async getStartDateInput() {
    return await this.startDateInput.getAttribute('value');
  }

  async setEndDateInput(endDate) {
    await this.endDateInput.sendKeys(endDate);
  }

  async getEndDateInput() {
    return await this.endDateInput.getAttribute('value');
  }

  async setStatusSelect(status) {
    await this.statusSelect.sendKeys(status);
  }

  async getStatusSelect() {
    return await this.statusSelect.element(by.css('option:checked')).getText();
  }

  async statusSelectLastOption(timeout?: number) {
    await this.statusSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setPlatformHardwareInfoInput(platformHardwareInfo) {
    await this.platformHardwareInfoInput.sendKeys(platformHardwareInfo);
  }

  async getPlatformHardwareInfoInput() {
    return await this.platformHardwareInfoInput.getAttribute('value');
  }

  async setDescriptionInput(description) {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput() {
    return await this.descriptionInput.getAttribute('value');
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

  async scenarioSelectLastOption(timeout?: number) {
    await this.scenarioSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async scenarioSelectOption(option) {
    await this.scenarioSelect.sendKeys(option);
  }

  getScenarioSelect(): ElementFinder {
    return this.scenarioSelect;
  }

  async getScenarioSelectedOption() {
    return await this.scenarioSelect.element(by.css('option:checked')).getText();
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

export class RunDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-run-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-run'));

  async getDialogTitle() {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
