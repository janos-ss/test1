#!/bin/bash

set -euo pipefail

function installTravisTools {
  curl -sSL https://raw.githubusercontent.com/sonarsource/travis-utils/v10/install.sh | bash
  source /tmp/travis-utils/env.sh
}

case "$TESTS" in

CI)
  installTravisTools

  mvn verify -B -e -V
  ;;

IT-DEV)
  installTravisTools

  echo "Not supported yet"
  ;;

IT-LATEST)
  installTravisTools

  echo "Not supported yet"
  ;;

esac
