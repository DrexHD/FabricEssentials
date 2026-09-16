plugins {
    id("dev.kikugie.stonecutter")
    id("org.jetbrains.changelog") version "2.2.0"
}
stonecutter active "26.3"

changelog {
    path = rootProject.file("CHANGELOG.md").path
}