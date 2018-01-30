(ns luminus-migrations.core
  (:require
    [clojure.set :refer [rename-keys]]
    [clojure.string :refer [join]]
    [migratus.core :as migratus]
    [to-jdbc-uri.core :refer [to-jdbc-uri]]))

(defn- parse-ids [args]
  (map #(Long/parseLong %) (rest args)))

(defn- parse-url
  ([opts] (parse-url opts identity))
  ([{:keys [database-url] :as opts} transformation]
   (if database-url
     (-> opts
         (dissoc :database-url)
         (assoc-in [:db :connection-uri]
                   (to-jdbc-uri (transformation database-url))))
     opts)))

(defn- remove-db-name [url]
  (when url
    (clojure.string/replace url #"(\/\/.*\/)(.*)(\?)" "$1$2$3")))

(def migrations
  {"reset"
   (fn [config _]
     (migratus/reset config))

   "destroy"
   (fn [config args]
     (if (> (count args) 1)
       (migratus/destroy config (second args))
       (migratus/destroy config)))

   "pending"
   (fn [config _]
     (migratus/pending-list config))

   "migrate"
   (fn [config args]
     (if (> (count args) 1)
       (apply migratus/up config (parse-ids args))
       (migratus/migrate config)))

   "rollback"
   (fn [config args]
     (if (> (count args) 1)
       (apply migratus/down config (parse-ids args))
       (migratus/rollback config)))})

(defn migration? [[arg]]
  (contains? (set (keys migrations)) arg))

(defn init
  "wrapper around migratus/init
   initializes the database using the script specified by the :init-script key
   opts - map of options specifying the database configuration.
   supported options are:
   :db - Migratus db config map
   :init-script - SQL script that initialized the database
   :database-url - URL of the application database
   :migration-dir - string specifying the directory of the migration files
   :migration-table-name - string specifying the migration table name"
  [opts]
  (let [config (merge {:store :database} (parse-url opts remove-db-name))]
    (migratus/init config)))

(defn create
  "Wrapper around migratus/create.
   Creates a migration file with generated timestamp-based migration id.
   name - string, name of migration to be created.
   opts - map of options specifying the database configuration.
   supported options are:
   :db - Migratus db config map
   :database-url - URL of the application database
   :migration-dir - string specifying the directory of the migration files
   :migration-table-name - string specifying the migration table name"
  [name opts]
  (let [config (merge {:store :database} (parse-url opts))]
    (migratus/create config name)))

(defn migrate
  "args - vector of arguments, e.g: [\"migrate\" \"201506104553\"]
   opts - map of options specifying the database configuration.
   supported options are:
   :database-url - URL of the application database
   :migration-dir - string specifying the directory of the migration files
   :migration-table-name - string specifying the migration table name"
  [args opts]
  (when-not (migration? args)
    (throw
     (IllegalArgumentException.
      (str "unrecognized option: " (first args)
           ", valid options are:" (join ", " (keys migrations))))))
  (let [config (merge {:store :database} (parse-url opts))]
    ((get migrations (first args)) config args)))
