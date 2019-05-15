#!/usr/bin/env bash
mvn versions:set -DnewVersion=$1-SNAPSHOT
mvn package deploy
