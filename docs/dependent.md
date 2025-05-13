# 已经发布到 Maven 仓库
```xml
<dependency>
    <groupId>io.github.zimoyin</groupId>
    <artifactId>qqbot</artifactId>
    <version>1.3.4</version>
</dependency>
```

---

---


# ~~如何添加依赖（废弃）~~
- [~~Maven~~](#maven)
- [~~Gradle~~](#gradle)
- [~~GradleKts~~](#gradleKt)
- [~~Version~~](https://jitpack.io/com/github/zimoyin/qqbot/) ~~列表: 请查看该列表并选择一个新的版本~~
- [~~Version~~](https://github.com/zimoyin/qqbot/releases) ~~列表: 请查看该列表并选择一个新的版本 （Github）~~
---

#### ~~<a name="maven"></a>Maven~~

```
<!--添加该库的仓库地址-->
<repositories>
    <!-- Maven 中央仓库 ： 如果需要使用国内镜像仓库也可以添加，该仓库是为了兼容低版本 idea-->
    <repository>
        <id>central</id>
        <url>https://repo.maven.apache.org/maven2</url>
    </repository>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
<!--引入依赖-->
<dependencies>
  <dependency>
    <groupId>io.github.zimoyin</groupId>
    <artifactId>qqbot</artifactId>
<!--      这里替换版本号-->
    <version>$VERSION</version>
  </dependency>
</dependencies>
```

---

#### ~~<a name="tab2"></a>Gradle~~
```
// 添加jitpack仓库
//dependencyResolutionManagement {
//  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
//  repositories {
//    mavenCentral()
//    maven { url 'https://jitpack.io' }
//  }
//}

// 或者 添加jitpack仓库
repositories {
  mavenCentral()
  maven { url 'https://jitpack.io' }
}
// 引入依赖 注意替换版本号
implementation 'io.github.zimoyin:qqbot:$VERSION'
```

---

#### ~~<a name="tab2"></a>Kotlin Gradle~~
```
// 添加jitpack仓库
repositories {
    mavenCentral()
  maven("https://jitpack.io")
}
// 引入依赖 注意替换版本号
implementation("io.github.zimoyin:qqbot:$VERSION")
```
---
