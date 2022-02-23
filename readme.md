# Flyway CSV extension

This is a Flyway extension to make it easier to import CSV files the same way as `Sql` or `Java` migrations. It extends Flyway naming convention to be able to specify the target table name as part of the file name. 

## Naming convention
The naming convention follows the pattern defined below:

* Prefix: V for versioned migrations, U for undo migrations, R for repeatable migrations
* Version: Underscores (automatically replaced by dots at runtime) separate as many parts as you like (Not for repeatable migrations)
* Separator: __ (two underscores)
* Table: The name of the target table
* Separator: __ (two underscores)
* Description: Underscores (automatically replaced by spaces at runtime) separate the words

Example: `V2__users__import_users`


# License

This repository is made available under Apache License: http://www.apache.org/licenses/LICENSE-2.0
