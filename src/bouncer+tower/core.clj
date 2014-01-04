(ns bouncer+tower.core
  (:require [taoensso.tower :as tower
             :refer (with-locale with-tscope t *locale*)]
            [bouncer.core :refer [validate]]
            [bouncer.validators :as v]))

(def my-tconfig
  {:dev-mode? true
   :fallback-locale :en
   :dictionary
   {:en
    {:person  {:name {:required "A person must have a name"}
               :age  {:number   "A person's age must be a number. You provided '%s'"}
               :address {:postcode {:required "Missing postcode in address"}}}
     :missing  "<Missing translation: [%1$s %2$s %3$s]>"}
    :pt-BR
    {:person  {:name {:required "Atributo Nome para Pessoa é obrigatório."}
               :age  {:number   "Atributo Idade para Pessoa deve ser um número. Valor recebido foi '%s'"}
               :address {:postcode {:required "Endereço deve ter um código postal"}}}
     :missing  "<Tradução ausente: [%1$s %2$s %3$s]>"}}})

(defn message-fn
  "Receives a locale, tscope and a map containing error metadata.

  Uses this information to return a I18n'ed string"
  [locale tscope {:keys [path value]
                  {validator :validator} :metadata}]
  (let [tr-key (->> (flatten [path validator])
                    (map name)
                    (clojure.string/join "/")
                    keyword)]
    (with-tscope tscope
      (t locale my-tconfig tr-key value))))


(def person {:age "NaN"})


(validate (partial message-fn :en :person)
          person
          :name v/required
          :age  v/number
          [:address :postcode] v/required)

(validate (partial message-fn :pt-BR :person)
          person
          :name v/required
          :age  v/number
          [:address :postcode] v/required)
