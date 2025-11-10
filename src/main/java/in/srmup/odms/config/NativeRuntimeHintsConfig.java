package in.srmup.odms.config;

import in.srmup.odms.model.*;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Runtime hints for GraalVM native image compilation.
 * Registers classes that need reflection, resources, and proxies.
 */
@Configuration
@ImportRuntimeHints(NativeRuntimeHintsConfig.Hints.class)
public class NativeRuntimeHintsConfig {

    static class Hints implements RuntimeHintsRegistrar {
        
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Register JPA entities for reflection
            Class<?>[] entities = {
                User.class,
                StudentMaster.class,
                FacultyMaster.class,
                EventRequest.class,
                ApprovalHistory.class,
                Participant.class
            };
            
            for (Class<?> entity : entities) {
                hints.reflection()
                    .registerType(entity, 
                        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_METHODS,
                        MemberCategory.DECLARED_FIELDS);
            }
            
            // Register Thymeleaf templates
            hints.resources()
                .registerPattern("templates/*.html")
                .registerPattern("static/**")
                .registerPattern("application*.properties")
                .registerPattern("META-INF/resources/**");
            
            // Register H2 database resources
            hints.resources()
                .registerPattern("org/h2/util/data.zip");
            
            // Register serialization for common types
            hints.serialization()
                .registerType(java.util.ArrayList.class)
                .registerType(java.util.HashMap.class)
                .registerType(java.util.LinkedHashMap.class);
        }
    }
}
