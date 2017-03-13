package com.jaynewstrom.json.gradle

import com.jaynewstrom.json.compiler.VERSION
import okio.Okio
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File

open class ManifestTask : DefaultTask() {
    @Suppress("unused") // Required to invalidate the task on version updates.
    @Input fun pluginVersion() = VERSION

    @Input var defaultPackage = ""

    @get:OutputDirectory var outputDirectory: File? = null

    @TaskAction fun execute(inputs: IncrementalTaskInputs) {
        val file = File(outputDirectory, "AndroidManifest.xml")
        val buffer = Okio.buffer(Okio.sink(file))
        buffer.writeUtf8("" +
                "<manifest\n" +
                "    xmlns:android=\"http://schemas.android.com/apk/res/android\">\n" +
                "    <application>\n" +
                "        <meta-data\n" +
                "            android:name=\"$defaultPackage.RealJsonSerializerFactory\"\n" +
                "            android:value=\"JsonSerializerFactory\"/>\n" +
                "        <meta-data\n" +
                "            android:name=\"$defaultPackage.RealJsonDeserializerFactory\"\n" +
                "            android:value=\"JsonDeserializerFactory\"/>\n" +
                "    </application>\n" +
                "</manifest>\n")
        buffer.close()
    }
}