#!/bin/bash
# Run Tower Defence game from project root

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR/../.."

cd "$PROJECT_ROOT" || exit 1

echo "Running Tower Defence..."
echo "Working directory: $(pwd)"

# Run using Maven exec plugin with correct working directory
cd src/xenoamess-s-tower-defence || exit 1
exec ../../mvnw exec:java \
    -Dexec.mainClass="com.xenoamess.cyan_potion.towerdefence.TowerDefenceGame" \
    -Dexec.workingDirectory="$PROJECT_ROOT" \
    -q
