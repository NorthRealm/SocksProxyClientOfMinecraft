plugins {
	id("fabric-loom") version "1.8-SNAPSHOT"
	id("java-library")
	id("com.github.johnrengelman.shadow") version "8.1.1"
	id("org.jetbrains.changelog") version "2.2.0"
	id("com.modrinth.minotaur") version "2.8.7"
}

buildscript {
	apply<crimsonedgehope.minecraft.fabric.socksproxyclient.gradle.Tasks>()
	apply(from = "properties.gradle.kts")
}

version = "${project.findProperty("mod_version")}+${project.findProperty("minecraft_version")}"
group = "${project.findProperty("maven_group")}"

repositories {
	maven("https://maven.shedaniel.me/")
	maven("https://maven.terraformersmc.com/releases/")
	maven("https://maven.isxander.dev/releases")
}

java {
	sourceCompatibility = JavaVersion.toVersion("${project.extra["compilation_java_version"]}")
	targetCompatibility = JavaVersion.toVersion("${project.extra["compilation_java_version"]}")
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(Integer.valueOf("${project.extra["compilation_java_version"]}"))
	options.encoding = "UTF-8"
}

base {
	archivesName.set("${project.findProperty("archives_base_name")}")
}

dependencies {
	minecraft("com.mojang:minecraft:${project.findProperty("minecraft_version")}")
	mappings("net.fabricmc:yarn:${project.findProperty("yarn_mappings")}:v2")

	modImplementation("net.fabricmc:fabric-loader:${project.findProperty("fabricloader_version")}")

	modApi("com.terraformersmc:modmenu:${project.findProperty("modmenu_version")}")
	modApi("dev.isxander:yet-another-config-lib:${project.findProperty("yacl_version")}+${project.findProperty("yacl_fabric")}")

	shadow(implementation("io.netty:netty-handler-proxy:${project.extra["netty_version"]}") {
		exclude(group = "io.netty", module = "netty-common")
		exclude(group = "io.netty", module = "netty-buffer")
		exclude(group = "io.netty", module = "netty-transport")
		exclude(group = "io.netty", module = "netty-codec")
		exclude(group = "io.netty", module = "netty-handler")
	})
	shadow(implementation("dnsjava:dnsjava:${project.extra["dnsjava_version"]}") {
		exclude(group = "org.slf4j")
	})

	annotationProcessor("org.projectlombok:lombok:${project.extra["lombok_version"]}")
	compileOnly("org.projectlombok:lombok:${project.extra["lombok_version"]}")

	testImplementation("net.fabricmc:fabric-loader-junit:${project.findProperty("fabricloader_version")}")
}

tasks.register<crimsonedgehope.minecraft.fabric.socksproxyclient.gradle.GenerateTranslateKeysTask>("generateTranslateKeys") {
	val packageTarget0 = "crimsonedgehope.minecraft.fabric.socksproxyclient.i18n"
	inputFile = file("${rootProject.projectDir}/src/main/resources/assets/socksproxyclient/lang/en_us.json")
	packageTarget = packageTarget0
	outputDir = "${rootProject.projectDir}/src/main/java/"
}

tasks.register<crimsonedgehope.minecraft.fabric.socksproxyclient.gradle.GenerateConstantsClassTask>("generateConstantsClass") {
	val packageTarget0 = "crimsonedgehope.minecraft.fabric.socksproxyclient"
	constants = mapOf(
		"mod_id" to project.findProperty("mod_id")
	)
	packageTarget = packageTarget0
	outputDir = "${rootProject.projectDir}/src/main/java/"
}

tasks.compileJava {
	dependsOn("generateTranslateKeys")
	mustRunAfter("generateTranslateKeys")
	dependsOn("generateConstantsClass")
	mustRunAfter("generateConstantsClass")
}

