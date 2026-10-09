import { expect, type Page } from '@playwright/test';

type TestAccount = {
  displayName: string;
  email: string;
  password: string;
};

type ReportDetails = {
  itemName: string;
  description: string;
  distinguishingDetails?: string;
};

export async function registerAndSignIn(page: Page): Promise<TestAccount> {
  const suffix = crypto.randomUUID();
  const account = {
    displayName: `Test User ${suffix.slice(0, 8)}`,
    email: `findit-${suffix}@example.com`,
    password: `test-pass-${suffix}`,
  };

  await page.goto('/register');
  await page.getByLabel('Display name').fill(account.displayName);
  await page.getByLabel('Email').fill(account.email);
  await page.getByLabel('Password').fill(account.password);
  await page.getByRole('button', { name: 'Create account' }).click();
  await expect(page).toHaveURL(/\/login\?registered=1$/);

  await page.getByLabel('Email').fill(account.email);
  await page.getByLabel('Password').fill(account.password);
  await page.getByRole('button', { name: 'Sign in' }).click();
  await expect(page.getByRole('link', { name: 'Dashboard' })).toBeVisible();
  await expect(page.getByText(account.displayName, { exact: true })).toBeVisible();
  return account;
}

export async function createReport(page: Page, details: ReportDetails): Promise<string> {
  await page.goto('/reports/new');
  await page.getByLabel('I am reporting').selectOption('LOST');
  await page.getByLabel('Item name').fill(details.itemName);
  await page.getByLabel('Category').fill('Test electronics');
  await page.getByLabel('Description').fill(details.description);
  if (details.distinguishingDetails) {
    await page.getByLabel(/Distinguishing details/).fill(details.distinguishingDetails);
  }
  await page.getByLabel('Campus location').fill('Test library');
  await page.getByRole('button', { name: 'Publish report' }).click();
  await expect(page.getByRole('heading', { name: details.itemName })).toBeVisible();
  return page.url();
}
