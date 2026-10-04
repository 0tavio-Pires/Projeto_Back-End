#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
(cd frontend && npm ci --no-fund && npm test && npm run build)
sh ./mvnw -B -ntp clean verify
printf '%s\n' 'Pronto: target/ProjetoCPTM-1.0.0-SNAPSHOT.jar'