tasks.processResources {
	dependsOn("projectGitHash")
	mustRunAfter("projectGitHash")
	filesMatching("fabric.mod.json") {
		expand(
			"mod_id" to project.findProperty("mod_id"),
			"mod_name" to project.findProperty("archives_base_name"),
			"mod_version" to project.findProperty("version"),
			"minecraft_version" to project.findProperty("minecraft_dependency"),
			"modmenu_version" to project.findProperty("modmenu_version"),
			"yacl_version" to project.findProperty("yacl_version"),
			"fabricloader_version" to project.findProperty("fabricloader_version"),
			"github_repository" to project.findProperty("github_repository")
		)
	}

	filesMatching("assets/socksproxyclient/lang/*.json") {
		expand(
			"compilation_java_version" to project.extra["compilation_java_version"],
			"git_hash" to project.extra["git_short_hash"]
		)
	}
}

tasks.shadowJar {
	configurations = listOf(project.configurations.getByName("shadow"))
	archiveClassifier.set("SHADOW")
}

tasks.remapJar {
	dependsOn("shadowJar")
	inputFile.set(tasks.shadowJar.get().archiveFile)
	archiveClassifier.set("SNAPSHOT")
}

changelog {
	header.set(provider {
		version.get()
	})
	combinePreReleases = false
	repositoryUrl = "${project.findProperty("github_repository")}"
}

tasks.test {
	reports {
		html.required = false
		junitXml.required = true
		junitXml.isOutputPerTestCase = true
	}
	workingDir = file("runtest")
	doFirst {
		workingDir.deleteRecursively()
		workingDir.mkdirs()
	}
	useJUnitPlatform()
}

modrinth {
	val verbose = project.findProperty("verbose")?.toString()?.toBoolean() ?: false
	val noUpload = project.findProperty("noUpload")?.toString()?.toBoolean() ?: false

	if (verbose) {
		println("\n========== Properties ==========")
		println("Mod version (gradle project.version): ${project.findProperty("version")}")
		println("Minecraft version: ${project.findProperty("minecraft_version")}")
		println("YACL version: ${project.findProperty("yacl_version")}")
		println("Modmenu version: ${project.findProperty("modmenu_version")}")
		println("========== Properties ==========\n")
	}

	val modrinth_versionName = "[Fabric ${project.findProperty("minecraft_version")}] ${version}"
	val modrinth_gameVersions = project.findProperty("modrinth_gameversions").toString().split(",").toList()
	val modrinth_loaders = listOf("fabric")
	var releaseType = project.findProperty("releaseType")?.toString()
	if (releaseType == null) {
		if (project.findProperty("version").toString().lowercase().contains("alpha")) {
			releaseType = "alpha"
		} else if (project.findProperty("version").toString().lowercase().contains("beta")) {
			releaseType = "beta"
		}
	}
	if (releaseType != "alpha" && releaseType != "beta" && releaseType != "release") {
		throw IllegalArgumentException("releaseType: alpha, beta or release")
	}

	if (noUpload) {
		println("\n========== Modrinth ==========")
		println("Project id: ${project.findProperty("modrinth_project_id")}")
		println("Version number: ${project.findProperty("version")}")
		println("Version name: $modrinth_versionName")
		println("Release type: $releaseType")
		println("Game versions: $modrinth_gameVersions")
		println("Loaders: $modrinth_loaders")
		println("========== Modrinth ==========\n")
		throw IllegalArgumentException("no upload")
	}

	token.set(System.getenv("MODRINTH_TOKEN"))
	projectId.set("${project.findProperty("modrinth_project_id")}")
	versionNumber.set("${project.findProperty("version")}")
	versionName.set(modrinth_versionName)
	versionType.set(releaseType)
	uploadFile.set(tasks.remapJar.get().archiveFile)
	gameVersions.addAll(modrinth_gameVersions)
	loaders.addAll(modrinth_loaders)
	changelog.set("See changelog at " +
			"[GitHub](${project.findProperty("github_repository")}/releases/tag/v${project.findProperty("version")})")

	dependencies {
		optional.project("fabric-api")
		optional.project("yacl")
		optional.project("modmenu")
	}
}
