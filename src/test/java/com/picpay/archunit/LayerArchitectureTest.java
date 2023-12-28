package com.picpay.archunit;


import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.picpay", importOptions = {
    ImportOption.DoNotIncludeArchives.class,
    ImportOption.DoNotIncludeJars.class,
    ImportOption.DoNotIncludeTests.class
})
class LayerArchitectureTest {
  static String API_LAYER_PACKAGES = "com.picpay.entrypoint.api..";
  static String CORE_LAYER_PACKAGES = "com.picpay.core..";
  static String DATA_PROVIDER_LAYER_PACKAGES = "com.picpay.dataprovider..";

  @ArchTest
  static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
      .consideringOnlyDependenciesInLayers()
      .layer(CORE_LAYER_PACKAGES).definedBy(CORE_LAYER_PACKAGES)
      .layer(API_LAYER_PACKAGES).definedBy(API_LAYER_PACKAGES)
      .layer(DATA_PROVIDER_LAYER_PACKAGES).definedBy(DATA_PROVIDER_LAYER_PACKAGES)

      .whereLayer(API_LAYER_PACKAGES).mayNotBeAccessedByAnyLayer()
      .whereLayer(DATA_PROVIDER_LAYER_PACKAGES).mayOnlyBeAccessedByLayers(CORE_LAYER_PACKAGES)
      .whereLayer(CORE_LAYER_PACKAGES).mayOnlyBeAccessedByLayers(API_LAYER_PACKAGES, DATA_PROVIDER_LAYER_PACKAGES);
}