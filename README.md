# luminus-migrations

The library is a command line wrapper for [Migratus](https://github.com/yogthos/migratus). 

## Usage

The migrations can now be invoked by running `luminus-migrations.core/migrate`. The function accepts
a vector of arguments follows by an options map. The options follow the Migratus ones, except for the
`:db` key that's set to the `:database-url` key using in Luminus.

```clojure
(ns myapp.db
  (:require [luminus-migrations.core :refer [migrate]]))

(def opts {:database-url "<db-url>"})

;;reset the databse
(migrate ["reset"] opts)

;;list pending migrations
(migrate ["pending"] opts)

;;destroy the migration
(migrate ["destroy" "201506104553"] opts)

;;run all outstanding migrations
(migrate ["migrate"] opts)

;;rollback last migration
(migrate ["rollback"] opts)

;;run specified migrations
(migrate ["migrate" "201506104553" "201506120401"] opts)

;;rollback specified migrations
(migrate ["rollback" "201506104553" "201506120401"] opts)
```

## License

Copyright © 2016 Dmitri Sotnikov

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
