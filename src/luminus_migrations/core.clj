(ns luminus-migrations.core
  (:require
    [clojure.set :refer [rename-keys]]
    [migratus.core :as migratus]
    [to-jdbc-uri.core :refer [to-jdbc-uri]]))

(defn parse-ids [args]
  (map #(Long/parseLong %) (rest args)))

(defn migrate [args opts]
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
      "migrate"
      (if (> (count args) 1)
        (apply migratus/up config (parse-ids args))
        (migratus/migrate config))
      "rollback"
      (if (> (count args) 1)
        (apply migratus/down config (parse-ids args))
        (migratus/rollback config)))))
