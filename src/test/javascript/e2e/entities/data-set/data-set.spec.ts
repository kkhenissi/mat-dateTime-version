// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DataSetComponentsPage, DataSetDeleteDialog, DataSetUpdatePage } from './data-set.page-object';

const expect = chai.expect;

describe('DataSet e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let dataSetUpdatePage: DataSetUpdatePage;
  let dataSetComponentsPage: DataSetComponentsPage;
  let dataSetDeleteDialog: DataSetDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load DataSets', async () => {
    await navBarPage.goToEntity('data-set');
    dataSetComponentsPage = new DataSetComponentsPage();
    await browser.wait(ec.visibilityOf(dataSetComponentsPage.title), 5000);
    expect(await dataSetComponentsPage.getTitle()).to.eq('Data Sets');
  });

  it('should load create DataSet page', async () => {
    await dataSetComponentsPage.clickOnCreateButton();
    dataSetUpdatePage = new DataSetUpdatePage();
    expect(await dataSetUpdatePage.getPageTitle()).to.eq('Create or edit a Data Set');
    await dataSetUpdatePage.cancel();
  });

  it('should create and save DataSets', async () => {
    const nbButtonsBeforeCreate = await dataSetComponentsPage.countDeleteButtons();

    await dataSetComponentsPage.clickOnCreateButton();
    await promise.all([dataSetUpdatePage.setNameInput('name')]);
    expect(await dataSetUpdatePage.getNameInput()).to.eq('name', 'Expected Name value to be equals to name');
    await dataSetUpdatePage.save();
    expect(await dataSetUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await dataSetComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last DataSet', async () => {
    const nbButtonsBeforeDelete = await dataSetComponentsPage.countDeleteButtons();
    await dataSetComponentsPage.clickOnLastDeleteButton();

    dataSetDeleteDialog = new DataSetDeleteDialog();
    expect(await dataSetDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Data Set?');
    await dataSetDeleteDialog.clickOnConfirmButton();

    expect(await dataSetComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
