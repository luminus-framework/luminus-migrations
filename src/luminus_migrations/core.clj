(ns luminus-migrations.core
  (:require
    [clojure.set :refer [rename-keys]]
    [migratus.core :as migratus]
    [to-jdbc-uri.core :refer [to-jdbc-uri]]))

(defn- format-url [opts]
    (if (:database-url opts)
      (update opts :database-url to-jdbc-uri)
      opts))

(defn parse-ids [args]
  (map #(Long/parseLong %) (rest args)))

(defn create
  "Wrapper around migratus/create.
   Creates a migration file with generated timestamp-based migration id.
   name - string, name of migration to be created.
   opts - map of options specifying the database configuration.
   supported options are:
   :database-url - URL of the application database
   :migration-dir - string specifying the directory of the migration files
   :migration-table-name - string specifying the migration table name"
  [name opts]
  (let [config (merge
                {:store :database}
                (-> opts format-url (rename-keys {:database-url :db})))]
    (migratus/create config name)))

(defn migrate
  "args - vector of arguments, e.g: [\"migrate\" \"201506104553\"]
   opts - map of options specifying the database configuration.
   supported options are:
   :database-url - URL of the application database
   :migration-dir - string specifying the directory of the migration files
   :migration-table-name - string specifying the migration table name"
  [args opts]
  (let [config (merge
                 {:store :database}
                 (rename-keys opts {:database-url :db}))]
    (case (first args)
      "reset"
      (migratus/reset config)
      "destroy"
      (if (= (count args) 1)
        (migratus/destroy config (first args))
        (migratus/destroy config))
      "pending"
      (migratus/pending-list config)
      "migrate"
      (if (> (count args) 1)
        (apply migratus/up config (parse-ids args))
        (migratus/migrate config))
      "rollback"
      (if (> (count args) 1)
        (apply migratus/down config (parse-ids args))
        (migratus/rollback config)))))
