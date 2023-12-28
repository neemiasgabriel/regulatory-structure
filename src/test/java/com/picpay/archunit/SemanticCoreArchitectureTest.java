package com.picpay.archunit;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.picpay", importOptions = {
    ImportOption.DoNotIncludeArchives.class,
    ImportOption.DoNotIncludeJars.class,
    ImportOption.DoNotIncludeTests.class
})
class SemanticCoreArchitectureTest {
//  @ArchTest
//  static final ArchRule useCaseClassesMustResideInUseCasePackage =
//      classes().that()
//          .haveNameMatching(".*UseCase").should()
//          .resideInAPackage("..usecase..")
//          .as("UseCase's class should reside inside use case package on core package ");

  @ArchTest
  static final ArchRule exceptionClassesMustResideInExceptionPackage =
      classes().that()
          .haveNameMatching(".*Exception").should()
          .resideInAPackage("..exception..")
          .as("Exception's class should reside inside exception package on core package ");

//  @ArchTest
//  static final ArchRule mapperClassesMustResideInMapperPackage =
//      classes().that()
//          .haveNameMatching(".*Mapper").should()
//          .resideInAPackage("..mapper..")
//          .as("Type's class should reside inside type package on core package ");
}