(ns luminus-migrations.util
  (:require [clojure.string :as s]))

;;based on https://github.com/pupeno/to-jdbc-uri
(defn- format-credentials [uri]
  (when (.getUserInfo uri)
    (let [[username password] (s/split (.getUserInfo uri) #":")]
      (when-let [user-and-pass
                 (->> [(when username (str "user=" username))
                       (when password (str "password=" password))]
                      (remove nil?)
                      (s/join "&")
                      (not-empty))]
        (str "?" user-and-pass)))))

(defn- format-query [uri sep-char]
  (when-let [raw-query (.getRawQuery uri)]
    (str sep-char raw-query)))

(defn- port [port]
  (when-not (neg? port)
    port))

(defn- host-and-port [uri]
  (s/join ":" (remove nil? [(.getHost uri) (port (.getPort uri))])))

(defn- format-jdbc-uri [uri db]
  (str "jdbc:" db "://"
       (host-and-port uri)
       (.getPath uri)
       (if-let [credentials (format-credentials uri)]
         (str credentials (format-query uri "&"))
         (format-query uri "?"))))

(defn to-jdbc-uri
  "Convert a non-JDBC URI to a JDBC one."
  [uri]
  (when (empty? uri)
    (throw (Exception. "URI connection string cannot be empty!")))
  (if (.startsWith uri "jdbc")
    uri
    (let [parsed-uri (java.net.URI. uri)]
      (case (.getScheme parsed-uri)
        ("postgres" "postgresql") (format-jdbc-uri parsed-uri "postgresql") ; Heroku uses postgres://, RedHat OpenShift uses postgresql://
        "mysql" (format-jdbc-uri parsed-uri "mysql")
        (throw (Exception. (str "Unsupported URI: " uri " please, submit an issue request and we'll try to add it. Pull requests also welcome")))))))
