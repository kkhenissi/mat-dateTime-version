import { element, by, ElementFinder } from 'protractor';

export class ScenarioComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-scenario div table .btn-danger'));
  title = element.all(by.css('jhi-scenario div h2#page-heading span')).first();

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

export class ScenarioUpdatePage {
  pageTitle = element(by.id('jhi-scenario-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  nameInput = element(by.id('field_name'));
  creationDateInput = element(by.id('field_creationDate'));
  simulationModeSelect = element(by.id('field_simulationMode'));
  startSimulatedDateInput = element(by.id('field_startSimulatedDate'));
  simulationInput = element(by.id('field_simulation'));
  descriptionInput = element(by.id('field_description'));
  ownerSelect = element(by.id('field_owner'));

  async getPageTitle() {
    return this.pageTitle.getText();
  }

  async setNameInput(name) {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput() {
    return await this.nameInput.getAttribute('value');
  }

  async setCreationDateInput(creationDate) {
    await this.creationDateInput.sendKeys(creationDate);
  }

  async getCreationDateInput() {
    return await this.creationDateInput.getAttribute('value');
  }

  async setSimulationModeSelect(simulationMode) {
    await this.simulationModeSelect.sendKeys(simulationMode);
  }

  async getSimulationModeSelect() {
    return await this.simulationModeSelect.element(by.css('option:checked')).getText();
  }

  async simulationModeSelectLastOption(timeout?: number) {
    await this.simulationModeSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setStartSimulatedDateInput(startSimulatedDate) {
    await this.startSimulatedDateInput.sendKeys(startSimulatedDate);
  }

  async getStartSimulatedDateInput() {
    return await this.startSimulatedDateInput.getAttribute('value');
  }

  async setEndSimulatedDateInput(simulation) {
    await this.simulationInput.sendKeys(simulation);
  }

  async getEndSimulatedDateInput() {
    return await this.simulationInput.getAttribute('value');
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

export class ScenarioDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-scenario-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-scenario'));

  async getDialogTitle() {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
