#!/bin/bash

# Function to increment versionCode in build.gradle.kts
increment_version_code() {
  file="./app/build.gradle.kts"
  temp_file="temp.txt"

  awk -v file="$file" '{
    if ($0 ~ /versionCode =/) {
      split($0, fields, /=/)
      fields[2] += 1
      $0 = fields[1] "= " fields[2]
    }
    print $0
  }' "$file" > "$temp_file" && mv "$temp_file" "$file"
}

# Function to build the Android app bundle
build_bundle() {
  PROJECT_PATH=~/projects/venom-android/;

  # Build the app bundle
  ./gradlew bundleRelease

  # Path to your keystore
  KEYSTORE_PATH=/Users/mitchstark/venomkeystore

  # Key alias
  KEY_ALIAS=venomkeystore

  # Path to the unsigned bundle
  UNSIGNED_BUNDLE=$PROJECT_PATH/app/build/outputs/bundle/release/app-release.aab

  # Sign the app bundle
  jarsigner -verbose -keystore $KEYSTORE_PATH -storepass $KEY_PASSWORD -keypass $KEY_PASSWORD $UNSIGNED_BUNDLE $KEY_ALIAS
}

if [[ -v KEY_PASSWORD ]]; then
  echo "KEY_PASSWORD is set. Proceeding..."
else
  echo "KEY_PASSWORD is not set"
  exit 1
fi

# Main script logic
cd ~/projects/venom-android/ || exit;

# Increment version code
increment_version_code

# Build the app bundle
build_bundle

open ./app/build/outputs/bundle/release/;