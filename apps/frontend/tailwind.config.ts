import type { Config } from 'tailwindcss';

const config: Config = {
  content: ['./app/**/*.{ts,tsx}', './components/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#f3f7ff',
          100: '#dfeeff',
          200: '#bfd9ff',
          300: '#8bb8ff',
          400: '#4d8dfd',
          500: '#246bf1',
          600: '#184fc4',
          700: '#1741a1',
          800: '#183a7d',
          900: '#1b315f',
        },
      },
      boxShadow: {
        soft: '0 16px 40px rgba(15, 23, 42, 0.08)',
      },
    },
  },
  plugins: [],
};

export default config;
