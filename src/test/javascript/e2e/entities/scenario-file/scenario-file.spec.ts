// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { ScenarioFileComponentsPage, ScenarioFileDeleteDialog, ScenarioFileUpdatePage } from './scenario-file.page-object';

const expect = chai.expect;

describe('ScenarioFile e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let scenarioFileUpdatePage: ScenarioFileUpdatePage;
  let scenarioFileComponentsPage: ScenarioFileComponentsPage;
  let scenarioFileDeleteDialog: ScenarioFileDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load ScenarioFiles', async () => {
    await navBarPage.goToEntity('scenario-file');
    scenarioFileComponentsPage = new ScenarioFileComponentsPage();
    await browser.wait(ec.visibilityOf(scenarioFileComponentsPage.title), 5000);
    expect(await scenarioFileComponentsPage.getTitle()).to.eq('Scenario Files');
  });

  it('should load create ScenarioFile page', async () => {
    await scenarioFileComponentsPage.clickOnCreateButton();
    scenarioFileUpdatePage = new ScenarioFileUpdatePage();
    expect(await scenarioFileUpdatePage.getPageTitle()).to.eq('Create or edit a Scenario File');
    await scenarioFileUpdatePage.cancel();
  });

  it('should create and save ScenarioFiles', async () => {
    const nbButtonsBeforeCreate = await scenarioFileComponentsPage.countDeleteButtons();

    await scenarioFileComponentsPage.clickOnCreateButton();
    await promise.all([
      scenarioFileUpdatePage.setFileTypeInput('fileType'),
      scenarioFileUpdatePage.setRelativePathInSTInput('relativePathInST'),
      scenarioFileUpdatePage.setLTInsertionDateInput('2000-12-31'),
      scenarioFileUpdatePage.setPathInLTInput('pathInLT'),
      scenarioFileUpdatePage.setFormatInput('format'),
      scenarioFileUpdatePage.setSubSystemAtOriginOfDataInput('subSystemAtOriginOfData'),
      scenarioFileUpdatePage.setTimeOfDataInput('2000-12-31'),
      scenarioFileUpdatePage.securityLevelSelectLastOption(),
      scenarioFileUpdatePage.setCrcInput('crc'),
      scenarioFileUpdatePage.ownerSelectLastOption(),
      // scenarioFileUpdatePage.scenariosSelectLastOption(),
      scenarioFileUpdatePage.jobSelectLastOption(),
      scenarioFileUpdatePage.datasetSelectLastOption(),
      scenarioFileUpdatePage.configDatasetSelectLastOption()
    ]);
    expect(await scenarioFileUpdatePage.getFileTypeInput()).to.eq('fileType', 'Expected FileType value to be equals to fileType');
    expect(await scenarioFileUpdatePage.getRelativePathInSTInput()).to.eq(
      'relativePathInST',
      'Expected RelativePathInST value to be equals to relativePathInST'
    );
    expect(await scenarioFileUpdatePage.getLTInsertionDateInput()).to.eq(
      '2000-12-31',
      'Expected lTInsertionDate value to be equals to 2000-12-31'
    );
    expect(await scenarioFileUpdatePage.getPathInLTInput()).to.eq('pathInLT', 'Expected PathInLT value to be equals to pathInLT');
    expect(await scenarioFileUpdatePage.getFormatInput()).to.eq('format', 'Expected Format value to be equals to format');
    expect(await scenarioFileUpdatePage.getSubSystemAtOriginOfDataInput()).to.eq(
      'subSystemAtOriginOfData',
      'Expected SubSystemAtOriginOfData value to be equals to subSystemAtOriginOfData'
    );
    expect(await scenarioFileUpdatePage.getTimeOfDataInput()).to.eq('2000-12-31', 'Expected timeOfData value to be equals to 2000-12-31');
    expect(await scenarioFileUpdatePage.getCrcInput()).to.eq('crc', 'Expected Crc value to be equals to crc');
    await scenarioFileUpdatePage.save();
    expect(await scenarioFileUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await scenarioFileComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last ScenarioFile', async () => {
    const nbButtonsBeforeDelete = await scenarioFileComponentsPage.countDeleteButtons();
    await scenarioFileComponentsPage.clickOnLastDeleteButton();

    scenarioFileDeleteDialog = new ScenarioFileDeleteDialog();
    expect(await scenarioFileDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Scenario File?');
    await scenarioFileDeleteDialog.clickOnConfirmButton();

    expect(await scenarioFileComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
