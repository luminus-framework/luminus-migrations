(ns luminus-migrations.core-test
  (:require 
   [clojure.test :refer :all]
   [luminus-migrations.core :refer :all]
   [luminus-migrations.util :refer :all]))

(deftest to-jdbc-uri-test
  (testing "Already JDBC URIs are left alone"
    (is (= (to-jdbc-uri "jdbc:postgresql://hostname:3306/dbname?user=username&password=password")
           "jdbc:postgresql://hostname:3306/dbname?user=username&password=password")))
  (testing "Heroku-like PostgreSQL URI"                     ;; I think this is actually a generic PostgreSQL URL and not Heroku specific.
    (is (= (to-jdbc-uri "postgres://username:password@hostname/dbname")
           "jdbc:postgresql://hostname/dbname?user=username&password=password")))
  (testing "Heroku-like PostgreSQL URI without username and password"
    (is (= (to-jdbc-uri "postgres://hostname:1234/dbname")
           "jdbc:postgresql://hostname:1234/dbname")))
  (testing "Heroku-like PostgreSQL URI with port and username; no password"
    (is (= (to-jdbc-uri "postgres://username@hostname:1234/dbname")
           "jdbc:postgresql://hostname:1234/dbname?user=username")))
  (testing "Heroku-like PostgreSQL URI with port, username and password"
    (is (= (to-jdbc-uri "postgres://username:password@hostname:1234/dbname")
           "jdbc:postgresql://hostname:1234/dbname?user=username&password=password")))
  (testing "RedHat OpenShift-like PostgreSQL URI with port, username and password"
    (is (= (to-jdbc-uri "postgresql://username:password@hostname:1234/dbname")
           "jdbc:postgresql://hostname:1234/dbname?user=username&password=password")))
  (testing "Heroku-like MySQL URI with port, username and password"
    (is (= (to-jdbc-uri "mysql://username:password@hostname:1234/dbname")
           "jdbc:mysql://hostname:1234/dbname?user=username&password=password")))
  (testing "Retains arguments without credentials"
    (is (= (to-jdbc-uri "postgres://hostname:1234/dbname?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory")
           "jdbc:postgresql://hostname:1234/dbname?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory")))
  (testing "Retains arguments and adds credentials"
    (is (= (to-jdbc-uri "postgres://username:password@hostname:1234/dbname?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory")
           "jdbc:postgresql://hostname:1234/dbname?user=username&password=password&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory")))
  (testing "Retains arguments for existing JDBC URI"
    (is (= (to-jdbc-uri "jdbc:postgresql://hostname:1234/dbname?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory")
           "jdbc:postgresql://hostname:1234/dbname?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory")))
  (testing "Empty uri raises excetion"
    (is (thrown? Exception (to-jdbc-uri "")))
    (is (thrown? Exception (to-jdbc-uri nil)))))
