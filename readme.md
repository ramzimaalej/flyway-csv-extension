# Flyway CSV extension

This is a Flyway extension to make it easier to import CSV files the same way as `Sql` or `Java` migrations. It extends Flyway naming convention to be able to specify the target table name as part of the file name. 

## Installation

Add `jitpack.io` as a new repository.

 ```
<repositories>
  <repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
  </repository>
</repositories>
 ```

Add dependency to your project.

```
<dependency>
  <groupId>com.github.ramzimaalej</groupId>
  <artifactId>flyway-csv-extension</artifactId>
  <version>Tag</version>
</dependency>
```
Make sure to change `Tag` with the version number you want to use.

Resolver

`com.mytechden.flyway_csv.impl.resolver.CSVMigrationResolver`

Micronaut Configuration

```
flyway:
  datasources:
    default:
      enabled: true
      resolvers:
        - com.mytechden.flyway_csv.impl.resolver.CSVMigrationResolver
```

## Naming convention
The naming convention follows the pattern defined below:

* Prefix: V for versioned migrations, U for undo migrations, R for repeatable migrations
* Version: Underscores (automatically replaced by dots at runtime) separate as many parts as you like (Not for repeatable migrations)
* Separator: __ (two underscores)
* Table: The name of the target table
* Separator: __ (two underscores)
* Description: Underscores (automatically replaced by spaces at runtime) separate the words

Example: `V2__users__import_users`

## Types mapping
This extension uses Java built-in support to map field types to their corresponding column types. 

### UUID
In order to import CSV files that include UUIDs, you need to make sure the database column is of type `BINARY(16)` or equivalent.

### Null values
All you have to do is to use null instead of an empty string in your csv files.



# License

This repository is made available under Apache License: http://www.apache.org/licenses/LICENSE-2.0
