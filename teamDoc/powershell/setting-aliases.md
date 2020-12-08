**Output your profile**
```
$profile
```

**Check if profile already created**
```
test-path $profile
```

**If not, create one**
```
new-item -path $profile -itemtype file -force
```

**Edit profile**
```
notepad $profile
```

**Add new aliases via command line**
```
"`nNew-Alias which get-command" | add-content $profile
"`nNew-Alias k kubectl" | add-content $profile
```

**To use editor and be able to save, powershell must be run as windows admin.**
Currently cannot be done through windows terminal, instead right-click, then "Run as Administrator"  
*reference: bash -c switch -> https://www.panix.com/~elflord/unix/bash.html*  
```
new-alias vi MyVi
function MyVi { bash -c vi $args }

new-alias vi MyNano
function MyNano { bash -c nano $args }
```

**Reload profile (like `source .bash_profile`)**
```
. $profile
```

**List aliases**
```
get-alias ??
```
