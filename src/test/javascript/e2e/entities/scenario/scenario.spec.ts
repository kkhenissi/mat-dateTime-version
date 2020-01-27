// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { ScenarioComponentsPage, ScenarioDeleteDialog, ScenarioUpdatePage } from './scenario.page-object';

const expect = chai.expect;

describe('Scenario e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let scenarioUpdatePage: ScenarioUpdatePage;
  let scenarioComponentsPage: ScenarioComponentsPage;
  let scenarioDeleteDialog: ScenarioDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Scenarios', async () => {
    await navBarPage.goToEntity('scenario');
    scenarioComponentsPage = new ScenarioComponentsPage();
    await browser.wait(ec.visibilityOf(scenarioComponentsPage.title), 5000);
    expect(await scenarioComponentsPage.getTitle()).to.eq('Scenarios');
  });

  it('should load create Scenario page', async () => {
    await scenarioComponentsPage.clickOnCreateButton();
    scenarioUpdatePage = new ScenarioUpdatePage();
    expect(await scenarioUpdatePage.getPageTitle()).to.eq('Create or edit a Scenario');
    await scenarioUpdatePage.cancel();
  });

  it('should create and save Scenarios', async () => {
    const nbButtonsBeforeCreate = await scenarioComponentsPage.countDeleteButtons();

    await scenarioComponentsPage.clickOnCreateButton();
    await promise.all([
      scenarioUpdatePage.setNameInput('name'),
      scenarioUpdatePage.setCreationDateInput('2000-12-31'),
      scenarioUpdatePage.simulationModeSelectLastOption(),
      scenarioUpdatePage.setStartSimulatedDateInput('2000-12-31'),
      scenarioUpdatePage.setEndSimulatedDateInput('2000-12-31'),
      scenarioUpdatePage.setDescriptionInput('description'),
      scenarioUpdatePage.ownerSelectLastOption()
    ]);
    expect(await scenarioUpdatePage.getNameInput()).to.eq('name', 'Expected Name value to be equals to name');
    expect(await scenarioUpdatePage.getCreationDateInput()).to.eq('2000-12-31', 'Expected creationDate value to be equals to 2000-12-31');
    expect(await scenarioUpdatePage.getStartSimulatedDateInput()).to.eq(
      '2000-12-31',
      'Expected startSimulatedDate value to be equals to 2000-12-31'
    );
    expect(await scenarioUpdatePage.getEndSimulatedDateInput()).to.eq(
      '2000-12-31',
      'Expected simulation value to be equals to 2000-12-31'
    );
    expect(await scenarioUpdatePage.getDescriptionInput()).to.eq('description', 'Expected Description value to be equals to description');
    await scenarioUpdatePage.save();
    expect(await scenarioUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await scenarioComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Scenario', async () => {
    const nbButtonsBeforeDelete = await scenarioComponentsPage.countDeleteButtons();
    await scenarioComponentsPage.clickOnLastDeleteButton();

    scenarioDeleteDialog = new ScenarioDeleteDialog();
    expect(await scenarioDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Scenario?');
    await scenarioDeleteDialog.clickOnConfirmButton();

    expect(await scenarioComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
