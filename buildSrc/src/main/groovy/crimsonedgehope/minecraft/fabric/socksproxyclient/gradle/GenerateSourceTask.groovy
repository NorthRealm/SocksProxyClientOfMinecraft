package crimsonedgehope.minecraft.fabric.socksproxyclient.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class GenerateSourceTask extends DefaultTask {
    @Input String outputDir
    @Input String packageTarget

    GenerateSourceTask(String description) {
        this.group = Tasks.GROUP
        this.description = description
    }

    @TaskAction
    abstract void generate() throws Exception
}
