rootProject.name = "foundation-grid-check"

plugins {
    id("com.gradle.enterprise").version("3.12.2")
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
