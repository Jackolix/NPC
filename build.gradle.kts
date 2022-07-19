plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.3.7"
  id("xyz.jpenilla.run-paper") version "1.0.6" // Adds runServer and runMojangMappedServer tasks for testing
}

group = "codes.Elix.papernpc"
version = "1.0"
description = "PaperNPC"

java {toolchain.languageVersion.set(JavaLanguageVersion.of(17))}

repositories {
  maven ("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
  paperDevBundle("1.19-R0.1-SNAPSHOT")
  compileOnly ("com.comphenix.protocol", "ProtocolLib", "4.8.0")
  // paperweightDevBundle("com.example.paperfork", "1.19-R0.1-SNAPSHOT")
  // You will need to manually specify the full dependency if using the groovy gradle dsl
  // (paperDevBundle and paperweightDevBundle functions do not work in groovy)
  // paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.19-R0.1-SNAPSHOT")
}

tasks {
  // Configure reobfJar to run when invoking the build task
  assemble {
    dependsOn(reobfJar)
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(17)
  }

  javadoc {options.encoding = Charsets.UTF_8.name()}
  processResources {filteringCharset = Charsets.UTF_8.name() }

}