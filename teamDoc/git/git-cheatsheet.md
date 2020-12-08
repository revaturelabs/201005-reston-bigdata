Intro to GitHub Flow (5 min read): [https://guides.github.com/introduction/flow/](https://guides.github.com/introduction/flow/)


**Useful commands:**

|cmd|desc|
|---|---|
|git add . | add current dir and subdir to *staging area* |
|git add `file/dir` | add specific file/dir to *staging area*|
|git commit -m `msg` | commits current state of *staging area* with commit message `msg`
|git commit -am `msg` | add all **tracked** files and commit them with message `msg`
|git push | send the changes to remote rep|
|git status|check whats going on |
|git log| check what happened before|
|git branch -l | list local branches |
|git branch -r| list remote branches |
|git branch -a| list all branches|
|git checkout `branch-name`| checkout (update your working dir to match state of `branch-name`
|git checkout -b `branch-name` | create a new branch and switch to it with `branch-name` |
|git remote -v | view remote branches |
|git fetch `remote`|fetch all remote branches |
|git fetch `remote-branch`|get specific remote branch|
|git fetch --dry-run | show me what happens but do not execute|
|git push -u origin `your-branch`|pushes your local branch, that does not yet exist, on remote|
---
Add, commit and push your changes:
1. git add .
2. git commit -m "message"
3. git push

or in a directory you are working in and have changed files, but not added new ones:

1. git commit -m "message"
2. git push
---

Pull changes from teammate's branch

1. git branch -r
2. git pull
3. git checkout --track origin/`branch-name`

---
Merging steps:

1. git checkout master
2. git merge <branch-name>
3. Resolve merge conflicts (https://lab.github.com/githubtraining/managing-merge-conflicts)
4. *Optional: delete branch*
5. Commit and push, if necessary.

--- 
List files that have conflicts:

```
git diff --name-only --diff-filter=U
```

---
Pull all branches from remote:
```
git branch -r | grep -v '\->' | while read remote; do git branch --track "${remote#origin/}" "$remote"; done  
git fetch --all  
git pull --all  
```
---

If you forked the repo, and want to update your repo from the changes in the original (aka upstream)

1. **V**iew your remote repositories: 
```
$ git remote -v
origin  git@github.com:username/201005-reston-bigdata.git (fetch)
origin  git@github.com:username/201005-reston-bigdata.git (push)
```
2. Add an upstream repository and check your remotes again:
```
$ git remote add upstream git@github.com:revaturelabs/201005-reston-bigdata.git
$ git remote -v
origin  git@github.com:username/201005-reston-bigdata.git (fetch)
origin  git@github.com:username/201005-reston-bigdata.git (push)
upstream        git@github.com:revaturelabs/201005-reston-bigdata.git (fetch)
upstream        git@github.com:revaturelabs/201005-reston-bigdata.git (push)
```
3. To pull the changes on the upstream repo:
```
// just main branch
$ git pull upstream main
//all branches
$ git pull upstream
```
extra: Delete a remote
```
$ git remote rm name
```
extra: Rename a remote
```
$ git remote rename old-name new-name
```
