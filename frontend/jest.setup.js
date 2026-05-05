require('@testing-library/jest-dom');

// Polyfills for jsdom
global.TextEncoder = require('util').TextEncoder;
global.TextDecoder = require('util').TextDecoder;