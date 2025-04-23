import { readFile } from 'fs/promises';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

export async function loadTemplate() {
  console.log("Loading release notes template...");
  
  const templatePath = path.join(__dirname, '../templates/default.md');
  const template = await readFile(templatePath, 'utf-8');
  return template;
}
