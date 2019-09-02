An opinionated plugin to assist with versioning.

| setting    | description      |
|------------|------------------|
| versionFile | The plugin expects an external file to contain the version. By default it's `VERSION` beside `build.sbt`, you can override it with this setting. |
| isClient | For versioning clients, the version string will be `1.2` and drop the patch version. This is ignored when an upgrade is being performed with a major/minor beta. |
| beforeUpgrade | Expects a function `(currentVersion: String, newVersion: String) => Unit` which is called just before performing the version changes. |
| afterUpgrade | Expects a function `(previousVersion: String, newVersion: String) => Unit` which is called just after performing version changes. |
| replaceVersions | Runs a regex over a file. Expects a `Map[File, (matchRegex: String, replacement: String)]`. You can use the term `VERSION` in your regex to refer to the respective version string. |

## Upgrading a version

In the sbt shell, execute:

`upgradeVersion --increment [patch|minor|major|patch-beta|minor-beta|major-beta] <--update>`

Specifying `--update` will perform the `beforeUpgrade`, `afterUpgrade` and `replaceVersion` steps. Otherwise, it will just output the next version.

# Contributing

Open a pull-request. Testing can be performed through `sbt test` and plugin testing through `sbt scripted`.