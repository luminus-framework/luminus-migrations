(ns luminus-migrations.core
  (:require
    [migratus.core :as migratus]
    [to-jdbc-uri.core :refer [to-jdbc-uri]]))

(defn parse-ids [args]
  (map #(Long/parseLong %) (rest args)))

(defn migrate [args db-url]
  (let [config {:store :database
                :db {:connection-uri db-url}}]
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
