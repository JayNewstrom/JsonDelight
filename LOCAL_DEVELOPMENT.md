Local Development
=================
This project uses the new composite gradle build feature, which is not yet supported by intellij.
In order to make changes, it's easiest if you open up the `plugin` directory and the `tests` directory separately in Android Studio.
Testing local changes can be done using `./gradlew test` from the root of the repository.

Test Local Changes
------------------
This will build your local changes for the plugin, and run the integration tests.

1. `./gradlew test`

Publish to mavenLocal
---------------------

1. Update `ARTIFACT_VERSION` in `plugin/gradle.properties`
2. Update `ARTIFACT_VERSION` in `tests/gradle.properties`
3. `cd plugin`
4. `./gradlew install`

Releasing to bintray
--------------------

1. `git checkout master && git pull`
2. Remove `SNAPSHOT` from `ARTIFACT_VERSION` in `plugin/gradle.properties`
3. Remove `SNAPSHOT` from `ARTIFACT_VERSION` in `tests/gradle.properties`
4. Update versions in `README.md`
5. `rm -rf ~/.m2/repositories/com/jaynewstrom/json/*`
6. `cd plugin`
7. `./gradlew clean bintrayUpload`
8. Publish the artifacts from the bintray website
9. `cd ..`
10. `git add . && git commit -m "Release version x.y.z"`
11. `git tag -a release-x.y.z -m "Release version x.y.z"`
12. Increment the version, and add `SNAPSHOT` to `ARTIFACT_VERSION` in `plugin/gradle.properties`
13. Increment the version, and add `SNAPSHOT` to `ARTIFACT_VERSION` in `tests/gradle.properties`
14. `git add . && git commit -m "Prepare for next development iteration"`
15. `git push && git push --tags`
