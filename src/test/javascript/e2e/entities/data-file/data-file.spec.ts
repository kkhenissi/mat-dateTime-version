// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DataFileComponentsPage, DataFileDeleteDialog, DataFileUpdatePage } from './data-file.page-object';
import * as path from 'path';

const expect = chai.expect;

describe('DataFile e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let dataFileUpdatePage: DataFileUpdatePage;
  let dataFileComponentsPage: DataFileComponentsPage;
  let dataFileDeleteDialog: DataFileDeleteDialog;
  const fileNameToUpload = 'logo-jhipster.png';
  const fileToUpload = '../../../../../../src/main/webapp/content/images/' + fileNameToUpload;
  const absolutePath = path.resolve(__dirname, fileToUpload);

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load DataFiles', async () => {
    await navBarPage.goToEntity('data-file');
    dataFileComponentsPage = new DataFileComponentsPage();
    await browser.wait(ec.visibilityOf(dataFileComponentsPage.title), 5000);
    expect(await dataFileComponentsPage.getTitle()).to.eq('Data Files');
  });

  it('should load create DataFile page', async () => {
    await dataFileComponentsPage.clickOnCreateButton();
    dataFileUpdatePage = new DataFileUpdatePage();
    expect(await dataFileUpdatePage.getPageTitle()).to.eq('Create or edit a Data File');
    await dataFileUpdatePage.cancel();
  });

  it('should create and save DataFiles', async () => {
    const nbButtonsBeforeCreate = await dataFileComponentsPage.countDeleteButtons();

    await dataFileComponentsPage.clickOnCreateButton();
    await promise.all([dataFileUpdatePage.setPathInLTInput('pathInLT'), dataFileUpdatePage.setRawInput(absolutePath)]);
    expect(await dataFileUpdatePage.getPathInLTInput()).to.eq('pathInLT', 'Expected PathInLT value to be equals to pathInLT');
    expect(await dataFileUpdatePage.getRawInput()).to.endsWith(fileNameToUpload, 'Expected Raw value to be end with ' + fileNameToUpload);
    await dataFileUpdatePage.save();
    expect(await dataFileUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await dataFileComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last DataFile', async () => {
    const nbButtonsBeforeDelete = await dataFileComponentsPage.countDeleteButtons();
    await dataFileComponentsPage.clickOnLastDeleteButton();

    dataFileDeleteDialog = new DataFileDeleteDialog();
    expect(await dataFileDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Data File?');
    await dataFileDeleteDialog.clickOnConfirmButton();

    expect(await dataFileComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
