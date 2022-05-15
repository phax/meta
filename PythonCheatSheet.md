# Memories to myself on Python

## pip

Update **all** outdated PIP packages (eventually needs to be repeated a few times):

```shell
pip list --outdated --format=freeze | grep -v '^\-e' | cut -d = -f 1 | xargs -n1 pip install -U 
```

(`grep -v` = inverted matching)
