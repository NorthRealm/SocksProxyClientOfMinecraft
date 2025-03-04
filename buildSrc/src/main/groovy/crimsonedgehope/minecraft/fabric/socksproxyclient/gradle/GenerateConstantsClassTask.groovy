package crimsonedgehope.minecraft.fabric.socksproxyclient.gradle

import com.squareup.javapoet.*
import lombok.NoArgsConstructor
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import javax.lang.model.element.Modifier

abstract class GenerateConstantsClassTask extends GenerateSourceTask {
    @Input Map<String, Object> constants

    GenerateConstantsClassTask() {
        super("Generate Constants class")
    }

    @Override
    @TaskAction
    void generate() throws Exception {
        final AnnotationSpec lombok =
                AnnotationSpec.builder(NoArgsConstructor.class)
                        .addMember("access", CodeBlock.builder().add('$L', "lombok.AccessLevel.PRIVATE").build()).build()

        final TypeSpec.Builder typeSpec =
                TypeSpec.classBuilder("Constants").addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addAnnotation(lombok)

        for (Map.Entry<String, Object> entry : constants) {
            final String k0 = entry.getKey().toUpperCase().replaceAll("[\\.\\-]+", "_")
            typeSpec.addField(FieldSpec.builder(String.class, k0)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("\"" + entry.getValue().toString() + "\"").build())
        }

        JavaFile out
        out = JavaFile.builder(packageTarget, typeSpec.build()).build()
        out.writeTo(new File(outputDir).toPath())
    }
}
