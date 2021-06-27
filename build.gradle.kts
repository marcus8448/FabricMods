import java.time.Year
import java.time.format.DateTimeFormatter

buildscript {
    repositories {
        maven {
            name = "Fabric"
            setUrl("https://maven.fabricmc.net/")
            content {
                includeGroup("net.fabricmc")
                includeGroup("net.fabricmc.fabric-api")
                includeGroup("fabric-loom")
            }
        }
        gradlePluginPortal()
    }
    dependencies {
        classpath("net.fabricmc:fabric-loom:0.8-SNAPSHOT")
        classpath("gradle.plugin.org.cadixdev.gradle:licenser:0.6.1")
        classpath("gradle.plugin.com.matthewprenger:CurseGradle:1.4.0")
        classpath("gradle.plugin.com.modrinth.minotaur:Minotaur:1.2.1")
    }
}

val minecraftVersion = rootProject.property("minecraft.version").toString()
val yarnBuild = rootProject.property("yarn.build").toString()
val loaderVersion = rootProject.property("loader.version").toString()

val fabricVersion = rootProject.property("fabric.version").toString()
val modmenuVersion = rootProject.property("modmenu.version").toString()
val clothConfigVersion = rootProject.property("cloth_config.version").toString()
val lbaVersion = rootProject.property("lba.version").toString()
val reiVersion = rootProject.property("rei.version").toString()
val wthitVersion = rootProject.property("wthit.version").toString()

group = "io.github.marcus8448.mods"

