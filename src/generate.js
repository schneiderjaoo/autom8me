import commits from '../commits/commits.js';

export async function generateReleaseNotes(template) {
  console.log("Generating release notes...");

  const version = "v1.2.3";
  const date = new Date().toISOString().split('T')[0];

  const commitsText = commits.map(commit => 
    `- ${commit.message} `
  ).join('\n');

  return template
    .replace("{{version}}", version)
    .replace("{{date}}", date)
    .replace("{{commits}}", commitsText);
}
