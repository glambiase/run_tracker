import com.glambiase.build_logic.convention.addUILayerDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureUIConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("runtracker.android.library.compose")
            }

            dependencies {
                addUILayerDependencies(project = target)
            }
        }
    }
}