# My personal SVN cheat sheet

## Virus scanner broke DB

Source error:

```
svn: E155009: Failed to run the WC DB work queue associated with 'F:\Devel\bc\dev\trunk\appShare\media\frontend\?_12x15.png', work item 53314 (file-install appShare/media/frontend/?_12x15.png 1 0 1 1)
svn: E720123: Can't move 'F:\Devel\bc\dev\trunk\.svn\tmp\svn-68A36D23' to 'F:\Devel\bc\dev\trunk\appShare\media\frontend\?_12x15.png': The filename, directory name, or volume label syntax is incorrect.
```

Solution:

```
cd {work-dir-base}
sqlite3 .svn/wc.db "delete from work_queue"
```
