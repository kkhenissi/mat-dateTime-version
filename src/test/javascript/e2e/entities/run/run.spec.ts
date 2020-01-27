// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { RunComponentsPage, RunDeleteDialog, RunUpdatePage } from './run.page-object';

const expect = chai.expect;

describe('Run e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let runUpdatePage: RunUpdatePage;
  let runComponentsPage: RunComponentsPage;
  let runDeleteDialog: RunDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Runs', async () => {
    await navBarPage.goToEntity('run');
    runComponentsPage = new RunComponentsPage();
    await browser.wait(ec.visibilityOf(runComponentsPage.title), 5000);
    expect(await runComponentsPage.getTitle()).to.eq('Runs');
  });

  it('should load create Run page', async () => {
    await runComponentsPage.clickOnCreateButton();
    runUpdatePage = new RunUpdatePage();
    expect(await runUpdatePage.getPageTitle()).to.eq('Create or edit a Run');
    await runUpdatePage.cancel();
  });

  it('should create and save Runs', async () => {
    const nbButtonsBeforeCreate = await runComponentsPage.countDeleteButtons();

    await runComponentsPage.clickOnCreateButton();
    await promise.all([
      runUpdatePage.setStartDateInput('2000-12-31'),
      runUpdatePage.setEndDateInput('2000-12-31'),
      runUpdatePage.statusSelectLastOption(),
      runUpdatePage.setPlatformHardwareInfoInput('platformHardwareInfo'),
      runUpdatePage.setDescriptionInput('description'),
      runUpdatePage.ownerSelectLastOption(),
      runUpdatePage.scenarioSelectLastOption()
    ]);
    expect(await runUpdatePage.getStartDateInput()).to.eq('2000-12-31', 'Expected startDate value to be equals to 2000-12-31');
    expect(await runUpdatePage.getEndDateInput()).to.eq('2000-12-31', 'Expected endDate value to be equals to 2000-12-31');
    expect(await runUpdatePage.getPlatformHardwareInfoInput()).to.eq(
      'platformHardwareInfo',
      'Expected PlatformHardwareInfo value to be equals to platformHardwareInfo'
    );
    expect(await runUpdatePage.getDescriptionInput()).to.eq('description', 'Expected Description value to be equals to description');
    await runUpdatePage.save();
    expect(await runUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await runComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Run', async () => {
    const nbButtonsBeforeDelete = await runComponentsPage.countDeleteButtons();
    await runComponentsPage.clickOnLastDeleteButton();

    runDeleteDialog = new RunDeleteDialog();
    expect(await runDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Run?');
    await runDeleteDialog.clickOnConfirmButton();

    expect(await runComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
