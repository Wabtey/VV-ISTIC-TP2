# Command to run

(for archlinux: `xorg-xhost`)

```text
docker build -t pmd-designer-javafx:7.7.0 ./pmd-designer-docker/
xhost +local:docker
docker run -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix pmd-designer-javafx:7.7.0
```

For windows :

```text
docker run -e DISPLAY=host.docker.internal:0 pmd-designer-javafx:7.7.0
```
