

`~/.sbt/0.13/plugins/local.sbt`

```
resolvers += Resolver.url(
  "bintray-x7c1-android",
  url("http://dl.bintray.com/x7c1/sbt-plugins"))(Resolver.ivyStylePatterns)

addSbtPlugin("x7c1" % "chaff-publish" % "0.1.0")
```


`~/.sbt/0.13/local.sbt`

```
x7c1.chaff.publish.PublishLocalSnapshot.definition
```

in sbt console

```
$ sbt
> your-project/publisLocalSnapshot
```
