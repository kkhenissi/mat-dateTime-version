// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { OutputFileComponentsPage, OutputFileDeleteDialog, OutputFileUpdatePage } from './output-file.page-object';

const expect = chai.expect;

describe('OutputFile e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let outputFileUpdatePage: OutputFileUpdatePage;
  let outputFileComponentsPage: OutputFileComponentsPage;
  let outputFileDeleteDialog: OutputFileDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load OutputFiles', async () => {
    await navBarPage.goToEntity('output-file');
    outputFileComponentsPage = new OutputFileComponentsPage();
    await browser.wait(ec.visibilityOf(outputFileComponentsPage.title), 5000);
    expect(await outputFileComponentsPage.getTitle()).to.eq('Output Files');
  });

  it('should load create OutputFile page', async () => {
    await outputFileComponentsPage.clickOnCreateButton();
    outputFileUpdatePage = new OutputFileUpdatePage();
    expect(await outputFileUpdatePage.getPageTitle()).to.eq('Create or edit a Output File');
    await outputFileUpdatePage.cancel();
  });

  it('should create and save OutputFiles', async () => {
    const nbButtonsBeforeCreate = await outputFileComponentsPage.countDeleteButtons();

    await outputFileComponentsPage.clickOnCreateButton();
    await promise.all([
      outputFileUpdatePage.setRelativePathInSTInput('relativePathInST'),
      outputFileUpdatePage.setLTInsertionDateInput('2000-12-31'),
      outputFileUpdatePage.setPathInLTInput('pathInLT'),
      outputFileUpdatePage.setFileTypeInput('fileType'),
      outputFileUpdatePage.setFormatInput('format'),
      outputFileUpdatePage.setSubSystemAtOriginOfDataInput('subSystemAtOriginOfData'),
      outputFileUpdatePage.setTimeOfDataInput('2000-12-31'),
      outputFileUpdatePage.securityLevelSelectLastOption(),
      outputFileUpdatePage.setCrcInput('crc'),
      outputFileUpdatePage.ownerSelectLastOption(),
      outputFileUpdatePage.runSelectLastOption(),
      outputFileUpdatePage.jobSelectLastOption()
    ]);
    expect(await outputFileUpdatePage.getRelativePathInSTInput()).to.eq(
      'relativePathInST',
      'Expected RelativePathInST value to be equals to relativePathInST'
    );
    expect(await outputFileUpdatePage.getLTInsertionDateInput()).to.eq(
      '2000-12-31',
      'Expected lTInsertionDate value to be equals to 2000-12-31'
    );
    expect(await outputFileUpdatePage.getPathInLTInput()).to.eq('pathInLT', 'Expected PathInLT value to be equals to pathInLT');
    expect(await outputFileUpdatePage.getFileTypeInput()).to.eq('fileType', 'Expected FileType value to be equals to fileType');
    expect(await outputFileUpdatePage.getFormatInput()).to.eq('format', 'Expected Format value to be equals to format');
    expect(await outputFileUpdatePage.getSubSystemAtOriginOfDataInput()).to.eq(
      'subSystemAtOriginOfData',
      'Expected SubSystemAtOriginOfData value to be equals to subSystemAtOriginOfData'
    );
    expect(await outputFileUpdatePage.getTimeOfDataInput()).to.eq('2000-12-31', 'Expected timeOfData value to be equals to 2000-12-31');
    expect(await outputFileUpdatePage.getCrcInput()).to.eq('crc', 'Expected Crc value to be equals to crc');
    await outputFileUpdatePage.save();
    expect(await outputFileUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await outputFileComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last OutputFile', async () => {
    const nbButtonsBeforeDelete = await outputFileComponentsPage.countDeleteButtons();
    await outputFileComponentsPage.clickOnLastDeleteButton();

    outputFileDeleteDialog = new OutputFileDeleteDialog();
    expect(await outputFileDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Output File?');
    await outputFileDeleteDialog.clickOnConfirmButton();

    expect(await outputFileComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
