// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { ConfigDataSetComponentsPage, ConfigDataSetDeleteDialog, ConfigDataSetUpdatePage } from './config-data-set.page-object';

const expect = chai.expect;

describe('ConfigDataSet e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let configDataSetUpdatePage: ConfigDataSetUpdatePage;
  let configDataSetComponentsPage: ConfigDataSetComponentsPage;
  let configDataSetDeleteDialog: ConfigDataSetDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load ConfigDataSets', async () => {
    await navBarPage.goToEntity('config-data-set');
    configDataSetComponentsPage = new ConfigDataSetComponentsPage();
    await browser.wait(ec.visibilityOf(configDataSetComponentsPage.title), 5000);
    expect(await configDataSetComponentsPage.getTitle()).to.eq('Config Data Sets');
  });

  it('should load create ConfigDataSet page', async () => {
    await configDataSetComponentsPage.clickOnCreateButton();
    configDataSetUpdatePage = new ConfigDataSetUpdatePage();
    expect(await configDataSetUpdatePage.getPageTitle()).to.eq('Create or edit a Config Data Set');
    await configDataSetUpdatePage.cancel();
  });

  it('should create and save ConfigDataSets', async () => {
    const nbButtonsBeforeCreate = await configDataSetComponentsPage.countDeleteButtons();

    await configDataSetComponentsPage.clickOnCreateButton();
    await promise.all([configDataSetUpdatePage.setNameInput('name')]);
    expect(await configDataSetUpdatePage.getNameInput()).to.eq('name', 'Expected Name value to be equals to name');
    await configDataSetUpdatePage.save();
    expect(await configDataSetUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await configDataSetComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last ConfigDataSet', async () => {
    const nbButtonsBeforeDelete = await configDataSetComponentsPage.countDeleteButtons();
    await configDataSetComponentsPage.clickOnLastDeleteButton();

    configDataSetDeleteDialog = new ConfigDataSetDeleteDialog();
    expect(await configDataSetDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Config Data Set?');
    await configDataSetDeleteDialog.clickOnConfirmButton();

    expect(await configDataSetComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
