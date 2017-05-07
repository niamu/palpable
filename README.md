# palpable

`palpable` is a script which downloads all audiobooks from an Audible
account and strips the DRM.

## Pre-requisites

### Activation

Get your `activation-bytes` from Audible using
[audible-activator](https://github.com/inAudible-NG/audible-activator).

Create "resources/config.edn" by copying "resources/sample.config.edn"
and add your `activation-bytes` to the newly created file.

### Library HTML

`palpable` expects to find a file located in "resources/library.html"
with the entire contents of your Audible library located on that page.
It will parse the page and download each audiobook into a folder
with the author's name and the filename as the name of the book.

You can get this file by logging into your Audible account in a
browser and going to your
[My Books](https://www.audible.com/lib?ref_=a_hp_lib_tnaft_1) page
and saving the page as "library.html".

Make sure that all books are visible on the page before saving with no
pagination.

## Usage

Optionally build and run the Docker image which contains `clojure` and
`ffmpeg` as a convenience.

```BASH
docker build -t palpable .;
docker run --rm -it palpable;
```

Or simply execute `script/run.sh` which will generate and execute a
secondary script `script/download.sh`. It will first download all of
the source audiobooks from Audible and then use the `activation-bytes`
with `ffmpeg` to copy the audio track into a new file without the DRM.

Once done, you'll have the original DRM files from Audible available
in "resources/sources/" and the DRM-free variants available in
"resources/clean/".
