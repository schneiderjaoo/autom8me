# Description

## GitHub
 
    I started by creating a repository on GitHub. I didn't add a README.md file during creation — just a simple repo setup. After that, I opened Visual Studio Code and used the terminal to continue working with Git.

## Git

    In Visual Studio Code, I opened the terminal by pressing ` Control + Shift + ` ` on macOS. The first command I used was:

```bash
    git init
```

    This command initializes a Git repository in the project folder.

    Then, I created a README.md file and wrote "Hello, world!" as a good luck message for the project. Next, I added the files to the staging area:

```bash
    git add .
```

    I used . to select all the files and changes in the project.

```bash
    git branch -m main
```

    This command renames the current branch to main, which will be the primary branch of the project.

```bash
    git remote add origin <url>
```

    This command links your local Git project to the remote GitHub repository using the URL.

```bash
    git push -u origin main
```

    Finally, this command pushes the local commits to the remote repository on GitHub, setting origin main as the default upstream branch.
