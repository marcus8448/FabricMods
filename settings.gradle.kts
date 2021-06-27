pluginManagement {
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
}
rootProject.name = "FabricMods"

include("ContextImmunity")
include("GamemodeOverhaul")
include("NoLanCheats")
include("PersonalCommands")
include("RunningProject")
include("Snowy")
include("Walkways")
include("WhosePetIsThis")