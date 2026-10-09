import { expect, test } from '@playwright/test';
import { registerAndSignIn } from './helpers';

test('a new user can register and sign in', async ({ page }) => {
  await registerAndSignIn(page);
  await page.reload();
  await expect(page.getByRole('link', { name: 'My reports' })).toBeVisible();
});

test('invalid credentials are rejected', async ({ page }) => {
  await page.goto('/login');
  await page.getByLabel('Email').fill(`missing-${crypto.randomUUID()}@example.com`);
  await page.getByLabel('Password').fill('incorrect-password');
  await page.getByRole('button', { name: 'Sign in' }).click();

  await expect(page.getByRole('alert')).toContainText('Invalid email or password');
  await expect(page.getByRole('link', { name: 'Dashboard' })).toHaveCount(0);
});
