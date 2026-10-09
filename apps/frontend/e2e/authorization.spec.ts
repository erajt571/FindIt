import { expect, test } from '@playwright/test';
import { createReport, registerAndSignIn } from './helpers';

test('anonymous visitors must sign in before creating reports', async ({ page }) => {
  await page.goto('/reports/new');
  await expect(page.getByText('Sign in to create a report.')).toBeVisible();
  await page.getByRole('link', { name: 'Sign in', exact: true }).click();
  await expect(page.getByRole('heading', { name: 'Sign in to FindIt' })).toBeVisible();
});

test('private ownership details are hidden from public report viewers', async ({ page }) => {
  await registerAndSignIn(page);
  const itemName = `Unclaimed wallet ${crypto.randomUUID()}`;
  const secret = `private-owner-proof-${crypto.randomUUID()}`;
  const reportUrl = await createReport(page, {
    itemName,
    description: 'A wallet found on campus.',
    distinguishingDetails: secret,
  });

  await page.getByRole('button', { name: 'Sign out' }).click();
  await expect(page.getByRole('link', { name: 'Sign in', exact: true })).toBeVisible();
  await page.goto(reportUrl);

  await expect(page.getByRole('heading', { name: itemName })).toBeVisible();
  await expect(page.getByText(secret, { exact: true })).toHaveCount(0);
});
