# luminus-migrations

The library is a command line wrapper for [Migratus](https://github.com/yogthos/migratus). 

## Usage

The migrations can now be invoked by running `luminus-migrations.core/migrate`:

```clojure
(ns myapp.db
  (:requiure [luminus-migrations.core :refer migrate]))

;;run all outstanding migrations
(migrate ["migrate"] "<db-url>")

;;rollback last migration
(migrate ["rollback"] "<db-url>")

;;run specified migrations
(migrate ["migrate" "201506104553" "201506120401"] "<db-url>")

;;rollback specified migrations
(migrate ["rollback" "201506104553" "201506120401"] "<db-url>")
```

## License

Copyright © 2016 Dmitri Sotnikov

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
