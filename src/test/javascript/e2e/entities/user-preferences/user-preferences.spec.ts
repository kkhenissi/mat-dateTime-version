// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { UserPreferencesComponentsPage, UserPreferencesDeleteDialog, UserPreferencesUpdatePage } from './user-preferences.page-object';

const expect = chai.expect;

describe('UserPreferences e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let userPreferencesUpdatePage: UserPreferencesUpdatePage;
  let userPreferencesComponentsPage: UserPreferencesComponentsPage;
  let userPreferencesDeleteDialog: UserPreferencesDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load UserPreferences', async () => {
    await navBarPage.goToEntity('user-preferences');
    userPreferencesComponentsPage = new UserPreferencesComponentsPage();
    await browser.wait(ec.visibilityOf(userPreferencesComponentsPage.title), 5000);
    expect(await userPreferencesComponentsPage.getTitle()).to.eq('User Preferences');
  });

  it('should load create UserPreferences page', async () => {
    await userPreferencesComponentsPage.clickOnCreateButton();
    userPreferencesUpdatePage = new UserPreferencesUpdatePage();
    expect(await userPreferencesUpdatePage.getPageTitle()).to.eq('Create or edit a User Preferences');
    await userPreferencesUpdatePage.cancel();
  });

  it('should create and save UserPreferences', async () => {
    const nbButtonsBeforeCreate = await userPreferencesComponentsPage.countDeleteButtons();

    await userPreferencesComponentsPage.clickOnCreateButton();
    await promise.all([userPreferencesUpdatePage.userSelectLastOption()]);
    await userPreferencesUpdatePage.save();
    expect(await userPreferencesUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await userPreferencesComponentsPage.countDeleteButtons()).to.eq(
      nbButtonsBeforeCreate + 1,
      'Expected one more entry in the table'
    );
  });

  it('should delete last UserPreferences', async () => {
    const nbButtonsBeforeDelete = await userPreferencesComponentsPage.countDeleteButtons();
    await userPreferencesComponentsPage.clickOnLastDeleteButton();

    userPreferencesDeleteDialog = new UserPreferencesDeleteDialog();
    expect(await userPreferencesDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this User Preferences?');
    await userPreferencesDeleteDialog.clickOnConfirmButton();

    expect(await userPreferencesComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
