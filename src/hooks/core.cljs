(ns hooks.core
  (:require ["use-sync-external-store/shim/with-selector" :refer [useSyncExternalStoreWithSelector]]))

(defn- setup-batched-updates-listener [^js ref]
  ;; Adding an atom holding a set of listeners on a ref if it wasn't added yet
  (when-not (.-react-listeners ref)
    (set! (.-react-listeners ref) (atom #{}))
    ;; When the ref is updated, execute all listeners in a batch
    (add-watch ref ::batched-subscribe
               (fn [_ _ _ _]
                 (react-dom/unstable_batchedUpdates
                   #(doseq [listener @(.-react-listeners ref)]
                      (listener)))))))

(defn- teardown-batched-updates-listener [^js ref]
  ;; When the last listener was removed,
  ;; remove batched updates listener from the ref
  (when (empty? @(.-react-listeners ref))
    (set! (.-react-listeners ref) nil)
    (remove-watch ref ::batched-subscribe)))

(defn- use-batched-subscribe
  "Takes an atom-like ref type and returns a function that subscribes to changes
  in the ref, where subscribed listeners execution is batched via `react-dom/unstable_batchedUpdates`"
  [^js ref]
  (setup-batched-updates-listener ref)
  (react/useCallback
    (fn [listener]
      (when ^boolean goog.DEBUG
         ;; Dev-only, to make sure that the hook still works
         ;; between hot-reloads for refs declared using `defonce`
        (setup-batched-updates-listener ref))
      (swap! (.-react-listeners ref) conj listener)
      (fn []
        (swap! (.-react-listeners ref) disj listener)
        (teardown-batched-updates-listener ref)))
    #js [ref]))

;; use-atom hook
(defn use-atom
  "Takes any Atom-like ref type (atom, ratom, re-frame subscription, etc.),
  subscribes UI component to changes in the ref
  and returns current state value of the ref"
  [ref]
  (let [subscribe (use-batched-subscribe ref)
        get-snapshot (react/useCallback #(-deref ref) #js [ref])]
    (useSyncExternalStoreWithSelector
      subscribe
      get-snapshot
      nil ;; getServerSnapshot, only needed for SSR
      identity ;; selector, not using, just returning the value itself
      =))) ;; value equality check

;; use-subscribe hook
(defn create-use-subscribe
  "Takes your re-frame `subscribe` function and returns a function that
  takes re-frame subscription query e.g. [:app/title],
  creates an instance of the subscription,
  subscribes UI component to changes in the subscription
  and returns current state value of the subscription

  (def use-subscribe (create-use-subscribe rf/subscribe))"
  [subscribe-fn]
  (fn use-subscribe [query] (use-atom (subscribe-fn query))))
