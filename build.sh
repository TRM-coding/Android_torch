#!/usr/bin/env bash
set -e

# Create module directory structure
mkdir -p app/src/main/java/com/example/helloworld \
         app/src/main/res/layout \
         app/src/main/res/values

# Create empty files
touch app/build.gradle \
      app/src/main/AndroidManifest.xml \
      app/src/main/java/com/example/helloworld/MainActivity.java \
      app/src/main/res/layout/activity_main.xml \
      app/src/main/res/values/strings.xml

echo "Android app module structure created under ./app"

