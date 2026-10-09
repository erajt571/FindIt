import { expect, test } from '@playwright/test';
import { createReport, registerAndSignIn } from './helpers';

test('a published report remains searchable after navigation and reload', async ({ page }) => {
  await registerAndSignIn(page);
  const itemName = `Campus keys ${crypto.randomUUID()}`;
  const description = `Found near the east entrance ${crypto.randomUUID()}`;

  await createReport(page, { itemName, description });
  await page.getByRole('link', { name: 'Browse', exact: true }).click();
  await page.getByRole('textbox', { name: 'Search' }).fill(itemName);
  await page.getByRole('button', { name: 'Apply filters' }).click();

  await expect(page.getByRole('heading', { name: itemName })).toBeVisible();
  await expect(page.getByText(description)).toBeVisible();
  await page.reload();
  await expect(page.getByRole('heading', { name: itemName })).toBeVisible();
});
