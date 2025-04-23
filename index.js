import { loadTemplate } from './src/templateLoader.js';
import { generateReleaseNotes } from './src/generate.js';

console.log("Starting release notes generation...");

const template = await loadTemplate();
const releaseNotes = await generateReleaseNotes(template);

console.log("Release notes generated successfully!\n");
console.log(releaseNotes);
