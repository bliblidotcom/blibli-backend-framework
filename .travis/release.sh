#!/usr/bin/env bash

set -e

echo "Ensuring that pom  matches $TRAVIS_TAG"
mvn org.codehaus.mojo:versions-maven-plugin:2.7:set -DnewVersion=$TRAVIS_TAG

echo "Uploading"
mvn deploy --settings .travis/settings.xml -DskipTests=true --batch-mode --update-snapshots -Prelease