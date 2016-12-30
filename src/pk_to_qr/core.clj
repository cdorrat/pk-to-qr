(ns pk-to-qr.core
  (:require [clj-pdf.core :as p]
            [clojure.java.io :as io]
            [buddy.core.keys :as ks]
            [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]])
  (:import [com.google.zxing EncodeHintType MultiFormatWriter BarcodeFormat]
           [com.google.zxing.common  BitMatrix]
           [com.google.zxing.client.j2se MatrixToImageWriter MatrixToImageConfig]
           [java.nio.file Paths])
    (:gen-class))

(defn content-to-qr-matrix [content version size]
  (-> (MultiFormatWriter.)
      (.encode content BarcodeFormat/QR_CODE size size
               {EncodeHintType/QR_VERSION  version
                EncodeHintType/ERROR_CORRECTION "M"})))

(defn- encode-as-qr-image [content version size]  
  (MatrixToImageWriter/toBufferedImage (content-to-qr-matrix content version size)))

(defn section-title [a-str]
  [:paragraph
   {:align :center
    :size 14
    :style :bold}
   a-str])

(defn- pdf-for-key [private-key-file public-key-file output-filename]
  (let [pkey (bean (ks/private-key private-key-file))
        priv-name (.getName (io/file private-key-file))
        pub-name (.getName (io/file public-key-file))]
   (p/pdf
    [{:title         (str "Keypair for " priv-name "/" pub-name)
      ;;     :header        "QR Encoded Key"
      :subject       "Some subject"
      :creator       "Cameron"
      :doc-header    ["inspired by" "William Shakespeare"]
      :right-margin  25
      :bottom-margin 25
      :left-margin   25
      :top-margin    25
      :size          "a4"
      :footer        "page"
      :pages true}

     [:paragraph {:size 8}
      [:table {:align :center
               :num-cols 2
               :padding 4
               :width 60
               :widths [1 2]}
       ["Created on host" (.getHostName (java.net.InetAddress/getLocalHost))]
       ["Private key filename" priv-name]
       ["Public key filename" pub-name]
       ["Algorithm" (:algorithm pkey)]
       ["Format" (:format pkey)]]]
     [:paragraph "\n"]
     (section-title "Public key (QR Encoded)")
     (when (.isFile (io/file public-key-file))
       [
        [:image
         {:xscale 1.0
          :align :center}
         (encode-as-qr-image (slurp public-key-file) 15 400)]
        (section-title "Public key (Raw Format)")
        [:paragraph {:align :center
                     :family  :courier
                     :size 12}
          (slurp public-key-file)]])
     [:pagebreak]
     (section-title "Private key (QR Encoded)")
     [:image
      {:xscale 1.0
       :align :center}
      (encode-as-qr-image (slurp private-key-file) 34 650)]
     [:pagebreak]
     (section-title "Private key (Raw Format)")
     [:paragraph {:align :center
                  :family  :courier
                  :size 12}
      "\n\n" (slurp private-key-file)]]
    output-filename)))


(def cli-opts
  [["-p" "--private-key filename" "Private key filename"
    :default (io/file (System/getenv "HOME") ".ssh" "id_rsa")]
   ["-u" "--public-key filename" "Public key fielname"
    :default (io/file (System/getenv "HOME") ".ssh" "id_rsa.pub")]
   ["-o" "--output filename" "Filename for the pdf output"
    :default "keypair.pdf"]
   ["-h" "--help"]])

(defn- usage [summary]
  (str "pk-to-qr - Converts key pairs to pdf for paper backup\n\n"
           summary))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-opts)
        {:keys [private-key public-key output help]} options]
    (cond
      help   (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))
    
    (pdf-for-key private-key public-key output)
    (exit 0 (str "Created pdf of keypair " private-key ":" public-key " in " output))))
