FROM clojure:lein-2.7.1-alpine

RUN apk update && \
    apk add ffmpeg

COPY . /usr/src/palpable/
WORKDIR /usr/src/palpable/

CMD ["./script/run.sh"]
