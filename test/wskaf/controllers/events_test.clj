(ns wskaf.controllers.events-test
  (:use [midje.sweet]
        [wskaf.controllers.events]))

(fact "validate topic returns nil for valid topic"
      (validate-topic ...zk... ...topic...) => nil
      (provided (topic? ...zk... ...topic...) => true))

(fact "validate topic returns 404 for invalid topic"
      (:status (validate-topic ...zk... ...topic...)) => 404
      (provided (topic? ...zk... ...topic...) => nil))

(fact "validate-payload returns nil for valid payload"
      (validate-payload ...payload...)  => nil
      (provided (valid-payload? ...payload...) => true))

(fact "validate-payload returns 400 for invalid payload"
      (:status (validate-payload ...payload...))  => 400
      (provided (valid-payload? ...payload...) => false))

