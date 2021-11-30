(ns hooks.impl
  (:require ["use-sync-external-store/shim/with-selector" :refer [useSyncExternalStoreWithSelector]]
            [react-dom]
            [react]))

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

(defn use-batched-subscribe
  "Takes an atom-like ref type and returns a function that subscribes to changes
  in the ref, where subscribed listeners execution is batched via `react-dom/unstable_batchedUpdates`"
  [^js ref]
  (react/useCallback
    (fn [listener]
      (setup-batched-updates-listener ref)
      (swap! (.-react-listeners ref) conj listener)
      (fn []
        (swap! (.-react-listeners ref) disj listener)
        (teardown-batched-updates-listener ref)))
    #js [ref]))

(defn use-sync-external-store [subscribe get-snapshot]
  (useSyncExternalStoreWithSelector
    subscribe
    get-snapshot
    nil ;; getServerSnapshot, only needed for SSR
    identity ;; selector, not using, just returning the value itself
    =)) ;; value equality check
