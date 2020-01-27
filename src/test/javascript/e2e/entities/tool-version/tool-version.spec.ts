// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { ToolVersionComponentsPage, ToolVersionDeleteDialog, ToolVersionUpdatePage } from './tool-version.page-object';

const expect = chai.expect;

describe('ToolVersion e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let toolVersionUpdatePage: ToolVersionUpdatePage;
  let toolVersionComponentsPage: ToolVersionComponentsPage;
  let toolVersionDeleteDialog: ToolVersionDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load ToolVersions', async () => {
    await navBarPage.goToEntity('tool-version');
    toolVersionComponentsPage = new ToolVersionComponentsPage();
    await browser.wait(ec.visibilityOf(toolVersionComponentsPage.title), 5000);
    expect(await toolVersionComponentsPage.getTitle()).to.eq('Tool Versions');
  });

  it('should load create ToolVersion page', async () => {
    await toolVersionComponentsPage.clickOnCreateButton();
    toolVersionUpdatePage = new ToolVersionUpdatePage();
    expect(await toolVersionUpdatePage.getPageTitle()).to.eq('Create or edit a Tool Version');
    await toolVersionUpdatePage.cancel();
  });

  it('should create and save ToolVersions', async () => {
    const nbButtonsBeforeCreate = await toolVersionComponentsPage.countDeleteButtons();

    await toolVersionComponentsPage.clickOnCreateButton();
    await promise.all([
      toolVersionUpdatePage.setNameInput('name'),
      toolVersionUpdatePage.setVersionInput('version'),
      toolVersionUpdatePage.runSelectLastOption()
    ]);
    expect(await toolVersionUpdatePage.getNameInput()).to.eq('name', 'Expected Name value to be equals to name');
    expect(await toolVersionUpdatePage.getVersionInput()).to.eq('version', 'Expected Version value to be equals to version');
    await toolVersionUpdatePage.save();
    expect(await toolVersionUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await toolVersionComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last ToolVersion', async () => {
    const nbButtonsBeforeDelete = await toolVersionComponentsPage.countDeleteButtons();
    await toolVersionComponentsPage.clickOnLastDeleteButton();

    toolVersionDeleteDialog = new ToolVersionDeleteDialog();
    expect(await toolVersionDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Tool Version?');
    await toolVersionDeleteDialog.clickOnConfirmButton();

    expect(await toolVersionComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
