#!/bin/sh

# script/support: Launch supporting services for the application.

set -e
cd "$(dirname "$0")/.."
overmind start -f Procfile.support -s .overmind.support.sock
