rootProject.name = "pridelib"

pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		maven { url = uri("https://maven.architectury.dev/") }
		maven { url = uri("https://files.minecraftforge.net/maven/") }
		gradlePluginPortal()
	}
}
