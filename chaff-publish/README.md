

## chaff-publish

Provides sbt tasks to publish artifacts.

## Usage

at `~/.sbt/0.13/plugins/local.sbt`

```scala
resolvers += Resolver.url(
  "bintray-x7c1-sbt-plugins",
  url("http://dl.bintray.com/x7c1/sbt-plugins"))(Resolver.ivyStylePatterns)

addSbtPlugin("x7c1" % "chaff-publish" % "0.1.0")
```

at `~/.sbt/0.13/local.sbt`

```scala
x7c1.chaff.publish.PublishLocalSnapshot.definition
```

in sbt-console

```
$ sbt
> your-project/publisLocalSnapshot
```
