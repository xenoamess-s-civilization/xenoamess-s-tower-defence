#!/bin/bash
# Run Tower Defence game
# This script must be run from the project root directory

if [ ! -f "settings/TowerDefenceSettings.x8l" ]; then
    echo "Error: settings/TowerDefenceSettings.x8l not found!"
    echo "Please run this script from the project root directory."
    exit 1
fi

echo "Running Tower Defence..."
cd src/xenoamess-s-tower-defence || exit 1
exec ../../mvnw exec:java \
    -Dexec.mainClass="com.xenoamess.cyan_potion.towerdefence.TowerDefenceGame" \
    -Dexec.workingDirectory="$(pwd)/../.." \
    -q
