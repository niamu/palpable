FROM clojure:lein-2.7.1

RUN apt-get update && \
    apt-get install -y ffmpeg mp4v2-utils

COPY . /usr/src/palpable/
WORKDIR /usr/src/palpable/

CMD ["./script/run.sh"]
