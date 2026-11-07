#!/bin/sh
# Maven Wrapper (script only): mengunduh Apache Maven sesuai
# .mvn/wrapper/maven-wrapper.properties lalu menjalankannya.
set -eu

BASE_DIR=$(cd "$(dirname "$0")" && pwd -P)
PROPS="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"

DIST_URL=$(sed -n 's/^distributionUrl=//p' "$PROPS" | tr -d '\r')
if [ -z "$DIST_URL" ]; then
  echo "distributionUrl tidak ditemukan di $PROPS" >&2
  exit 1
fi

NAME=${DIST_URL##*/}
DIR_NAME=${NAME%-bin.tar.gz}
DIR_NAME=${DIR_NAME%-bin.zip}

MAVEN_USER_HOME=${MAVEN_USER_HOME:-${HOME:-/tmp}/.m2}
DISTS="$MAVEN_USER_HOME/wrapper/dists"
MVN_HOME="$DISTS/$DIR_NAME"

if [ ! -x "$MVN_HOME/bin/mvn" ]; then
  echo "Mengunduh Maven: $DIST_URL" >&2
  mkdir -p "$DISTS"
  TMP=$(mktemp -d)
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL -o "$TMP/$NAME" "$DIST_URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$TMP/$NAME" "$DIST_URL"
  else
    echo "curl atau wget diperlukan untuk mengunduh Maven" >&2
    rm -rf "$TMP"
    exit 1
  fi
  case "$NAME" in
    *.zip) unzip -q "$TMP/$NAME" -d "$TMP" ;;
    *)     tar -xzf "$TMP/$NAME" -C "$TMP" ;;
  esac
  rm -rf "$MVN_HOME"
  mv "$TMP/$DIR_NAME" "$MVN_HOME"
  rm -rf "$TMP"
fi

exec "$MVN_HOME/bin/mvn" "$@"
