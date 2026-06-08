# Constraints

1. Development must be thoroughly planned before implementation begins.
2. Development tasks must be broken down into multiple steps; each step must adhere to the established Git workflow and undergo comprehensive testing before being committed or merged.
3. Testing Requirements:
- Prioritize testing externally observable behaviors.
- Minimize testing of internal implementation details.
- Give priority to testing branching logic, state machines, boundary conditions, and error conditions.
- For purely delegating layers, either omit tests entirely or retain only a minimal set of "smoke tests."
- Tests should ideally remain unchanged following source code refactoring.
4. Source code must not contain any comments.
5. This is an Android Kotlin project; development must strictly adhere to the principles of Clean Architecture and SOLID principles.
6. Modifications to `libs.versions.toml` and `build.gradle.kts` are strictly prohibited. If you deem it necessary to add a new dependency, please notify me so that I may add it manually.