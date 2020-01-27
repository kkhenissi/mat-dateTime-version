// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { TransferComponentsPage, TransferDeleteDialog, TransferUpdatePage } from './transfer.page-object';

const expect = chai.expect;

describe('Transfer e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let transferUpdatePage: TransferUpdatePage;
  let transferComponentsPage: TransferComponentsPage;
  let transferDeleteDialog: TransferDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Transfers', async () => {
    await navBarPage.goToEntity('transfer');
    transferComponentsPage = new TransferComponentsPage();
    await browser.wait(ec.visibilityOf(transferComponentsPage.title), 5000);
    expect(await transferComponentsPage.getTitle()).to.eq('Transfers');
  });

  it('should load create Transfer page', async () => {
    await transferComponentsPage.clickOnCreateButton();
    transferUpdatePage = new TransferUpdatePage();
    expect(await transferUpdatePage.getPageTitle()).to.eq('Create or edit a Transfer');
    await transferUpdatePage.cancel();
  });

  it('should create and save Transfers', async () => {
    const nbButtonsBeforeCreate = await transferComponentsPage.countDeleteButtons();

    await transferComponentsPage.clickOnCreateButton();
    await promise.all([
      transferUpdatePage.setNameInput('name'),
      transferUpdatePage.directionSelectLastOption(),
      transferUpdatePage.statusSelectLastOption(),
      transferUpdatePage.ownerSelectLastOption()
    ]);
    expect(await transferUpdatePage.getNameInput()).to.eq('name', 'Expected Name value to be equals to name');
    await transferUpdatePage.save();
    expect(await transferUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await transferComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Transfer', async () => {
    const nbButtonsBeforeDelete = await transferComponentsPage.countDeleteButtons();
    await transferComponentsPage.clickOnLastDeleteButton();

    transferDeleteDialog = new TransferDeleteDialog();
    expect(await transferDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Transfer?');
    await transferDeleteDialog.clickOnConfirmButton();

    expect(await transferComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
