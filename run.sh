#!/bin/sh

rm -rf one-vs-rest-model
rm -rf one-vs-rest-results
rm -rf one-vs-rest-train

make clean
make test
