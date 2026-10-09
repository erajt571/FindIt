import { defineConfig, devices } from '@playwright/test';
import path from 'node:path';

const backendDirectory = path.resolve(__dirname, '..', 'backend');
const backendCommand = process.platform === 'win32'
  ? '.\\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test'
  : './mvnw spring-boot:run -Dspring-boot.run.profiles=test';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: false,
  workers: 1,
  retries: process.env.CI ? 1 : 0,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    ...devices['Desktop Chrome'],
    baseURL: 'http://127.0.0.1:3000',
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
  },
  webServer: [
    {
      command: backendCommand,
      cwd: backendDirectory,
      url: 'http://127.0.0.1:8080/api/health',
      timeout: 120_000,
      reuseExistingServer: !process.env.CI,
      env: {
        SPRING_PROFILES_ACTIVE: 'test',
        SERVER_PORT: '8080',
      },
    },
    {
      command: 'npm run dev -- --hostname 127.0.0.1',
      url: 'http://127.0.0.1:3000',
      timeout: 120_000,
      reuseExistingServer: !process.env.CI,
      env: {
        FINDIT_BACKEND_URL: 'http://127.0.0.1:8080',
      },
    },
  ],
});
