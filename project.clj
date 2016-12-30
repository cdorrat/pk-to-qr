
(defproject pk-to-qr "0.1.0-SNAPSHOT"
  :description "Project to create printable backups of public/private key pairs"
  :url "http://github.com/cdorrat"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.google.zxing/javase "3.3.0"]
                 [clj-pdf "2.2.11"]
                 [buddy "0.8.3"]
                 [org.clojure/tools.cli "0.3.5"]]
  :main ^:skip-aot pk-to-qr.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
