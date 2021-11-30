(ns hooks.reagent
  (:require [hooks.impl]
            [reagent.ratom]))

;; use-reaction hook
(defn use-reaction
  "Takes Reagent's Reaction,
  subscribes UI component to changes in the reaction
  and returns current state value of the reaction"
  [reaction]
  (assert (instance? ratom/Reaction reaction) "reaction should be an instance of reagent.ratom/Reaction")
  (let [subscribe (hooks.impl/use-batched-subscribe reaction)
        get-snapshot (react/useCallback (fn []
                                          ;; Mocking ratom context
                                          ;; This makes sure that watchers added to the `reaction`
                                          ;; will be triggered when the `reaction` gets updated.
                                          (binding [ratom/*ratom-context* #js {}]
                                            @reaction))
                                        #js [reaction])]
    (hooks.impl/use-sync-external-store subscribe get-snapshot)))

;; use-subscribe hook
(defn create-use-subscribe
  "Takes your re-frame `subscribe` function and returns a function that
  takes re-frame subscription query e.g. [:app/title],
  creates an instance of the subscription,
  subscribes UI component to changes in the subscription
  and returns current state value of the subscription

  (def use-subscribe (create-use-subscribe rf/subscribe))"
  [subscribe-fn]
  (fn use-subscribe [query] (use-reaction (subscribe-fn query))))
