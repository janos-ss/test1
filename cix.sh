set -euo pipefail

mvn package test -P$TEST