subprojects {
    apply(plugin = "java")
    apply(plugin = "fabric-loom")
    apply(plugin = "org.cadixdev.licenser")
    apply(plugin = "com.modrinth.minotaur")
    apply(plugin = "com.matthewprenger.cursegradle")

    val modId = project.property("mod.id").toString()
    val modName = project.property("mod.name").toString()
    val modVersion = project.property("mod.version").toString()
    val modDescription = project.property("mod.description").toString()

    val curseforgeId = project.property("curseforge.project_id").toString()
    val modrinthId = project.property("modrinth.project_id").toString()

    val lbaEnabled = project.property("lba.enabled").toString() == "true"
    val clothConfigEnabled = project.property("cloth_config.enabled").toString() == "true"
    val modmenuEnabled = project.property("modmenu.enabled").toString() == "true"
    val reiEnabled = project.property("rei.enabled").toString() == "true"
    val wthitEnabled = project.property("wthit.enabled").toString() == "true"
    val fabricModules = project.property("fabric.modules").toString().split(",".toRegex())
    val fabricRuntimeModules = project.property("fabric.runtime.modules").toString().split(",".toRegex())
    val runtimeOptionalEnabled = (project.property("optional_dependencies.enabled") ?: "false") == "true"

    repositories {
        mavenLocal()
        if (reiEnabled || clothConfigEnabled) {
            maven("https://maven.shedaniel.me/") {
                content {
                    includeGroup("me.shedaniel.cloth.api")
                    includeGroup("me.shedaniel.cloth")
                    includeGroup("me.shedaniel")
                    includeGroup("dev.architectury")
                }
            }
        }
        if (lbaEnabled) {
            maven("https://alexiil.uk/maven/") {
                content {
                    includeGroup("alexiil.mc.lib")
                }
            }
        }
        if (modmenuEnabled) {
            maven("https://maven.terraformersmc.com/") {
                content {
                    includeGroup("com.terraformersmc")
                }
            }
        }
        if (wthitEnabled) {
            maven("https://bai.jfrog.io/artifactory/maven/") {
                content {
                    includeGroup("mcp.mobius.waila")
                }
            }
        }
    }

    project.version = modVersion

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_16
        targetCompatibility = JavaVersion.VERSION_16
    }

    project.extensions.getByType(org.cadixdev.gradle.licenser.LicenseExtension::class).apply {
        setHeader(rootProject.file("LICENSE_HEADER"))
        ext {
            set("company", "marcus8448")
            set("year", Year.now().value.toString())
        }
        include("**/io/github/marcus8448/**/*.java")
        include("build.gradle.kts")
    }

    fun minecraft(dependencyNotation: Any) {
        project.dependencies.add("minecraft", project.dependencies.create(dependencyNotation))
    }

    fun mappings(dependencyNotation: Any) {
        project.dependencies.add("mappings", project.dependencies.create(dependencyNotation))
    }

    fun modImplementation(dependencyNotation: Any) {
        project.dependencies.add("modImplementation", project.dependencies.create(dependencyNotation))
    }

    fun modIncludedApi(dependencyNotation: Any, configuration: Action<ModuleDependency>) {
        configuration.execute(
            project.dependencies.add(
                "modApi",
                project.dependencies.create(dependencyNotation)
            ) as ModuleDependency
        )
        configuration.execute(
            project.dependencies.add(
                "include",
                project.dependencies.create(dependencyNotation)
            ) as ModuleDependency
        )
    }

    fun modRuntime(dependencyNotation: Any) {
        project.dependencies.add("modRuntime", project.dependencies.create(dependencyNotation))
    }

    fun fabricApiImplementation(moduleName: String) {
        modImplementation(
            "net.fabricmc.fabric-api:$moduleName:${
                project.extensions.getByType(net.fabricmc.loom.configuration.FabricApiExtension::class)
                    .moduleVersion(moduleName, fabricVersion)
            }"
        )
    }

    fun modOptionalImplementation(dependencyNotation: Any, configuration: Action<ModuleDependency>) {
        configuration.execute(
            project.dependencies.add(
                "modCompileOnly",
                project.dependencies.create(dependencyNotation)
            ) as ModuleDependency
        )
        if (runtimeOptionalEnabled) configuration.execute(
            project.dependencies.add(
                "modRuntime",
                project.dependencies.create(dependencyNotation)
            ) as ModuleDependency
        )
    }

    dependencies {
        minecraft("com.mojang:minecraft:${minecraftVersion}")
        mappings("net.fabricmc:yarn:${minecraftVersion}+build.${yarnBuild}:v2")
        modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")

        if (lbaEnabled) {
            modIncludedApi("alexiil.mc.lib:libblockattributes-core:${lbaVersion}") { isTransitive = false }
            modIncludedApi("alexiil.mc.lib:libblockattributes-items:${lbaVersion}") { isTransitive = false }
            modIncludedApi("alexiil.mc.lib:libblockattributes-fluids:${lbaVersion}") { isTransitive = false }
        }

        if (clothConfigEnabled) {
            modIncludedApi("me.shedaniel.cloth:cloth-config-fabric:${clothConfigVersion}") {
                exclude(group = "net.fabricmc.fabric-api")
                exclude(group = "net.fabricmc")
            }
        }

        if (fabricModules[0].isNotEmpty()) {
            fabricModules.forEach {
                fabricApiImplementation(it)
            }
        }

        if (fabricRuntimeModules[0].isNotEmpty()) {
            if (fabricRuntimeModules.size == 1 && fabricRuntimeModules[0] == "*") {
                modRuntime("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")
            } else {
                fabricRuntimeModules.forEach {
                    fabricApiImplementation(it)
                }
            }
        }

        if (modmenuEnabled) modOptionalImplementation("com.terraformersmc:modmenu:${modmenuVersion}") {
            isTransitive = false
        }
        if (wthitEnabled) modOptionalImplementation("mcp.mobius.waila:wthit:fabric-${wthitVersion}") {
            isTransitive = false
        }
        if (reiEnabled) modOptionalImplementation("me.shedaniel:RoughlyEnoughItems-fabric:${reiVersion}") {
            exclude(group = "net.fabricmc")
            exclude(group = "net.fabricmc.fabric-api")
        }
    }

    tasks.withType(ProcessResources::class) {
        inputs.property("version", project.version)
        inputs.property("mod_id", modId)
        inputs.property("mod_name", modName)
        inputs.property("mod_description", modDescription)

        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to project.version,
                    "mod_id" to modId,
                    "mod_name" to modName,
                    "mod_description" to modDescription,
                )
            )
        }

        // Minify json resources
        // https://stackoverflow.com/questions/41028030/gradle-minimize-json-resources-in-processresources#41029113
        doLast {
            fileTree(
                mapOf(
                    "dir" to outputs.files.asPath,
                    "includes" to listOf("**/*.json", "**/*.mcmeta")
                )
            ).forEach { file: File ->
                file.writeText(groovy.json.JsonOutput.toJson(groovy.json.JsonSlurper().parse(file)))
            }
        }
    }

    project.convention.getPluginByName<BasePluginConvention>("base").apply {
        archivesBaseName = modName
    }

    project.extensions.getByType(net.fabricmc.loom.LoomGradleExtension::class).apply {
        refmapName = "${modId}.refmap.json"
        if (project.file("src/main/resources/$modId.accesswidener").exists()) {
            accessWidener = project.file("src/main/resources/$modId.accesswidener")
        }
    }

    project.extensions.getByType(JavaPluginExtension::class).apply {
        withSourcesJar()
    }

    project.tasks.withType(JavaCompile::class) {
        dependsOn(tasks.named("checkLicenses"))
        options.encoding = "UTF-8"
        options.release.set(16)
    }

    project.tasks.withType(Jar::class) {
        archiveBaseName.set(modName)
        from("LICENSE")
        manifest {
            attributes(
                mapOf(
                    "Implementation-Title" to modName,
                    "Implementation-Version" to project.version,
                    "Implementation-Vendor" to "marcus8448",
                    "Implementation-Timestamp" to DateTimeFormatter.ISO_DATE_TIME,
                    "Maven-Artifact" to "${rootProject.group}:${modName}:${project.version}"
                )
            )
        }
    }

    if (System.getenv("CURSEFORGE_API_KEY") != null && curseforgeId.isNotBlank()) {
        project.extensions.getByType(com.matthewprenger.cursegradle.CurseExtension::class).apply {
            apiKey = System.getenv("CURSEFORGE_API_KEY")
            project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
                id = curseforgeId
                releaseType = if (project.properties.containsKey("curseforge.release_type")) {
                    project.property("curseforge.release_type")
                } else {
                    "release"
                }
                changelog = ""

                addGameVersion("Fabric")
                addGameVersion("Java 16")
                addGameVersion(minecraftVersion)

                relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
                    if (fabricModules.isNotEmpty()) requiredDependency("fabric-api")
                    if (clothConfigEnabled) requiredDependency("cloth-config")
                    if (lbaEnabled) embeddedLibrary("libblockattributes")
                    if (reiEnabled) optionalDependency("roughly-enough-items")
                    if (modmenuEnabled) optionalDependency("modmenu")
                    if (wthitEnabled) optionalDependency("wthit")
                })
                mainArtifact(file("${project.buildDir}/libs/${project.convention.getPluginByName<BasePluginConvention>("base").archivesBaseName}-${project.version}.jar"))

                options(closureOf<com.matthewprenger.cursegradle.Options> {
                    forgeGradleIntegration = false
                    javaVersionAutoDetect = false
                })
            })
        }
    }

    if (System.getenv("MODRINTH_API_KEY") != null && modrinthId.isNotBlank()) {
        tasks.create<com.modrinth.minotaur.TaskModrinthUpload>("publishModrinth") {
            token = System.getenv("MODRINTH_API_KEY")
            projectId = modrinthId
            versionNumber = modVersion
            uploadFile =
                file("${project.buildDir}/libs/${project.convention.getPluginByName<BasePluginConvention>("base").archivesBaseName}-${project.version}.jar")
            addGameVersion(minecraftVersion)
            addLoader("fabric")
        }
    }
}

tasks.create("publishAll") {
    rootProject.allprojects.forEach { project ->
        project.tasks.forEach { task ->
            if (task.name == "publishModrinth" || task.name.startsWith("curseforge")) {
                this.dependsOn(":${project.name}:${task.name}")
            }
        }
    }
}