package com.jaynewstrom.json.composite

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.jaynewstrom.json.compiler.VERSION
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies
import java.io.File

class CompositePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.all {
            when (it) {
                is AppPlugin -> configureAndroid(project,
                        project.extensions.getByType(AppExtension::class.java).applicationVariants)
                is LibraryPlugin -> configureAndroid(project,
                        project.extensions.getByType(LibraryExtension::class.java).libraryVariants)
            }
        }
    }

    private fun <T : BaseVariant> configureAndroid(project: Project, variants: DomainObjectSet<T>) {
        val generateCompositeFactories = project.task("generateJsonCompositeFactories")

        val compileDeps = project.configurations.getByName("compile").dependencies
        project.gradle.addListener(object : DependencyResolutionListener {
            override fun beforeResolve(dependencies: ResolvableDependencies?) {
                compileDeps.add(project.dependencies.create("com.jaynewstrom.json:runtime:$VERSION"))
                project.gradle.removeListener(this)
            }

            override fun afterResolve(dependencies: ResolvableDependencies?) {
            }
        })

        variants.all {
            val taskName = "generate${it.name.capitalize()}JsonCompositeFactories"
            val task = project.tasks.create(taskName, CompositeTask::class.java)
            task.group = "jsoncomposite"
            task.buildDirectory = project.buildDir
            task.description = "Generate Json Composite Factories for ${it.name}"
            task.source("src")
            task.include("**/resources/META-INF/jsonComposite/JsonSerializers.json".replace('/', File.separatorChar))
            task.include("**/resources/META-INF/jsonComposite/JsonDeserializers.json".replace('/', File.separatorChar))

            generateCompositeFactories.dependsOn(task)

            it.registerJavaGeneratingTask(task, task.outputDirectory)
        }
    }
}