

## Chaff-publish

Provides sbt tasks to publish artifacts.

## Usage

1) Add resolver and plugin

```scala
// ./project/build.sbt or ~/.sbt/0.13/plugins/local.sbt
resolvers += Resolver.url(
  "bintray-x7c1-sbt-plugins",
  url("http://dl.bintray.com/x7c1/sbt-plugins"))(Resolver.ivyStylePatterns)

addSbtPlugin("x7c1" % "chaff-publish" % "0.1.0")
```

2) Add definitions to you project

```scala
// ./build.sbt
import x7c1.chaff.publish.PublishLocalSnapshot
lazy val `your-project` = project.settings(PublishLocalSnapshot.definition)
```

or

```scala
// ~/.sbt/0.13/local.sbt
x7c1.chaff.publish.PublishLocalSnapshot.definition
```

3) Run task in sbt-console

```
$ sbt
> your-project/publisLocalSnapshot
```
