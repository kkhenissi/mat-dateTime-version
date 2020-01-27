import { element, by, ElementFinder } from 'protractor';

export class DataFileComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-data-file div table .btn-danger'));
  title = element.all(by.css('jhi-data-file div h2#page-heading span')).first();

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

export class DataFileUpdatePage {
  pageTitle = element(by.id('jhi-data-file-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  pathInLTInput = element(by.id('field_pathInLT'));
  rawInput = element(by.id('file_raw'));

  async getPageTitle() {
    return this.pageTitle.getText();
  }

  async setPathInLTInput(pathInLT) {
    await this.pathInLTInput.sendKeys(pathInLT);
  }

  async getPathInLTInput() {
    return await this.pathInLTInput.getAttribute('value');
  }

  async setRawInput(raw) {
    await this.rawInput.sendKeys(raw);
  }

  async getRawInput() {
    return await this.rawInput.getAttribute('value');
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

export class DataFileDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-dataFile-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-dataFile'));

  async getDialogTitle() {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
