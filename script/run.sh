#!/bin/sh

lein run && \
    chmod +x script/download.sh && \
    ./script/download.sh
