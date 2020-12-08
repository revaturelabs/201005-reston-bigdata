## Git configuration files

- **git-cheatsheet.md** commonly used git cmds for teams.

#### Subdirectories

/samples/ - contains sample .gitignore and .gitconfig files.

---

**.gitignore** is a file you can place at the project directory level, your home directory or anywhere in between.  It prevents git from adding files that match (wildcard/pattern matching available) files and directories in the .gitignore file.  For a typical scala project, its unecessary to upload most of the /project/ and /target/ directories.  The exceptions would be /project/plugins.sbt and /project/build.properties.  Its also best to exclude the /.idea/ which contains personal settings and may affect the Intellij setup of others who pull from the repository. More information about pattern matching is available [here](https://git-scm.com/docs/gitignore).

**.gitconfig** allows you to add aliases, configuring git settings and identify yourself to get credit for git commits on a site like GitHub.  




