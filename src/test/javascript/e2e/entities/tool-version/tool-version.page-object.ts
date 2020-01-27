import { element, by, ElementFinder } from 'protractor';

export class ToolVersionComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-tool-version div table .btn-danger'));
  title = element.all(by.css('jhi-tool-version div h2#page-heading span')).first();

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

export class ToolVersionUpdatePage {
  pageTitle = element(by.id('jhi-tool-version-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  nameInput = element(by.id('field_name'));
  versionInput = element(by.id('field_version'));
  runSelect = element(by.id('field_run'));

  async getPageTitle() {
    return this.pageTitle.getText();
  }

  async setNameInput(name) {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput() {
    return await this.nameInput.getAttribute('value');
  }

  async setVersionInput(version) {
    await this.versionInput.sendKeys(version);
  }

  async getVersionInput() {
    return await this.versionInput.getAttribute('value');
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

export class ToolVersionDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-toolVersion-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-toolVersion'));

  async getDialogTitle() {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
