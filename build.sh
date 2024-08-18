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
  ./gradlew bundleRelease
}

# Main script logic
cd ~/projects/venom-android/ || exit;

# Increment version code
increment_version_code

# Build the app bundle
build_bundle

open ./app/build/outputs/bundle/release/;